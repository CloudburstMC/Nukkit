/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server;

import cn.nukkit.api.Player;
import com.fasterxml.jackson.core.type.TypeReference;
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
