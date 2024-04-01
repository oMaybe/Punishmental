package net.ruxor.punishments.punishment;

public enum PunishmentType {

    WARN("warned", "Warns"),
    MUTE("muted", "Mutes"),
    KICK("kicked", "Kicks"),
    BAN("banned", "Bans"),
    BLACKLIST("blacklisted", "Blacklists"); // ip ig

    private String pastTense;
    private String guiName;

    PunishmentType(String pastTense, String guiName) {
        this.pastTense = pastTense;
        this.guiName = guiName;
    }

    public String getGuiName() {
        return guiName;
    }

    public String getPastTense() {
        return pastTense;
    }
}
