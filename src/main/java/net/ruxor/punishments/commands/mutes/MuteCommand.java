package net.ruxor.punishments.commands.mutes;

import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.commands.CommandHelper;
import net.ruxor.punishments.commands.ConsoleCommand;
import net.ruxor.punishments.data.Profile;
import net.ruxor.punishments.punishment.Punishment;
import net.ruxor.punishments.punishment.PunishmentType;
import net.ruxor.punishments.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MuteCommand extends ConsoleCommand {
    public MuteCommand() {
        super("mute", "punish.mute");
        setUsage("Usage: /mute <player> [time] [reason] [-s]");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(getUsage());
            return;
        }

        String playerName = args[0];
        UUID IDFetcher = UUIDFetcher.getUUID(playerName);
        if (IDFetcher == null){ // java one
            IDFetcher = Bukkit.getOfflinePlayer(playerName).getUniqueId();
            if (IDFetcher == null){
                sender.sendMessage(ChatColor.RED + "Could not find a player with that name!");
                return;
            }
        }

        Profile targetProfile;
        Player target = Bukkit.getPlayer(playerName);
        if (target != null && target.isOnline()){
            targetProfile = Punishmental.getInstance().getProfileHandler().getProfile(target.getUniqueId()).get();
        }else{
            targetProfile = Punishmental.getInstance().getProfileHandler().createProfile(IDFetcher, playerName);
        }

        if (targetProfile == null) {
            sender.sendMessage(ChatColor.RED + "Could not create a profile for that player and therefore does not exist!");
            return;
        }

        if (targetProfile.isMuted()){
            sender.sendMessage(ChatColor.RED + "That player is already muted!");
            return;
        }

        boolean silent;
        String reason;
        long time;

        if (args.length < 2) {
            silent = false;
            time = -1L;
            reason = "No reason specified.";
        }else{
            String builtArgs = StringUtil.buildString(args, 1).trim();

            time = TimeUtils.parseTime(args[1]);

            if (time != -1) {
                builtArgs = builtArgs.substring(args[1].length());
            }

            silent = builtArgs.endsWith("-s");

            if (silent) {
                if (builtArgs.equals("-s")) {
                    reason = "No reason specified.";
                } else {
                    reason = builtArgs.substring(0, builtArgs.length() - 2).trim();
                }
            } else {
                reason = builtArgs;
            }
        }

        if (reason == null || reason.isEmpty()){
            reason = "No reason specified.";
        }

        String finalReason = reason;
        CompletableFuture.runAsync(() -> {
            Punishment punishment = new Punishment(UUID.randomUUID(), targetProfile.getPlayerID(), playerName, targetProfile.getLastIP(), PunishmentType.MUTE, System.currentTimeMillis(), time, finalReason, sender.getName());
            Punishmental.getInstance().getDatabaseManager().getDatabase().addPunishment(punishment);

            targetProfile.load();

            if (target != null && target.isOnline()){
                String message = Messages.MUTE_MESSAGE;

                message = message.replace("%player%", target.getName())
                        .replace("%reason%", finalReason)
                        .replace("%time%", TimeUtils.formatTimeMillis(time));

                target.sendMessage(message);
            }

            if (!silent) {
                Bukkit.broadcastMessage(CC.translate(Messages.PUBLIC_MUTE_MESSAGE
                        .replace("%player%", playerName)
                        .replace("%reason%", finalReason)
                        .replace("%time%", time == -1L ? "ever" : TimeUtils.formatTimeMillis(time))
                        .replace("%staff%", sender.getName())));
                // maybe add bungee support?
            }else{
                for (Player staff : Bukkit.getOnlinePlayers()){
                    if (staff.hasPermission((String) FileUtils.getOrDefaultConfig("permission.punish_silent"))){
                        staff.sendMessage(CC.translate(Messages.SILENT_MUTE_MESSAGE
                                .replace("%player%", playerName)
                                .replace("%reason%", finalReason)
                                .replace("%time%", time == -1L ? "ever" : TimeUtils.formatTimeMillis(time))
                                .replace("%staff%", sender.getName())));
                    }
                }
            }
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return CommandHelper.player(args[0]);
        }

        return new ArrayList<>();
    }
}
