package org.popcraft.popcraft;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.popcraft.popcraft.commands.*;
import org.popcraft.popcraft.tasks.*;
import org.popcraft.popcraft.utils.*;

public final class PopCraft extends JavaPlugin implements Listener {

    private static Plugin plugin;
    public static FileConfiguration config;
    MagicMessage MagicMessage = new MagicMessage();
    JonsLogger jonslogger = new JonsLogger(this, getConfig().getString("jonslogger.flag").split(","),
	    getConfig().getString("jonslogger.commands").split(","));

    @Override
    public void onEnable() {
	getLogger().info("PopCraft plugin starting up...");
	plugin = this;
	registerEvents(this, this, new PVP(), new AnvilColor(), new AnvilLogger(), jonslogger, new Piggyback(),
		new Aura());
	getCommand("textures").setExecutor(new Textures());
	getCommand("getscore").setExecutor(new GetScore());
	getCommand("music").setExecutor(new Music());
	getCommand("listgen").setExecutor(new Listgen());
	getCommand("me").setExecutor(new Me());
	getCommand("donate").setExecutor(new Donate());
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
	config = getConfig();
	config.options().copyDefaults(true);
	saveConfig();
	if (config.getBoolean("magicmessage.enableonstartup")) {
	    MagicMessage.setMessage(config.getString("magicmessage.defaultmessage"));
	    MagicMessage.setEnabled(true);
	    MagicMessage.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(this, MagicMessage,
		    config.getLong("magicmessage.defaultinterval"), config.getLong("magicmessage.defaultinterval")));
	}
    }

    @Override
    public void onDisable() {
	plugin = null;
	getLogger().info("PopCraft plugin shutting down...");
    }

    public static Plugin getPlugin() {
	return plugin;
    }

    public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
	for (Listener listener : listeners) {
	    Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
	}
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lol, String[] args) {
	if (cmd.getName().equalsIgnoreCase("magicmessage")) {
	    commandMagicMessage((Player) sender, args);
	}
	if (cmd.getName().equalsIgnoreCase("worldspawn")) {
	    commandWorldSpawn(sender);
	}
	if (sender instanceof Player) {
	    if (cmd.getName().equalsIgnoreCase("trail")) {
		commandTrail((Player) sender, args);
	    }
	    if (cmd.getName().equalsIgnoreCase("flames")) {
		commandFlames((Player) sender, args);
	    }
	    if (cmd.getName().equalsIgnoreCase("hearts")) {
		commandHearts((Player) sender, args);
	    }
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

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws EventException {
	final Player player = event.getPlayer();
	if (Bukkit.getServer().getOfflinePlayer(player.getUniqueId()).hasPlayedBefore() == true) {
	    event.setJoinMessage(ChatColor.GREEN + "\u2714 " + player.getName());
	    Bukkit.getScoreboardManager().getMainScoreboard().getObjective("Minutes").getScore(player.getName())
		    .setScore((int) (Math.round(player.getStatistic(Statistic.PLAY_ONE_TICK) / 1200)));
	} else {
	    event.setJoinMessage(ChatColor.GREEN + "Welcome to PopCraft, " + ChatColor.DARK_GREEN
		    + player.getDisplayName() + ChatColor.GREEN + "!");
	    BukkitScheduler scheduler = Bukkit.getScheduler();
	    scheduler.runTaskLater(this, new Runnable() {
		public void run() {
		    Message.whisper(player, "Type /tpr if you would like to teleport away from spawn.");
		    Message.whisper(player, "Server rules can be displayed with /rules.");
		}
	    }, 15L);
	    onPlayerJoinFirework(player);
	}
	if (Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(player) == null) {
	    setTabColor(player, "f");
	}
	if (player.hasPermission("popcraft.tab.donator")
		&& Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(player).getName().equals("f")) {
	    removeTabColor(player, "f");
	    setTabColor(player, "6");
	}
	if (!player.hasPermission("popcraft.tab.donator")
		&& Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(player).getName().equals("6")) {
	    removeTabColor(player, "6");
	    setTabColor(player, "f");
	}
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
	Player player = event.getPlayer();
	event.setQuitMessage(ChatColor.GREEN + "\u2715 " + player.getName());
	if (playertrail.containsKey(player.getName())) {
	    playertrail.remove(player.getName());
	}
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
	Player player = event.getPlayer();
	if (playertrail.containsKey(player.getName())) {
	    Material trail = playertrail.get(player.getName());
	    if (trail == Material.ARROW) {
		player.getWorld().playEffect(player.getLocation().add(0, -1, 0), Effect.MOBSPAWNER_FLAMES, 32);
	    }
	    if (trail == Material.ACACIA_DOOR_ITEM) {
		player.getWorld().playEffect(player.getLocation(), Effect.CLOUD, 32);
	    }
	    if (trail == Material.DIAMOND) {
		player.getWorld().playEffect(player.getLocation(), Effect.COLOURED_DUST, 32);
	    }
	    if (trail == Material.APPLE) {
		player.getWorld().playEffect(player.getLocation(), Effect.CRIT, 32);
	    }
	    if (trail == Material.BAKED_POTATO) {
		player.getWorld().playEffect(player.getLocation(), Effect.EXPLOSION, 32);
	    }
	    if (trail == Material.BED) {
		player.getWorld().playEffect(player.getLocation(), Effect.EXPLOSION_HUGE, 32);
	    }
	    if (trail == Material.BIRCH_DOOR_ITEM) {
		player.getWorld().playEffect(player.getLocation(), Effect.EXPLOSION_LARGE, 32);
	    }
	    if (trail == Material.BLAZE_POWDER) {
		player.getWorld().playEffect(player.getLocation(), Effect.FIREWORKS_SPARK, 32);
	    }
	    if (trail == Material.BLAZE_ROD) {
		player.getWorld().playEffect(player.getLocation(), Effect.FLAME, 32);
	    }
	    if (trail == Material.BOAT) {
		player.getWorld().playEffect(player.getLocation(), Effect.FLYING_GLYPH, 32);
	    }
	    if (trail == Material.BONE) {
		player.getWorld().playEffect(player.getLocation(), Effect.FOOTSTEP, 32);
	    }
	    if (trail == Material.BOOK) {
		player.getWorld().playEffect(player.getLocation(), Effect.HAPPY_VILLAGER, 32);
	    }
	    if (trail == Material.BOOK_AND_QUILL) {
		player.getWorld().playEffect(player.getLocation(), Effect.HEART, 32);
	    }
	    if (trail == Material.BOW) {
		player.getWorld().playEffect(player.getLocation(), Effect.INSTANT_SPELL, 32);
	    }
	    if (trail == Material.BOWL) {
		player.getWorld().playEffect(player.getLocation(), Effect.LARGE_SMOKE, 32);
	    }
	    if (trail == Material.BREAD) {
		player.getWorld().playEffect(player.getLocation(), Effect.LAVA_POP, 32);
	    }
	    if (trail == Material.BREWING_STAND_ITEM) {
		player.getWorld().playEffect(player.getLocation(), Effect.LAVADRIP, 32);
	    }
	    if (trail == Material.BUCKET) {
		player.getWorld().playEffect(player.getLocation(), Effect.MAGIC_CRIT, 32);
	    }
	    if (trail == Material.CAKE) {
		player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 32);
	    }
	    if (trail == Material.CARROT_ITEM) {
		player.getWorld().playEffect(player.getLocation(), Effect.NOTE, 32);
	    }
	    if (trail == Material.CARROT_STICK) {
		player.getWorld().playEffect(player.getLocation(), Effect.PARTICLE_SMOKE, 32);
	    }
	    if (trail == Material.CAULDRON_ITEM) {
		player.getWorld().playEffect(player.getLocation(), Effect.PORTAL, 32);
	    }
	    if (trail == Material.CHAINMAIL_BOOTS) {
		player.getWorld().playEffect(player.getLocation(), Effect.POTION_BREAK, 32);
	    }
	    if (trail == Material.CHAINMAIL_CHESTPLATE) {
		player.getWorld().playEffect(player.getLocation(), Effect.POTION_SWIRL, 32);
	    }
	    if (trail == Material.CHAINMAIL_HELMET) {
		player.getWorld().playEffect(player.getLocation(), Effect.POTION_SWIRL_TRANSPARENT, 32);
	    }
	    if (trail == Material.CHAINMAIL_LEGGINGS) {
		player.getWorld().playEffect(player.getLocation(), Effect.SLIME, 32);
	    }
	    if (trail == Material.CLAY_BALL) {
		player.getWorld().playEffect(player.getLocation(), Effect.SMALL_SMOKE, 32);
	    }
	    if (trail == Material.COAL) {
		player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 32);
	    }
	    if (trail == Material.COMPASS) {
		player.getWorld().playEffect(player.getLocation(), Effect.SNOW_SHOVEL, 32);
	    }
	    if (trail == Material.COOKED_BEEF) {
		player.getWorld().playEffect(player.getLocation(), Effect.SNOWBALL_BREAK, 32);
	    }
	    if (trail == Material.COOKED_CHICKEN) {
		player.getWorld().playEffect(player.getLocation(), Effect.SPELL, 32);
	    }
	    if (trail == Material.COOKED_FISH) {
		player.getWorld().playEffect(player.getLocation(), Effect.SPLASH, 32);
	    }
	    if (trail == Material.COOKED_MUTTON) {
		player.getWorld().playEffect(player.getLocation(), Effect.VILLAGER_THUNDERCLOUD, 32);
	    }
	    if (trail == Material.COOKED_RABBIT) {
		player.getWorld().playEffect(player.getLocation(), Effect.VOID_FOG, 32);
	    }
	    if (trail == Material.COOKIE) {
		player.getWorld().playEffect(player.getLocation(), Effect.WATERDRIP, 32);
	    }
	    if (trail == Material.DARK_OAK_DOOR_ITEM) {
		player.getWorld().playEffect(player.getLocation(), Effect.WITCH_MAGIC, 32);
	    }
	    if (trail == Material.LAVA_BUCKET) {
		for (int x = 1; x <= 2; x++)
		    player.getWorld().playEffect(Trail.randomizeLocation(player.getLocation(), 0.5, 0.8, false),
			    Effect.COLOURED_DUST, 32);
	    }
	    if (trail == Material.LEASH) {
		for (int x = 1; x <= 2; x++)
		    player.getWorld().playEffect(player.getLocation(), Effect.CRIT, 32);
	    }
	    if (trail == Material.LEATHER) {
		for (int x = 1; x <= 3; x++)
		    player.getWorld().playEffect(player.getLocation(), Effect.FLYING_GLYPH, 32);
	    }
	    if (trail == Material.LEATHER_BOOTS) {
		for (int x = 1; x <= 2; x++)
		    player.getWorld().playEffect(Trail.randomizeLocation(player.getLocation(), 0.5, 0.8, false),
			    Effect.HAPPY_VILLAGER, 32);
	    }
	    if (trail == Material.LEATHER_CHESTPLATE) {
		player.getWorld().playEffect(Trail.randomizeLocation(player.getLocation(), 0.5, 0.8, false),
			Effect.HEART, 32);
	    }
	    if (trail == Material.LEATHER_HELMET) {
		for (int x = 1; x <= 2; x++)
		    player.getWorld().playEffect(player.getLocation(), Effect.INSTANT_SPELL, 32);
	    }
	    if (trail == Material.LEATHER_LEGGINGS) {
		player.getWorld().playEffect(player.getLocation(), Effect.LAVA_POP, 32);
	    }
	    if (trail == Material.MAGMA_CREAM) {
		for (int x = 1; x <= 5; x++)
		    player.getWorld().playEffect(Trail.moveUp(player.getLocation(), 0.4), Effect.LAVADRIP, 32);
	    }
	    if (trail == Material.MAP) {
		for (int x = 1; x <= 2; x++)
		    player.getWorld().playEffect(player.getLocation(), Effect.MAGIC_CRIT, 32);
	    }
	    if (trail == Material.MELON) {
		player.getWorld().playEffect(player.getLocation().add(0, -1, 0), Effect.MOBSPAWNER_FLAMES, 32);
	    }
	    if (trail == Material.MELON_SEEDS) {
		player.getWorld().playEffect(Trail.randomizeLocation(player.getLocation(), 0.5, 0.8, false),
			Effect.NOTE, 32);
	    }
	    if (trail == Material.MILK_BUCKET) {
		for (int x = 1; x <= 10; x++)
		    player.getWorld().playEffect(player.getLocation(), Effect.PORTAL, 32);
	    }
	    if (trail == Material.MINECART) {
		player.getWorld().playEffect(Trail.randomizeLocation(player.getLocation(), 0.5, 0.8, false),
			Effect.POTION_SWIRL, 32);
	    }
	    if (trail == Material.MUSHROOM_SOUP) {
		for (int x = 1; x <= 2; x++)
		    player.getWorld().playEffect(Trail.randomizeLocation(player.getLocation(), 0.1, 0, false),
			    Effect.SLIME, 32);
	    }
	    if (trail == Material.MUTTON) {
		for (int x = 1; x <= 10; x++)
		    player.getWorld().playEffect(Trail.randomizeLocation(player.getLocation(), 0.5, 0.8, false),
			    Effect.SMALL_SMOKE, 32);
	    }
	    if (trail == Material.NAME_TAG) {
		for (int x = 1; x <= 2; x++)
		    player.getWorld().playEffect(Trail.randomizeLocation(player.getLocation(), 0.1, 0, false),
			    Effect.SNOWBALL_BREAK, 32);
	    }
	    if (trail == Material.NETHER_BRICK_ITEM) {
		for (int x = 1; x <= 3; x++)
		    player.getWorld().playEffect(Trail.randomizeLocation(player.getLocation(), 0.1, 0, false),
			    Effect.SPLASH, 32);
	    }
	    if (trail == Material.NETHER_STAR) {
		player.getWorld().playEffect(Trail.randomizeLocation(player.getLocation(), 0.6, 0.3, false),
			Effect.VILLAGER_THUNDERCLOUD, 32);
	    }
	    if (trail == Material.PAINTING) {
		for (int x = 1; x <= 5; x++)
		    player.getWorld().playEffect(Trail.moveUp(player.getLocation(), 0.4), Effect.WATERDRIP, 32);
	    }
	    if (trail == Material.PAPER) {
		for (int x = 1; x <= 2; x++)
		    player.getWorld().playEffect(Trail.randomizeLocation(player.getLocation(), 0.1, 0, false),
			    Effect.WITCH_MAGIC, 32);
	    }
	    if (trail == Material.POISONOUS_POTATO) {
		player.spawnParticle(Particle.END_ROD, player.getLocation(), 32, 0.5, 0, 0.5);
	    }
	    if (trail == Material.PORK) {
		player.spawnParticle(Particle.DRAGON_BREATH, player.getLocation(), 32, 0.5, 0, 0.5);
	    }
	    if (trail == Material.POTATO_ITEM) {
		player.spawnParticle(Particle.DAMAGE_INDICATOR, player.getLocation(), 32, 0.5, 0, 0.5);
	    }
	    if (trail == Material.POTION) {
		player.spawnParticle(Particle.SWEEP_ATTACK, player.getLocation(), 32, 0.5, 0, 0.5);
	    }
	    if (trail.isBlock()) {
		if (style.get(player.getName()).equals("dots")) {
		    player.getWorld().spigot().playEffect(player.getLocation(), Effect.TILE_DUST, trail.getId(),
			    damage.get(player.getName()), 0.0F, 0.0F, 0.0F, 0.0F, 32, 32);
		} else if (style.get(player.getName()).equals("rain")) {
		    for (int x = 0; x < 16; x++)
			player.getWorld().spigot().playEffect(
				Trail.randomizeLocation(player.getLocation(), 1).add(0, 4, 0), Effect.TILE_DUST,
				trail.getId(), damage.get(player.getName()), 0.0F, 0.0F, 0.0F, 0.0F, 2, 32);
		} else {
		    player.getWorld().spigot().playEffect(player.getLocation(), Effect.TILE_BREAK, trail.getId(),
			    damage.get(player.getName()), 0.0F, 0.0F, 0.0F, 0.0F, 32, 32);
		}
	    }
	}
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
	final Player player = event.getPlayer();
	if (player.hasPermission("popcraft.jumper")) {
	    if (event.getAction() == Action.RIGHT_CLICK_AIR) {
		if ((player.getInventory().getItemInMainHand().getType() == Material.FEATHER)
			|| (player.getInventory().getItemInOffHand().getType() == Material.FEATHER)) {
		    if (Cooldown.check(player, "jumper", 5100)) {
			invincibilityEffect(100, 4).apply(player);
			player.setVelocity(playerJump(player, 3, 3, 3));
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

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
	if (config.getBoolean("playerheads.enabled")) {
	    if (config.getDouble("playerheads.chance") > Math.random()) {
		Bukkit.getPlayer(event.getEntity().getUniqueId()).getWorld()
			.dropItemNaturally(event.getEntity().getLocation(), getPlayerHead(event.getEntity().getName()));
	    }
	}
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent e) {
	if (e.getItem().equals(new ItemStack(Material.HOPPER_MINECART))) {
	    e.setCancelled(true);
	}
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
	if (e.getEntityType().equals(EntityType.ENDER_CRYSTAL))
	    e.setCancelled(true);
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

    private PotionEffect invincibilityEffect(int time, int strength) {
	int duration = time;
	int amplifier = strength;
	PotionEffect PotionEffect = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, amplifier);
	return PotionEffect;
    }

    public Vector playerJump(Player player, int xval, int yval, int zval) {
	Vector direction = player.getLocation().getDirection();
	int x = xval;
	int y = yval;
	int z = zval;
	Vector multiplier = new Vector(x, y, z);
	Vector newdirection = direction.multiply(multiplier);
	return newdirection;

    }

    @SuppressWarnings("deprecation")
    public void setTabColor(Player player, String teamname) {
	Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
	Team team = scoreboard.getTeam(teamname);
	team.addPlayer(player);
    }

    @SuppressWarnings({ "deprecation" })
    public void removeTabColor(Player player, String teamname) {
	Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
	Team team = scoreboard.getTeam(teamname);
	team.removePlayer(player);
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

    private static HashMap<String, Material> playertrail = new HashMap<String, Material>();
    private static HashMap<String, Integer> damage = new HashMap<String, Integer>();
    private static HashMap<String, String> style = new HashMap<String, String>();

    public void commandTrail(final Player player, final String[] trailargs) {
	Bukkit.getScheduler().runTask(this, new Runnable() {
	    @SuppressWarnings("deprecation")
	    public void run() {
		boolean materialExists = true;
		damage.put(player.getName(), 0);
		style.put(player.getName(), "none");
		if (trailargs.length == 0) {
		    if (playertrail.containsKey(player.getName())) {
			playertrail.remove(player.getName());
			Message.normal(player, "Cleared trail.");
		    } else {
			Message.usage(player, "trail <clear/list/type>");
		    }
		} else if (trailargs.length == 1) {
		    if (trailargs[0].equalsIgnoreCase("clear")) {
			if (playertrail.containsKey(player.getName())) {
			    playertrail.remove(player.getName());
			    Message.normal(player, "Cleared trail.");
			} else {
			    Message.error(player, "You don't have a trail enabled!");
			}
		    } else if (trailargs[0].equalsIgnoreCase("list")) {
			Message.normal(player, "Trails: " + ChatColor.RESET
				+ "bubbles, flames, glitter, lavadrops, letters, love, magic, magma, music, rainbow, raindrops, slime, smoke, snow, sparkles, sparks, swirls, teleport, thunderclouds, waterdrops");
		    } else if (trailargs[0].equalsIgnoreCase("type")) {
			Message.normal(player, "Trails: " + ChatColor.RESET
				+ "bubbles, flames, glitter, lavadrops, letters, love, magic, magma, music, rainbow, raindrops, slime, smoke, snow, sparkles, sparks, swirls, teleport, thunderclouds, waterdrops");
		    } else if (trailargs[0].equals("CLOUD")) {
			playertrail.put(player.getName(), Material.ACACIA_DOOR_ITEM);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "CLOUD" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("COLOURED_DUST")) {
			playertrail.put(player.getName(), Material.DIAMOND);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "COLOURED_DUST" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("CRIT")) {
			playertrail.put(player.getName(), Material.APPLE);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "CRIT" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("EXPLOSION")) {
			playertrail.put(player.getName(), Material.BAKED_POTATO);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "EXPLOSION" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("EXPLOSION_HUGE")) {
			playertrail.put(player.getName(), Material.BED);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "EXPLOSION_HUGE" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("EXPLOSION_LARGE")) {
			playertrail.put(player.getName(), Material.BIRCH_DOOR_ITEM);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "EXPLOSION_LARGE" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("FIREWORKS_SPARK")) {
			playertrail.put(player.getName(), Material.BLAZE_POWDER);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "FIREWORKS_SPARK" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("FLAME")) {
			playertrail.put(player.getName(), Material.BLAZE_ROD);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "FLAME" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("FLYING_GLYPH")) {
			playertrail.put(player.getName(), Material.BOAT);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "FLYING_GLYPH" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("FOOTSTEP")) {
			playertrail.put(player.getName(), Material.BONE);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "FOOTSTEP" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("HAPPY_VILLAGER")) {
			playertrail.put(player.getName(), Material.BOOK);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "HAPPY_VILLAGER" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("HEART")) {
			playertrail.put(player.getName(), Material.BOOK_AND_QUILL);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "HEART" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("INSTANT_SPELL")) {
			playertrail.put(player.getName(), Material.BOW);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "INSTANT_SPELL" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("LARGE_SMOKE")) {
			playertrail.put(player.getName(), Material.BOWL);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "LARGE_SMOKE" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("LAVA_POP")) {
			playertrail.put(player.getName(), Material.BREAD);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "LAVA_POP" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("LAVADRIP")) {
			playertrail.put(player.getName(), Material.BREWING_STAND_ITEM);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "LAVADRIP" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("MAGIC_CRIT")) {
			playertrail.put(player.getName(), Material.BUCKET);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "MAGIC_CRIT" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("MOBSPAWNER_FLAMES")) {
			playertrail.put(player.getName(), Material.CAKE);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "MOBSPAWNER_FLAMES" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("NOTE")) {
			playertrail.put(player.getName(), Material.CARROT_ITEM);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "NOTE" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("PARTICLE_SMOKE")) {
			playertrail.put(player.getName(), Material.CARROT_STICK);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "PARTICLE_SMOKE" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("PORTAL")) {
			playertrail.put(player.getName(), Material.CAULDRON_ITEM);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "PORTAL" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("POTION_BREAK")) {
			playertrail.put(player.getName(), Material.CHAINMAIL_BOOTS);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "POTION_BREAK" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("POTION_SWIRL")) {
			playertrail.put(player.getName(), Material.CHAINMAIL_CHESTPLATE);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "POTION_SWIRL" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("POTION_SWIRL_TRANSPARENT")) {
			playertrail.put(player.getName(), Material.CHAINMAIL_HELMET);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "POTION_SWIRL_TRANSPARENT"
				+ ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("SLIME")) {
			playertrail.put(player.getName(), Material.CHAINMAIL_LEGGINGS);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "SLIME" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("SMALL_SMOKE")) {
			playertrail.put(player.getName(), Material.CLAY_BALL);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "SMALL_SMOKE" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("SMOKE")) {
			playertrail.put(player.getName(), Material.COAL);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "SMOKE" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("SNOW_SHOVEL")) {
			playertrail.put(player.getName(), Material.COMPASS);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "SNOW_SHOVEL" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("SNOWBALL_BREAK")) {
			playertrail.put(player.getName(), Material.COOKED_BEEF);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "SNOWBALL_BREAK" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("SPELL")) {
			playertrail.put(player.getName(), Material.COOKED_CHICKEN);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "SPELL" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("SPLASH")) {
			playertrail.put(player.getName(), Material.COOKED_FISH);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "SPLASH" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("VILLAGER_THUNDERCLOUD")) {
			playertrail.put(player.getName(), Material.COOKED_MUTTON);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "VILLAGER_THUNDERCLOUD"
				+ ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("VOID_FOG")) {
			playertrail.put(player.getName(), Material.COOKED_RABBIT);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "VOID_FOG" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("WATERDRIP")) {
			playertrail.put(player.getName(), Material.COOKIE);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "WATERDRIP" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("WITCH_MAGIC")) {
			playertrail.put(player.getName(), Material.DARK_OAK_DOOR_ITEM);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "WITCH_MAGIC" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("rainbow")) {
			playertrail.put(player.getName(), Material.LAVA_BUCKET);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "rainbow" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("sparks")) {
			playertrail.put(player.getName(), Material.LEASH);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "sparks" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("letters")) {
			playertrail.put(player.getName(), Material.LEATHER);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "letters" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("sparkles")) {
			playertrail.put(player.getName(), Material.LEATHER_BOOTS);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "sparkles" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("love")) {
			playertrail.put(player.getName(), Material.LEATHER_CHESTPLATE);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "love" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("glitter")) {
			playertrail.put(player.getName(), Material.LEATHER_HELMET);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "glitter" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("magma")) {
			playertrail.put(player.getName(), Material.LEATHER_LEGGINGS);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "magma" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("lavadrops")) {
			playertrail.put(player.getName(), Material.MAGMA_CREAM);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "lava drops" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("bubbles")) {
			playertrail.put(player.getName(), Material.MAP);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "bubbles" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("flames")) {
			playertrail.put(player.getName(), Material.MELON);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "flames" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("music")) {
			playertrail.put(player.getName(), Material.MELON_SEEDS);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "music" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("teleport")) {
			playertrail.put(player.getName(), Material.MILK_BUCKET);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "teleport" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("swirls")) {
			playertrail.put(player.getName(), Material.MINECART);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "swirls" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("slime")) {
			playertrail.put(player.getName(), Material.MUSHROOM_SOUP);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "slime" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("smoke")) {
			playertrail.put(player.getName(), Material.MUTTON);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "smoke" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("snow")) {
			playertrail.put(player.getName(), Material.NAME_TAG);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "snow" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("raindrops")) {
			playertrail.put(player.getName(), Material.NETHER_BRICK_ITEM);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "raindrops" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("thunderclouds")) {
			playertrail.put(player.getName(), Material.NETHER_STAR);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "thunder clouds" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("waterdrops")) {
			playertrail.put(player.getName(), Material.PAINTING);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "water drops" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equalsIgnoreCase("magic")) {
			playertrail.put(player.getName(), Material.PAPER);
			Message.normal(player, "Trail effect set to " + ChatColor.RED + "magic" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("END_ROD_")) {
			playertrail.put(player.getName(), Material.POISONOUS_POTATO);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "END_ROD" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("DRAGON_BREATH")) {
			playertrail.put(player.getName(), Material.PORK);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "DRAGON_BREATH" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("DAMAGE_INDICATOR")) {
			playertrail.put(player.getName(), Material.POTATO_ITEM);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "DAMAGE_INDICATOR" + ChatColor.GOLD + ".");
		    } else if (trailargs[0].equals("SWEEP_ATTACK")) {
			playertrail.put(player.getName(), Material.POTION);
			Message.normal(player,
				"Trail effect set to " + ChatColor.RED + "SWEEP_ATTACK" + ChatColor.GOLD + ".");
		    } else {
			if (player.hasPermission("popcraft.trail.custom")) {
			    try {
				playertrail.put(player.getName(), Material.valueOf(trailargs[0].toUpperCase()));
			    } catch (IllegalArgumentException e) {
				Message.error(player, "That trail doesn't exist!");
				materialExists = false;
			    }
			    if ((materialExists == true) && (playertrail.get(player.getName()).isBlock())) {
				Message.normal(player,
					"Trail set to " + ChatColor.RED
						+ playertrail.get(player.getName()).toString().toLowerCase()
							.replace("_", " ")
						+ " (" + Material.valueOf(trailargs[0].toUpperCase()).getId() + ":"
						+ damage.get(player.getName()) + ")" + ChatColor.GOLD + ".");
			    } else {
				playertrail.remove(player.getName());
			    }
			} else {
			    Message.error(player, "You aren't allowed to use custom trails!");
			}
		    }
		} else if (trailargs.length == 2) {
		    if (player.hasPermission("popcraft.trail.custom")) {
			try {
			    playertrail.put(player.getName(), Material.valueOf(trailargs[0].toUpperCase()));
			} catch (IllegalArgumentException e) {
			    Message.error(player, "That trail doesn't exist!");
			    materialExists = false;
			}
			if (trailargs[1].equalsIgnoreCase("dots"))
			    style.put(player.getName(), "dots");
			if (trailargs[1].equalsIgnoreCase("rain"))
			    style.put(player.getName(), "rain");
			if (!(style.get(player.getName()).equals("dots"))
				&& !(style.get(player.getName()).equals("rain"))) {
			    try {
				damage.put(player.getName(), Integer.parseInt(trailargs[1]));
			    } catch (NumberFormatException e) {
				Message.error(player, "That damage value doesn't exist!");
				materialExists = false;
			    }
			}
			if ((materialExists == true) && (playertrail.get(player.getName()).isBlock())) {
			    if (style.get(player.getName()).equals("dots"))
				Message.normal(player,
					"Trail set to " + ChatColor.RED
						+ playertrail.get(player.getName()).toString().toLowerCase()
							.replace("_", " ")
						+ " dots" + " (" + Material.valueOf(trailargs[0].toUpperCase()).getId()
						+ ":" + damage.get(player.getName()) + ")" + ChatColor.GOLD + ".");
			    else if (style.get(player.getName()).equals("rain"))
				Message.normal(player,
					"Trail set to " + ChatColor.RED
						+ playertrail.get(player.getName()).toString().toLowerCase()
							.replace("_", " ")
						+ " rain" + " (" + Material.valueOf(trailargs[0].toUpperCase()).getId()
						+ ":" + damage.get(player.getName()) + ")" + ChatColor.GOLD + ".");
			    else {
				Message.normal(player,
					"Trail set to " + ChatColor.RED
						+ playertrail.get(player.getName()).toString().toLowerCase()
							.replace("_", " ")
						+ " (" + Material.valueOf(trailargs[0].toUpperCase()).getId() + ":"
						+ damage.get(player.getName()) + ")" + ChatColor.GOLD + ".");
			    }
			} else {
			    playertrail.remove(player.getName());
			}
		    } else {
			Message.error(player, "You aren't allowed to use custom trails!");
		    }
		} else if (trailargs.length == 3) {
		    if (player.hasPermission("popcraft.trail.custom") && trailargs[2].equalsIgnoreCase("dots")) {
			style.put(player.getName(), "dots");
			try {
			    playertrail.put(player.getName(), Material.valueOf(trailargs[0].toUpperCase()));
			} catch (IllegalArgumentException e) {
			    Message.error(player, "That trail doesn't exist!");
			    materialExists = false;
			}
			try {
			    damage.put(player.getName(), Integer.parseInt(trailargs[1]));
			} catch (NumberFormatException e) {
			    Message.error(player, "That damage value doesn't exist!");
			    materialExists = false;
			}
			if ((materialExists == true) && (playertrail.get(player.getName()).isBlock())) {
			    Message.normal(player,
				    "Trail set to " + ChatColor.RED
					    + playertrail.get(player.getName()).toString().toLowerCase().replace("_",
						    " ")
					    + " dots" + " (" + Material.valueOf(trailargs[0].toUpperCase()).getId()
					    + ":" + damage.get(player.getName()) + ")" + ChatColor.GOLD + ".");
			} else {
			    playertrail.remove(player.getName());
			}
		    } else if (player.hasPermission("popcraft.trail.custom") && trailargs[2].equalsIgnoreCase("rain")) {
			style.put(player.getName(), "rain");
			try {
			    playertrail.put(player.getName(), Material.valueOf(trailargs[0].toUpperCase()));
			} catch (IllegalArgumentException e) {
			    Message.error(player, "That trail doesn't exist!");
			    materialExists = false;
			}
			try {
			    damage.put(player.getName(), Integer.parseInt(trailargs[1]));
			} catch (NumberFormatException e) {
			    Message.error(player, "That damage value doesn't exist!");
			    materialExists = false;
			}
			if ((materialExists == true) && (playertrail.get(player.getName()).isBlock())) {
			    Message.normal(player,
				    "Trail set to " + ChatColor.RED
					    + playertrail.get(player.getName()).toString().toLowerCase().replace("_",
						    " ")
					    + " rain" + " (" + Material.valueOf(trailargs[0].toUpperCase()).getId()
					    + ":" + damage.get(player.getName()) + ")" + ChatColor.GOLD + ".");
			} else {
			    playertrail.remove(player.getName());
			}
		    } else {
			Message.error(player, "You aren't allowed to use custom trails!");
		    }
		} else {
		    Message.usage(player, "trail <clear/list/type>");
		}
	    }
	});
    }

    public void commandFlames(Player player, String[] args) {
	if (args.length == 0) {
	    if (playertrail.containsKey(player.getName())) {
		playertrail.remove(player.getName());
		Message.normal(player, "Cleared trail.");
	    } else {
		playertrail.put(player.getName(), Material.ARROW);
		Message.normal(player, "Trail effect set to " + ChatColor.RED + "flames" + ChatColor.GOLD + ".");
	    }
	} else {
	    Message.usage(player, "flames");
	}
    }

    public void commandHearts(Player player, String[] args) {
	if (args.length == 0) {
	    if (playertrail.containsKey(player.getName())) {
		playertrail.remove(player.getName());
	    } else {
		playertrail.put(player.getName(), Material.LEATHER_CHESTPLATE);
	    }
	} else {
	    Message.usage(player, "hearts");
	}
    }

    public void commandWorldSpawn(CommandSender sender) {
	Bukkit.getWorld("world").setSpawnLocation((int) config.getDouble("spawn.coordinate-x"),
		(int) config.getDouble("spawn.coordinate-y"), (int) config.getDouble("spawn.coordinate-z"));
    }

}