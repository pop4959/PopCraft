package org.popcraft.popcraft.commands;

import com.google.common.collect.Sets;
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
import org.popcraft.popcraft.PopCommand;
import org.popcraft.popcraft.utils.Cooldown;
import org.popcraft.popcraft.utils.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.lang.String.format;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.Material.LAVA_BUCKET;
import static org.bukkit.Sound.ENTITY_CHICKEN_AMBIENT;
import static org.bukkit.Sound.ENTITY_EVOCATION_ILLAGER_PREPARE_WOLOLO;
import static org.bukkit.potion.PotionEffectType.*;

@PopCommand("pvp")
public class PVP extends PlayerCommand implements Listener {

    private static final Set<PotionEffectType> harmfullTypes = Sets.newHashSet(HARM, POISON, SLOW, WEAKNESS);
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

        final Entity shooter = this.getPossibleShooter(attacker);
        if (shooter != null) {
            attacker = shooter;
        }

        if (attacker instanceof CraftAreaEffectCloud) {
            EntityAreaEffectCloud nmsEffectCloud = ((CraftAreaEffectCloud) attacker).getHandle();
            if (nmsEffectCloud.getSource() != null) {
                attacker = nmsEffectCloud.getSource().getBukkitEntity();
            }
        }

        if (this.pvpDisabled(victim, attacker)) {
            e.setCancelled(true);
        } else {
            this.getCooldown().reset(victim.getUniqueId());
        }
    }

    @EventHandler
    public void onEntityCombustByEntity(EntityCombustByEntityEvent e) {
        final Entity victim = e.getEntity();
        final Entity attacker = e.getCombuster();
        if (!(victim instanceof Player)) {
            return;
        }
        if (this.pvpDisabled(victim, getPossibleShooter(attacker))) {
            e.setCancelled(true);
        }
    }

    private Entity getPossibleShooter(final Object attacker) {
        if (attacker instanceof Projectile) {
            Projectile projectile = (Projectile) attacker;
            if (projectile.getShooter() instanceof Entity) {
                return (Entity) projectile.getShooter();
            }
        }
        return null;
    }


    @EventHandler
    public void onPotionSplash(PotionSplashEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }
        //Apply Potions to non players regardless of pvp
        e.getAffectedEntities()
                .stream()
                .filter(entity -> !(entity instanceof Player))
                .forEach(entity -> e.getPotion().getEffects().forEach(effect -> effect.apply(entity)));

        //true if any players interactions do not have pvp enabled
        final boolean pvpNotEnabled = e.getAffectedEntities()
                .stream()
                .anyMatch(entity -> this.pvpDisabled(entity, getPossibleShooter(entity)));

        //true if any potions are harmfull
        final boolean harmPotionDetected = e.getPotion()
                .getEffects()
                .stream()
                .map(PotionEffect::getType)
                .anyMatch(harmfullTypes::contains);

        e.setCancelled(pvpNotEnabled && harmPotionDetected);
    }

    @EventHandler
    public void onLingeringPotionSplash(LingeringPotionSplashEvent e) {
        //To be implemented later in development
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
