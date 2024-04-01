package net.ruxor.punishments.commands.ban;

import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.commands.ConsoleCommand;
import net.ruxor.punishments.data.Profile;
import net.ruxor.punishments.punishment.Punishment;
import net.ruxor.punishments.punishment.PunishmentType;
import net.ruxor.punishments.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BanCommand extends ConsoleCommand {

    public BanCommand() {
        super("ban", "punish.ban");
        setAliases("banish");
        setUsage(CC.Red + "Usage: /ban <player> [time] [reason] [-s]");
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

        if (targetProfile.isBanned()){
            sender.sendMessage(ChatColor.RED + "That player is already banned!");
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

        // ban Player 30d -s
        //

        CompletableFuture.runAsync(() -> {
            Punishment punishment = new Punishment(UUID.randomUUID(), targetProfile.getPlayerID(), playerName, targetProfile.getLastIP(), PunishmentType.BAN, System.currentTimeMillis(), time, reason, sender.getName());
            Punishmental.getInstance().getDatabaseManager().getDatabase().addPunishment(punishment);

            // maybe remove if bugs
            targetProfile.load();

            if (target != null && target.isOnline()) {
                Tasks.run(Punishmental.getInstance(), () -> {

                    StringBuilder reasonBuilder = new StringBuilder();
                    for (String s : Messages.BAN_MESSAGE) {
                        reasonBuilder.append(s).append("\n");
                    }

                    String newReason = reasonBuilder.toString();
                    target.kickPlayer(newReason
                            .replace("%banned_on%", TimeUtils.when(System.currentTimeMillis()))
                            .replace("%reason%", reason)
                            .replace("%time%", time == -1L ? "never" : TimeUtils.formatTimeMillis(time))
                            .replace("%staff%", sender.getName()));
                });
            }

            if (!silent) {
                Bukkit.broadcastMessage(CC.translate(Messages.PUBLIC_BAN_MESSAGE
                        .replace("%player%", playerName)
                        .replace("%reason%", reason)
                        .replace("%time%", time == -1L ? "ever" : TimeUtils.formatTimeMillis(time))
                        .replace("%staff%", sender.getName())));
                // maybe add bungee support?
            }else{
                for (Player staff : Bukkit.getOnlinePlayers()){
                    if (staff.hasPermission((String) FileUtils.getOrDefaultConfig("permission.punish_silent"))){
                        staff.sendMessage(CC.translate(Messages.SILENT_BAN_MESSAGE
                                .replace("%player%", playerName)
                                .replace("%reason%", reason)
                                .replace("%time%", time == -1L ? "ever" : TimeUtils.formatTimeMillis(time))
                                .replace("%staff%", sender.getName())));
                    }
                }
            }

            String message = CC.Gray + "You have banned " + CC.Red + playerName + CC.Gray + " for " + CC.Red + (time == -1L ? "ever" : TimeUtils.formatTimeMillis(time)) + CC.Gray + " for " + CC.Red + reason + ".";
            sender.sendMessage(message);
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String input = args[0].toLowerCase(); // Convert the input to lowercase for case-insensitive matching
            return Bukkit.getOnlinePlayers().stream()
                    .map(p -> p.getName())
                    .filter(name -> name.toLowerCase().startsWith(input)) // Filter names that start with the input
                    .collect(Collectors.toList());
        }

        // If there are more than one argument, or no player name is entered yet, return an empty list
        return new ArrayList<>();
    }
}
