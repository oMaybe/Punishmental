package net.ruxor.punishments.commands.warns;

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
import java.util.stream.Collectors;

public class WarnCommand extends ConsoleCommand {
    public WarnCommand() {
        super("warn", "punish.warn");
        setUsage(CC.Red + "Usage: /warn <player> [time] [reason]");
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

        String reason;
        long time;

        /*
        *
        * /warn oMaybe
        * /warn oMaybe 30d
        * /warn oMaybe 30d test
        * /warn oMaybe test
        *
        * */

        if (args.length < 2) {
            time = -1L;
            reason = "No reason specified.";
        }else{
            time = TimeUtils.parseTime(args[1]);

            if (time != -1) {
                reason = StringUtil.buildString(args, 2).trim();
            }else{
                time = -1L;
                reason = StringUtil.buildString(args, 1).trim();
            }
        }

        if (reason == null || reason.isEmpty()){
            reason = "No reason specified.";
        }

        // ban Player 30d -s
        //

        long finalTime = time;
        String finalReason = reason;
        CompletableFuture.runAsync(() -> {
            Punishment punishment = new Punishment(UUID.randomUUID(), targetProfile.getPlayerID(), playerName, targetProfile.getLastIP(), PunishmentType.WARN, System.currentTimeMillis(), finalTime, finalReason, sender.getName());
            Punishmental.getInstance().getDatabaseManager().getDatabase().addPunishment(punishment);

            // maybe remove if bugs
            targetProfile.load();

            if (target != null && target.isOnline()) {
                Tasks.run(Punishmental.getInstance(), () -> {

                    target.sendMessage(Messages.WARN_MESSAGE
                            .replace("%reason%", finalReason)
                            .replace("%time%", finalTime == -1L ? "never" : TimeUtils.formatTimeMillis(finalTime)));
                });
            }else{
                // maybe add it to a toSend system to send it when the player logs in
            }

            for (Player staff : Bukkit.getOnlinePlayers()){
                if (staff.hasPermission((String) FileUtils.getOrDefaultConfig("permission.punish_silent"))){
                    staff.sendMessage(CC.translate(Messages.SILENT_WARN_MESSAGE
                            .replace("%player%", playerName)
                            .replace("%reason%", finalReason)
                            .replace("%time%", finalTime == -1L ? "ever" : TimeUtils.formatTimeMillis(finalTime))
                            .replace("%staff%", sender.getName())));
                }
            }

            String message = CC.Gray + "You have warned " + CC.Red + playerName + CC.Gray + " for " + CC.Red + (finalTime == -1L ? "ever" : TimeUtils.formatTimeMillis(finalTime)) + CC.Gray + " for " + CC.Red + finalReason + ".";
            sender.sendMessage(message);
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
