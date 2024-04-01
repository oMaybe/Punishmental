package net.ruxor.punishments.database.mysql;

import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.database.QueryType;
import net.ruxor.punishments.database.StorageType;
import net.ruxor.punishments.punishment.Punishment;
import net.ruxor.punishments.punishment.PunishmentType;
import net.ruxor.punishments.util.FileUtils;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MySQLBackend implements StorageType {

    private Connection conn;

    public MySQLBackend(){
        init();
    }

    @Override
    public void init() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection((String) FileUtils.getOrDefaultConfig("mysql.url"));
                conn.setAutoCommit(true);
                Bukkit.getLogger().info("Connection to MySQL has been established.");
                CompletableFuture<?> creation = query(QueryType.POST, "CREATE TABLE IF NOT EXISTS punishments (punishid VARCHAR(36), " +
                        "playerid VARCHAR(36), playername VARCHAR(36), ip VARCHAR(100), type VARCHAR(255), reason VARCHAR(255), date VARCHAR(255), duration VARCHAR(255), " +
                        "staff VARCHAR(255));");

                creation.thenAccept(result -> {
                    if (result == null) {
                        Bukkit.getLogger().info("MYSQL - Successfully created table.");
                    } else {
                        Bukkit.getLogger().info("MYSQL - Failed to create table");
                    }
                }).exceptionally(ex -> {
                    Bukkit.getLogger().severe("MYSQL - An error occurred: " + ex.getMessage());
                    ex.printStackTrace();
                    return null;
                }).get();

                // create table for history of punishments, and maybe one for player data
                CompletableFuture<?> history = query(QueryType.POST, "CREATE TABLE IF NOT EXISTS history (punishid VARCHAR(36), " +
                        "playerid VARCHAR(36), playername VARCHAR(36), ip VARCHAR(100), type VARCHAR(255), reason VARCHAR(255), date VARCHAR(255), duration VARCHAR(255), " +
                        "staff VARCHAR(255), removedBy VARCHAR(36), removeReason VARCHAR(255), removeTime VARCHAR(255));");

                history.thenAccept(result -> {
                    if (result == null) {
                        Bukkit.getLogger().info("MYSQL - Successfully created history table.");
                    } else {
                        Bukkit.getLogger().info("MYSQL - Failed to create history table");
                    }
                }).exceptionally(ex -> {
                    Bukkit.getLogger().severe("MYSQL - An error occurred: " + ex.getMessage());
                    ex.printStackTrace();
                    return null;
                }).get();

                CompletableFuture<?> playerData = query(QueryType.POST, "CREATE TABLE IF NOT EXISTS playerdata (playerid VARCHAR(36), " +
                        "playername VARCHAR(36), ips VARCHAR(16000));");

                playerData.thenAccept(result -> {
                    if (result == null) {
                        Bukkit.getLogger().info("MYSQL - Successfully created playerdata table.");
                    } else {
                        Bukkit.getLogger().info("MYSQL - Failed to create playerdata table");
                    }
                }).exceptionally(ex -> {
                    Bukkit.getLogger().severe("MYSQL - An error occurred: " + ex.getMessage());
                    ex.printStackTrace();
                    return null;
                }).get();
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to load mysql: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean connected() {
        try {
            if (conn == null || conn.isClosed()) {
                return false;
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to check if mysql is connected: " + e.getMessage());
        }
        return true;
    }

    @Override
    public void shutdown() {
        if (connected()) {
            try {
                conn.close();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to close mysql connection: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean addPunishment(Punishment punishment) {
        CompletableFuture<?> alb = query(QueryType.POST, "INSERT INTO punishments (punishid, playerid, playername, ip, type, reason, date, duration, staff) VALUES ('" +
                punishment.getPunishID().toString() + "', '" +
                punishment.getPlayerID().toString() + "', '" +
                punishment.getPlayerName() + "', '" +
                punishment.getIp() + "', '" +
                punishment.getType().name() + "', '" +
                punishment.getReason() + "', '" +
                punishment.getTimePunished() + "', '" +
                punishment.getExpiry() + "', '" +
                punishment.getStaffName() + "');");

        try {
            alb.thenAccept(result -> {
                if (result == null) {
                    Bukkit.getLogger().info("MYSQL - Successfully added punishment.");
                } else {
                    Bukkit.getLogger().info("MYSQL - Failed to add punishment id=" + punishment.getPunishID());
                }
            }).exceptionally(ex -> {
                Bukkit.getLogger().severe("MYSQL - An error occurred: " + ex.getMessage());
                return null;
            }).get();
        }catch (Exception e){
            Bukkit.getLogger().severe("Failed to add punishment: " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean removePunishment(UUID punishmentID) {
        CompletableFuture<?> alb = query(QueryType.POST, "DELETE FROM punishments WHERE punishid='" + punishmentID.toString() + "';");

        try {
            alb.thenAccept(result -> {
                if (result == null) {
                    Bukkit.getLogger().info("MYSQL - Successfully removed punishment.");
                } else {
                    Bukkit.getLogger().info("MYSQL - Failed to remove punishment id=" + punishmentID);
                }
            }).exceptionally(ex -> {
                Bukkit.getLogger().severe("MYSQL - An error occurred: " + ex.getMessage());
                return null;
            }).get();
        }catch (Exception e){
            Bukkit.getLogger().severe("Failed to remove punishment: " + e.getMessage());
        }
        return true;
    }

    @Override
    public void addToHistory(Punishment punishment) {
        if (getPunishment(punishment.getPunishID()) != null) {
            removePunishment(punishment.getPunishID());
        }

        CompletableFuture<?> alb = query(QueryType.POST, "INSERT INTO history (punishid, playerid, playername, ip, type, reason, date, duration, staff, removedBy, removeReason, removeTime) VALUES ('" +
                punishment.getPunishID().toString() + "', '" +
                punishment.getPlayerID().toString() + "', '" +
                punishment.getPlayerName() + "', '" +
                punishment.getIp() + "', '" +
                punishment.getType().name() + "', '" +
                punishment.getReason() + "', '" +
                punishment.getTimePunished() + "', '" +
                punishment.getExpiry() + "', '" +
                punishment.getStaffName() + "', '" +
                punishment.getRemoveStaffName() + "', '" +
                punishment.getRemoveReason() + "', '" +
                punishment.getRemoveTime() + "');");

        try {
            alb.thenAccept(result -> {
                if (result == null) {
                    Bukkit.getLogger().info("MYSQL - Successfully added punishment to history.");
                } else {
                    Bukkit.getLogger().info("MYSQL - Failed to add punishment to history id=" + punishment.getPunishID());
                }
            }).exceptionally(ex -> {
                Bukkit.getLogger().severe("MYSQL - An error occurred: " + ex.getMessage());
                return null;
            }).get();
        }catch (Exception e){
            Bukkit.getLogger().severe("Failed to add punishment to history: " + e.getMessage());
        }
    }

    public Punishment getPunishment(UUID punishmentID){
        CompletableFuture<Object> results = query(QueryType.RS, "SELECT * FROM punishments WHERE punishid='" + punishmentID.toString() + "';");

        AtomicReference<Punishment> punishment = new AtomicReference<>();
        try {
            results.thenAccept(result -> {
                if (result == null) {
                    Bukkit.getLogger().info("MYSQL - No punishment found for punishid=" + punishmentID);
                } else {
                    try {
                        ResultSet rs = (ResultSet) result;
                        if (rs.next()) {
                            String punishid = rs.getString("punishid");
                            String playerid = rs.getString("playerid");
                            String playername = rs.getString("playername");
                            String ip = rs.getString("ip");
                            String type = rs.getString("type");
                            String reason = rs.getString("reason");
                            String date = rs.getString("date");
                            String duration = rs.getString("duration");
                            String staff = rs.getString("staff");
                            punishment.set(new Punishment(UUID.fromString(punishid), UUID.fromString(playerid), playername, ip, PunishmentType.valueOf(type), Long.parseLong(date), Long.parseLong(duration), reason, staff));
                        }
                    } catch (SQLException e) {
                        Bukkit.getLogger().severe("Failed to get punishment: " + e.getMessage());
                    }
                }
            }).exceptionally(ex -> {
                Bukkit.getLogger().severe("MYSQL - An error occurred: " + ex.getMessage());
                return null;
            }).get();
        }catch (Exception e){
            Bukkit.getLogger().severe("Failed to get punishment: " + e.getMessage());
        }
        return punishment.get();
    }

    @Override
    public List<Punishment> getPunishments(UUID playerID) {
        CompletableFuture<Object> results = query(QueryType.RS, "SELECT * FROM punishments WHERE playerid='" + playerID.toString() + "';");

        ArrayList<Punishment> punishments = new ArrayList<>();
        try {
            results.thenAccept(result -> {
                if (result == null) {
                    Bukkit.getLogger().info("MYSQL - No punishments found for playerid=" + playerID);
                } else {
                    try {

                        ResultSet rs = (ResultSet) result;
                        while (rs.next()) {
                            String punishid = rs.getString("punishid");
                            String playerid = rs.getString("playerid");
                            String playername = rs.getString("playername");
                            String ip = rs.getString("ip");
                            String type = rs.getString("type");
                            String reason = rs.getString("reason");
                            String date = rs.getString("date");
                            String duration = rs.getString("duration");
                            String staff = rs.getString("staff");
                            Punishment punishment = new Punishment(UUID.fromString(punishid), UUID.fromString(playerid), playername, ip, PunishmentType.valueOf(type), Long.parseLong(date), Long.parseLong(duration), reason, staff);
                            punishments.add(punishment);
                        }
                    } catch (SQLException e) {
                        Bukkit.getLogger().severe("Failed to get punishments: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).exceptionally(ex -> {
                Bukkit.getLogger().severe("MYSQL - An error occurred: " + ex.getMessage());
                return null;
            }).get();
        }catch (Exception e){
            Bukkit.getLogger().severe("Failed to get punishments: " + e.getMessage());
            e.printStackTrace();
        }

        return punishments;
    }

    @Override
    public List<Punishment> getPunishmentsByType(UUID playerID, PunishmentType type) {
        return getPunishments(playerID).stream().filter(punishment -> punishment.getType().equals(type)).toList();
    }

    @Override
    public List<Punishment> getPunishmentsByTypeFromHistory(UUID playerID, PunishmentType punishType) {
        CompletableFuture<Object> results = query(QueryType.RS, "SELECT * FROM history WHERE playerid='" + playerID.toString() + "' AND type='" + punishType.name() + "'");

        ArrayList<Punishment> punishments = new ArrayList<>();
        try {
            results.thenAccept(result -> {
                if (result == null) {
                    Bukkit.getLogger().info("MYSQL - No history punishments found for playerid=" + playerID);
                } else {
                    try {

                        ResultSet rs = (ResultSet) result;
                        while (rs.next()) {
                            String punishid = rs.getString("punishid");
                            String playerid = rs.getString("playerid");
                            String playername = rs.getString("playername");
                            String ip = rs.getString("ip");
                            String type = rs.getString("type");
                            String reason = rs.getString("reason");
                            String date = rs.getString("date");
                            String duration = rs.getString("duration");
                            String staff = rs.getString("staff");
                            String removedBy = rs.getString("removedBy");
                            String removeReason = rs.getString("removeReason");
                            String removeTime = rs.getString("removeTime");
                            Punishment punishment = new Punishment(UUID.fromString(punishid), UUID.fromString(playerid), playername, ip, PunishmentType.valueOf(type), Long.parseLong(date), Long.parseLong(duration), reason, staff);
                            punishment.setRemoveStaffName(removedBy);
                            punishment.setRemoveReason(removeReason);
                            punishment.setRemoveTime(Long.parseLong(removeTime));
                            punishments.add(punishment);
                        }
                    } catch (SQLException e) {
                        Bukkit.getLogger().severe("Failed to get punishments: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).exceptionally(ex -> {
                Bukkit.getLogger().severe("MYSQL - An error occurred: " + ex.getMessage());
                return null;
            }).get();
        }catch (Exception e){
            Bukkit.getLogger().severe("Failed to get punishments: " + e.getMessage());
            e.printStackTrace();
        }

        return punishments;
    }

    public CompletableFuture<Object> query(QueryType type, String query){
        CompletableFuture<Object> futureResult = new CompletableFuture<>();
        Punishmental.getInstance().getService().submit(() -> {
            try {
                if (type == QueryType.GET) {
                    PreparedStatement pst = this.conn.prepareStatement(query);
                    ResultSet rs = pst.executeQuery();
                    String[] column_split = query.split(" ");
                    String column = column_split[1];
                    if (rs.next()) {
                        futureResult.complete(rs.getString(column));
                    }else{
                        futureResult.complete(null);
                    }
                } else if (type == QueryType.RS) {
                    PreparedStatement pst = this.conn.prepareStatement(query);
                    futureResult.complete(pst.executeQuery());
                } else if (type == QueryType.POST) {
                    PreparedStatement pst = this.conn.prepareStatement(query);
                    pst.executeUpdate();
                    futureResult.complete(null);
                }
            } catch (Exception e) {
                futureResult.completeExceptionally(e);
            }
        });
        return futureResult;
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
    public List<String> getIPs(UUID playerID) {
        CompletableFuture<Object> results = query(QueryType.GET, "SELECT ips FROM playerdata WHERE playerid='" + playerID.toString() + "';");

        AtomicReference<String> concattedIps = new AtomicReference<>("");
        try {
            results.thenAccept(result -> {
                if (result == null) {
                    Bukkit.getLogger().info("MYSQL - No ips found for playerid=" + playerID);
                } else {
                    try {
                        String ips = (String) result;
                        concattedIps.set(ips);
                    } catch (Exception e) {
                        Bukkit.getLogger().severe("Failed to get ips: " + e.getMessage());
                    }
                }
            }).exceptionally(ex -> {
                Bukkit.getLogger().severe("MYSQL - An error occurred: " + ex.getMessage());
                return null;
            }).get();
        }catch (Exception e){
            Bukkit.getLogger().severe("Failed to get ips: " + e.getMessage());
        }

        System.out.println("IPS FOUND: " + concattedIps.get());

        if ( concattedIps.get() == null) return new ArrayList<>();
        return new ArrayList<>(List.of(concattedIps.get().split(";")));
    }

    @Override
    public void insertIP(UUID playerID, String ipAddress) {
        List<String> ips = getIPs(playerID);
        System.out.println("IPS FOUND HERE: " + ips);
        if (ips.contains(ipAddress)) return;
        ips.add(ipAddress);

        StringBuilder concattedIps = new StringBuilder();
        for (String ip : ips){
            concattedIps.append(ip).append(";");
        }

        CompletableFuture<?> alb = query(QueryType.POST, "INSERT INTO playerdata (playerid, playername, ips) VALUES ('" +
                playerID.toString() + "', '" +
                Punishmental.getInstance().getProfileHandler().getProfile(playerID).get().getPlayerName() + "', '" +
                concattedIps.toString() + "');");

        try {
            alb.thenAccept(result -> {
                if (result == null) {
                    Bukkit.getLogger().info("MYSQL - Successfully added ip.");
                } else {
                    Bukkit.getLogger().info("MYSQL - Failed to add ip for playerid=" + playerID);
                }
            }).exceptionally(ex -> {
                Bukkit.getLogger().severe("MYSQL - An error occurred: " + ex.getMessage());
                return null;
            }).get();
        }catch (Exception e){
            Bukkit.getLogger().severe("Failed to add ip: " + e.getMessage());
        }
    }

    @Override
    public boolean is(String ip, PunishmentType type){
        CompletableFuture<Object> results = query(QueryType.GET, "SELECT * FROM punishments WHERE ip='" + ip + "' WHERE type='" + type.name() + "';");

        AtomicBoolean found = new AtomicBoolean(false);
        try {
            results.thenAccept(result -> {
                if (result == null) {
                    Bukkit.getLogger().info("MYSQL - No blacklist found for ip=" + ip);
                } else {
                    try {
                        ResultSet rs = (ResultSet) result;
                        if (rs.next()) {
                            found.set(true);
                        }
                    } catch (SQLException e) {
                        Bukkit.getLogger().severe("Failed to get blacklist: " + e.getMessage());
                    }
                }
            }).exceptionally(ex -> {
                Bukkit.getLogger().severe("MYSQL - An error occurred: " + ex.getMessage());
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return found.get();
    }


}
