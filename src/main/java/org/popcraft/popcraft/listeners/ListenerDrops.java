package org.popcraft.popcraft.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Listens for death events to provide custom loot drops
 */
public class ListenerDrops extends PopCraftListener {

    private boolean playerHeadEnabled = config.getBoolean("drops.playerHead.enabled");
    private double playerHeadBaseChance = config.getDouble("drops.playerHead.baseChance");
    private double playerHeadLootingBonus = config.getDouble("drops.playerHead.lootingBonus");

    private boolean dragonHeadEnabled = config.getBoolean("drops.dragonHead.enabled");
    private double dragonHeadBaseChance = config.getDouble("drops.dragonHead.baseChance");
    private double dragonHeadLootingBonus = config.getDouble("drops.dragonHead.lootingBonus");

    private boolean dragonEggEnabled = config.getBoolean("drops.dragonEgg.enabled");
    private double dragonEggBaseChance = config.getDouble("drops.dragonEgg.baseChance");
    private double dragonEggLootingBonus = config.getDouble("drops.dragonEgg.lootingBonus");

    private boolean guardianSpongeEnabled = config.getBoolean("drops.guardianSponge.enabled");
    private double guardianSpongeBaseChance = config.getDouble("drops.guardianSponge.baseChance");
    private double guardianSpongeLootingBonus = config.getDouble("drops.guardianSponge.lootingBonus");

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = killed.getKiller();
        if (killer != null && playerHeadEnabled
                && isLucky(playerHeadBaseChance, getLootingLevel(killer), playerHeadLootingBonus)) {
            killed.getWorld().dropItemNaturally(killed.getLocation(), getPlayerHead(killed));
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        World world = event.getEntity().getWorld();
        Location location = event.getEntity().getLocation();
        if (EntityType.ENDER_DRAGON.equals(event.getEntityType())) {
            if (dragonHeadEnabled && isLucky(dragonHeadBaseChance, getLootingLevel(killer), dragonHeadLootingBonus)) {
                world.dropItemNaturally(location, new ItemStack(Material.DRAGON_HEAD, 1));
            }
            if (dragonEggEnabled && isLucky(dragonEggBaseChance, getLootingLevel(killer), dragonEggLootingBonus)) {
                world.dropItemNaturally(location, new ItemStack(Material.DRAGON_EGG, 1));
            }
        } else if (EntityType.GUARDIAN.equals(event.getEntityType())
                && guardianSpongeEnabled
                && isLucky(guardianSpongeBaseChance, getLootingLevel(killer), guardianSpongeLootingBonus)) {
            world.dropItemNaturally(location, new ItemStack(Material.WET_SPONGE, 1));
        }
    }

    private boolean isLucky(double baseChance, int lootingLevel, double lootingBonus) {
        return (baseChance + lootingLevel * lootingBonus) > Math.random();
    }

    private int getLootingLevel(Player player) {
        if (player == null) {
            return 0;
        }
        return Math.max(
                player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS),
                player.getInventory().getItemInOffHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)
        );
    }

    private ItemStack getPlayerHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(player);
            item.setItemMeta(skullMeta);
        }
        return item;
    }

}
