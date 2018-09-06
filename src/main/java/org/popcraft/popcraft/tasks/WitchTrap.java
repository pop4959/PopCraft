package org.popcraft.popcraft.tasks;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftSplashPotion;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Illager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class WitchTrap implements Listener {

    private Set<UUID> activated = new HashSet<UUID>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
	Entity e = event.getEntity();
	if (e.getType() == EntityType.WITCH && !activated.contains(e.getUniqueId())) {
	    if (event.getDamager() instanceof CraftSplashPotion) {
		Player damager = Bukkit
			.getPlayer(((CraftSplashPotion) event.getDamager()).getHandle().getShooter().getUniqueID());
		if (event.getCause() == DamageCause.MAGIC) {
		    Location loc = damager.getLocation();
		    if (loc.getBlock().getBiome() == Biome.DARK_FOREST) {
			Random random = new Random();
			for (int i = 0; i < 14; i++) {
			    double[] direction = { Math.random() > 0.5 ? 1 : -1, Math.random() > 0.5 ? 1 : -1 };
			    Location spawnLoc = loc.add(random.nextDouble() * 16 * direction[0], 0,
				    random.nextDouble() * 32 * direction[1]);
			    spawnLoc.setY(spawnLoc.getWorld().getHighestBlockYAt(spawnLoc));
			    Illager ill = (Illager) loc.getWorld().spawnEntity(spawnLoc, EntityType.VINDICATOR);
			    ill.setTarget(damager);
			}
			for (int i = 0; i < 1; i++) {
			    double[] direction = { Math.random() > 0.5 ? 1 : -1, Math.random() > 0.5 ? 1 : -1 };
			    Location spawnLoc = loc.add(random.nextDouble() * 16 * direction[0], 0,
				    random.nextDouble() * 32 * direction[1]);
			    spawnLoc.setY(spawnLoc.getWorld().getHighestBlockYAt(spawnLoc));
			    Illager ill = (Illager) loc.getWorld().spawnEntity(spawnLoc, EntityType.EVOKER);
			    ill.setTarget(damager);
			}
			for (int i = 0; i < 1; i++) {
			    double[] direction = { Math.random() > 0.5 ? 1 : -1, Math.random() > 0.5 ? 1 : -1 };
			    Location spawnLoc = loc.add(random.nextDouble() * 16 * direction[0], 0,
				    random.nextDouble() * 32 * direction[1]);
			    spawnLoc.setY(spawnLoc.getWorld().getHighestBlockYAt(spawnLoc));
			    Illager ill = (Illager) loc.getWorld().spawnEntity(spawnLoc, EntityType.ILLUSIONER);
			    ill.setTarget(damager);
			}
			activated.add(e.getUniqueId());
		    }
		}
	    }
	}
    }
}