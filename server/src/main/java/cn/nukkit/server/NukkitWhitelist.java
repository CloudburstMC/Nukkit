package cn.nukkit.server;

import cn.nukkit.api.Whitelist;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NukkitWhitelist implements Whitelist{
    @Getter
    private Map<String, Entry> entries;

    public static NukkitWhitelist load(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            NukkitWhitelist whitelist = new NukkitWhitelist();

            List<Entry> entryList = NukkitServer.JSON_MAPPER.readValue(reader, new TypeReference<List<Entry>>(){});
            whitelist.entries = new HashMap<>();
            entryList.stream().forEach(entry -> {
                whitelist.entries.put(entry.name, entry);
                whitelist.entries.put(entry.uuid, entry);
                whitelist.entries.put(entry.xuid, entry);
            });

            return whitelist;
        }
    }

    public static void save(Path path, NukkitWhitelist whitelist) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            NukkitServer.PROPERTIES_MAPPER.writerWithDefaultPrettyPrinter().writeValue(writer, whitelist.entries);
        }
    }

    public static NukkitWhitelist defaultConfiguration() {
        NukkitWhitelist whitelist = new NukkitWhitelist();
        whitelist.entries = new HashMap<>();
        return whitelist;
    }

    public void addToWhitelist(String name) {
        entries.put(name, null);
    }

    public void removeFromWhitelist(String name) {
        entries.remove(name);
    }

    @Getter
    public static final class Entry {
        private final String xuid;
        private final String uuid;
        private final String name;

        public Entry(String xuid, String uuid, String name) {
            this.xuid = xuid;
            this.uuid = uuid;
            this.name = name;
        }
    }
}
