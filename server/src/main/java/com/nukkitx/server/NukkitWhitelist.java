package com.nukkitx.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nukkitx.api.Player;
import com.nukkitx.api.Whitelist;
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

public class NukkitWhitelist implements Whitelist{
    private List<NukkitWhitelist.Entry> entries;

    public static NukkitWhitelist load(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            NukkitWhitelist whitelist = new NukkitWhitelist();

            whitelist.entries = NukkitServer.JSON_MAPPER.readValue(reader, new TypeReference<List<NukkitWhitelist.Entry>>() {
            });

            if (whitelist.entries == null) {
                whitelist.entries = new ArrayList<>();
            }

            return whitelist;
        }
    }

    public static void save(Path path, NukkitWhitelist whitelist) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            NukkitServer.JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValue(writer, whitelist.entries);
        }
    }

    public static NukkitWhitelist defaultConfiguration() {
        NukkitWhitelist whitelist = new NukkitWhitelist();
        whitelist.entries = new ArrayList<>();
        return whitelist;
    }

    public void deWhitelist(Player player) {
        deWhitelist(player.getUniqueId(), player.getName());
    }

    public void deWhitelist(UUID uuid) {
        deWhitelist(uuid, null);
    }

    public void deWhitelist(String name) {
        deWhitelist(null, name);
    }

    @Synchronized("entries")
    private void deWhitelist(UUID uuid, String name) {
        for (NukkitWhitelist.Entry entry : entries) {
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

    public void whitelist(Player player) {
        whitelist(player.getUniqueId(), player.getName());
    }

    public void whitelist(String name) {
        whitelist(null, name);
    }

    public void whitelist(UUID uuid) {
        whitelist(uuid, null);
    }

    private void whitelist(UUID uuid, String name) {
        if (isWhitelisted(uuid, name)) {
            return;
        }
        entries.add(new NukkitWhitelist.Entry(uuid, name));
    }

    public boolean isWhitelisted(Player player) {
        return isWhitelisted(player.getUniqueId(), player.getName());
    }

    public boolean isWhitelisted(String name) {
        return isWhitelisted(null, name);
    }

    public boolean isWhitelisted(UUID uuid) {
        return isWhitelisted(uuid, null);
    }

    @Synchronized("entries")
    private boolean isWhitelisted(UUID uuid, String name) {
        entries.removeIf(Objects::isNull);
        for (NukkitWhitelist.Entry entry : entries) {
            if (entry.uuid != null && entry.uuid.equals(uuid)) {
                if (entry.name == null & name != null) {
                    entries.remove(entry);
                    entries.add(new NukkitWhitelist.Entry(uuid, name));
                }
                return true;
            } else if (entry.name != null && entry.name.equalsIgnoreCase(name)) {
                if (entry.uuid == null && uuid != null) {
                    entries.remove(entry);
                    entries.add(new NukkitWhitelist.Entry(uuid, name));
                }
                return true;
            }
        }
        return false;
    }

    @AllArgsConstructor
    private static class Entry {
        private final UUID uuid;
        private final String name;
    }
}
