package org.popcraft.popcraft.commands;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.Cooldown;

import java.util.*;

public class CommandPvp extends PopCraftCommand {

    private Cooldown cooldown = new Cooldown(plugin.getConfig().getLong("command.pvp.cooldown"));
    private Map<UUID, Boolean> pvp = new HashMap<>();

    public CommandPvp() {
        super("pvp");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Result.UNSUPPORTED_SENDER;
        }
        Player player = (Player) sender;
        if (cooldown.isFinished(player)) {
            boolean pvpEnabled = !pvp.getOrDefault(player.getUniqueId(), false);
            pvp.put(player.getUniqueId(), pvpEnabled);
            player.sendMessage(plugin.getMessage("pvpToggle", pvpEnabled ? "enabled" : "disabled"));
            if (pvpEnabled) {
                player.getWorld().spawnParticle(Particle.LAVA, player.getLocation(), 50);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_WOLOLO, 2, 1);
            } else {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CHICKEN_AMBIENT, 2, 1);
            }
            cooldown.set(player);
        } else {
            player.sendMessage(plugin.getMessage("commandOnCooldown", cooldown.getFormattedTimeRemaining(player)));
        }
        return Result.SUCCESS;
    }

    public boolean isPvpEnabled(Entity player) {
        return pvp.getOrDefault(player.getUniqueId(), false);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

}
