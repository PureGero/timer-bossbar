package com.github.puregero.timerbossbar;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

public class TimerBossbarPlugin extends JavaPlugin {

    private Instant timerStartTime = null;
    private Instant timerEndTime = null;
    private Instant timerPausedAt = null;

    @Override
    public void onEnable() {
        new TimerBossbarCommand(this);
        new TimerBossbarRemainingCommand(this);
        new TimerBossbarUpdater(this);
    }

    @Nullable
    public Instant timerStartTime() {
        return timerStartTime;
    }

    @Nullable
    public Instant timerEndTime() {
        return timerEndTime;
    }

    @Nullable
    public Instant timerPausedAt() {
        return timerPausedAt;
    }

    public void timer(@Nullable Instant startTime, @Nullable Instant endTime) {
        this.timer(startTime, endTime, null);
    }

    public void timer(@Nullable Instant startTime, @Nullable Instant endTime, @Nullable Instant pausedAt) {
        this.timerStartTime = startTime;
        this.timerEndTime = endTime;
        this.timerPausedAt = pausedAt;
    }

    @Nullable
    public Duration totalTime() {
        if (this.timerEndTime == null || this.timerStartTime == null) return null;
        return Duration.between(this.timerStartTime, this.timerEndTime);
    }

    @Nullable
    public Duration timeRemaining() {
        if (this.timerEndTime == null) return null;
        if (this.timerPausedAt != null) return Duration.between(this.timerPausedAt, this.timerEndTime);
        return Duration.between(Instant.now(), this.timerEndTime);
    }

}
