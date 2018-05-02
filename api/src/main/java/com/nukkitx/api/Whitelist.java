package com.nukkitx.api;

import java.util.UUID;

public interface Whitelist {

    void whitelist(String name);

    void whitelist(UUID uuid);

    void whitelist(Player player);

    void deWhitelist(String name);

    void deWhitelist(UUID uuid);

    void deWhitelist(Player player);

    boolean isWhitelisted(String name);

    boolean isWhitelisted(UUID uuid);

    boolean isWhitelisted(Player player);

}
