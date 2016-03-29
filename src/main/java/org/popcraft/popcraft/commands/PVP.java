package org.popcraft.popcraft.commands;

import java.util.Collection;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.popcraft.popcraft.utils.Cooldown;
import org.popcraft.popcraft.utils.Message;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftAreaEffectCloud;
import net.minecraft.server.v1_9_R1.EntityAreaEffectCloud;

public class PVP implements Listener, CommandExecutor {

    public static HashMap<String, Boolean> pvp = new HashMap<String, Boolean>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	Player player = (Player) sender;
	if (cmd.getName().equalsIgnoreCase("pvp")) {
	    if (Cooldown.check(player, "pvp", 5000)) {
		if (!PVP.getPvp(player)) {
		    PVP.setPvp(player, true);
		    Message.normal(player, "Your PvP is now " + ChatColor.RED + "enabled" + ChatColor.GOLD + "!");
		    for (int i = 0; i < 50; i++)
			player.getWorld().playEffect(player.getLocation(), Effect.LAVA_POP, 32);
		} else {
		    PVP.setPvp(player, false);
		    Message.normal(player, "Your PvP is now " + ChatColor.RED + "disabled" + ChatColor.GOLD + "!");
		    for (int i = 0; i < 50; i++)
			player.getWorld().playEffect(player.getLocation(), Effect.LAVA_POP, 32);
		}
	    } else {
		Message.cooldown(player, "pvp", 5000);
	    }
	    return true;
	}
	return false;
    }

    @EventHandler
    public static void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
	Entity victim = e.getEntity();
	Entity attacker = e.getDamager();
	if (victim instanceof Player) {
	    try {
		if (victim.getLocation().distance(
			Bukkit.getWorld(Bukkit.getServer().getWorlds().get(0).getName()).getSpawnLocation()) < 16)
		    e.setCancelled(true);
	    } catch (IllegalArgumentException ex) {
	    }
	    if (attacker instanceof Player) {
		if (!(PVP.getPvp((Player) victim) && PVP.getPvp((Player) attacker)))
		    e.setCancelled(true);
	    }
	    if (attacker instanceof Projectile) {
		Projectile projectile = (Projectile) e.getDamager();
		try {
		    attacker = (Player) ((Projectile) e.getDamager()).getShooter();
		} catch (ClassCastException ex) {
		}
		if (attacker instanceof Player && !(PVP.getPvp((Player) victim) && PVP.getPvp((Player) attacker))) {
		    if (projectile instanceof Arrow || projectile instanceof Egg || projectile instanceof FishHook
			    || projectile instanceof Snowball || projectile instanceof EnderPearl) {
			e.setCancelled(true);
		    }
		}
	    }
	    if (attacker instanceof AreaEffectCloud) {
		EntityAreaEffectCloud nmsEffectCloud = ((CraftAreaEffectCloud) ((AreaEffectCloud) attacker))
			.getHandle();
		if (nmsEffectCloud.w() != null) {
		    attacker = nmsEffectCloud.w().getBukkitEntity();
		    if (attacker instanceof Player && !(PVP.getPvp((Player) victim) && PVP.getPvp((Player) attacker))) {
			PotionEffectType potionType = ((AreaEffectCloud) nmsEffectCloud.getBukkitEntity())
				.getBasePotionData().getType().getEffectType();
			if (potionType == PotionEffectType.HARM || potionType == PotionEffectType.POISON)
			    e.setCancelled(true);
		    }
		}
	    }
	    Cooldown.reset((Player) victim, "pvp", 5000);
	}
    }

    @EventHandler
    public static void onPotionSplash(PotionSplashEvent e) {
	Bukkit.getServer().broadcastMessage(e.getEntity().toString());
	if (e.getEntity().getShooter() instanceof Player) {
	    for (LivingEntity entity : e.getAffectedEntities()) {
		if (!(entity instanceof Player)) {
		    Collection<PotionEffect> pes = e.getPotion().getEffects();
		    for (PotionEffect pe : pes) {
			pe.apply(entity);
		    }
		}
	    }
	    for (LivingEntity entity : e.getAffectedEntities()) {
		if (entity instanceof Player
			&& !(PVP.getPvp((Player) entity) && PVP.getPvp((Player) e.getEntity().getShooter()))) {
		    Cooldown.reset((Player) entity, "pvp", 5000);
		    PotionEffectType[] pvpPotions = { PotionEffectType.HARM, PotionEffectType.POISON,
			    PotionEffectType.SLOW, PotionEffectType.WEAKNESS };
		    for (PotionEffectType t : pvpPotions)
			for (PotionEffect p : e.getPotion().getEffects())
			    if (p.getType().equals(t))
				e.setCancelled(true);
		}
	    }
	}
    }

    @EventHandler
    public static void onLingeringPotionSplash(LingeringPotionSplashEvent e) {
	// TODO: Replace NMS
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
	Material bucket = e.getBucket();
	if (bucket.toString().contains("LAVA_BUCKET")) {
	    for (Entity en : e.getPlayer().getNearbyEntities(16, 16, 16)) {
		if (en instanceof Player) {
		    if (!PVP.getPvp(((Player) en)))
		    {
			e.setCancelled(true);
			e.getPlayer().updateInventory();
		    }
		}
	    }
	}
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
	if (e.getEntity().getKiller() instanceof Player) {
	    e.setKeepInventory(true);
	    e.getDrops().clear();
	    e.setDroppedExp(0);
	    e.setKeepLevel(true);
	}
    }

    private static void setPvp(Player player, Boolean state) {
	pvp.put(player.getName(), state);
    }

    private static boolean getPvp(Player player) {
	if (!pvp.containsKey(player.getName())) {
	    pvp.put(player.getName(), false);
	}
	Boolean state = pvp.get(player.getName());
	return state;
    }
}
