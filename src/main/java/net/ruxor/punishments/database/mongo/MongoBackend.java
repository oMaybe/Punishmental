package net.ruxor.punishments.database.mongo;

import net.ruxor.punishments.database.StorageType;
import net.ruxor.punishments.punishment.Punishment;
import net.ruxor.punishments.punishment.PunishmentType;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class MongoBackend implements StorageType {

    public MongoBackend(){
        init();
    }

    @Override
    public boolean connected() {
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public boolean addPunishment(Punishment punishment) {
        return false;
    }

    @Override
    public boolean removePunishment(UUID punishmentID) {
        return false;
    }

    @Override
    public void addToHistory(Punishment punishment) {

    }

    @Override
    public Punishment getPunishment(UUID punishmentID) {
        return null;
    }

    @Override
    public List<Punishment> getPunishments(UUID playerID) {
        return null;
    }

    @Override
    public List<Punishment> getPunishmentsByType(UUID playerID, PunishmentType type) {
        return null;
    }

    @Override
    public List<Punishment> getPunishmentsByTypeFromHistory(UUID playerID, PunishmentType type) {
        return null;
    }

    @Override
    public UUID getUUIDFromName(String name) {
        return null;
    }

    @Override
    public String getNameFromUUID(UUID uuid) {
        return null;
    }

    @Override
    public Collection<String> getIPs(UUID playerID) {
        return null;
    }

    @Override
    public void insertIP(UUID playerID, String ipAddress) {

    }

    @Override
    public boolean is(String ip, PunishmentType type) {
        return false;
    }

    @Override
    public void removeFromHistory(UUID punishmentID) {

    }
}
