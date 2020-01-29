package cn.nukkit.permission;

import cn.nukkit.Nukkit;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
public class BanEntry {
    public static final String format = "yyyy-MM-dd HH:mm:ss Z";

    private static final TypeReference<TreeMap<String, String>> BAN_ENTRY_TYPE_REFERENCE =
            new TypeReference<TreeMap<String, String>>() {};

    private final String name;
    private Date creationDate = null;
    private String source = "(Unknown)";
    private Date expirationDate = null;
    private String reason = "Banned by an operator.";

    public BanEntry(String name) {
        this.name = name.toLowerCase();
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
        map.put("name", getName());
        map.put("creationDate", new SimpleDateFormat(format).format(getCreationDate()));
        map.put("source", this.getSource());
        map.put("expireDate", getExpirationDate() != null ? new SimpleDateFormat(format).format(getExpirationDate()) : "Forever");
        map.put("reason", this.getReason());
        return map;
    }

    public static BanEntry fromMap(Map<String, String> map) {
        BanEntry banEntry = new BanEntry(map.get("name"));
        try {
            banEntry.setCreationDate(new SimpleDateFormat(format).parse(map.get("creationDate")));
            banEntry.setExpirationDate(!map.get("expireDate").equals("Forever") ? new SimpleDateFormat(format).parse(map.get("expireDate")) : null);
        } catch (ParseException e) {
            log.throwing(Level.ERROR, e);
        }
        banEntry.setSource(map.get("source"));
        banEntry.setReason(map.get("reason"));
        return banEntry;
    }

    public String getString() {
        try {
            return Nukkit.JSON_MAPPER.writeValueAsString(this.getMap());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static BanEntry fromString(String str) {
        Map<String, String> map;
        try {
            map = Nukkit.JSON_MAPPER.readValue(str, BAN_ENTRY_TYPE_REFERENCE);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        BanEntry banEntry = new BanEntry(map.get("name"));
        try {
            banEntry.setCreationDate(new SimpleDateFormat(format).parse(map.get("creationDate")));
            banEntry.setExpirationDate(!map.get("expireDate").equals("Forever") ? new SimpleDateFormat(format).parse(map.get("expireDate")) : null);
        } catch (ParseException e) {
            log.throwing(Level.ERROR, e);
        }
        banEntry.setSource(map.get("source"));
        banEntry.setReason(map.get("reason"));
        return banEntry;
    }

}
