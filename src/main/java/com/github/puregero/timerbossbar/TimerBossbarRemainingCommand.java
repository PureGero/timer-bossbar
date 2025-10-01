package com.github.puregero.timerbossbar;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TimerBossbarRemainingCommand implements CommandExecutor {

    private final TimerBossbarPlugin plugin;

    public TimerBossbarRemainingCommand(TimerBossbarPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("timeremaining").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(Component.text("Time remaining: " + TimeBossbarUtil.formatDuration(this.plugin.timeRemaining())));
        return true;
    }
}
