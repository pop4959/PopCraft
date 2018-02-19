package org.popcraft.popcraft.utils;

import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.*;
import static org.bukkit.potion.PotionEffectType.*;

public class FeatherJump implements Listener {

    private static final PotionEffect jumpResEffect = new PotionEffect(DAMAGE_RESISTANCE, 100, 4);
    private final Cooldown cooldown;

    @Inject
    public FeatherJump(final Cooldown cooldown) {
        this.cooldown = cooldown;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player.hasPermission("popcraft.jumper") && event.getAction() == RIGHT_CLICK_AIR) {
            checkValidJumpComand(player);
        }
    }

    private void checkValidJumpComand(final Player player) {
        final PlayerInventory inv = player.getInventory();
        if (validMaterial(inv.getItemInMainHand(), inv.getItemInOffHand()) && this.cooldown.check(player.getUniqueId())) {
            jumpResEffect.apply(player);
            player.setVelocity(player.getLocation().getDirection().multiply(new Vector(3, 3, 3)));
        }
    }

    private boolean validMaterial(final ItemStack... stacks) {
        for (final ItemStack stack : stacks) {
            if (stack.getType() == FEATHER) {
                return true;
            }
        }
        return false;
    }

}
