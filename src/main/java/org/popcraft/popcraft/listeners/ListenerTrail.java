package org.popcraft.popcraft.listeners;

import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.popcraft.popcraft.commands.CommandAura;
import org.popcraft.popcraft.commands.CommandTrail;
import org.popcraft.popcraft.data.Trail;

import java.util.Map;
import java.util.UUID;

public class ListenerTrail extends PopCraftListener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        CommandTrail commandTrail = (CommandTrail) plugin.getCommands().get("trail");
        Map<UUID, Trail> trailMap = commandTrail.getTrailMap();
        if (trailMap.containsKey(player.getUniqueId())) {
            Trail trail = trailMap.get(player.getUniqueId());
            if (trail.getType() == Trail.Type.EFFECT) {
                for (int i = 0; i < trail.getCount(); i++)
                    player.getWorld().playEffect(
                            player.getLocation().add(trail.getShiftX(), trail.getShiftY(), trail.getShiftZ()),
                            (Effect) trail.getTrail(), trail.getExtra());
            } else if (trail.getType() == Trail.Type.PARTICLE) {
                player.getWorld().spawnParticle((Particle) trail.getTrail(),
                        player.getLocation().add(trail.getShiftX(), trail.getShiftY(), trail.getShiftZ()),
                        trail.getCount(), trail.getOffsetX(), trail.getOffsetY(), trail.getOffsetZ(), trail.getExtra(),
                        null);
            } else if (trail.getType() == Trail.Type.BLOCK) {
                player.getWorld().spawnParticle((Particle) trail.getTrail(),
                        player.getLocation().add(trail.getShiftX(), trail.getShiftY(), trail.getShiftZ()),
                        trail.getCount(), trail.getOffsetX(), trail.getOffsetY(), trail.getOffsetZ(), trail.getExtra(),
                        trail.getData());
            } else if (trail.getType() == Trail.Type.ITEM) {
                player.getWorld().spawnParticle((Particle) trail.getTrail(),
                        player.getLocation().add(trail.getShiftX(), trail.getShiftY(), trail.getShiftZ()),
                        trail.getCount(), trail.getOffsetX(), trail.getOffsetY(), trail.getOffsetZ(), trail.getExtra(),
                        new ItemStack(trail.getData().getMaterial()));
            }
        }
        CommandAura commandAura = (CommandAura) plugin.getCommands().get("aura");
        Map<UUID, Trail> auraMap = commandAura.getAuraMap();
        if (auraMap.containsKey(player.getUniqueId())) {
            Trail aura = auraMap.get(player.getUniqueId());
            player.getWorld().spawnParticle((Particle) aura.getTrail(),
                    player.getLocation().add(aura.getShiftX(), aura.getShiftY(), aura.getShiftZ()), aura.getCount(),
                    aura.getOffsetX(), aura.getOffsetY(), aura.getOffsetZ(), aura.getExtra(),
                    (aura.getData() == null) ? null : new ItemStack(aura.getData().getMaterial()));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CommandTrail commandTrail = (CommandTrail) plugin.getCommands().get("trail");
        Map<UUID, Trail> trailMap = commandTrail.getTrailMap();
        trailMap.remove(player.getUniqueId());
        CommandAura commandAura = (CommandAura) plugin.getCommands().get("aura");
        Map<UUID, Trail> auraMap = commandAura.getAuraMap();
        auraMap.remove(player.getUniqueId());
    }

}
