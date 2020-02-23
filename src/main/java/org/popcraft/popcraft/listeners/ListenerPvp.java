package org.popcraft.popcraft.listeners;

import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import org.popcraft.popcraft.commands.CommandPvp;

import java.util.Arrays;
import java.util.List;

/**
 * Handles PVP related events. See CommandPvp for more.
 */
public class ListenerPvp extends PopCraftListener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity(), attacker = event.getDamager();
        // It is not PVP if the victim is not a player
        if (!(victim instanceof Player)) {
            return;
        }
        // Under no circumstances is spawn camping permitted
        if (victim.getLocation().distance(victim.getWorld().getSpawnLocation()) < 16) {
            event.setCancelled(true);
            return;
        }
        // No damage is dealt by fireworks created by this plugin
        if (attacker instanceof Firework) {
            Firework firework = (Firework) attacker;
            List<String> lore = firework.getFireworkMeta().getLore();
            if (lore != null && lore.contains(plugin.getName())) {
                event.setCancelled(true);
                return;
            }
        }
        // Direct damage PVP check
        if (attacker instanceof Player && !areBothPvpEnabled(victim, attacker)) {
            event.setCancelled(true);
            return;
        }
        // Projectile PVP check
        if (attacker instanceof Projectile) {
            Projectile projectile = (Projectile) attacker;
            // Projectile source must be a player for this to be PVP
            if (!(projectile.getShooter() instanceof Player)) {
                return;
            }
            attacker = (Player) projectile.getShooter();
            // Blocks AbstractArrow (arrow, spectral arrow, tipped arrow, trident), Egg, Fishhook, Enderpearl, Snowball
            if (!areBothPvpEnabled(victim, attacker) && instanceOfAny(projectile, AbstractArrow.class, Egg.class,
                    EnderPearl.class, FishHook.class, Snowball.class)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
        Entity victim = event.getEntity();
        // It is not PVP if the victim is not a player
        if (!(victim instanceof Player)) {
            return;
        }
        // Arrow flame PVP check
        if (event.getCombuster() instanceof AbstractArrow) {
            ProjectileSource attacker = ((AbstractArrow) event.getCombuster()).getShooter();
            if (attacker instanceof Player && !areBothPvpEnabled(victim, (Player) attacker)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        // It is not PVP if the attacker is not a player
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player attacker = (Player) event.getEntity().getShooter();
        List<PotionEffectType> pvpPotions = Arrays.asList(PotionEffectType.HARM, PotionEffectType.POISON,
                PotionEffectType.SLOW, PotionEffectType.WEAKNESS);
        // If the potions aren't harmful, we don't care
        if (event.getPotion().getEffects().stream().noneMatch(p -> pvpPotions.contains(p.getType()))) {
            return;
        }
        // Splash potion PVP check
        for (LivingEntity victim : event.getAffectedEntities()) {
            // It is not PVP if the victim is not a player
            if (!(victim instanceof Player)) {
                // Non-player entities should still be affected by potions regardless of the event's success
                event.getPotion().getEffects().forEach(p -> p.apply(victim));
            } else if (!areBothPvpEnabled(victim, attacker)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAreaEffectCloudApply(AreaEffectCloudApplyEvent event) {
        // It is not PVP if the attacker is not a player
        if (!(event.getEntity().getSource() instanceof Player)) {
            return;
        }
        Player attacker = (Player) event.getEntity().getSource();
        List<PotionType> pvpPotions = Arrays.asList(PotionType.INSTANT_DAMAGE, PotionType.POISON, PotionType.SLOWNESS,
                PotionType.WEAKNESS);
        // If the potions aren't harmful, we don't care
        if (!pvpPotions.contains(event.getEntity().getBasePotionData().getType())) {
            return;
        }
        // Area effect cloud PVP check
        for (LivingEntity victim : event.getAffectedEntities()) {
            if (victim instanceof Player && !areBothPvpEnabled(victim, attacker)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        // Lava bucket PVP check
        if (Material.LAVA_BUCKET.equals(event.getBucket()) && !areNearbyPvpEnabled(event.getPlayer())) {
            event.getPlayer().sendMessage(plugin.getMessage("pvpActionBlocked"));
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        // Flint and steel PVP check
        if (event.getIgnitingEntity() instanceof Player && event.getPlayer() != null
                && !areNearbyPvpEnabled(event.getPlayer())) {
            event.getPlayer().sendMessage(plugin.getMessage("pvpActionBlocked"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Players killed in PVP should not lose anything
        if (event.getEntity().getKiller() != null) {
            event.setKeepInventory(true);
            event.getDrops().clear();
            event.setDroppedExp(0);
            event.setKeepLevel(true);
        }
    }

    private boolean isPvpEnabled(Entity entity) {
        return ((CommandPvp) plugin.getCommands().get("pvp")).isPvpEnabled(entity);
    }

    private boolean areBothPvpEnabled(Entity entity1, Entity entity2) {
        return isPvpEnabled(entity1) && isPvpEnabled(entity2);
    }

    private boolean areNearbyPvpEnabled(Entity entity) {
        return entity.getNearbyEntities(8, 8, 8).stream()
                .filter(e -> e instanceof Player).allMatch(this::isPvpEnabled);
    }

    private boolean instanceOfAny(Object object, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isInstance(object)) {
                return true;
            }
        }
        return false;
    }

}
