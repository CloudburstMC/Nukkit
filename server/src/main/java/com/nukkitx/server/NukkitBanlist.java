package com.nukkitx.server;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import com.nukkitx.api.Banlist;
import com.nukkitx.api.Player;
import lombok.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        return pardon(player.getUniqueId());
    }

    public void ban(@Nonnull Player player, @Nullable Date expireDate, String reason, String source) {
        Preconditions.checkNotNull(player, "player");
        ban(player.getUniqueId(), expireDate, reason, source);
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
