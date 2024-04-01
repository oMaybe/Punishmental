package net.ruxor.punishments.commands.ban;

import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.commands.CommandHelper;
import net.ruxor.punishments.commands.ConsoleCommand;
import net.ruxor.punishments.data.Profile;
import net.ruxor.punishments.punishment.Punishment;
import net.ruxor.punishments.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnbanCommand extends ConsoleCommand {

    public UnbanCommand() {
        super("unban", "punish.unban");
        setUsage(CC.Red + "Usage: /unban <player> [reason] [-s]");
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

        Profile targetProfile = Punishmental.getInstance().getProfileHandler().createProfile(IDFetcher, playerName);
        if (targetProfile == null) {
            sender.sendMessage(ChatColor.RED + "Could not create a profile for that player and therefore does not exist!");
            return;
        }

        if (!targetProfile.isBanned()){
            sender.sendMessage(ChatColor.RED + "That player is not banned!");
            return;
        }

        StringBuilder reasonBuilder = new StringBuilder();

        for (int i = 1; i < args.length; ++i) {
            reasonBuilder.append(args[i]).append(" ");
        }
        if (reasonBuilder.isEmpty()) reasonBuilder.append("No Reason");

        String reason = reasonBuilder.toString().trim();
        boolean silent =  reason.contains("-s");

        if (reason.contains("-s")) {
            reason = reason.replace("-s", "");
        }

        Punishment punishment = targetProfile.getActiveBan();
        if (punishment == null) {
            sender.sendMessage(ChatColor.RED + "This is a fail safe to prevent any issues. Although the player is banned, no active bans were found for this player. Please contact an administrator.");
            return;
        }

        punishment.setRemoveStaffName(sender.getName());
        punishment.setRemoveReason(reason);
        punishment.setRemoveTime(System.currentTimeMillis());

        if (silent){
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission((String) FileUtils.getOrDefaultConfig("punish.punish_silent"))) {
                    staff.sendMessage(Messages.SILENT_UNBAN_MESSAGE
                            .replace("%player%", targetProfile.getPlayerName())
                            .replace("%staff%", sender.getName())
                            .replace("%reason%", reason));
                }
            }
        }else{
            Bukkit.broadcastMessage(Messages.PUBLIC_UNBAN_MESSAGE
                    .replace("%player%", targetProfile.getPlayerName())
                    .replace("%staff%", sender.getName())
                    .replace("%reason%", reason));
        }

        Punishmental.getInstance().getDatabaseManager().getDatabase().removePunishment(punishment.getPunishID());
        Punishmental.getInstance().getDatabaseManager().getDatabase().addToHistory(punishment);

        targetProfile.load();

        Punishmental.getInstance().getProfileHandler().remove(targetProfile.getPlayerID());

        String message = CC.Gray + "You have unbanned " + CC.Red + playerName + CC.Gray + " for " + CC.Red + reason + ".";
        sender.sendMessage(message);
    }
}
