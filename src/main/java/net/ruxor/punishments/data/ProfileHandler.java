package net.ruxor.punishments.data;

import com.google.common.collect.Maps;

import java.util.*;

public class ProfileHandler {

    private Map<UUID, Profile> profiles;

    public ProfileHandler() {
        this.profiles = Maps.newConcurrentMap();
    }

    public void remove(UUID player) {
        this.profiles.remove(player);
    }

    public Profile createProfile(UUID playerID, String playerName){
        this.profiles.put(playerID, new Profile(playerID, playerName));
        return getProfile(playerID).orElse(null);
    }

    public Optional<Profile> getProfile(UUID player) {
        return Optional.ofNullable(this.profiles.get(player));
    }

    public Map<UUID, Profile> getProfiles() {
        return this.profiles;
    }
}
