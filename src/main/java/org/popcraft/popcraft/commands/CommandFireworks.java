package org.popcraft.popcraft.commands;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.popcraft.popcraft.utils.TabCompleteUtil;

import java.util.*;
import java.util.stream.Collectors;

public class CommandFireworks extends PopCraftCommand {

    private Random random = new Random(System.currentTimeMillis());
    private static Map<String, Color> colors = new HashMap<>();
    private static Map<String, FireworkEffect.Type> types = new HashMap<>();
    private static Map<String, Integer> powers = new HashMap<>();

    static {
        colors.put("aqua", Color.AQUA);
        colors.put("black", Color.BLACK);
        colors.put("blue", Color.BLUE);
        colors.put("fuchsia", Color.FUCHSIA);
        colors.put("gray", Color.GRAY);
        colors.put("green", Color.GREEN);
        colors.put("lime", Color.LIME);
        colors.put("maroon", Color.MAROON);
        colors.put("navy", Color.NAVY);
        colors.put("olive", Color.OLIVE);
        colors.put("orange", Color.ORANGE);
        colors.put("purple", Color.PURPLE);
        colors.put("red", Color.RED);
        colors.put("silver", Color.SILVER);
        colors.put("teal", Color.TEAL);
        colors.put("white", Color.WHITE);
        colors.put("yellow", Color.YELLOW);
        types.put("small", FireworkEffect.Type.BALL);
        types.put("large", FireworkEffect.Type.BALL_LARGE);
        types.put("burst", FireworkEffect.Type.BURST);
        types.put("creeper", FireworkEffect.Type.CREEPER);
        types.put("star", FireworkEffect.Type.STAR);
        powers.put("low", 0);
        powers.put("medium", 1);
        powers.put("high", 2);
        powers.put("extreme", 3);
    }

    public CommandFireworks() {
        super("fireworks");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Result.UNSUPPORTED_SENDER;
        }
        Player player = (Player) sender;
        FireworkEffect.Builder fireworkEffectBuilder = FireworkEffect.builder();
        Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setLore(Collections.singletonList(plugin.getName()));
        for (String arg : args) {
            String option = arg.indexOf(':') == -1 ? "" : arg.substring(arg.indexOf(':') + 1).toLowerCase();
            if (arg.startsWith("color:")) {
                Color color = getColor(option);
                if (color != null) {
                    fireworkEffectBuilder.withColor(color);
                }
            } else if (arg.startsWith("fade:")) {
                Color color = getColor(option);
                if (color != null) {
                    fireworkEffectBuilder.withFade(color);
                }
            } else if (arg.startsWith("effect:")) {
                if ("random".equals(option)) {
                    option = random.nextBoolean() ? "flicker" : "trail";
                }
                if ("flicker".equals(option)) {
                    fireworkEffectBuilder.withFlicker();
                } else if ("trail".equals(option)) {
                    fireworkEffectBuilder.withTrail();
                }
            } else if (arg.startsWith("type:")) {
                if ("random".equals(option)) {
                    fireworkEffectBuilder.with(new ArrayList<>(types.values()).get(random.nextInt(types.size())));
                } else if (types.containsKey(option)) {
                    fireworkEffectBuilder.with(types.get(option));
                }
            } else if (arg.startsWith("power:")) {
                if ("random".equals(option)) {
                    fireworkMeta.setPower(random.nextInt(3));
                } else if (powers.containsKey(option)) {
                    fireworkMeta.setPower(powers.get(option));
                } else {
                    try {
                        fireworkMeta.setPower(Integer.parseInt(option));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        try {
            fireworkMeta.addEffect(fireworkEffectBuilder.build());
        } catch (IllegalStateException ignored) {
        }
        firework.setFireworkMeta(fireworkMeta);
        return Result.SUCCESS;
    }

    private Color getColor(String option) {
        if ("random".equals(option)) {
            return new ArrayList<>(colors.values()).get(random.nextInt(colors.size()));
        } else if (colors.containsKey(option)) {
            // Use a default color
            return colors.get(option);
        } else if (option.length() == 6) {
            try {
                // Attempt to parse as a custom hex color code
                return Color.fromRGB(
                        Integer.parseInt(option.substring(0, 2), 16),
                        Integer.parseInt(option.substring(2, 4), 16),
                        Integer.parseInt(option.substring(4, 6), 16));
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        suggestions.addAll(TabCompleteUtil.startsWithLastArg(colors.keySet().stream()
                .map(k -> "color:" + k).collect(Collectors.toList()), args));
        suggestions.addAll(TabCompleteUtil.startsWithLastArg(colors.keySet().stream()
                .map(k -> "fade:" + k).collect(Collectors.toList()), args));
        suggestions.addAll(TabCompleteUtil.startsWithLastArg(
                Arrays.asList("effect:flicker", "effect:trail"), args));
        suggestions.addAll(TabCompleteUtil.startsWithLastArg(types.keySet().stream()
                .map(k -> "type:" + k).collect(Collectors.toList()), args));
        suggestions.addAll(TabCompleteUtil.startsWithLastArg(powers.keySet().stream()
                .map(k -> "power:" + k).collect(Collectors.toList()), args));
        suggestions.addAll(TabCompleteUtil.startsWithLastArg(
                Arrays.asList("color:random", "fade:random", "effect:random", "type:random", "power:random"), args));
        return suggestions;
    }

}
