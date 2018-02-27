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

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PopCommand("fireworks")
public class Fireworks extends PlayerCommand {

    private static final Map<String, Color> COLORS = Stream.of(DyeColor.values()).collect(Collectors.toMap(color -> color.name().toLowerCase(), DyeColor::getFireworkColor));
    private static final Map<String, Type> TYPES = Stream.of(Type.values()).collect(Collectors.toMap(type -> type.name().toLowerCase(), type -> type));
    private static final ImmutableMap<String, Integer> HEIGHTS = ImmutableMap.of("low", 0, "medium", 1, "high", 2, "extreme", 3);

    @Override
    public boolean onPlayerCommand(Player player, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        else if (args.length == 1 && "list".equalsIgnoreCase(args[0])) {
            Message.normal(player, "Firework properties:\nColors: "
                    + ChatColor.RESET + Joiner.on(", ").join(COLORS.keySet()) + ", random"
                    + ChatColor.GOLD + "\nFades: " + ChatColor.RESET + "Prepend a color with *\n" + ChatColor.GOLD
                    + "Types: " + ChatColor.RESET + Joiner.on(", ").join(TYPES.keySet()) + ChatColor.GOLD
                    + "\nHeights: " + ChatColor.RESET + Joiner.on(", ").join(HEIGHTS.keySet()) + ChatColor.GOLD
                    + "\nEffects: " + ChatColor.RESET + "flicker, trail");
        } else {
            newBuilder(args)
                    .withAction(arg -> arg.contains("flicker"), FireworkEffect.Builder::withFlicker)
                    .withAction(arg -> arg.contains("trail"), FireworkEffect.Builder::withTrail)
                    .withAction(arg -> arg.contains("*random"), builder -> builder.withFade(getRandomColor()))
                    .withRandomColor()
                    .smartBuild(player);
        }
        return true;
    }

    private static Color getRandomColor() {
        List<Color> colors = new ArrayList<>(COLORS.values());
        Collections.shuffle(colors);
        return colors.get(0);
    }

    public static PopBuilder newBuilder(final String...args) {
        return new PopBuilder(args);
    }

    private static class PopBuilder {

        private final FireworkEffect.Builder builder = FireworkEffect.builder();
        private final Set<String> arguments;

        private Color color = null;
        private Color fade = null;
        private Type type = null;
        private int height = HEIGHTS.get("low");


        private PopBuilder(final String... args) {
            this.arguments = Stream.of(args).map(String::toLowerCase).collect(Collectors.toSet());
        }

        public PopBuilder withAction(final Function<Set<String>, Boolean> test, final Consumer<FireworkEffect.Builder> action) {
            if (test.apply(arguments)) {
                action.accept(builder);
            }
            return this;
        }

        public PopBuilder withRandomColor() {
            if (this.arguments.contains("random")) {
                this.color = getRandomColor();
            }
            return this;
        }

        public void smartBuild(final Player player) {
            for (final String arg: arguments) {
                if (arg.startsWith("*")) {
                    this.fade = COLORS.getOrDefault(arg.substring(1), this.fade);
                } else {
                    this.color = COLORS.getOrDefault(arg, this.color);
                    this.type = TYPES.getOrDefault(arg, this.type);
                    this.height = HEIGHTS.getOrDefault(arg, this.height);
                }
            }

            this.buildFirework(player);
        }

        private void buildFirework(final Player player) {
            if (this.color != null) {
                builder.withColor(this.color);
                if (this.fade != null) {
                    builder.withFade(this.fade);
                }
                if (this.type != null) {
                    builder.with(this.type);
                }
                FireworkEffect fireworkEffect = builder.build();
                Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(fireworkEffect);
                meta.setPower(this.height);
                firework.setFireworkMeta(meta);
            }
        }
    }

}