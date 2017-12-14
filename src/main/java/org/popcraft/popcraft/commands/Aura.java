package org.popcraft.popcraft.commands;

import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.popcraft.newCode.PopCommand;
import org.popcraft.popcraft.utils.Message;
import org.popcraft.popcraft.utils.TrailMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import static org.popcraft.popcraft.utils.TrailMeta.TrailType;

@PopCommand("aura")
public class Aura implements Listener, CommandExecutor {

    private final JavaPlugin plugin;
    private static final String BASIC_AURAS = "clouds, dragon, flames, glow, gusts, puffs, smoke, waterdrops";
    public static final Map<String, TrailMeta> AURA_TYPES = new HashMap<>();
    public static final Map<UUID, TrailMeta> PLAYER_AURA = new HashMap<>();

    static {
        for (Material m : Material.values()) {
            AURA_TYPES.put(m.toString().toLowerCase(), TrailMeta.of(Particle.ITEM_CRACK, TrailType.ITEM).setData((new ItemStack(m)).getData()).setParticleCount(5).setExtraData(1));
        }
        AURA_TYPES.put("clouds", TrailMeta.of(Particle.CLOUD, TrailType.PARTICLE).setParticleCount(5).setExtraData(1));
        AURA_TYPES.put("flames", TrailMeta.of(Particle.FLAME, TrailType.PARTICLE).setParticleCount(5).setExtraData(1));
        AURA_TYPES.put("smoke", TrailMeta.of(Particle.SMOKE_LARGE, TrailType.PARTICLE).setParticleCount(5).setExtraData(1));
        AURA_TYPES.put("dragon", TrailMeta.of(Particle.DRAGON_BREATH, TrailType.PARTICLE).setParticleCount(5).setExtraData(1));
        AURA_TYPES.put("glow", TrailMeta.of(Particle.END_ROD, TrailType.PARTICLE).setParticleCount(5).setExtraData(1));
        AURA_TYPES.put("gusts", TrailMeta.of(Particle.SNOW_SHOVEL, TrailType.PARTICLE).setParticleCount(5).setExtraData(1));
        AURA_TYPES.put("puffs", TrailMeta.of(Particle.SMOKE_NORMAL, TrailType.PARTICLE).setParticleCount(5).setExtraData(1));
        AURA_TYPES.put("waterdrops", TrailMeta.of(Particle.WATER_WAKE, TrailType.PARTICLE).setParticleCount(10).setExtraData(1));
    }

    @Inject
    public Aura(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;
        final Player player = (Player) sender;
        if (args.length == 0) {
            if (!PLAYER_AURA.containsKey(player.getUniqueId()))
                return false;
            PLAYER_AURA.remove(player.getUniqueId());
            Message.normal(player, "Cleared aura.");
            return true;
        } else
            return parseArguments(player, args);
    }

    private boolean parseArguments(final Player player, final String[] args) {
        if ("clear".equalsIgnoreCase(args[0])) {
            if (PLAYER_AURA.containsKey(player.getUniqueId())) {
                PLAYER_AURA.remove(player.getUniqueId());
                Message.normal(player, "Cleared aura.");
            } else {
                Message.error(player, "You don't have an aura enabled!");
            }
        } else if ("list".equalsIgnoreCase(args[0])) {
            Message.normal(player, "Auras: " + ChatColor.RESET + BASIC_AURAS);
        } else {
            if (!AURA_TYPES.containsKey(args[0].toLowerCase()))
                return false;
            TrailMeta aura = new TrailMeta(AURA_TYPES.get(args[0].toLowerCase()));
            processArgument(aura, args);
            short durability = aura.getType().equals(TrailType.ITEM) ? aura.getData().toItemStack(0).getDurability() : 0;
            PLAYER_AURA.put(player.getUniqueId(), aura);
            Message.normal(player, "Aura set to " + ChatColor.RED + args[0].toLowerCase().replace("_", " ") + (durability != 0 ? ":" + durability : "") + (aura.getStyle() != TrailMeta.TrailStyle.NORMAL ? " " + aura.getStyle().toString().toLowerCase() : "") + ChatColor.GOLD + ".");
        }
        return true;
    }

    private void processArgument(TrailMeta aura, String[] args) {
        for (String arg : args) {
            if (aura.getType().equals(TrailType.ITEM)) {
                try {
                    short durability = Short.parseShort(arg);
                    ItemStack i = (aura.getData()).toItemStack(0);
                    i.setDurability(durability);
                    aura.setData(i.getData());
                } catch (NumberFormatException e) {
                    plugin.getLogger().log(Level.FINE, "Unable to parse argument as a short", e);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (PLAYER_AURA.containsKey(player.getUniqueId())) {
            TrailMeta aura = PLAYER_AURA.get(player.getUniqueId());
            player.getWorld().spawnParticle((Particle) aura.getTrail(),
                    player.getLocation().add(aura.getShiftX(), aura.getShiftY(), aura.getShiftZ()), aura.getParticleCount(),
                    aura.getOffsetX(), aura.getOffsetY(), aura.getOffsetZ(), aura.getExtraData(),
                    (aura.getData() == null) ? null : (aura.getData().toItemStack(0)));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (PLAYER_AURA.containsKey(player.getUniqueId())) {
            PLAYER_AURA.remove(player.getUniqueId());
        }
    }
}
