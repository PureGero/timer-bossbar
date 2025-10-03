package com.github.puregero.timerbossbar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;

public class TimerBossbarUpdater implements Runnable, Listener {

    private final TimerBossbarPlugin plugin;
    private BossBar activeBossBar = null;
    private TimerStyle activeStyle = null;

    public TimerBossbarUpdater(TimerBossbarPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, t -> this.run(), 20, 20);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void run() {
        Duration remaining = this.plugin.timeRemaining();
        Duration totalDuration = this.plugin.totalTime();
        if (remaining == null || remaining.toSeconds() < -5 || totalDuration == null || totalDuration.isZero() || totalDuration.isNegative()) {
            if (this.activeBossBar != null) {
                this.activeBossBar.removeAll();
                this.activeBossBar = null;
            }
            return;
        }

        if (this.activeBossBar == null) {
            this.activeBossBar = this.plugin.getServer().createBossBar("", BarColor.GREEN, BarStyle.SEGMENTED_20);
            Bukkit.getOnlinePlayers().forEach(this.activeBossBar::addPlayer);
        }

        double progress = remaining.isNegative() ? 0 : Math.max(0, Math.min(1, (double) remaining.toMillis() / totalDuration.toMillis()));
        TimerStyle timerStyle = progress > 0.25 ? TimerStyle.GREEN : progress > 0.1 ? TimerStyle.YELLOW : TimerStyle.RED;

        if (this.activeStyle != timerStyle) {
            this.activeStyle = timerStyle;
            this.activeBossBar.setColor(timerStyle.barColor());
        }

        this.activeBossBar.setTitle(timerStyle.darkChatColor() + "Time Remaining: " + timerStyle.lightChatColor() + TimeBossbarUtil.formatDuration(remaining.isNegative() ? Duration.ZERO : remaining));
        this.activeBossBar.setProgress(progress);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (this.activeBossBar != null) {
            this.activeBossBar.addPlayer(event.getPlayer());
        }
    }

    private record TimerStyle(
            ChatColor darkChatColor,
            ChatColor lightChatColor,
            BarColor barColor
    ) {
        static final TimerStyle GREEN = new TimerStyle(ChatColor.DARK_GREEN, ChatColor.GREEN, BarColor.GREEN);
        static final TimerStyle YELLOW = new TimerStyle(ChatColor.GOLD, ChatColor.YELLOW, BarColor.YELLOW);
        static final TimerStyle RED = new TimerStyle(ChatColor.DARK_RED, ChatColor.RED, BarColor.RED);
    }

}
