package org.popcraft.popcraft.commands;

import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
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

public class Aura implements Listener, CommandExecutor {

    public static HashMap<String, TrailMeta> auratypes = new HashMap<String, TrailMeta>();
    public static HashMap<UUID, TrailMeta> playeraura = new HashMap<UUID, TrailMeta>();

    static {
	for (Material m : Material.values()) {
		if (m.isBlock()) {
			auratypes.put(m.toString().toLowerCase(), new TrailMeta(Particle.ITEM_CRACK, m.createBlockData(),
					TrailType.ITEM, TrailStyle.NORMAL, 5, 1, 0, 0, 0, 0, 0, 0));
		}
	}
	auratypes.put("clouds",
		new TrailMeta(Particle.CLOUD, null, TrailType.PARTICLE, TrailStyle.NORMAL, 5, 1, 0, 0, 0, 0, 0, 0));
	auratypes.put("flames",
		new TrailMeta(Particle.FLAME, null, TrailType.PARTICLE, TrailStyle.NORMAL, 5, 1, 0, 0, 0, 0, 0, 0));
	auratypes.put("smoke", new TrailMeta(Particle.SMOKE_LARGE, null, TrailType.PARTICLE, TrailStyle.NORMAL, 5, 1, 0,
		0, 0, 0, 0, 0));
	auratypes.put("dragon", new TrailMeta(Particle.DRAGON_BREATH, null, TrailType.PARTICLE, TrailStyle.NORMAL, 5, 1,
		0, 0, 0, 0, 0, 0));
	auratypes.put("glow",
		new TrailMeta(Particle.END_ROD, null, TrailType.PARTICLE, TrailStyle.NORMAL, 5, 1, 0, 0, 0, 0, 0, 0));
	auratypes.put("gusts", new TrailMeta(Particle.SNOW_SHOVEL, null, TrailType.PARTICLE, TrailStyle.NORMAL, 5, 1, 0,
		0, 0, 0, 0, 0));
	auratypes.put("puffs", new TrailMeta(Particle.SMOKE_NORMAL, null, TrailType.PARTICLE, TrailStyle.NORMAL, 5, 1,
		0, 0, 0, 0, 0, 0));
	auratypes.put("waterdrops", new TrailMeta(Particle.WATER_WAKE, null, TrailType.PARTICLE, TrailStyle.NORMAL, 10,
		1, 0, 0, 0, 0, 0, 0));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	Player player = (Player) sender;
	if (cmd.getName().equalsIgnoreCase("aura")) {
	    if (args.length == 0) {
		if (playeraura.containsKey(player.getUniqueId())) {
		    playeraura.remove(player.getUniqueId());
		    Message.normal(player, "Cleared aura.");
		} else {
		    Message.usage(player, "aura <clear/list/type>");
		}
	    } else if (args.length == 1) {
		if (args[0].equalsIgnoreCase("clear")) {
		    if (playeraura.containsKey(player.getUniqueId())) {
			playeraura.remove(player.getUniqueId());
			Message.normal(player, "Cleared aura.");
		    } else {
			Message.error(player, "You don't have an aura enabled!");
		    }
		} else if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("type")) {
		    Message.normal(player, "Auras: " + ChatColor.RESET
			    + "clouds, dragon, flames, glow, gusts, puffs, smoke, waterdrops");
		} else if (auratypes.containsKey(args[0])) {
		    TrailMeta aura = new TrailMeta(auratypes.get(args[0]));
		    playeraura.put(player.getUniqueId(), aura);
		    String auraname = args[0].toLowerCase().replace("_", " ");
		    Message.normal(player, "Aura set to " + ChatColor.RED + auraname + ChatColor.GOLD + ".");
		} else {
		    Message.usage(player, "aura <clear/list/type>");
		}
	    } else {
		Message.usage(player, "aura <clear/list/type>");
	    }
	}
	return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
	Player player = event.getPlayer();
	if (playeraura.containsKey(player.getUniqueId())) {
	    TrailMeta aura = playeraura.get(player.getUniqueId());
	    player.getWorld().spawnParticle((Particle) aura.getTrail(),
		    player.getLocation().add(aura.getShiftX(), aura.getShiftY(), aura.getShiftZ()), aura.getCount(),
		    aura.getOffsetX(), aura.getOffsetY(), aura.getOffsetZ(), aura.getExtra(),
		    (aura.getData() == null) ? null : new ItemStack(aura.getData().getMaterial()));
	}
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
	Player player = event.getPlayer();
	if (playeraura.containsKey(player.getUniqueId())) {
	    playeraura.remove(player.getUniqueId());
	}
    }
}