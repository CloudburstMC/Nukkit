package cn.nukkit.permission;

import cn.nukkit.Nukkit;
import cn.nukkit.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class BanList {

    private static final TypeReference<LinkedList<TreeMap<String, String>>> BANLIST_TYPE_REFERENCE =
            new TypeReference<LinkedList<TreeMap<String, String>>>() {};

    private LinkedHashMap<String, BanEntry> list = new LinkedHashMap<>();

    private final String file;

    private boolean enable = true;

    public BanList(String file) {
        this.file = file;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public LinkedHashMap<String, BanEntry> getEntires() {
        removeExpired();
        return this.list;
    }

    public boolean isBanned(String name) {
        if (!this.isEnable() || name == null) {
            return false;
        } else {
            this.removeExpired();

            return this.list.containsKey(name.toLowerCase());
        }
    }

    public void add(BanEntry entry) {
        this.list.put(entry.getName(), entry);
        this.save();
    }

    public BanEntry addBan(String target) {
        return this.addBan(target, null);
    }

    public BanEntry addBan(String target, String reason) {
        return this.addBan(target, reason, null);
    }

    public BanEntry addBan(String target, String reason, Date expireDate) {
        return this.addBan(target, reason, expireDate, null);
    }

    public BanEntry addBan(String target, String reason, Date expireDate, String source) {
        BanEntry entry = new BanEntry(target);
        entry.setSource(source != null ? source : entry.getSource());
        entry.setExpirationDate(expireDate);
        entry.setReason(reason != null ? reason : entry.getReason());

        this.add(entry);

        return entry;
    }

    public void remove(String name) {
        name = name.toLowerCase();
        if (this.list.containsKey(name)) {
            this.list.remove(name);
            this.save();
        }
    }


    public void removeExpired() {
        for (String name : new ArrayList<>(this.list.keySet())) {
            BanEntry entry = this.list.get(name);
            if (entry.hasExpired()) {
                list.remove(name);
            }
        }
    }

    public void load() {
        this.list = new LinkedHashMap<>();
        File file = new File(this.file);
        try {
            if (!file.exists()) {
                file.createNewFile();
                this.save();
            } else {

                LinkedList<TreeMap<String, String>> list = Nukkit.JSON_MAPPER.readValue(Utils.readFile(this.file),
                        BANLIST_TYPE_REFERENCE);

                for (TreeMap<String, String> map : list) {
                    BanEntry entry = BanEntry.fromMap(map);
                    this.list.put(entry.getName(), entry);
                }
            }
        } catch (IOException e) {
            log.error("Could not load ban list", e);
        }

    }

    public void save() {
        this.removeExpired();

        try {
            File file = new File(this.file);
            if (!file.exists()) {
                file.createNewFile();
            }

            LinkedList<LinkedHashMap<String, String>> list = new LinkedList<>();
            for (BanEntry entry : this.list.values()) {
                list.add(entry.getMap());
            }
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                Nukkit.JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValue(stream, list);
                Utils.writeFile(this.file, new ByteArrayInputStream(stream.toByteArray()));
            }
        } catch (IOException e) {
            log.error("Could not save ban list", e);
        }
    }
}
