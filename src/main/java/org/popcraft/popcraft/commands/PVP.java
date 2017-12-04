package org.popcraft.popcraft.commands;

import net.minecraft.server.v1_12_R1.EntityAreaEffectCloud;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
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
import org.popcraft.popcraft.utils.Cooldown;
import org.popcraft.popcraft.utils.Message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.lang.String.format;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.Sound.ENTITY_CHICKEN_AMBIENT;
import static org.bukkit.Sound.ENTITY_EVOCATION_ILLAGER_PREPARE_WOLOLO;
import static org.popcraft.popcraft.utils.Cooldown.reset;

@Deprecated
public class PVP extends PlayerCommand implements Listener {

    private Map<String, Boolean> pvp = new HashMap<>();

    private static final Function<Player, Boolean> cooldownCheck = Cooldown.defaultCooldown("pvp", 5000);

    @Override
    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        final boolean newState = !this.getPvp(player);
        this.pvp.put(player.getName(), newState);
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

    @Override
    public boolean playerCheck(Player player) {
        return cooldownCheck.apply(player);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
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
                if (!(this.getPvp((Player) victim) && this.getPvp((Player) attacker)))
                    e.setCancelled(true);
            }
            if (attacker instanceof Projectile) {
                Projectile projectile = (Projectile) e.getDamager();
                try {
                    attacker = (Player) ((Projectile) e.getDamager()).getShooter();
                } catch (ClassCastException ex) {
                }
                if (attacker instanceof Player && !(this.getPvp((Player) victim) && this.getPvp((Player) attacker))) {
                    if (projectile instanceof Arrow || projectile instanceof Egg || projectile instanceof FishHook
                            || projectile instanceof Snowball || projectile instanceof EnderPearl) {
                        e.setCancelled(true);
                    }
                }
            }
            if (attacker instanceof AreaEffectCloud) {
                EntityAreaEffectCloud nmsEffectCloud = ((CraftAreaEffectCloud) ((AreaEffectCloud) attacker))
                        .getHandle();
                if (nmsEffectCloud.getSource() != null) {
                    attacker = nmsEffectCloud.getSource().getBukkitEntity();
                    if (attacker instanceof Player && !(this.getPvp((Player) victim) && this.getPvp((Player) attacker))) {
                        PotionEffectType potionType = ((AreaEffectCloud) nmsEffectCloud.getBukkitEntity())
                                .getBasePotionData().getType().getEffectType();
                        if (potionType == PotionEffectType.HARM || potionType == PotionEffectType.POISON)
                            e.setCancelled(true);
                    }
                }
            }
            reset((Player) victim, "pvp", 5000);
        }
    }

    @EventHandler
    public void onEntityCombustByEntity(EntityCombustByEntityEvent e) {
        Entity victim = e.getEntity();
        if (victim instanceof Player) {
            if (e.getCombuster() instanceof Arrow) {
                ProjectileSource source = ((Arrow) e.getCombuster()).getShooter();
                if (source instanceof Player) {
                    if (!(this.getPvp((Player) victim) && this.getPvp((Player) source)))
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
                        && !(this.getPvp((Player) entity) && this.getPvp((Player) e.getEntity().getShooter()))) {
                    reset((Player) entity, "pvp", 5000);
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
        // TODO: Replace NMS
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        Material bucket = e.getBucket();
        if (bucket.toString().contains("LAVA_BUCKET")) {
            for (Entity en : e.getPlayer().getNearbyEntities(16, 16, 16)) {
                if (en instanceof Player) {
                    if (!this.getPvp(((Player) en))) {
                        e.setCancelled(true);
                        e.getPlayer().updateInventory();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockIgniteEvent(BlockIgniteEvent e) {
        if (e.getIgnitingEntity() instanceof Player) {
            for (Entity en : e.getPlayer().getNearbyEntities(16, 16, 16)) {
                if (en instanceof Player) {
                    if (!this.getPvp(((Player) en))) {
                        e.setCancelled(true);
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

    private boolean getPvp(Player player) {
        return pvp.getOrDefault(player.getName(), false);
    }
}
