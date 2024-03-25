package org.popcraft.popcraft.contexts;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class PlaytimeContextCalculator implements ContextCalculator<Player> {
    private final String key;
    private final long requiredPlaytime;

    public PlaytimeContextCalculator(final String key, final long requiredPlaytime) {
        this.key = key;
        this.requiredPlaytime = requiredPlaytime;
    }

    @Override
    public void calculate(Player target, ContextConsumer contextConsumer) {
        if (target.getStatistic(Statistic.PLAY_ONE_MINUTE) > requiredPlaytime) {
            contextConsumer.accept(key, "true");
        }
    }

    @Override
    public ContextSet estimatePotentialContexts() {
        return ImmutableContextSet.of(key, "true");
    }
}