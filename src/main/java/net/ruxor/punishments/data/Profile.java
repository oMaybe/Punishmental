package net.ruxor.punishments.data;

import lombok.Getter;
import lombok.Setter;
import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.punishment.Punishment;
import net.ruxor.punishments.punishment.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.BooleanUtils.negate;

public class Profile {

    @Getter private UUID playerID;
    @Getter private String playerName;

    // alts
    private List<Punishment> punishments;
    private List<Punishment> history;

    @Getter @Setter private String currentIP;
    @Getter private List<String> ips;

    public Profile(UUID uuid, String playerName) {
        this.playerID = uuid;
        this.playerName = playerName;
        this.punishments = new ArrayList<>();
        this.history = new ArrayList<>();
        this.ips = new ArrayList<>();
        load();
    }

    public void load(){
        if (playerID == null) return;
        // load alts
        // load other data like ip & maybe version & other stuff
        this.punishments.addAll(Punishmental.getInstance().getDatabaseManager().getDatabase().getPunishments(playerID));
        for (int i = 0; i < this.punishments.size(); i++) {
            Punishment punishment = punishments.get(i);
            if (punishment.hasExpired()){
                punishment.setRemoveStaffName("Console");
                punishment.setRemoveReason("Expired");
                punishment.setExpiry(System.currentTimeMillis());
                history.add(punishment);

                Punishmental.getInstance().getDatabaseManager().getDatabase().removePunishment(punishment.getPunishID());

                punishments.remove(punishment);
                // update the punishment in the database
            }
        }

        // load ips
        ips.addAll(Punishmental.getInstance().getDatabaseManager().getDatabase().getIPs(playerID));
    }

    public boolean isBanned() {
        return punishments.stream().filter(punishment -> punishment.getType().equals(PunishmentType.BAN)).anyMatch(Punishment::isActive);
    }

    public boolean isMuted() {
        return punishments.stream().filter(punishment -> punishment.getType().equals(PunishmentType.MUTE)).anyMatch(Punishment::isActive);
    }

    public boolean isWarned() {
        return punishments.stream().filter(punishment -> punishment.getType().equals(PunishmentType.WARN)).anyMatch(Punishment::isActive);
    }

    public boolean isBlacklisted() {
        return punishments.stream().anyMatch(punishment -> punishment.getType().equals(PunishmentType.BLACKLIST));
    }

    public Punishment getActiveBan() {
        return this.punishments.stream().filter(punishment -> punishment.isActive() && punishment.getType() == PunishmentType.BAN).findFirst().orElse(null);
    }

    public Punishment getActiveMute() {
        return this.punishments.stream().filter(punishment -> punishment.isActive() && punishment.getType() == PunishmentType.MUTE).findFirst().orElse(null);
    }

    public Punishment getActiveWarn() {
        return this.punishments.stream().filter(punishment -> punishment.isActive() && punishment.getType() == PunishmentType.WARN).findFirst().orElse(null);
    }

    public Punishment getActiveBlacklist() {
        return this.punishments.stream().filter(punishment -> punishment.isActive() && punishment.getType() == PunishmentType.BLACKLIST).findFirst().orElse(null);
    }

    public List<Punishment> getPunishments(PunishmentType type) {
        return this.punishments.stream().filter(punishment -> punishment.getType() == type).collect(Collectors.toList());
    }

    public void insertIP(String replace) {
        this.currentIP = replace;
        if (!ips.contains(replace)){
            ips.add(replace);
            Punishmental.getInstance().getDatabaseManager().getDatabase().insertIP(playerID, replace);
        }
    }

    public String getLastIP() {
        // maybe get from player data if not found in list
        return ips.get(ips.size() - 1);
    }

    public boolean isAltBlacklisted() {
        return ips.stream().anyMatch(ip -> Punishmental.getInstance().getDatabaseManager().getDatabase().is(ip, PunishmentType.BLACKLIST));
    }
}
