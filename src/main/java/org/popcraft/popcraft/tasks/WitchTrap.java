package org.popcraft.popcraft.tasks;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.SplashPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class WitchTrap implements Listener {

    private Random random = new Random();
    private Set<UUID> activated = new HashSet<>();
    private static final int SPAWN_RANGE = 24;

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity witch = event.getEntity();
        if (witch.getType() == EntityType.WITCH && !activated.contains(witch.getUniqueId())) {
            Entity damager = event.getDamager();
            if (damager instanceof SplashPotion) {
                ProjectileSource projectileSource = ((SplashPotion) damager).getShooter();
                if (projectileSource instanceof Player && event.getCause() == DamageCause.MAGIC) {
                    Player player = (Player) projectileSource;
                    if (witch.getLocation().getBlock().getBiome() == Biome.DARK_FOREST) {
                        spawnEntitiesOnPlayer(EntityType.VINDICATOR, 14, player);
                        spawnEntitiesOnPlayer(EntityType.EVOKER, 1, player);
                        spawnEntitiesOnPlayer(EntityType.ILLUSIONER, 1, player);
                        activated.add(witch.getUniqueId());
                    }
                }
            }
        }
    }

    private void spawnEntitiesOnPlayer(EntityType entityType, int count, Player player) {
        for (int i = 0; i < count; ++i) {
            double dirX = Math.random() > 0.5 ? 1 : -1;
            double dirY = Math.random() > 0.5 ? 1 : -1;
            Location spawnPoint = player.getLocation().add(dirX * SPAWN_RANGE * random.nextDouble(), 0, dirY * SPAWN_RANGE * random.nextDouble());
            if (spawnPoint.getWorld() == null) {
                return;
            }
            spawnPoint.setY(spawnPoint.getWorld().getHighestBlockYAt(spawnPoint));
            Entity entity = spawnPoint.getWorld().spawnEntity(spawnPoint, entityType);
            if (entity instanceof Mob) {
                ((Mob) entity).setTarget(player);
            }
        }
    }

}
