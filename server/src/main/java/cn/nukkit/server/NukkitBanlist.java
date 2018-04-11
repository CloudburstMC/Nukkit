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

import cn.nukkit.api.Banlist;
import cn.nukkit.api.Player;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import lombok.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class NukkitBanlist implements Banlist {
    private Map<UUID, BanEntry> entries;

    public static NukkitBanlist load(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            NukkitBanlist banlist = new NukkitBanlist();

            banlist.entries = NukkitServer.JSON_MAPPER.readValue(reader, new TypeReference<Map<UUID, BanEntry>>() {
            });

            if (banlist.entries == null) {
                banlist.entries = new HashMap<>();
            }

            return banlist;
        }
    }

    public static void save(Path path, NukkitBanlist banlist) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            NukkitServer.JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValue(writer, banlist.entries);
        }
    }

    public static NukkitBanlist defaultConfiguration() {
        NukkitBanlist banlist = new NukkitBanlist();
        banlist.entries = new HashMap<>();
        return banlist;
    }

    public boolean pardon(@Nonnull UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid");
        return entries.remove(uuid) != null;
    }

    public boolean pardon(@Nonnull Player player) {
        Preconditions.checkNotNull(player, "player");
        Optional<UUID> offline;
        if (player.getXuid().isPresent()) {
            return pardon(player.getUniqueId());
        } else if ((offline = player.getOfflineUuid()).isPresent()) {
            return pardon(offline.get());
        }
        return false;
    }

    public boolean pardon(@Nonnull String name) {
        Preconditions.checkNotNull(name, "name");
        return pardon(UUID.nameUUIDFromBytes(name.toLowerCase().getBytes(StandardCharsets.UTF_8)));
    }

    public void ban(@Nonnull Player player, @Nullable Date expireDate, String reason, String source) {
        Preconditions.checkNotNull(player, "player");
        Optional<UUID> offline;
        if (player.getXuid().isPresent()) {
            ban(player.getUniqueId(), expireDate, reason, source);
        } else if ((offline = player.getOfflineUuid()).isPresent()) {
            ban(offline.get(), expireDate, reason, source);
        } else {
            throw new IllegalStateException("Player does not have online or offline uuid");
        }
    }

    public void ban(@Nonnull String name, Date expireDate, String reason, String source) {
        Preconditions.checkNotNull(name, "name");
        ban(UUID.nameUUIDFromBytes(name.toLowerCase().getBytes(StandardCharsets.UTF_8)), expireDate, reason, source);
    }

    public void ban(@Nonnull UUID uuid, Date expireDate, String reason, String source) {
        entries.remove(uuid); // Remove existing data for this uuid.
        entries.put(uuid, new BanEntry(new Date(), source, expireDate, reason));
    }

    @Value
    private static class BanEntry {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
        private final Date creationDate;
        private final String source;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
        private final Date expireDate;
        private final String reason;
    }
}
