package com.nukkitx.api;

import java.util.UUID;

public interface Whitelist {

    void whitelist(String name);

    void whitelist(UUID uuid);

    void whitelist(Player player);

    void whitelist(UUID uuid, String name);

    void deWhitelist(String name);

    void deWhitelist(UUID uuid);

    void deWhitelist(Player player);

    void deWhitelist(UUID uuid, String name);

    boolean isWhitelisted(String name);

    boolean isWhitelisted(UUID uuid);

    boolean isWhitelisted(Player player);

    boolean isWhitelisted(UUID uuid, String name);
}
