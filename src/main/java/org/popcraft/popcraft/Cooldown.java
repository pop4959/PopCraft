package org.popcraft.popcraft;

import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Implements a cool down via HashMap, with some convenience methods for managing them.
 */
public class Cooldown extends HashMap<String, Long> {

    private long cooldown;

    private Cooldown() {
    }

    public Cooldown(int initialCapacity, float loadFactor, long cooldown) {
        super(initialCapacity, loadFactor);
        this.cooldown = cooldown;
    }

    public Cooldown(int initialCapacity, long cooldown) {
        super(initialCapacity);
        this.cooldown = cooldown;
    }

    public Cooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public Cooldown(Map<? extends String, ? extends Long> m, long cooldown) {
        super(m);
        this.cooldown = cooldown;
    }

    /**
     * Set the cool down
     *
     * @param key
     */
    public void set(String key) {
        this.put(key, System.currentTimeMillis());
    }

    public void set(Entity entity) {
        this.set(entity.getUniqueId().toString());
    }

    /**
     * Checks if the cool down is active
     *
     * @param key
     * @return whether a cool down is currently active
     */
    public boolean isActive(String key) {
        return this.getTimeRemaining(key) > 0;
    }

    public boolean isActive(Entity entity) {
        return this.isActive(entity.getUniqueId().toString());
    }

    /**
     * Checks if the cool down is finished
     *
     * @param key
     * @return whether the cool down finished
     */
    public boolean isFinished(String key) {
        return !this.isActive(key);
    }

    public boolean isFinished(Entity entity) {
        return this.isFinished(entity.getUniqueId().toString());
    }

    /**
     * Get the remaining time on the cool down
     *
     * @param key
     * @return remaining time on the cool down, or zero if there is currently no cool down active
     */
    public long getTimeRemaining(String key) {
        if (this.containsKey(key)) {
            return Math.max(0, this.cooldown - (System.currentTimeMillis() - this.get(key)));
        }
        return 0;
    }

    public long getTimeRemaining(Entity entity) {
        return this.getTimeRemaining(entity.getUniqueId().toString());
    }

    /**
     * Get the remaining time on the cool down as a string. Displays days, hours, minutes, and seconds
     *
     * @param key
     * @return time remaining formatted; e.g. "4 minutes 23 seconds"
     */
    public String getFormattedTimeRemaining(String key) {
        StringBuilder output = new StringBuilder();
        long timeRemaining = this.getTimeRemaining(key);
        long days = TimeUnit.DAYS.convert(timeRemaining, TimeUnit.MILLISECONDS);
        if (days > 0) {
            output.append(days).append(" days");
            timeRemaining -= TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS);
        }
        long hours = TimeUnit.HOURS.convert(timeRemaining, TimeUnit.MILLISECONDS);
        if (hours > 0) {
            output.append(output.length() > 0 ? " " : "").append(hours).append(" hours");
            timeRemaining -= TimeUnit.MILLISECONDS.convert(hours, TimeUnit.HOURS);
        }
        long minutes = TimeUnit.MINUTES.convert(timeRemaining, TimeUnit.MILLISECONDS);
        if (minutes > 0) {
            output.append(output.length() > 0 ? " " : "").append(minutes).append(" minutes");
            timeRemaining -= TimeUnit.MILLISECONDS.convert(minutes, TimeUnit.MINUTES);
        }
        long seconds = TimeUnit.SECONDS.convert(timeRemaining, TimeUnit.MILLISECONDS);
        if (seconds > 0) {
            output.append(output.length() > 0 ? " " : "").append(seconds).append(" seconds");
            timeRemaining -= TimeUnit.MILLISECONDS.convert(seconds, TimeUnit.SECONDS);
        }
        if (output.length() == 0) {
            output.append("moment");
        }
        return output.toString();
    }

    public String getFormattedTimeRemaining(Entity entity) {
        return this.getFormattedTimeRemaining(entity.getUniqueId().toString());
    }

}
