package cn.nukkit.permission;

import cn.nukkit.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BanList {

    private LinkedList<BanEntry> list = new LinkedList<BanEntry>();

    private String file;

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

    public LinkedList<BanEntry> getEntires() {
        removeExpired();
        return this.list;
    }

    public boolean isBanned(String name) {
        name = name.toLowerCase();
        if (!this.isEnable()) {
            return false;
        } else {
            this.removeExpired();
            for (BanEntry entry : list) {
                if (entry.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void add(BanEntry entry) {
        this.list.add(entry);
        this.save();
    }

    public BanEntry addBan(String target) {
        return this.addBan(target, null);
    }

    public BanEntry addBan(String target, String reason) {
        return this.addBan(target, null, null);
    }

    public BanEntry addBan(String target, String reason, Date expireDate) {
        return this.addBan(target, null, null, null);
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
        for (BanEntry entry : list) {
            if (entry.getName().equals(name)) {
                list.remove(entry);
                this.save();
            }
        }
    }


    public void removeExpired() {
        for (BanEntry entry : list) {
            if (entry.hasExpired()) {
                list.remove(entry);
            }
        }
    }

    public void load() {
        this.list = new LinkedList<BanEntry>();
        File file = new File(this.file);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.save();
        } else {
            try {
                LinkedList<TreeMap<String, String>> list = new Gson().fromJson(Utils.readFile(this.file), new TypeToken<LinkedList<TreeMap<String, String>>>() {
                }.getType());
                for (TreeMap<String, String> map : list) {
                    this.list.add(BanEntry.fromMap(map));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void save() {
        File file = new File(this.file);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            LinkedList<TreeMap<String, String>> list = new LinkedList<TreeMap<String, String>>();
            for (BanEntry entry : this.list) {
                list.add(entry.getMap());
            }
            Utils.writeFile(this.file, new ByteArrayInputStream(new GsonBuilder().setPrettyPrinting().create().toJson(list).getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
