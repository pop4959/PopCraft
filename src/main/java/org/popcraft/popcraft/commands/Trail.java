package org.popcraft.popcraft.commands;

import com.google.inject.Inject;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.popcraft.PopCommand;
import org.popcraft.popcraft.utils.Message;
import org.popcraft.popcraft.utils.TrailMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import static org.popcraft.popcraft.utils.TrailMeta.TrailStyle;
import static org.popcraft.popcraft.utils.TrailMeta.TrailType;

@PopCommand("trail")
public class Trail implements Listener, CommandExecutor {

    private final JavaPlugin plugin;
    private static final String BASIC_TRAILS = "bubbles, flames, glitter, glow, glyphs, hearts, lavadrops, love, magic, music, party, rainbow, raindrops, redstone, slime, smoke, snowy, sparkles, sparks, swirls, teleport, thunderclouds, volcano, waterdrops";
    private static final Map<String, TrailMeta> TRAIL_TYPES = new HashMap<>();
    private static final Map<UUID, TrailMeta> PLAYER_TRAIL = new HashMap<>();

    static {
        for (Material material : Material.values()) {
            if (material.isBlock())
                TRAIL_TYPES.put(material.toString().toLowerCase(), TrailMeta.of(Particle.BLOCK_CRACK, TrailType.BLOCK).setData((new ItemStack(material)).getData()));
            else
                TRAIL_TYPES.put(material.toString().toLowerCase(), TrailMeta.of(Particle.ITEM_CRACK, TrailType.ITEM).setData((new ItemStack(material)).getData()).setOffset(0.5, 0.5, 0.5));
        }
        for (Effect effect : Effect.values()) {
            if (effect.getType() != Effect.Type.SOUND)
                TRAIL_TYPES.put(effect.name().toLowerCase() + "_debug_effect", TrailMeta.of(effect, TrailType.EFFECT));
        }
        for (Particle particle : Particle.values()) {
            TRAIL_TYPES.put(particle.name().toLowerCase() + "_debug_particle", TrailMeta.of(particle, TrailType.PARTICLE));
        }
        TRAIL_TYPES.put("bubbles", TrailMeta.of(Particle.CRIT_MAGIC, TrailType.PARTICLE).setParticleCount(2).setOffset(0.5, 0.8, 0.5));
        TRAIL_TYPES.put("flames", TrailMeta.of(Particle.FLAME, TrailType.PARTICLE).setParticleCount(5).setOffset(0.5, 0.5, 0.5));
        TRAIL_TYPES.put("glitter", TrailMeta.of(Particle.SPELL_INSTANT, TrailType.PARTICLE).setParticleCount(15).setOffset(0.2, 0, 0.2));
        TRAIL_TYPES.put("glow", TrailMeta.of(Particle.END_ROD, TrailType.PARTICLE).setParticleCount(1).setOffset(0.5, 0.8, 0.5));
        TRAIL_TYPES.put("glyphs", TrailMeta.of(Particle.ENCHANTMENT_TABLE, TrailType.PARTICLE).setParticleCount(10).setOffset(0.5, 0.8, 0.5));
        TRAIL_TYPES.put("hearts", TrailMeta.of(Particle.DAMAGE_INDICATOR, TrailType.PARTICLE).setParticleCount(1).setOffset(0.5, 0.8, 0.5));
        TRAIL_TYPES.put("lavadrops", TrailMeta.of(Particle.DRIP_LAVA, TrailType.PARTICLE).setParticleCount(1).setShiftY(0.4));
        TRAIL_TYPES.put("love", TrailMeta.of(Particle.HEART, TrailType.PARTICLE).setParticleCount(1).setOffset(0.5, 0.8, 0.5));
        TRAIL_TYPES.put("magic", TrailMeta.of(Particle.SPELL_WITCH, TrailType.PARTICLE).setParticleCount(15).setOffset(0.2, 0, 0.2));
        TRAIL_TYPES.put("music", TrailMeta.of(Particle.NOTE, TrailType.PARTICLE).setParticleCount(1).setExtraData(1).setOffset(0.5, 0.8, 0.5));
        TRAIL_TYPES.put("party", TrailMeta.of(Particle.TOTEM, TrailType.PARTICLE).setParticleCount(3).setOffset(0.5, 0.8, 0.5));
        TRAIL_TYPES.put("rainbow", TrailMeta.of(Particle.REDSTONE, TrailType.PARTICLE).setParticleCount(4).setExtraData(1).setOffset(0.5, 0.8, 0.5));
        TRAIL_TYPES.put("raindrops", TrailMeta.of(Particle.WATER_SPLASH, TrailType.PARTICLE).setParticleCount(10).setOffset(0.2, 0, 0.2));
        TRAIL_TYPES.put("redstone", TrailMeta.of(Particle.REDSTONE, TrailType.PARTICLE).setParticleCount(4).setOffset(0.5, 0.8, 0.5));
        TRAIL_TYPES.put("slime", TrailMeta.of(Particle.SLIME, TrailType.PARTICLE).setParticleCount(4).setOffset(0.1, 0, 0.1));
        TRAIL_TYPES.put("smoke", TrailMeta.of(Particle.SMOKE_LARGE, TrailType.PARTICLE).setParticleCount(2).setOffset(0.3, 0, 0.3));
        TRAIL_TYPES.put("snowy", TrailMeta.of(Particle.SNOWBALL, TrailType.PARTICLE).setParticleCount(4).setOffset(0.1, 0, 0.1));
        TRAIL_TYPES.put("sparkles", TrailMeta.of(Particle.VILLAGER_HAPPY, TrailType.PARTICLE).setParticleCount(2).setOffset(0.5, 0.8, 0.5));
        TRAIL_TYPES.put("sparks", TrailMeta.of(Particle.CRIT, TrailType.PARTICLE).setParticleCount(2).setOffset(0.5, 0.8, 0.5));
        TRAIL_TYPES.put("swirls", TrailMeta.of(Particle.SPELL_MOB, TrailType.PARTICLE).setParticleCount(2).setExtraData(1).setOffset(0.5, 0.8, 0.5));
        TRAIL_TYPES.put("teleport", TrailMeta.of(Particle.PORTAL, TrailType.PARTICLE).setParticleCount(10).setOffset(0.5, 0.8, 0.5));
        TRAIL_TYPES.put("thunderclouds", TrailMeta.of(Particle.VILLAGER_ANGRY, TrailType.PARTICLE).setParticleCount(1).setOffset(0.6, 0.3, 0.6));
        TRAIL_TYPES.put("volcano", TrailMeta.of(Particle.LAVA, TrailType.PARTICLE).setParticleCount(2));
        TRAIL_TYPES.put("waterdrops", TrailMeta.of(Particle.DRIP_WATER, TrailType.PARTICLE).setParticleCount(1).setShiftY(0.4));
    }

    @Inject
    public Trail(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;
        final Player player = (Player) sender;
        if (args.length == 0) {
            if (!PLAYER_TRAIL.containsKey(player.getUniqueId()))
                return false;
            PLAYER_TRAIL.remove(player.getUniqueId());
            Message.normal(player, "Cleared trail.");
            return true;
        } else
            return parseArguments(player, args);
    }

    private boolean parseArguments(final Player player, final String[] args) {
        if ("clear".equalsIgnoreCase(args[0])) {
            if (PLAYER_TRAIL.containsKey(player.getUniqueId())) {
                PLAYER_TRAIL.remove(player.getUniqueId());
                Message.normal(player, "Cleared trail.");
            } else {
                Message.error(player, "You don't have a trail enabled!");
            }
        } else if ("list".equalsIgnoreCase(args[0])) {
            Message.normal(player, "Trails: " + ChatColor.RESET + BASIC_TRAILS);
        } else {
            if (!TRAIL_TYPES.containsKey(args[0].toLowerCase()))
                return false;
            TrailMeta trail = new TrailMeta(TRAIL_TYPES.get(args[0].toLowerCase()));
            processArgument(trail, args);
            short durability = trail.getType().equals(TrailType.BLOCK) ? trail.getData().toItemStack(0).getDurability() : 0;
            PLAYER_TRAIL.put(player.getUniqueId(), trail);
            Message.normal(player, "Trail set to " + ChatColor.RED + args[0].toLowerCase().replace("_", " ") + (durability != 0 ? ":" + durability : "") + (trail.getStyle() != TrailStyle.NORMAL ? " " + trail.getStyle().toString().toLowerCase() : "") + ChatColor.GOLD + ".");
        }
        return true;
    }

    private void processArgument(TrailMeta trail, String[] args) {
        for (String arg : args) {
            try {
                trail.changeStyle(TrailStyle.valueOf(arg.toUpperCase()));
                continue;
            } catch (IllegalArgumentException e) {
                plugin.getLogger().log(Level.FINE, "Unable to parse argument as a style", e);
            }
            if (trail.getType().equals(TrailType.BLOCK)) {
                try {
                    short durability = Short.parseShort(arg);
                    ItemStack i = (trail.getData()).toItemStack(0);
                    i.setDurability(durability);
                    trail.setData(i.getData());
                } catch (NumberFormatException e) {
                    plugin.getLogger().log(Level.FINE, "Unable to parse argument as a short", e);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (PLAYER_TRAIL.containsKey(player.getUniqueId())) {
            TrailMeta trail = PLAYER_TRAIL.get(player.getUniqueId());
            if (trail.getType().equals(TrailType.EFFECT)) {
                for (int i = 0; i < trail.getParticleCount(); ++i)
                    player.getWorld().playEffect(player.getLocation().add(trail.getShiftX(), trail.getShiftY(), trail.getShiftZ()), (Effect) trail.getTrail(), trail.getExtraData());
            } else if (trail.getType().equals(TrailType.PARTICLE)) {
                player.getWorld().spawnParticle((Particle) trail.getTrail(), player.getLocation().add(trail.getShiftX(), trail.getShiftY(), trail.getShiftZ()), trail.getParticleCount(), trail.getOffsetX(), trail.getOffsetY(), trail.getOffsetZ(), trail.getExtraData(), null);
            } else if (trail.getType().equals(TrailType.BLOCK)) {
                player.getWorld().spawnParticle((Particle) trail.getTrail(), player.getLocation().add(trail.getShiftX(), trail.getShiftY(), trail.getShiftZ()), trail.getParticleCount(), trail.getOffsetX(), trail.getOffsetY(), trail.getOffsetZ(), trail.getExtraData(), (MaterialData) trail.getData());
            } else if (trail.getType().equals(TrailType.ITEM)) {
                player.getWorld().spawnParticle((Particle) trail.getTrail(), player.getLocation().add(trail.getShiftX(), trail.getShiftY(), trail.getShiftZ()), trail.getParticleCount(), trail.getOffsetX(), trail.getOffsetY(), trail.getOffsetZ(), trail.getExtraData(), (ItemStack) trail.getData().toItemStack());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (PLAYER_TRAIL.containsKey(player.getUniqueId()))
            PLAYER_TRAIL.remove(player.getUniqueId());
    }

}
