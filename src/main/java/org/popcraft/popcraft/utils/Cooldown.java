package org.popcraft.popcraft.utils;

import org.bukkit.entity.Player;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.lang.String.format;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;

public class Cooldown {

    private static final PrettyTime FORMAT = new PrettyTime();
    private final Map<UUID, Date> cooldownMap;
    private final long duration;

    public Cooldown(final long time) {
        this(time, TimeUnit.MILLISECONDS);
    }

    public Cooldown(final long time, TimeUnit unit) {
        this.cooldownMap = Collections.synchronizedMap(new HashMap<>());
        this.duration = unit.toMillis(time);
    }

    public boolean check(final UUID uuid) {
        final Date nextTimeToActivate = this.cooldownMap.get(uuid);
        return nextTimeToActivate == null || nextTimeToActivate.after(new Date());
    }

    public boolean use(final UUID uuid) {
        if (this.check(uuid)) {
            this.cooldownMap.put(uuid, new Date(System.currentTimeMillis() + this.duration));
            return true;
        }
        return false;
    }

    public String getTimeRemaining(final UUID uuid) {
        if (this.check(uuid)) {
            return FORMAT.format(this.cooldownMap.get(uuid));
        }
        return "NaN";
    }

    public static Function<Player, Boolean> defaultCooldown(final long time) {
        return defaultCooldown(new Cooldown(time));
    }

    public static Function<Player, Boolean> defaultCooldown(final Cooldown cooldown) {
        return (player) -> {
            final UUID uuid = player.getUniqueId();
            if (cooldown.use(uuid)) {
                return true;
            }
            player.sendMessage(format(
                    "%sYou can't use this command again in %s%s%s.",
                    GOLD, RED, cooldown.getTimeRemaining(uuid), GOLD)
            );
            return false;
        };
    }

}
