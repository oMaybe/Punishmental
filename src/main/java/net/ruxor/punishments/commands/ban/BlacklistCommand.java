package net.ruxor.punishments.commands.ban;

import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.commands.CommandHelper;
import net.ruxor.punishments.commands.ConsoleCommand;
import net.ruxor.punishments.data.Profile;
import net.ruxor.punishments.punishment.Punishment;
import net.ruxor.punishments.punishment.PunishmentType;
import net.ruxor.punishments.util.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BlacklistCommand extends ConsoleCommand {

    public BlacklistCommand() {
        super("blacklist", "punishmental.blacklist");
        setUsage("/blacklist <player> [reason] [-s]");
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

        if (targetProfile.isBlacklisted()){ // maybe check if banned too to see if conflicts..?
            sender.sendMessage(ChatColor.RED + "That player is already banned!");
            return;
        }

        boolean silent = StringUtils.contains(args[args.length - 1], "-s");
        String reason;

        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; ++i) {
            reasonBuilder.append(args[i]).append(" ");
        }

        if (!reasonBuilder.isEmpty()) {
            reason = reasonBuilder.toString().replace("-s", "").trim();
        } else {
            reason = "No Reason Specified.";
        }

        CompletableFuture.runAsync(() -> {
            Punishment punishment = new Punishment(UUID.randomUUID(), targetProfile.getPlayerID(), playerName, targetProfile.getLastIP(), PunishmentType.BLACKLIST, System.currentTimeMillis(), -1L, reason, sender.getName());
            Punishmental.getInstance().getDatabaseManager().getDatabase().addPunishment(punishment);

            // maybe remove if bugs
            targetProfile.load();

            String message = Messages.BLACKLIST_MESSAGE.stream().collect(Collectors.joining("\n"));

            message = message
                    .replace("%banned_on%", TimeUtils.when(System.currentTimeMillis()))
                    .replace("%reason%", reason)
                    .replace("%staff%", sender.getName());

            if (target != null && target.isOnline()){
                String finalMessage = message;
                Tasks.run(Punishmental.getInstance(), () -> {
                    target.kickPlayer(CC.translate(finalMessage));
                });
            }

            if (!silent) {
                Bukkit.broadcastMessage(CC.translate(Messages.PUBLIC_BLACKLIST_MESSAGE
                        .replace("%player%", playerName)
                        .replace("%reason%", reason)
                        .replace("%staff%", sender.getName())));
                // maybe add bungee support?
            }else{
                for (Player staff : Bukkit.getOnlinePlayers()){
                    if (staff.hasPermission((String) FileUtils.getOrDefaultConfig("permission.punish_silent"))){
                        staff.sendMessage(CC.translate(Messages.SILENT_BLACKLIST_MESSAGE
                                .replace("%player%", playerName)
                                .replace("%reason%", reason)
                                .replace("%staff%", sender.getName())));
                    }
                }
            }

        }).exceptionally(ex -> {
            sender.sendMessage(ChatColor.RED + "An error occurred while trying to blacklist the player.");
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return CommandHelper.player(args[0]);
        }

        // If there are more than one argument, or no player name is entered yet, return an empty list
        return new ArrayList<>();
    }
}
