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
            this.activeBossBar = this.plugin.getServer().createBossBar("", BarColor.YELLOW, BarStyle.SEGMENTED_20);
            Bukkit.getOnlinePlayers().forEach(this.activeBossBar::addPlayer);
        }

        this.activeBossBar.setTitle(ChatColor.GOLD + "Time Remaining: " + ChatColor.YELLOW + TimeBossbarUtil.formatDuration(remaining.isNegative() ? Duration.ZERO : remaining));
        this.activeBossBar.setProgress(remaining.isNegative() ? 0 : Math.max(0, Math.min(1, (double) remaining.toMillis() / totalDuration.toMillis())));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (this.activeBossBar != null) {
            this.activeBossBar.addPlayer(event.getPlayer());
        }
    }

}
