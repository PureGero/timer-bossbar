package com.github.puregero.timerbossbar;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

public class TimerBossbarCommand implements CommandExecutor, TabCompleter {

    private final TimerBossbarPlugin plugin;

    public TimerBossbarCommand(TimerBossbarPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("timer").setExecutor(this);
        plugin.getCommand("timer").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return this.sendUsage(sender, label);
        }

        String subcommand = args[0].toLowerCase();

        if (subcommand.equalsIgnoreCase("start")) {
            try {
                Duration duration = this.parseDuration(args[1]);
                Instant now = Instant.now();
                this.plugin.timer(now, now.plus(duration));
                sender.sendMessage(Component.text("Timer started for " + TimeBossbarUtil.formatDuration(duration) + " minutes.").color(NamedTextColor.GREEN));
                return true;
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                sender.sendMessage(Component.text("Usage: /" + label + " " + subcommand + " <90m|30s>").color(NamedTextColor.RED));
                return false;
            }
        }

        if (subcommand.equalsIgnoreCase("cancel")) {
            if (this.plugin.timerStartTime() == null && this.plugin.timerEndTime() == null && this.plugin.timerPausedAt() == null) {
                sender.sendMessage(Component.text("No timer to cancel.").color(NamedTextColor.RED));
                return false;
            }
            this.plugin.timer(null, null);
            sender.sendMessage(Component.text("Timer cancelled.").color(NamedTextColor.GREEN));
            return true;
        }

        if (subcommand.equalsIgnoreCase("pause")) {
            if (this.plugin.timerEndTime() == null || this.plugin.timerPausedAt() != null) {
                sender.sendMessage(Component.text("No running timer to pause.").color(NamedTextColor.RED));
                return false;
            }

            this.plugin.timer(this.plugin.timerStartTime(), this.plugin.timerEndTime(), Instant.now());
            sender.sendMessage(Component.text("Timer paused.").color(NamedTextColor.GREEN));
            return true;
        }

        if (subcommand.equalsIgnoreCase("resume")) {
            if (this.plugin.timerEndTime() == null || this.plugin.timerPausedAt() == null) {
                sender.sendMessage(Component.text("No paused timer to resume.").color(NamedTextColor.RED));
                return false;
            }

            Instant now = Instant.now();
            Duration pausedDuration = Duration.between(this.plugin.timerPausedAt(), now);
            this.plugin.timer(this.plugin.timerStartTime().plus(pausedDuration), this.plugin.timerEndTime().plus(pausedDuration), null);
            sender.sendMessage(Component.text("Timer resumed.").color(NamedTextColor.GREEN));
            return true;
        }

        if (subcommand.equalsIgnoreCase("settotaltime")) {
            try {
                Duration duration = this.parseDuration(args[1]);
                Instant endTime = this.plugin.timerEndTime();
                if (endTime == null) {
                    sender.sendMessage(Component.text("No running timer to set total time for.").color(NamedTextColor.RED));
                    return false;
                }

                this.plugin.timer(endTime.minus(duration), endTime, this.plugin.timerPausedAt());
                sender.sendMessage(Component.text("Total time set to " + TimeBossbarUtil.formatDuration(duration) + " minutes.").color(NamedTextColor.GREEN));
                return true;
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                sender.sendMessage(Component.text("Usage: /" + label + " " + subcommand + " <90m|30s>").color(NamedTextColor.RED));
                return false;
            }
        }

        if (subcommand.equalsIgnoreCase("setremainingtime")) {
            try {
                Duration duration = this.parseDuration(args[1]);
                Instant startTime = this.plugin.timerStartTime();
                Instant endTime = this.plugin.timerEndTime();
                Duration originalRemainingTime = this.plugin.timeRemaining();
                if (startTime == null || endTime == null || originalRemainingTime == null) {
                    sender.sendMessage(Component.text("No running timer to set remaining time for.").color(NamedTextColor.RED));
                    return false;
                }

                Duration diff = duration.minus(originalRemainingTime);
                this.plugin.timer(startTime.plus(diff), endTime.plus(diff), this.plugin.timerPausedAt());
                sender.sendMessage(Component.text("Remaining time set to " + TimeBossbarUtil.formatDuration(duration) + " minutes.").color(NamedTextColor.GREEN));
                return true;
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                sender.sendMessage(Component.text("Usage: /" + label + " " + subcommand + " <90m|30s>").color(NamedTextColor.RED));
                return false;
            }
        }

        return this.sendUsage(sender, label);
    }

    private boolean sendUsage(CommandSender sender, String label) {
        sender.sendMessage(Component.text("Usage: /" + label + " <start|cancel|pause|resume|settotaltime|setremainingtime>").color(NamedTextColor.RED));
        return false;
    }

    private Duration parseDuration(String arg) {
        if (arg.endsWith("ms")) return Duration.ofMillis(Long.parseLong(arg.substring(0, arg.length() - 2)));
        if (arg.endsWith("s")) return Duration.ofSeconds(Long.parseLong(arg.substring(0, arg.length() - 1)));
        if (arg.endsWith("m")) return Duration.ofMinutes(Long.parseLong(arg.substring(0, arg.length() - 1)));
        if (arg.endsWith("h")) return Duration.ofHours(Long.parseLong(arg.substring(0, arg.length() - 1)));
        if (arg.endsWith("d")) return Duration.ofDays(Long.parseLong(arg.substring(0, arg.length() - 1)));
        return Duration.ofMinutes(Long.parseLong(arg));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("start", "cancel", "pause", "resume", "settotaltime", "setremainingtime").filter(s -> s.startsWith(args[0])).toList();
        }
        return List.of();
    }

}
