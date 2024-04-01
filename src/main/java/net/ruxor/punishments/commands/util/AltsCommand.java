package net.ruxor.punishments.commands.util;

import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.commands.ConsoleCommand;
import net.ruxor.punishments.data.Profile;
import net.ruxor.punishments.punishment.PunishmentType;
import net.ruxor.punishments.util.CC;
import net.ruxor.punishments.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class AltsCommand extends ConsoleCommand {
    public AltsCommand() {
        super("alts", "punish.alts");
        setUsage("Usage: /alts <player>");
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

        Player target = Bukkit.getPlayer(IDFetcher);
        Profile targetProfile;
        if (target != null && target.isOnline()) {
            targetProfile = Punishmental.getInstance().getProfileHandler().getProfile(IDFetcher).get();
        } else {
            targetProfile = Punishmental.getInstance().getProfileHandler().createProfile(IDFetcher, playerName);
        }

        if (targetProfile == null) {
            sender.sendMessage(ChatColor.RED + "Could not create a profile for that player and therefore does not exist!");
            return;
        }

        if (targetProfile.getIps().size() < 2) { //
            sender.sendMessage(ChatColor.RED + "That player does not have any alts!");
            return;
        }

        StringBuilder bottomMessage = new StringBuilder();
        for (String ip : targetProfile.getIps()){
            Profile profile = Punishmental.getInstance().getProfileHandler().createProfile(IDFetcher, playerName);
            if (profile == null){
                Bukkit.getConsoleSender().sendMessage(CC.Red + "Could not create a profile for " + playerName + " with the ip " + ip);
                continue;
            }

            if (Punishmental.getInstance().getDatabaseManager().getDatabase().is(ip, PunishmentType.BLACKLIST)){
                bottomMessage.append(CC.Dark_Red + playerName + ", ");
            }else if (Punishmental.getInstance().getDatabaseManager().getDatabase().is(ip, PunishmentType.BAN)){
                bottomMessage.append(CC.Red + playerName + ", ");
            }

            if (target != null && target.isOnline()) {
                bottomMessage.append(CC.Green + playerName + ", ");
            } else {
                bottomMessage.append(CC.Gray + playerName + ", ");
            }
        }

        bottomMessage.deleteCharAt(bottomMessage.length() - 2);

        sender.sendMessage(CC.Gray + "Scanning the player " + CC.White + playerName + CC.Gray + " for alts.");
        sender.sendMessage(CC.translate("&f[&aOnline&f] &f[&7Offline&f] &f[&cBanned&f] &f[&4Blacklisted&f]"));
        sender.sendMessage(bottomMessage.toString());
    }
}
