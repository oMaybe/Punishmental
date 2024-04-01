package net.ruxor.punishments.database;

import net.ruxor.punishments.punishment.Punishment;
import net.ruxor.punishments.punishment.PunishmentType;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface StorageType {

    boolean connected();
    void init();

    void shutdown();

    boolean addPunishment(Punishment punishment);

    boolean removePunishment(UUID punishmentID);

    void addToHistory(Punishment punishment);

    Punishment getPunishment(UUID punishmentID);

    List<Punishment> getPunishments(UUID playerID);

    List<Punishment> getPunishmentsByType(UUID playerID, PunishmentType type);

    List<Punishment> getPunishmentsByTypeFromHistory(UUID playerID, PunishmentType type);

    UUID getUUIDFromName(String name);

    String getNameFromUUID(UUID uuid);

    Collection<String> getIPs(UUID playerID);

    void insertIP(UUID playerID, String ipAddress);

    boolean is(String ip, PunishmentType type);
}
