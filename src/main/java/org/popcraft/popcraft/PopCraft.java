package org.popcraft.popcraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.popcraft.popcraft.commands.Aura;
import org.popcraft.popcraft.commands.Discord;
import org.popcraft.popcraft.commands.Donate;
import org.popcraft.popcraft.commands.Fireworks;
import org.popcraft.popcraft.commands.GetScore;
import org.popcraft.popcraft.commands.Glow;
import org.popcraft.popcraft.commands.Handicap;
import org.popcraft.popcraft.commands.History;
import org.popcraft.popcraft.commands.Listgen;
import org.popcraft.popcraft.commands.Lockdown;
import org.popcraft.popcraft.commands.Me;
import org.popcraft.popcraft.commands.Music;
import org.popcraft.popcraft.commands.Name;
import org.popcraft.popcraft.commands.PVP;
import org.popcraft.popcraft.commands.Piggyback;
import org.popcraft.popcraft.commands.Plugins;
import org.popcraft.popcraft.commands.Pop;
import org.popcraft.popcraft.commands.Spoof;
import org.popcraft.popcraft.commands.Staff;
import org.popcraft.popcraft.commands.SuperSay;
import org.popcraft.popcraft.commands.TeamSpeak;
import org.popcraft.popcraft.commands.Textures;
import org.popcraft.popcraft.commands.TicketCommand;
import org.popcraft.popcraft.commands.Tpr;
import org.popcraft.popcraft.commands.Trail;
import org.popcraft.popcraft.commands.Uuid;
import org.popcraft.popcraft.commands.Version;
import org.popcraft.popcraft.commands.Vote;
import org.popcraft.popcraft.tasks.AnvilColor;
import org.popcraft.popcraft.tasks.AnvilLogger;
import org.popcraft.popcraft.tasks.JonsLogger;
import org.popcraft.popcraft.tasks.MagicMessage;
import org.popcraft.popcraft.tasks.VoteHandler;
import org.popcraft.popcraft.tasks.WitchTrap;
import org.popcraft.popcraft.tasks.XPBoard;
import org.popcraft.popcraft.utils.Cooldown;
import org.popcraft.popcraft.utils.Message;
import org.popcraft.popcraft.utils.TeamManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PopCraft extends JavaPlugin implements Listener {

    private static Plugin plugin;
    public static FileConfiguration config;
    MagicMessage MagicMessage = new MagicMessage();
    JonsLogger jonslogger = new JonsLogger(this, getConfig().getString("jonslogger.flag").split(","),
            getConfig().getString("jonslogger.commands").split(","));

    @Override
    public void onEnable() {
        plugin = this;
        config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
        if (config.getBoolean("magicmessage.enableonstartup")) {
            MagicMessage.setMessage(config.getString("magicmessage.defaultmessage"));
            MagicMessage.setEnabled(true);
            MagicMessage.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(this, MagicMessage,
                    config.getLong("magicmessage.defaultinterval"), config.getLong("magicmessage.defaultinterval")));
        }
        ShapedRecipe recipeElytra = new ShapedRecipe(new NamespacedKey(this, "elytra"), new ItemStack(Material.ELYTRA))
                .shape("fcf", "fsf", "f f").setIngredient('c', Material.CHAINMAIL_CHESTPLATE)
                .setIngredient('f', Material.FEATHER).setIngredient('s', Material.NETHER_STAR);
        getServer().addRecipe(recipeElytra);
        ShapedRecipe recipeSkulkerShell = new ShapedRecipe(new NamespacedKey(this, "shulker_shell"),
                new ItemStack(Material.SHULKER_SHELL)).shape("ccc", "cfc", "c c")
                .setIngredient('c', Material.CHORUS_FRUIT).setIngredient('f', Material.END_CRYSTAL);
        getServer().addRecipe(recipeSkulkerShell);
        registerEvents(this, this, new PVP(), new AnvilColor(), new AnvilLogger(), jonslogger, new Piggyback(),
                new Aura(), new Trail(), new Fireworks(), new Glow(), new WitchTrap(), new XPBoard(), new VoteHandler());
        getCommand("textures").setExecutor(new Textures());
        getCommand("getscore").setExecutor(new GetScore());
        getCommand("music").setExecutor(new Music());
        getCommand("listgen").setExecutor(new Listgen());
        getCommand("me").setExecutor(new Me());
        getCommand("donate").setExecutor(new Donate());
        getCommand("discord").setExecutor(new Discord());
        getCommand("uuid").setExecutor(new Uuid());
        getCommand("staff").setExecutor(new Staff());
        getCommand("fireworks").setExecutor(new Fireworks());
        getCommand("tpr").setExecutor(new Tpr());
        getCommand("supersay").setExecutor(new SuperSay());
        getCommand("pop").setExecutor(new Pop());
        getCommand("lockdown").setExecutor(new Lockdown());
        getCommand("plugins").setExecutor(new Plugins());
        getCommand("version").setExecutor(new Version());
        getCommand("handicap").setExecutor(new Handicap());
        getCommand("vote").setExecutor(new Vote());
        getCommand("piggyback").setExecutor(new Piggyback());
        getCommand("lol").setExecutor(jonslogger);
        getCommand("lolreload").setExecutor(jonslogger);
        getCommand("aura").setExecutor(new Aura());
        getCommand("pvp").setExecutor(new PVP());
        getCommand("trail").setExecutor(new Trail());
        getCommand("flames").setExecutor(new Trail());
        getCommand("hearts").setExecutor(new Trail());
        getCommand("history").setExecutor(new History());
        getCommand("name").setExecutor(new Name());
        getCommand("spoof").setExecutor(new Spoof());
        getCommand("glow").setExecutor(new Glow());
        getCommand("teamspeak").setExecutor(new TeamSpeak());
        getCommand("ticket").setExecutor(new TicketCommand());
    }

    @Override
    public void onDisable() {
        plugin = null;
        getLogger().info("PopCraft plugin shutting down...");
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static void registerEvents(Plugin plugin, Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    private Map<Player, Boolean> nowos = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("magicmessage")) {
            commandMagicMessage((Player) sender, args);
        }
        if (cmd.getName().equalsIgnoreCase("worldspawn")) {
            commandWorldSpawn(sender);
        }
        if (cmd.getName().equalsIgnoreCase("nowo") && sender instanceof Player) {
            nowos.put((Player) sender, !nowos.getOrDefault(sender, false));
        }
        return true;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (Lockdown.isLockdown()) {
            e.disallow(Result.KICK_OTHER, ChatColor.GREEN + "PopCraft" + ChatColor.RESET
                    + "\n\nServer temporarily unavailable. Please try again later!");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws EventException {
        Player player = event.getPlayer();
        if (Bukkit.getServer().getOfflinePlayer(player.getUniqueId()).hasPlayedBefore() == true) {
            event.setJoinMessage(ChatColor.GREEN + "\u2714 " + player.getName());
            Bukkit.getScoreboardManager().getMainScoreboard().getObjective("Minutes").getScore(player.getName())
                    .setScore((int) (Math.round(player.getStatistic(Statistic.PLAY_ONE_MINUTE))));
        } else {
            event.setJoinMessage(ChatColor.GREEN + "Welcome to PopCraft, " + ChatColor.DARK_GREEN
                    + player.getDisplayName() + ChatColor.GREEN + "!");
            BukkitScheduler scheduler = Bukkit.getScheduler();
            final Player finalPlayer = event.getPlayer();
            scheduler.runTaskLater(this, new Runnable() {
                public void run() {
                    Message.whisper(finalPlayer, "Type /tpr if you would like to teleport away from spawn.");
                    Message.whisper(finalPlayer, "Server rules can be displayed with /rules.");
                }
            }, 15L);
            onPlayerJoinFirework(finalPlayer);
        }
        Glow.disableGlow(player);
        TeamManager.assignTeam(player);
        player.setPlayerListHeaderFooter(ChatColor.translateAlternateColorCodes('&', "&2PopCraft Survival"), ChatColor.translateAlternateColorCodes('&', "&2mc.popcraft.org"));
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
                    if (Cooldown.check(player, "jumper", 5100)) {
                        (new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 4)).apply(player);
                        player.setVelocity(player.getLocation().getDirection().multiply(new Vector(3, 3, 3)));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        String msg = event.getMessage();
        List<String> word = getConfig().getStringList("profanityprotect");
        for (String s : word) {
            if (msg.toLowerCase().contains(s)) {
                Bukkit.getScheduler().runTask(this, new Runnable() {
                    public void run() {
                        Message.kick(player, "Swearing is not allowed on this server!");
                    }
                });
                event.setCancelled(true);
                return;
            }
        }
        if (config.getBoolean("antispam.enabled")) {
            if (!Cooldown.check(player, "chat", config.getInt("antispam.cooldown"))) {
                Bukkit.getScheduler().runTask(this, new Runnable() {
                    public void run() {
                        Message.kick(player, "Spamming is not allowed on this server!");
                    }
                });
                event.setCancelled(true);
                return;
            }
        }
        if (nowos.getOrDefault(player, false)) {
            String aprilFoolsMessage = event.getMessage();
            aprilFoolsMessage.replaceAll("[Oo]", "owo");
            aprilFoolsMessage.replaceAll("[Uu]", "uwu");
            event.setMessage(aprilFoolsMessage);
        }
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent e) {
        if (e.getMaxPlayers() > Bukkit.getMaxPlayers())
            e.setMaxPlayers(e.getNumPlayers());
        if (Lockdown.isLockdown())
            e.setMaxPlayers(0);
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
            if (!Cooldown.check(p, "chat", config.getInt("antispam.cooldown"))) {
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

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        int lootingLevel = killer == null ? 0 : killer.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        double lootingBonus = lootingLevel * config.getDouble("heads.player.looting");
        if (config.getBoolean("heads.player.enabled")) {
            if (config.getDouble("heads.player.chance") + lootingBonus > Math.random()) {
                Bukkit.getPlayer(event.getEntity().getUniqueId()).getWorld()
                        .dropItemNaturally(event.getEntity().getLocation(), getPlayerHead(event.getEntity().getName()));
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        int lootingLevel = killer == null ? 0 : killer.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        double lootingBonus = lootingLevel * config.getDouble("heads.dragon.looting");
        if (config.getBoolean("heads.dragon.enabled")) {
            if (e.getEntityType().equals(EntityType.ENDER_DRAGON)) {
                World w = e.getEntity().getWorld();
                w.dropItem(e.getEntity().getLocation(), new ItemStack(Material.DRAGON_EGG, 1));
                if (config.getDouble("heads.dragon.chance") + lootingBonus > Math.random()) {
                    w.dropItem(e.getEntity().getLocation(), new ItemStack(Material.DRAGON_HEAD, 1, (short) 5));
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntityType().equals(EntityType.ENDER_CRYSTAL))
            e.setCancelled(true);
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().length() > 3 && e.getMessage().substring(0, 4).equalsIgnoreCase("/buy")) {
            e.setCancelled(true);
        }
    }

    private void onPlayerJoinFirework(final Player PLAYER) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(PopCraft.getPlugin(), new Runnable() {
            public void run() {
                int n = 1;
                while (n <= 10) {
                    Location LOCATION = Fireworks.commandLocationFirework(PLAYER);
                    Color COLOR = Fireworks.colorFirework();
                    Color FADE = Fireworks.fadeFirework(COLOR);
                    Fireworks.spawnFirework(PLAYER, LOCATION, COLOR, FADE, Type.BALL_LARGE, true, false, 0);
                    n++;
                }
            }
        }, 30L);
    }

    public ItemStack getPlayerHead(String playername) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
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