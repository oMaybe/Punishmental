package net.ruxor.punishments.punishment;

import lombok.Getter;
import lombok.Setter;
import net.ruxor.punishments.util.CC;
import net.ruxor.punishments.util.TimeUtils;
import org.bukkit.Bukkit;

import java.util.UUID;

@Getter
@Setter
public class Punishment {

    private UUID punishID;
    private UUID playerID;
    private String playerName;
    private PunishmentType type;
    private long timePunished;
    private long expiry;
    private String reason;
    private String staffName;
    private String ip;

    private String removeReason;
    private String removeStaffName;
    private long removeTime;

    public Punishment(UUID id, UUID playerID, String playerName, String ip, PunishmentType type, long timePunished, long expiry, String reason, String staffName) {
        this.punishID = id; // MAKE THIS SEARCHABLE FROM /LOGS GUI TO BE EASY FOR STAFF MEMBERS TO FIND
        this.playerID = playerID;
        this.playerName = playerName;
        this.ip = ip;
        this.type = type;
        this.timePunished = timePunished;
        this.expiry = expiry;
        this.reason = reason;
        this.staffName = staffName;
    }

    public boolean hasExpired(){
        return expiry != -1 && System.currentTimeMillis() > (timePunished + expiry);
    }

    public boolean isActive(){
        return !hasExpired() && !isRemoved();
    }

    public String getFormattedDifference(){
        if (expiry == -1) return "never";
        return TimeUtils.formatTimeMillis((getTimePunished() + getExpiry()) - System.currentTimeMillis());
    }

    public boolean isRemoved() {
        return removeTime > 0 && removeReason != null && removeStaffName != null;
    }

    @Override
    public String toString() {
        return "Punishment{" +
                "punishID=" + punishID +
                ", playerID=" + playerID +
                ", playerName='" + playerName + '\'' +
                ", ip='" + ip + "'" +
                ", type=" + type +
                ", timePunished=" + timePunished +
                ", duration=" + expiry +
                ", reason='" + reason + '\'' +
                ", staffName='" + staffName + '\'' +
                '}';
    }

    public String getSeperator() {
        int maxLength = 0;

        // Compare the length of each variable's value
        String punishIDString = punishID.toString();
        maxLength = Math.max(maxLength, punishIDString.length());

        String playerIDString = playerID.toString();
        maxLength = Math.max(maxLength, playerIDString.length());

        maxLength = Math.max(maxLength, playerName.length());

        // Convert long variables to String representations for comparison
        maxLength = Math.max(maxLength, String.valueOf(timePunished).length());
        maxLength = Math.max(maxLength, String.valueOf(expiry).length());

        maxLength = Math.max(maxLength, reason.length());
        maxLength = Math.max(maxLength, staffName.length());

        maxLength = Math.max(maxLength, removeTime != -1 ? String.valueOf(removeTime).length() : 0);
        maxLength = Math.max(maxLength, removeReason != null ? removeReason.length() : 0);
        maxLength = Math.max(maxLength, removeStaffName != null ? removeStaffName.length() : 0);

        StringBuilder separator = new StringBuilder("&7&l&m");
        for (int i = 0; i < maxLength; i++) {
            separator.append("-");
        }

        return CC.translate(separator.toString());
    }
}
