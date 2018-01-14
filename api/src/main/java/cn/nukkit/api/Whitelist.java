package cn.nukkit.api;

import java.util.Map;
import java.util.UUID;

public interface Whitelist {

    Map<UUID, Entry> getEntries();

    void addToWhitelist(String name);

    void removeFromWhitelist(String name);

    boolean isOnWhitelist(String name);

    interface Entry {
        UUID getUuid();
        String getName();
    }
}
