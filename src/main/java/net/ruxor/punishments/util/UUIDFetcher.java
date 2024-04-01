package net.ruxor.punishments.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public final class UUIDFetcher {

    private UUIDFetcher() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the UUID of the searched player.
     *
     * @param player The player.
     * @return The UUID of the given player.
     */
    //Uncomment this if you want the helper method for BungeeCord:


    /**
     * Returns the UUID of the searched player.
     *
     * @param player The player.
     * @return The UUID of the given player.
     */
    //Uncomment this if you want the helper method for Bukkit/Spigot:
    /*
    public static UUID getUUID(Player player) {
        return getUUID(player.getName());
    }
    */

    /**
     * Returns the UUID of the searched player.
     *
     * @param name The name of the player.
     * @return The UUID of the given player.
     */
    public static UUID getUUID(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                String temp = "";
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    if (inputLine.contains("id")) {
                        temp = inputLine;
                        break;
                    }
                in.close();

                if (!temp.isEmpty()) {
                    String t = insertDashes(temp.replace("\"id\" : \"", "").replace("\",", "").replace(" ", ""));
                    return UUID.fromString(t);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Helper method for inserting dashes into
     * unformatted UUID.
     *
     * @return Formatted UUID with dashes.
     */
    public static String insertDashes(String uuid) {
        StringBuilder sb = new StringBuilder(uuid);
        sb.insert(8, '-');
        sb.insert(13, '-');
        sb.insert(18, '-');
        sb.insert(23, '-');
        return sb.toString();
    }

    private static String callURL(String urlStr) {
        StringBuilder sb = new StringBuilder();
        URLConnection conn;
        BufferedReader br = null;
        InputStreamReader in = null;
        try {
            conn = new URL(urlStr).openConnection();
            if (conn != null) {
                conn.setReadTimeout(60 * 1000);
            }
            if (conn != null && conn.getInputStream() != null) {
                in = new InputStreamReader(conn.getInputStream(), "UTF-8");
                br = new BufferedReader(in);
                String line = br.readLine();
                while (line != null) {
                    sb.append(line).append("\n");
                    line = br.readLine();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Throwable ignored) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable ignored) {
                }
            }
        }
        return sb.toString();
    }

}