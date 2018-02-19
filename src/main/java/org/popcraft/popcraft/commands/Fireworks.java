package org.popcraft.popcraft.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.Command;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.popcraft.popcraft.PopCommand;
import org.popcraft.popcraft.utils.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PopCommand("fireworks")
public class Fireworks extends PlayerCommand {

    private static final Map<String, Color> COLORS = Stream.of(DyeColor.values()).collect(Collectors.toMap(color -> color.name().toLowerCase(), DyeColor::getFireworkColor));
    private static final Map<String, Type> TYPES = Stream.of(Type.values()).collect(Collectors.toMap(type -> type.name().toLowerCase(), type -> type));
    private static final ImmutableMap<String, Integer> HEIGHTS = ImmutableMap.of("low", 0, "medium", 1, "high", 2, "extreme", 3);

    @Override
    public boolean onPlayerCommand(Player player, Command cmd, String label, String[] args) {
        if (args.length == 0)
            return false;
        if (args.length == 1 && "list".equalsIgnoreCase(args[0])) {
            Message.normal(player, "Firework properties:\nColors: "
                    + ChatColor.RESET + Joiner.on(", ").join(COLORS.keySet()) + ", random"
                    + ChatColor.GOLD + "\nFades: " + ChatColor.RESET + "Prepend a color with *\n" + ChatColor.GOLD
                    + "Types: " + ChatColor.RESET + Joiner.on(", ").join(TYPES.keySet()) + ChatColor.GOLD
                    + "\nHeights: " + ChatColor.RESET + Joiner.on(", ").join(HEIGHTS.keySet()) + ChatColor.GOLD
                    + "\nEffects: " + ChatColor.RESET + "flicker, trail");
        }
        FireworkEffect.Builder fireworkBuilder = FireworkEffect.builder();
        List<String> arguments = Stream.of(args).map(String::toLowerCase).collect(Collectors.toList());
        boolean valid = false;
        int height = HEIGHTS.get("low");
        for (String argument : arguments) {
            if (COLORS.containsKey(argument)) {
                fireworkBuilder.withColor(COLORS.get(argument));
                valid = true;
            } else if (COLORS.containsKey(argument.replaceFirst("[*]", ""))) {
                fireworkBuilder.withFade(COLORS.get(argument.replaceFirst("[*]", "")));
            } else if (TYPES.containsKey(argument)) {
                fireworkBuilder.with(TYPES.get(argument));
            } else if (HEIGHTS.containsKey(argument)) {
                height = HEIGHTS.get(argument);
            } else if ("flicker".equals(argument)) {
                fireworkBuilder.withFlicker();
            } else if ("trail".equals(argument)) {
                fireworkBuilder.withTrail();
            } else if ("random".equals(argument)) {
                fireworkBuilder.withColor(this.getRandomColor());
                valid = true;
            } else if ("*random".equals(argument)) {
                fireworkBuilder.withFade(this.getRandomColor());
            }
        }
        if (valid) {
            FireworkEffect fireworkEffect = fireworkBuilder.build();
            Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.addEffect(fireworkEffect);
            meta.setPower(height);
            firework.setFireworkMeta(meta);
        }
        return true;
    }

    private Color getRandomColor() {
        List<Color> colors = new ArrayList<>(COLORS.values());
        Collections.shuffle(colors);
        return colors.get(0);
    }

}