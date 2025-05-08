package cn.nukkit.permission;

import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BanList {

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
        if (!this.enable || name == null) {
            return false;
        } else {
            this.removeExpired();

            return this.list.containsKey(name.toLowerCase(Locale.ROOT));
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
        name = name.toLowerCase(Locale.ROOT);
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

                LinkedList<TreeMap<String, String>> list = new Gson().fromJson(Utils.readFile(this.file), new LinkedListTypeToken());
                for (TreeMap<String, String> map : list) {
                    BanEntry entry = BanEntry.fromMap(map);
                    this.list.put(entry.getName(), entry);
                }
            }
        } catch (IOException e) {
            MainLogger.getLogger().error("Could not load ban list", e);
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
            Utils.writeFile(this.file, new ByteArrayInputStream(new GsonBuilder().setPrettyPrinting().create().toJson(list).getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            MainLogger.getLogger().error("Could not save ban list", e);
        }
    }

    private static class LinkedListTypeToken extends TypeToken<LinkedList<TreeMap<String, String>>> {
    }
}
