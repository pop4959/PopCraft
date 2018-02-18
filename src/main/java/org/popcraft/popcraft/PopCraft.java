package org.popcraft.popcraft;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.popcraft.popcraft.commands.*;
import org.popcraft.popcraft.tasks.MagicMessage;
import org.popcraft.popcraft.utils.CooldownOld;
import org.popcraft.popcraft.utils.Message;
import org.popcraft.popcraft.utils.TeamManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static java.lang.String.format;
import static org.popcraft.popcraft.PopCraftModule.*;

//TODO Figure out this MagicMessage business
@Deprecated
public final class PopCraft extends JavaPlugin implements Listener {

    private static Plugin plugin;
    public static FileConfiguration config;
    private MagicMessage MagicMessage = new MagicMessage();

    @Override
    public void onEnable() {
        //TODO Deprecate this
        plugin = this;
        config = getConfig();

        getLogger().info("Popcraft plugin starting up...");
        getConfig().options().copyDefaults(true);
        saveConfig();
        final Injector injector = Guice.createInjector(new PopCraftModule(this));

        //Register Commands
        getLogger().info("Registering Commands");
        final Map<String, CommandExecutor> commands = injector.getInstance(COMMAND_KEY);
        commands.map(pair -> format("Registering /%s with %s", pair._1, pair._2)).forEach(getLogger()::info);
        commands.mapKeys(this::getCommand).forEach(PluginCommand::setExecutor);

        Messages.registerCommands(this);

        //Register Listeners
        getLogger().info("Registering Listeners");
        final Set<Listener> listeners = injector.getInstance(LISTENERS_KEY);
        listeners.map(listener -> format("Registering %s as a listener", listener)).forEach(getLogger()::info);
        listeners.forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        //Register Recipes
        getLogger().info("Registering Recipes");
        final Set<Recipe> recipes = injector.getInstance(RECIPES_KEY);
        getLogger().info(format("Registering %d recipes", recipes.size()));
        recipes.forEach(getServer()::addRecipe);

        getLogger().info("PopCraft is now completely enabled");
    }

    @Override
    public void onDisable() {
        plugin = null;
        getLogger().info("PopCraft plugin shutting down...");
    }

    @Deprecated
    public static Plugin getPlugin() {
        return plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("magicmessage")) {
            commandMagicMessage((Player) sender, args);
        }
        if (cmd.getName().equalsIgnoreCase("worldspawn")) {
            commandWorldSpawn(sender);
        }
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws EventException {
        Player player = event.getPlayer();
        if (Bukkit.getServer().getOfflinePlayer(player.getUniqueId()).hasPlayedBefore()) {
            event.setJoinMessage(ChatColor.GREEN + "\u2714 " + player.getName());
            Bukkit.getScoreboardManager().getMainScoreboard().getObjective("Minutes").getScore(player.getName())
                    .setScore((int) (Math.round(player.getStatistic(Statistic.PLAY_ONE_TICK) / 1200)));
        } else {
            event.setJoinMessage(ChatColor.GREEN + "Welcome to PopCraft, " + ChatColor.DARK_GREEN
                    + player.getDisplayName() + ChatColor.GREEN + "!");
            BukkitScheduler scheduler = Bukkit.getScheduler();
            final Player finalPlayer = event.getPlayer();
            scheduler.runTaskLater(this, () -> {
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
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player.hasPermission("popcraft.jumper")) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                if ((player.getInventory().getItemInMainHand().getType() == Material.FEATHER)
                        || (player.getInventory().getItemInOffHand().getType() == Material.FEATHER)) {
                    if (CooldownOld.check(player, "jumper", 5100)) {
                        (new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 4)).apply(player);
                        player.setVelocity(player.getLocation().getDirection().multiply(new Vector(3, 3, 3)));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        final Player p = event.getPlayer();
        String msg = event.getMessage();
        List<String> word = getConfig().getStringList("profanityprotect");
        for (String s : word) {
            if (msg.toLowerCase().contains(s)) {
                Bukkit.getScheduler().runTask(this, new Runnable() {
                    public void run() {
                        Message.kick(p, "Swearing is not allowed on this server!");
                    }
                });
                event.setCancelled(true);
                return;
            }
        }
        if (config.getBoolean("antispam.enabled")) {
            if (!CooldownOld.check(p, "chat", config.getInt("antispam.cooldown"))) {
                Bukkit.getScheduler().runTask(this, new Runnable() {
                    public void run() {
                        Message.kick(p, "Spamming is not allowed on this server!");
                    }
                });
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        final Player p = event.getPlayer();
        String ml = event.getMessage().toLowerCase();
        if (!p.hasPermission("popcraft.staff")) {
            List<String> word = getConfig().getStringList("profanityprotect");
            for (String s : word) {
                if (ml.contains(s)) {
                    Bukkit.getScheduler().runTask(this, new Runnable() {
                        public void run() {
                            Message.kick(p, "Swearing is not allowed on this server!");
                        }
                    });
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (config.getBoolean("antispam.enabled")) {
            if (!CooldownOld.check(p, "chat", config.getInt("antispam.cooldown"))) {
                Bukkit.getScheduler().runTask(this, new Runnable() {
                    public void run() {
                        Message.kick(p, "Spamming is not allowed on this server!");
                    }
                });
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerEditBookEvent(PlayerEditBookEvent event) {
        try {
            File bookfile = new File("books.txt");
            if (!bookfile.exists()) {
                bookfile.createNewFile();
            }
            FileWriter bookwriter = new FileWriter(bookfile, true);
            BufferedWriter bookbuffered = new BufferedWriter(bookwriter);
            String playername = event.getPlayer().getName();
            String timestamp = Message.getCurrentTime();
            String booktext = event.getNewBookMeta().getPages().toString().replace("[", "\"").replace("]", "\"");
            bookbuffered.append(playername + " - " + timestamp + " - " + booktext);
            bookbuffered.newLine();
            bookbuffered.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void SignChangeEvent(SignChangeEvent event) {
        try {
            File signfile = new File("signs.txt");
            if (!signfile.exists()) {
                signfile.createNewFile();
            }
            FileWriter signwriter = new FileWriter(signfile, true);
            BufferedWriter signbuffered = new BufferedWriter(signwriter);
            String playername = event.getPlayer().getName();
            String timestamp = Message.getCurrentTime();
            String signtext = "\"" + event.getLine(0) + " " + event.getLine(1) + " " + event.getLine(2) + " "
                    + event.getLine(3) + "\"";
            String location = "[" + Math.round(event.getBlock().getLocation().getX()) + ","
                    + Math.round(event.getBlock().getLocation().getY()) + ","
                    + Math.round(event.getBlock().getLocation().getZ()) + "]";
            signbuffered.append(playername + " - " + timestamp + " - " + location + " - " + signtext);
            signbuffered.newLine();
            signbuffered.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (config.getBoolean("heads.player.enabled")) {
            if (config.getDouble("heads.player.chance") > Math.random()) {
                Bukkit.getPlayer(event.getEntity().getUniqueId()).getWorld()
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

    public void commandMagicMessage(Player sender, String args[]) {
        if (args.length >= 1) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("stop")) {
                    if (MagicMessage.getEnabled()) {
                        Bukkit.getScheduler().cancelTask(MagicMessage.getTaskId());
                        MagicMessage.setEnabled(false);
                        Message.normal(sender, "MagicMessage stopped.");
                    } else {
                        Message.error(sender, "MagicMessage is already stopped!");
                    }
                } else if (args[0].equalsIgnoreCase("show")) {
                    Message.normal(sender, "Current message: " + MagicMessage.getMessage());
                } else if (args[0].equalsIgnoreCase("force")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.hasPermission("popcraft.magicmessage.receive")) {
                            Message.normal(sender, MagicMessage.getMessage());
                        }
                    }
                } else {
                    Message.usage(sender, "magicmessage [start <message>/stop/show/force]");
                }
            } else {
                if (args[0].equalsIgnoreCase("start")) {
                    if (!MagicMessage.getEnabled()) {
                        String message = "";
                        int n = 1;
                        while (n < args.length) {
                            message = message + args[n] + " ";
                            n++;
                        }
                        MagicMessage.setMessage(message);
                        MagicMessage.setEnabled(true);
                        MagicMessage.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(this, MagicMessage,
                                config.getLong("magicmessage.defaultinterval"),
                                config.getLong("magicmessage.defaultinterval")));
                        Message.normal(sender, "MagicMessage started...");
                    } else {
                        Message.error(sender, "MagicMessage is already running!");
                    }
                }
            }
        } else {
            Message.usage(sender, "magicmessage [start <message>/stop/show/force]");
        }
    }

    public void commandWorldSpawn(CommandSender sender) {
        Bukkit.getWorld("world").setSpawnLocation((int) config.getDouble("spawn.coordinate-x"),
                (int) config.getDouble("spawn.coordinate-y"), (int) config.getDouble("spawn.coordinate-z"));
    }

}