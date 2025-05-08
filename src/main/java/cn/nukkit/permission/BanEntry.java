package cn.nukkit.permission;

import cn.nukkit.Server;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BanEntry {

    public static final String format = "yyyy-MM-dd HH:mm:ss Z";

    private final String name;
    private Date creationDate;
    private String source = "(Unknown)";
    private Date expirationDate = null;
    private String reason = "Banned";

    public BanEntry(String name) {
        this.name = name.toLowerCase(Locale.ROOT);
        this.creationDate = new Date();
    }

    public String getName() {
        return name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean hasExpired() {
        Date now = new Date();
        return this.expirationDate != null && this.expirationDate.before(now);
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LinkedHashMap<String, String> getMap() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("name", name);
        map.put("creationDate", new SimpleDateFormat(format).format(creationDate));
        map.put("source", this.source);
        map.put("expireDate", expirationDate != null ? new SimpleDateFormat(format).format(expirationDate) : "Forever");
        map.put("reason", this.reason);
        return map;
    }

    public static BanEntry fromMap(Map<String, String> map) {
        BanEntry banEntry = new BanEntry(map.get("name"));
        try {
            banEntry.setCreationDate(new SimpleDateFormat(format).parse(map.get("creationDate")));
            banEntry.setExpirationDate(!map.get("expireDate").equals("Forever") ? new SimpleDateFormat(format).parse(map.get("expireDate")) : null);
        } catch (ParseException e) {
            Server.getInstance().getLogger().logException(e);
        }
        banEntry.setSource(map.get("source"));
        banEntry.setReason(map.get("reason"));
        return banEntry;
    }

    public String getString() {
        return new Gson().toJson(this.getMap());
    }

    public static BanEntry fromString(String str) {
        Map<String, String> map = new Gson().fromJson(str, new TreeMapTypeToken());
        BanEntry banEntry = new BanEntry(map.get("name"));
        try {
            banEntry.setCreationDate(new SimpleDateFormat(format).parse(map.get("creationDate")));
            banEntry.setExpirationDate(!map.get("expireDate").equals("Forever") ? new SimpleDateFormat(format).parse(map.get("expireDate")) : null);
        } catch (ParseException e) {
            Server.getInstance().getLogger().logException(e);
        }
        banEntry.setSource(map.get("source"));
        banEntry.setReason(map.get("reason"));
        return banEntry;
    }

    private static class TreeMapTypeToken extends TypeToken<TreeMap<String, String>> {
    }
}
