package org.popcraft.popcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.util.*;

public class CommandPiggyback extends PopCraftCommand {

    private Map<UUID, Boolean> rideable = new HashMap<>();

    public CommandPiggyback() {
        super("piggyback");
    }

    @Override
    public Result execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Result.UNSUPPORTED_SENDER;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            boolean ridingEnabled = !rideable.getOrDefault(player.getUniqueId(), false);
            rideable.put(player.getUniqueId(), ridingEnabled);
            player.sendMessage(plugin.getMessage("piggybackToggle", ridingEnabled ? "enabled" : "disabled"));
        } else {
            return Result.INCORRECT_USAGE;
        }
        return Result.SUCCESS;
    }

    public boolean isRideable(Entity entity) {
        if (entity instanceof Player) {
            return rideable.getOrDefault(entity.getUniqueId(), false);
        } else {
            return instanceOfAny(entity,
                    Boat.class, Bat.class, Cat.class, Chicken.class, Cod.class, Cow.class, Donkey.class, Fox.class,
                    Horse.class, MushroomCow.class, Mule.class, Ocelot.class, Parrot.class, Pig.class,
                    PufferFish.class, Rabbit.class, Salmon.class, Sheep.class, SkeletonHorse.class,
                    Snowman.class, Squid.class, TropicalFish.class, Turtle.class, Villager.class,
                    WanderingTrader.class, Bee.class, Dolphin.class, IronGolem.class, Llama.class,
                    Panda.class, PolarBear.class, Wolf.class, Goat.class, Axolotl.class, GlowSquid.class,
                    Allay.class, Frog.class, Tadpole.class, Camel.class, Sniffer.class);
        }
    }

    private boolean instanceOfAny(Object object, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isInstance(object)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

}
