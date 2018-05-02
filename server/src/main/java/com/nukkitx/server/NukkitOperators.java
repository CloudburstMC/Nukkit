package com.nukkitx.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nukkitx.api.Player;
import lombok.AllArgsConstructor;
import lombok.Synchronized;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class NukkitOperators {
    private List<Entry> entries;

    public static NukkitOperators load(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            NukkitOperators operators = new NukkitOperators();

            operators.entries = NukkitServer.JSON_MAPPER.readValue(reader, new TypeReference<List<Entry>>() {
            });

            if (operators.entries == null) {
                operators.entries = new ArrayList<>();
            }

            return operators;
        }
    }

    public static void save(Path path, NukkitOperators operators) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            NukkitServer.JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValue(writer, operators.entries);
        }
    }

    public static NukkitOperators defaultConfiguration() {
        NukkitOperators operators = new NukkitOperators();
        operators.entries = new ArrayList<>();
        return operators;
    }

    public void removeOperator(Player player) {
        removeOperator(player.getUniqueId(), player.getName());
    }

    public void removeOperator(UUID uuid) {
        removeOperator(uuid, null);
    }

    public void removeOperator(String name) {
        removeOperator(null, name);
    }

    @Synchronized("entries")
    private void removeOperator(UUID uuid, String name) {
        for (Entry entry : entries) {
            if (entry.uuid != null && entry.uuid.equals(uuid)) {
                entries.remove(entry);
                return;
            }
            if (entry.name != null && entry.name.equalsIgnoreCase(name)) {
                entries.remove(entry);
                return;
            }
        }
    }

    public void addOperator(Player player) {
        addOperator(player.getUniqueId(), player.getName());
    }

    public void addOperator(String name) {
        addOperator(null, name);
    }

    public void addOperator(UUID uuid) {
        addOperator(uuid, null);
    }

    private void addOperator(UUID uuid, String name) {
        if (isOperator(uuid, name)) {
            return;
        }
        entries.add(new Entry(uuid, name));
    }

    public boolean isOperator(Player player) {
        return isOperator(player.getUniqueId(), player.getName());
    }

    public boolean isOperator(String name) {
        return isOperator(null, name);
    }

    public boolean isOperator(UUID uuid) {
        return isOperator(uuid, null);
    }

    @Synchronized("entries")
    private boolean isOperator(UUID uuid, String name) {
        entries.removeIf(Objects::isNull);
        for (Entry entry : entries) {
            if (entry.uuid != null && entry.uuid.equals(uuid)) {
                if (entry.name == null & name != null) {
                    entries.remove(entry);
                    entries.add(new Entry(uuid, name));
                }
                return true;
            } else if (entry.name != null && entry.name.equalsIgnoreCase(name)) {
                if (entry.uuid == null && uuid != null) {
                    entries.remove(entry);
                    entries.add(new Entry(uuid, name));
                }
                return true;
            }
        }
        return false;
    }

    @AllArgsConstructor
    public static class Entry {
        private final UUID uuid;
        private final String name;
    }
}
