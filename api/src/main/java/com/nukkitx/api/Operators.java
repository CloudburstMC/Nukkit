package com.nukkitx.api;

import java.util.UUID;

public interface Operators {

    void removeOperator(Player player);

    void removeOperator(UUID uuid);

    void removeOperator(String name);

    void removeOperator(UUID uuid, String name);

    void addOperator(Player player);

    void addOperator(String name);

    void addOperator(UUID uuid);

    void addOperator(UUID uuid, String name);

    boolean isOperator(Player player);

    boolean isOperator(String name);

    boolean isOperator(UUID uuid);

    boolean isOperator(UUID uuid, String name);
}
