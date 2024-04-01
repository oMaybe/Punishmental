package net.ruxor.punishments.listeners;

import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.data.Profile;
import net.ruxor.punishments.punishment.Punishment;
import net.ruxor.punishments.util.FileUtils;
import net.ruxor.punishments.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) // maybe remove ignoreCancelled
    public void onChat(AsyncPlayerChatEvent event){
        // do something
        Player player = event.getPlayer();
        Optional<Profile> profile = Punishmental.getInstance().getProfileHandler().getProfile(player.getUniqueId());
        if (!profile.isPresent()) {
            Bukkit.getLogger().severe("Could not find a profile for " + player.getName() + " (" + player.getUniqueId() + ")");
            return;
        }

        if (!profile.get().isMuted()){
            return;
        }

        Punishment mute = profile.get().getActiveMute();
        if (mute == null){
            Bukkit.getLogger().severe("Could not find an active mute for " + player.getName() + " (" + player.getUniqueId() + ")");
            return;
        }

        if (!mute.hasExpired()){
            if (mute.getExpiry() == -1L){
                player.sendMessage(Messages.MUTE_MESSAGE.replace("%reason%", mute.getReason()).replace("%time%", "forever").replace("%staff%", mute.getStaffName()));
                event.setCancelled(true);
                return;
            }

            String message = Messages.MUTE_MESSAGE;

            message = message
                    .replace("%reason%", mute.getReason())
                    .replace("%time%", mute.getExpiry() == -1L ? "never" : mute.getFormattedDifference())
                    .replace("%staff%", mute.getStaffName());

            player.sendMessage(message);
            event.setCancelled(true);
            return;
        }

        // maybe implement filter by default or something?

    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        // blacklist certain commands? (config)
        String[] message = event.getMessage().split(" ");
        String command = message[0];

        Player player = event.getPlayer();
        Optional<Profile> profile = Punishmental.getInstance().getProfileHandler().getProfile(player.getUniqueId());
        if (!profile.isPresent()) {
            Bukkit.getLogger().severe("Could not find a profile for " + player.getName() + " (" + player.getUniqueId() + ")");
            return;
        }

        if (!profile.get().isMuted()){
            return;
        }

        Punishment mute = profile.get().getActiveMute();
        if (mute == null){
            Bukkit.getLogger().severe("Could not find an active mute for " + player.getName() + " (" + player.getUniqueId() + ")");
            return;
        }

        if (!mute.hasExpired()){
            List<String> blacklistedCommands = (List<String>) FileUtils.getOrDefaultConfig("blacklisted-commands");
            if (blacklistedCommands.contains(command)){
                if (mute.getExpiry() == -1L){
                    player.sendMessage(Messages.MUTE_MESSAGE.replace("%reason%", mute.getReason()).replace("%time%", "forever").replace("%staff%", mute.getStaffName()));
                    event.setCancelled(true);
                    return;
                }

                player.sendMessage(Messages.MUTE_MESSAGE
                        .replace("%reason%", mute.getReason())
                        .replace("%time%", mute.getExpiry() == -1L ? "never" : mute.getFormattedDifference())
                        .replace("%staff%", mute.getStaffName()));
                event.setCancelled(true);
                return;
            }
        }
    }
}
