package net.ruxor.punishments.listeners;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.data.Profile;
import net.ruxor.punishments.punishment.Punishment;
import net.ruxor.punishments.punishment.PunishmentType;
import net.ruxor.punishments.util.CC;
import net.ruxor.punishments.util.FileUtils;
import net.ruxor.punishments.util.Messages;
import net.ruxor.punishments.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.stream.Collectors;

public class GenericListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFirstLogin(AsyncPlayerPreLoginEvent event){
        // check if banned here lol
        Profile profile = Punishmental.getInstance().getProfileHandler().createProfile(event.getUniqueId(), event.getName());
        if (profile == null){
            String failedProfile = Messages.INVALID_PROFILE;
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.translate(failedProfile));
            return;
        }

        if (System.currentTimeMillis() - Punishmental.getInstance().getLoadTime() < 8000){
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.translate(Messages.LOAD_MESSAGE));
            return;
        }

        String ip = event.getAddress().toString().replace("/", "");

        profile.insertIP(ip);
        profile.load();

        if (profile.isBlacklisted()){
            // check for alts cuz ip ban

            StringBuilder reason = new StringBuilder();
            for (String s : Messages.BLACKLIST_MESSAGE){
                reason.append(s).append("\n");
            }

            Punishment punishment = profile.getActiveBlacklist();
            String newReason = reason.toString();

            newReason = newReason.replace("%banned_on%", TimeUtils.when(System.currentTimeMillis()))
                    .replace("%reason%", punishment.getReason())
                    .replace("%staff%", punishment.getStaffName());

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, newReason);
            return;
        }

        boolean isAltBlacklisted = profile.isAltBlacklisted();
        if (isAltBlacklisted){
            boolean banPlayer = (boolean) FileUtils.getOrDefaultConfig("blacklist_alt");

            if (banPlayer) {
                String reason = Messages.ALT_BLACKLIST_MESSAGE.stream().collect(Collectors.joining("\n"));

                Punishment punishment = new Punishment(UUID.randomUUID(), event.getUniqueId(), event.getName(), ip, PunishmentType.BLACKLIST, System.currentTimeMillis(), -1L, "Ban Evasion", "Console");
                reason = reason.replace("%banned_on%", TimeUtils.when(System.currentTimeMillis()))
                        .replace("%reason%", punishment.getReason())
                        .replace("%staff%", punishment.getStaffName());

                Punishmental.getInstance().getDatabaseManager().getDatabase().addPunishment(punishment);

                for (Player player : Bukkit.getOnlinePlayers()){
                    if (player.hasPermission((String) FileUtils.getOrDefaultConfig("permission.punish_silent"))){
                        player.sendMessage(CC.translate(Messages.SILENT_ALT_EVASION.replace("%player%", event.getName())));
                    }
                }

                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, reason);
                return;
            }else{
                for (Player player : Bukkit.getOnlinePlayers()){
                    if (player.hasPermission((String) FileUtils.getOrDefaultConfig("permission.punish_silent"))){
                        TextComponent component = new TextComponent(CC.translate(Messages.SILENT_POSSIBLE_ALT_EVASION.replace("%player%", event.getName())));

                        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(CC.translate("&cClick to blacklist this player for ban evasion"))}));
                        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/blacklist " + event.getName() + " Ban Evasion -s"));

                        player.spigot().sendMessage(component);
                    }
                }
            }
        }

        if (profile.isBanned()){
            StringBuilder reason = new StringBuilder();
            for (String s : Messages.BAN_MESSAGE){
                reason.append(s).append("\n");
            }

            Punishment punishment = profile.getActiveBan();

            String newReason = reason.toString();
            newReason = newReason.replace("%banned_on%", TimeUtils.when(System.currentTimeMillis()))
                    .replace("%reason%", punishment.getReason())
                    .replace("%time%", punishment.getExpiry() == -1L ? "never" : punishment.getFormattedDifference())
                    .replace("%staff%", punishment.getStaffName());

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, newReason);
            return;
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Punishmental.getInstance().getProfileHandler().remove(event.getPlayer().getUniqueId());
    }
}
