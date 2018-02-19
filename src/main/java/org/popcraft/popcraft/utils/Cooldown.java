package org.popcraft.popcraft.utils;

import org.bukkit.entity.Player;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.lang.String.format;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;

public class Cooldown implements Function<Player, Boolean> {

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
            this.reset(uuid);
            return true;
        }
        return false;
    }

    public void reset(final UUID uuid) {
        this.cooldownMap.put(uuid, new Date(System.currentTimeMillis() + this.duration));
    }

    public String getTimeRemaining(final UUID uuid) {
        if (this.check(uuid)) {
            return FORMAT.format(this.cooldownMap.get(uuid));
        }
        return "NaN";
    }

    @Override
    public Boolean apply(Player player) {
        final UUID uuid = player.getUniqueId();
        if (this.use(uuid)) {
            return true;
        }

        player.sendMessage(format(
                "%sYou can't use this command again in %s%s%s.",
                GOLD, RED, this.getTimeRemaining(uuid), GOLD)
        );
        return false;
    }
}
