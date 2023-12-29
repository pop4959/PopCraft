package org.popcraft.popcraft.commands;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.popcraft.popcraft.data.Trail;
import org.popcraft.popcraft.utils.TabCompleteUtil;

import java.util.*;

public class CommandAura extends PopCraftCommand {

    private static Map<String, Trail> types = new HashMap<>();
    private static List<String> presetAuraTypeNames = new ArrayList<>();
    private Map<UUID, Trail> aura = new HashMap<>();

    static {
        types.put("clouds", new Trail(Particle.CLOUD, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                5, 1, 0, 0, 0, 0, 0, 0));
        types.put("flames", new Trail(Particle.FLAME, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                5, 1, 0, 0, 0, 0, 0, 0));
        types.put("smoke", new Trail(Particle.SMOKE_LARGE, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                5, 1, 0, 0, 0, 0, 0, 0));
        types.put("dragon", new Trail(Particle.DRAGON_BREATH, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                5, 1, 0, 0, 0, 0, 0, 0));
        types.put("glow", new Trail(Particle.END_ROD, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                5, 1, 0, 0, 0, 0, 0, 0));
        types.put("gusts", new Trail(Particle.SNOW_SHOVEL, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                5, 1, 0, 0, 0, 0, 0, 0));
        types.put("puffs", new Trail(Particle.SMOKE_NORMAL, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                5, 1, 0, 0, 0, 0, 0, 0));
        types.put("waterdrops", new Trail(Particle.WATER_WAKE, null, Trail.Type.PARTICLE, Trail.Style.NORMAL,
                10, 1, 0, 0, 0, 0, 0, 0));
        presetAuraTypeNames.addAll(types.keySet());
        for (Material m : Material.values()) {
            if (m.isBlock()) {
                types.put(m.toString().toLowerCase(), new Trail(Particle.ITEM_CRACK, m.createBlockData(),
                        Trail.Type.ITEM, Trail.Style.NORMAL, 5, 1, 0, 0, 0, 0, 0, 0));
            }
        }
    }

    public CommandAura() {
        super("aura");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Result.UNSUPPORTED_SENDER;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clear")) {
                aura.remove(player.getUniqueId());
                player.sendMessage(plugin.getMessage("auraClear"));
                return Result.SUCCESS;
            } else if (args[0].equalsIgnoreCase("list")) {
                player.sendMessage(plugin.getMessage("auraList", String.join(", ", presetAuraTypeNames)));
                return Result.SUCCESS;
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            if (types.containsKey(args[1].toLowerCase())) {
                Trail newAura = new Trail(types.get(args[1].toLowerCase()));
                String auraName = args[1].toLowerCase().replace("_", " ");
                aura.put(player.getUniqueId(), newAura);
                player.sendMessage(plugin.getMessage("auraSet", auraName));
            } else {
                player.sendMessage(plugin.getMessage("error", plugin.getMessage("auraErrorDoesntExist")));
            }
            return Result.SUCCESS;
        }
        return Result.INCORRECT_USAGE;
    }

    public Map<UUID, Trail> getAuraMap() {
        return aura;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return TabCompleteUtil.startsWithLastArg(Arrays.asList("clear", "list", "set"), args);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return TabCompleteUtil.startsWithLastArg(types.keySet(), args);
        } else {
            return Collections.emptyList();
        }
    }

}
