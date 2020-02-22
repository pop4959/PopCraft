package org.popcraft.popcraft.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.data.Trail;
import org.popcraft.popcraft.utils.TabCompleteUtil;

import java.util.*;

public class CommandTrail extends PopCraftCommand {

    private static Map<String, Trail> types = new HashMap<>();
    private static List<String> presetTrailTypeNames = new ArrayList<>();
    private Map<UUID, Trail> trail = new HashMap<>();

    static {
        types.put("bubbles", new Trail(Particle.CRIT_MAGIC, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                2, 0, 0.5, 0.8, 0.5, 0, 0, 0));
        types.put("darkness", new Trail(Particle.SQUID_INK, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                2, 0, 0.5, 0.7, 0.5, 0, 0, 0));
        types.put("flames", new Trail(Particle.FLAME, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                5, 0, 0.5, 0.5, 0.5, 0, 0, 0));
        types.put("glitter", new Trail(Particle.SPELL_INSTANT, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                15, 0, 0.2, 0, 0.2, 0, 0, 0));
        types.put("glow", new Trail(Particle.END_ROD, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                1, 0, 0.5, 0.8, 0.5, 0, 0, 0));
        types.put("glyphs", new Trail(Particle.ENCHANTMENT_TABLE, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                10, 0, 0.5, 0.8, 0.5, 0, 0, 0));
        types.put("hearts", new Trail(Particle.DAMAGE_INDICATOR, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                1, 0, 0.5, 0.8, 0.5, 0, 0, 0));
        types.put("lavadrops", new Trail(Particle.DRIP_LAVA, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                1, 0, 0, 0, 0, 0, 0.4, 0));
        types.put("love", new Trail(Particle.HEART, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                1, 0, 0.5, 0.8, 0.5, 0, 0, 0));
        types.put("magic", new Trail(Particle.SPELL_WITCH, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                15, 0, 0.2, 0, 0.2, 0, 0, 0));
        types.put("mist", new Trail(Particle.SPIT, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                3, 0, 0.5, 0.6, 0.5, 0, 0, 0));
        types.put("music", new Trail(Particle.NOTE, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                1, 1, 0.5, 0.8, 0.5, 0, 0, 0));
        types.put("party", new Trail(Particle.TOTEM, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                3, 0, 0.5, 0.8, 0.5, 0, 0, 0));
        types.put("raindrops", new Trail(Particle.WATER_SPLASH, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                10, 0, 0.2, 0, 0.2, 0, 0, 0));
        types.put("slime", new Trail(Particle.SLIME, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                4, 0, 0.1, 0, 0.1, 0, 0, 0));
        types.put("smoke", new Trail(Particle.SMOKE_LARGE, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                2, 0, 0.3, 0, 0.3, 0, 0, 0));
        types.put("snowy", new Trail(Particle.SNOWBALL, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                4, 0, 0.1, 0, 0.1, 0, 0, 0));
        types.put("sparkles", new Trail(Particle.VILLAGER_HAPPY, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                2, 0, 0.5, 0.8, 0.5, 0, 0, 0));
        types.put("sparks", new Trail(Particle.CRIT, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                2, 0, 0.5, 0.8, 0.5, 0, 0, 0));
        types.put("swirls", new Trail(Particle.SPELL_MOB, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                2, 1, 0.5, 0.8, 0.5, 0, 0, 0));
        types.put("teleport", new Trail(Particle.PORTAL, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                10, 0, 0.5, 0.8, 0.5, 0, 0, 0));
        types.put("thunderclouds", new Trail(Particle.VILLAGER_ANGRY, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                1, 0, 0.6, 0.3, 0.6, 0, 0, 0));
        types.put("volcano", new Trail(Particle.LAVA, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                2, 0, 0, 0, 0, 0, 0, 0));
        types.put("waterdrops", new Trail(Particle.DRIP_WATER, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                1, 0, 0, 0, 0, 0, 0.4, 0));
        presetTrailTypeNames.addAll(types.keySet());
        for (Material m : Material.values()) {
            if (m.isBlock()) {
                types.put(m.toString().toLowerCase(), new Trail(Particle.BLOCK_CRACK,
                        m.createBlockData(), Trail.Type.BLOCK, Trail.Style.NORMAL, 32, 0, 0, 0, 0, 0, 0, 0));
            }
        }
    }

    public CommandTrail() {
        super("trail");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Result.UNSUPPORTED_SENDER;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clear")) {
                trail.remove(player.getUniqueId());
                player.sendMessage(plugin.getMessage("trailClear"));
                return Result.SUCCESS;
            } else if (args[0].equalsIgnoreCase("list")) {
                player.sendMessage(plugin.getMessage("trailList", StringUtils.join(presetTrailTypeNames, ", ")));
                return Result.SUCCESS;
            }
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("set")) {
            if (types.containsKey(args[1].toLowerCase())) {
                Trail newTrail = new Trail(types.get(args[1].toLowerCase()));
                String trailName = args[1].toLowerCase().replace("_", " "), trailStyle = "";
                if (args.length >= 3) {
                    trailStyle = args[2].toLowerCase();
                    Trail.Style style = Trail.Style.valueOf(args[2].toUpperCase());
                    newTrail = newTrail.changeStyle(style);
                }
                trail.put(player.getUniqueId(), newTrail);
                player.sendMessage(plugin.getMessage("trailSet", trailName, trailStyle));
            } else {
                player.sendMessage(plugin.getMessage("error", plugin.getMessage("trailErrorDoesntExist")));
            }
            return Result.SUCCESS;
        }
        return Result.INCORRECT_USAGE;
    }

    public Map<UUID, Trail> getTrailMap() {
        return trail;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return TabCompleteUtil.startsWithLastArg(Arrays.asList("clear", "list", "set"), args);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return TabCompleteUtil.startsWithLastArg(types.keySet(), args);
        } else if (args.length == 3 && types.containsKey(args[1].toLowerCase())) {
            return TabCompleteUtil.startsWithLastArg(Arrays.asList("dots", "rain", "dust", "spread"), args);
        } else {
            return Collections.emptyList();
        }
    }

}
