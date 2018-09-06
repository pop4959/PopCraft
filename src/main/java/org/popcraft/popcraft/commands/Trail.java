package org.popcraft.popcraft.commands;

import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang.math.NumberUtils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import org.popcraft.popcraft.utils.Message;
import org.popcraft.popcraft.utils.TrailMeta;
import org.popcraft.popcraft.utils.TrailMeta.TrailStyle;
import org.popcraft.popcraft.utils.TrailMeta.TrailType;

public class Trail implements Listener, CommandExecutor {

    private static HashMap<String, TrailMeta> trailtypes = new HashMap<String, TrailMeta>();
    private static HashMap<UUID, TrailMeta> playertrail = new HashMap<UUID, TrailMeta>();

    static {
	for (Material m : Material.values()) {
	    if (m.isBlock()) {
            trailtypes.put(m.toString().toLowerCase(), new TrailMeta(Particle.BLOCK_CRACK,
                    m.createBlockData(), TrailType.BLOCK, TrailStyle.NORMAL, 32, 0, 0, 0, 0, 0, 0, 0));
        }
	}
	for (Effect e : Effect.values()) {
	    if (e.getType() != Effect.Type.SOUND)
		trailtypes.put(e.name().toLowerCase() + "_debug_effect",
			new TrailMeta(e, null, TrailType.EFFECT, TrailStyle.NORMAL, 32, 0, 0, 0, 0, 0, 0, 0));
	}
	for (Particle p : Particle.values()) {
	    trailtypes.put(p.name().toLowerCase() + "_debug_particle",
		    new TrailMeta(p, null, TrailType.PARTICLE, TrailStyle.NORMAL, 32, 0, 0, 0, 0, 0, 0, 0));
	}
	trailtypes.put("bubbles", new TrailMeta(Particle.CRIT_MAGIC, null, TrailType.PARTICLE, TrailStyle.NORMAL, 2, 0,
		0.5, 0.8, 0.5, 0, 0, 0));
	trailtypes.put("flames", new TrailMeta(Particle.FLAME, null, TrailType.PARTICLE, TrailStyle.NORMAL, 5, 0, 0.5,
		0.5, 0.5, 0, 0, 0));
	trailtypes.put("glitter", new TrailMeta(Particle.SPELL_INSTANT, null, TrailType.PARTICLE, TrailStyle.NORMAL, 15,
		0, 0.2, 0, 0.2, 0, 0, 0));
	trailtypes.put("glow", new TrailMeta(Particle.END_ROD, null, TrailType.PARTICLE, TrailStyle.NORMAL, 1, 0, 0.5,
		0.8, 0.5, 0, 0, 0));
	trailtypes.put("glyphs", new TrailMeta(Particle.ENCHANTMENT_TABLE, null, TrailType.PARTICLE, TrailStyle.NORMAL,
		10, 0, 0.5, 0.8, 0.5, 0, 0, 0));
	trailtypes.put("hearts", new TrailMeta(Particle.DAMAGE_INDICATOR, null, TrailType.PARTICLE, TrailStyle.NORMAL,
		1, 0, 0.5, 0.8, 0.5, 0, 0, 0));
	trailtypes.put("lavadrops", new TrailMeta(Particle.DRIP_LAVA, null, TrailType.PARTICLE, TrailStyle.NORMAL, 1, 0,
		0, 0, 0, 0, 0.4, 0));
	trailtypes.put("love", new TrailMeta(Particle.HEART, null, TrailType.PARTICLE, TrailStyle.NORMAL, 1, 0, 0.5,
		0.8, 0.5, 0, 0, 0));
	trailtypes.put("magic", new TrailMeta(Particle.SPELL_WITCH, null, TrailType.PARTICLE, TrailStyle.NORMAL, 15, 0,
		0.2, 0, 0.2, 0, 0, 0));
	trailtypes.put("music", new TrailMeta(Particle.NOTE, null, TrailType.PARTICLE, TrailStyle.NORMAL, 1, 1, 0.5,
		0.8, 0.5, 0, 0, 0));
	trailtypes.put("party", new TrailMeta(Particle.TOTEM, null, TrailType.PARTICLE, TrailStyle.NORMAL, 3, 0, 0.5,
		0.8, 0.5, 0, 0, 0));
	trailtypes.put("raindrops", new TrailMeta(Particle.WATER_SPLASH, null, TrailType.PARTICLE, TrailStyle.NORMAL,
		10, 0, 0.2, 0, 0.2, 0, 0, 0));
	trailtypes.put("slime",
		new TrailMeta(Particle.SLIME, null, TrailType.PARTICLE, TrailStyle.NORMAL, 4, 0, 0.1, 0, 0.1, 0, 0, 0));
	trailtypes.put("smoke", new TrailMeta(Particle.SMOKE_LARGE, null, TrailType.PARTICLE, TrailStyle.NORMAL, 2, 0,
		0.3, 0, 0.3, 0, 0, 0));
	trailtypes.put("snowy", new TrailMeta(Particle.SNOWBALL, null, TrailType.PARTICLE, TrailStyle.NORMAL, 4, 0, 0.1,
		0, 0.1, 0, 0, 0));
	trailtypes.put("sparkles", new TrailMeta(Particle.VILLAGER_HAPPY, null, TrailType.PARTICLE, TrailStyle.NORMAL,
		2, 0, 0.5, 0.8, 0.5, 0, 0, 0));
	trailtypes.put("sparks", new TrailMeta(Particle.CRIT, null, TrailType.PARTICLE, TrailStyle.NORMAL, 2, 0, 0.5,
		0.8, 0.5, 0, 0, 0));
	trailtypes.put("swirls", new TrailMeta(Particle.SPELL_MOB, null, TrailType.PARTICLE, TrailStyle.NORMAL, 2, 1,
		0.5, 0.8, 0.5, 0, 0, 0));
	trailtypes.put("teleport", new TrailMeta(Particle.PORTAL, null, TrailType.PARTICLE, TrailStyle.NORMAL, 10, 0,
		0.5, 0.8, 0.5, 0, 0, 0));
	trailtypes.put("thunderclouds", new TrailMeta(Particle.VILLAGER_ANGRY, null, TrailType.PARTICLE,
		TrailStyle.NORMAL, 1, 0, 0.6, 0.3, 0.6, 0, 0, 0));
	trailtypes.put("volcano",
		new TrailMeta(Particle.LAVA, null, TrailType.PARTICLE, TrailStyle.NORMAL, 2, 0, 0, 0, 0, 0, 0, 0));
	trailtypes.put("waterdrops", new TrailMeta(Particle.DRIP_WATER, null, TrailType.PARTICLE, TrailStyle.NORMAL, 1,
		0, 0, 0, 0, 0, 0.4, 0));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
	Player player = event.getPlayer();
	if (playertrail.containsKey(player.getUniqueId())) {
	    playertrail.remove(player.getUniqueId());
	}
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	if (sender instanceof Player) {
	    Player player = (Player) sender;
	    if (cmd.getName().equalsIgnoreCase("trail")) {
		try {
		    if (args.length == 0) {
			if (playertrail.containsKey(player.getUniqueId())) {
			    playertrail.remove(player.getUniqueId());
			    Message.normal(player, "Cleared trail.");
			} else {
			    Message.usage(player, "trail <trail/list/clear>");
			}
		    } else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("clear")) {
			    if (playertrail.containsKey(player.getUniqueId())) {
				playertrail.remove(player.getUniqueId());
				Message.normal(player, "Cleared trail.");
			    } else {
				Message.error(player, "You don't have a trail enabled!");
			    }
			} else if (args[0].equalsIgnoreCase("list")) {
			    Message.normal(player, "Trails: " + ChatColor.RESET
				    + "bubbles, flames, glitter, glow, glyphs, hearts, lavadrops, love, magic, music, party, raindrops, slime, smoke, snowy, sparkles, sparks, swirls, teleport, thunderclouds, volcano, waterdrops");
			} else if (args[0].equalsIgnoreCase("trail")) {
			    Message.error(player, "Invalid trail. Type \"/trail list\" to see available trails.");
			} else if (trailtypes.containsKey(args[0].toLowerCase())) {
			    TrailMeta trail = new TrailMeta(trailtypes.get(args[0].toLowerCase()));
			    playertrail.put(player.getUniqueId(), trail);
			    String trailname = args[0].toLowerCase().replace("_", " ");
			    Message.normal(player, "Trail set to " + ChatColor.RED + trailname + ChatColor.GOLD + ".");
			} else {
			    Message.usage(player, "trail <trail/list/clear>");
			}
		    } else if (args.length == 2) {
			if (trailtypes.containsKey(args[0].toLowerCase())) {
			    TrailMeta trail = new TrailMeta(trailtypes.get(args[0].toLowerCase()));
				TrailStyle s = TrailStyle.valueOf(args[1].toUpperCase());
				trail = trail.changeStyle(s);
			    playertrail.put(player.getUniqueId(), trail);
			    String trailname = args[0].toLowerCase().replace("_", " ");
			    Message.normal(player,
				    "Trail set to " + ChatColor.RED + trailname
					    + (trail.getStyle() != TrailStyle.NORMAL
						    ? " " + trail.getStyle().toString().toLowerCase() : "")
				    + ChatColor.GOLD + ".");
			} else {
			    Message.usage(player, "trail <trail/list/clear>");
			}
		    } else {
			Message.usage(player, "trail <trail/list/clear>");
		    }
		} catch (Exception e) {
		    Message.usage(player, "trail <trail/list/clear>");
		}
	    }
	    if (cmd.getName().equalsIgnoreCase("flames")) {
		if (args.length == 0) {
		    if (playertrail.containsKey(player.getUniqueId())) {
			playertrail.remove(player.getUniqueId());
			Message.normal(player, "Cleared trail.");
		    } else {
			playertrail.put(player.getUniqueId(), trailtypes.get("flames"));
			Message.normal(player, "Trail set to " + ChatColor.RED + "flames" + ChatColor.GOLD + ".");
		    }
		} else {
		    Message.usage(player, "flames");
		}
	    }
	    if (cmd.getName().equalsIgnoreCase("hearts")) {
		if (args.length == 0) {
		    if (playertrail.containsKey(player.getUniqueId())) {
			playertrail.remove(player.getUniqueId());
			Message.normal(player, "Cleared trail.");
		    } else {
			playertrail.put(player.getUniqueId(), trailtypes.get("love"));
			Message.normal(player, "Trail set to " + ChatColor.RED + "hearts" + ChatColor.GOLD + ".");
		    }
		} else {
		    Message.usage(player, "hearts");
		}
	    }
	}
	return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
	Player player = event.getPlayer();
	if (playertrail.containsKey(player.getUniqueId())) {
	    TrailMeta trail = playertrail.get(player.getUniqueId());
	    if (trail.getType() == TrailType.EFFECT) {
		for (int i = 0; i < trail.getCount(); i++)
		    player.getWorld().playEffect(
			    player.getLocation().add(trail.getShiftX(), trail.getShiftY(), trail.getShiftZ()),
			    (Effect) trail.getTrail(), trail.getExtra());
	    } else if (trail.getType() == TrailType.PARTICLE) {
		player.getWorld().spawnParticle((Particle) trail.getTrail(),
			player.getLocation().add(trail.getShiftX(), trail.getShiftY(), trail.getShiftZ()),
			trail.getCount(), trail.getOffsetX(), trail.getOffsetY(), trail.getOffsetZ(), trail.getExtra(),
			null);
	    } else if (trail.getType() == TrailType.BLOCK) {
		player.getWorld().spawnParticle((Particle) trail.getTrail(),
			player.getLocation().add(trail.getShiftX(), trail.getShiftY(), trail.getShiftZ()),
			trail.getCount(), trail.getOffsetX(), trail.getOffsetY(), trail.getOffsetZ(), trail.getExtra(),
			trail.getData());
	    } else if (trail.getType() == TrailType.ITEM) {
		player.getWorld().spawnParticle((Particle) trail.getTrail(),
			player.getLocation().add(trail.getShiftX(), trail.getShiftY(), trail.getShiftZ()),
			trail.getCount(), trail.getOffsetX(), trail.getOffsetY(), trail.getOffsetZ(), trail.getExtra(),
			new ItemStack(trail.getData().getMaterial()));
	    }
	}
    }

}