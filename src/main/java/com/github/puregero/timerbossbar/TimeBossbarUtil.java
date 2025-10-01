package com.github.puregero.timerbossbar;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class TimeBossbarUtil {

    public static String formatDuration(@Nullable Duration duration) {
        if (duration == null) return "--:--";
        return "%02d:%02d".formatted(duration.toMinutes(), duration.toSecondsPart());
    }

}
