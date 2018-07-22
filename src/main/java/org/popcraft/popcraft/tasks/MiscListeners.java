package org.popcraft.popcraft.tasks;

import com.google.inject.Inject;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.popcraft.popcraft.commands.Glow;
import org.popcraft.popcraft.commands.TicketCommand;
import org.popcraft.popcraft.utils.Message;
import org.popcraft.popcraft.utils.TeamManager;

public class MiscListeners implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Server server;

    @Inject
    public MiscListeners(final JavaPlugin plugin, final FileConfiguration config, final Server server) {
        this.plugin = plugin;
        this.config = config;
        this.server = server;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.server.getOfflinePlayer(player.getUniqueId()).hasPlayedBefore()) {
            event.setJoinMessage(ChatColor.GREEN + "\u2714 " + player.getName());
            this.server.getScoreboardManager().getMainScoreboard().getObjective("Minutes").getScore(player.getName())
                    .setScore((int) (Math.round(player.getStatistic(Statistic.PLAY_ONE_TICK) / 1200)));
        } else {
            event.setJoinMessage(ChatColor.GREEN + "Welcome to PopCraft, " + ChatColor.DARK_GREEN
                    + player.getDisplayName() + ChatColor.GREEN + "!");
            BukkitScheduler scheduler = this.server.getScheduler();
            final Player finalPlayer = event.getPlayer();
            scheduler.runTaskLater(this.plugin, () -> {
                Message.whisper(finalPlayer, "Type /tpr if you would like to teleport away from spawn.");
                Message.whisper(finalPlayer, "Server rules can be displayed with /rules.");
            }, 15L);
        }
        Glow.disableGlow(player);
        TeamManager.assignTeam(player);
        if (player.hasPermission("popcraft.ticket.mod")) {
            int ticketCount = TicketCommand.getTicketManager().ticketCount(true);
            if (ticketCount > 0)
                Message.normal(player, "There are " + ChatColor.RED + ticketCount + ChatColor.GOLD
                        + " tickets open! Type " + ChatColor.RED + "/ticket list" + ChatColor.GOLD + " to view them.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(ChatColor.GREEN + "\u2715 " + player.getName());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (config.getBoolean("heads.player.enabled")) {
            if (config.getDouble("heads.player.chance") > Math.random()) {
                this.server.getPlayer(event.getEntity().getUniqueId()).getWorld()
                        .dropItemNaturally(event.getEntity().getLocation(), getPlayerHead(event.getEntity().getName()));
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (config.getBoolean("heads.dragon.enabled")) {
            if (e.getEntityType().equals(EntityType.ENDER_DRAGON)) {
                World w = e.getEntity().getWorld();
                w.getBlockAt(0, w.getHighestBlockYAt(0, 0) + 1, 0).setType(Material.DRAGON_EGG);
                if (config.getDouble("heads.dragon.chance") > Math.random()) {
                    w.dropItem(e.getEntity().getLocation(), new ItemStack(Material.SKULL_ITEM, 1, (short) 5));
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntityType().equals(EntityType.ENDER_CRYSTAL))
            e.setCancelled(true);
    }

    public ItemStack getPlayerHead(String playername) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(playername);
        item.setItemMeta(meta);
        return item;
    }

}
