package org.popcraft.popcraft.commands;

import com.google.inject.Inject;
import net.minecraft.server.v1_12_R1.EntityAreaEffectCloud;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftAreaEffectCloud;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.popcraft.popcraft.PopCommand;
import org.popcraft.popcraft.utils.Cooldown;
import org.popcraft.popcraft.utils.Message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.Material.LAVA_BUCKET;
import static org.bukkit.Sound.ENTITY_CHICKEN_AMBIENT;
import static org.bukkit.Sound.ENTITY_EVOCATION_ILLAGER_PREPARE_WOLOLO;

@PopCommand("pvp")
public class PVP extends PlayerCommand implements Listener {

    private final Server server;
    private Map<UUID, Boolean> pvpMap = new HashMap<>();

    @Inject
    public PVP(final Server server, final Cooldown cooldown) {
        super(cooldown);
        this.server = server;
    }

    @Override
    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        final boolean newState = !this.pvpEnabled(player);
        this.pvpMap.put(player.getUniqueId(), newState);
        Message.normal(player, format("Your PvP is now %s%s%s!", RED, newState ? "enabled" : "disabled", GOLD));

        final World world = player.getWorld();

        world.spawnParticle(newState ? Particle.LAVA : Particle.SPIT, player.getLocation(), 50);
        world.playSound(
                player.getLocation(),
                newState ? ENTITY_EVOCATION_ILLAGER_PREPARE_WOLOLO : ENTITY_CHICKEN_AMBIENT,
                2,
                1
        );
        return true;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        final Entity victim = e.getEntity();
        Entity attacker = e.getDamager();

        if (!(victim instanceof Player) || e.getDamage() <= 0) {
            return;
        }

        final World overworld = this.server.getWorld(this.server.getWorlds().get(0).getName());
        if (victim.getLocation().distance(overworld.getSpawnLocation()) < 16) {
            e.setCancelled(true);
        }

        if (attacker instanceof Projectile) {
            Projectile projectile = (Projectile) e.getDamager();
            if (projectile.getShooter() instanceof Entity) {
                attacker = (Entity) projectile.getShooter();
            }
        }

        if (attacker instanceof CraftAreaEffectCloud) {
            EntityAreaEffectCloud nmsEffectCloud = ((CraftAreaEffectCloud) attacker).getHandle();
            if (nmsEffectCloud.getSource() != null) {
                attacker = nmsEffectCloud.getSource().getBukkitEntity();
            }
        }

        if (this.pvpDisabled(victim, attacker)) {
            e.setCancelled(true);
        }

        this.getCooldown().reset(victim.getUniqueId());
    }

    @EventHandler
    public void onEntityCombustByEntity(EntityCombustByEntityEvent e) {
        Entity victim = e.getEntity();
        if (victim instanceof Player) {
            if (e.getCombuster() instanceof Arrow) {
                ProjectileSource source = ((Arrow) e.getCombuster()).getShooter();
                if (source instanceof Player) {
                    if (!(this.pvpEnabled(victim) && this.pvpEnabled((Player) source)))
                        e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent e) {
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
                        && !(this.pvpEnabled((Player) entity) && this.pvpEnabled((Player) e.getEntity().getShooter()))) {
                    this.getCooldown().reset(entity.getUniqueId());
                    PotionEffectType[] pvpPotions = {PotionEffectType.HARM, PotionEffectType.POISON,
                            PotionEffectType.SLOW, PotionEffectType.WEAKNESS};
                    for (PotionEffectType t : pvpPotions)
                        for (PotionEffect p : e.getPotion().getEffects())
                            if (p.getType().equals(t))
                                e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onLingeringPotionSplash(LingeringPotionSplashEvent e) {
        System.out.println("Hello");
        // TODO: Replace NMS
    }

    @EventHandler
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent e) {
        if (LAVA_BUCKET.equals(e.getBucket()) && this.nearDisabledPlayers(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onBlockIgniteEvent(final BlockIgniteEvent e) {
        e.setCancelled(e.getIgnitingEntity() instanceof Player && this.nearDisabledPlayers(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            e.setKeepInventory(true);
            e.getDrops().clear();
            e.setDroppedExp(0);
            e.setKeepLevel(true);
        }
    }

    private boolean nearDisabledPlayers(final Player player) {
        return player.getNearbyEntities(16, 16, 16).stream().anyMatch(this::pvpDisabled);
    }

    /**
     * Returns true if all entities are players and have pvp enabled
     *
     * @param entities
     * @return
     */
    private boolean pvpEnabled(final Entity... entities) {
        for (Entity entity : entities) {
            if (!(entity instanceof Player && this.pvpMap.getOrDefault(entity.getUniqueId(), false))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if any entity are not players or if the player has pvp disabled
     *
     * @param entities
     * @return
     */
    private boolean pvpDisabled(final Entity... entities) {
        return !this.pvpEnabled(entities);
    }

}
