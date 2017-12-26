package cn.nukkit.api;

import java.util.Map;

public interface Whitelist {

    Map<String, Entry> getEntries();

    void addToWhitelist(String name);

    void removeFromWhitelist(String name);

    boolean isOnWhitelist(String name);

    interface Entry {
        String getXuid();
        String getUuid();
        String getName();
    }
}
