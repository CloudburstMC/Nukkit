package cn.nukkit.player;

import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.ByteTag;
import com.nukkitx.nbt.tag.CompoundTag;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class PlayerData {
    private static final String TAG_FIRST_PLAYED = "firstPlayed";
    private static final String TAG_LAST_PLAYED = "lastPlayed";
    private static final String TAG_LAST_ADDRESS = "lastIP";
    private static final String TAG_LEVEL = "Level";
    private static final String TAG_SPAWN_LEVEL = "SpawnLevel";
    private static final String TAG_SPAWN_X = "SpawnX";
    private static final String TAG_SPAWN_Y = "SpawnY";
    private static final String TAG_SPAWN_Z = "SpawnZ";
    private static final String TAG_ACHIEVEMENTS = "Achievements";
    private static final String TAG_GAME_TYPE = "playerGameType";
    private final Set<String> achievements = new HashSet<>();
    private long firstPlayed;
    private long lastPlayed;
    private String level;
    private String spawnLevel;
    private Vector3i spawnLocation;
    private int gamemode;
    private InetAddress lastAddress;

    public void loadData(CompoundTag tag) {
        tag.listenForLong(TAG_FIRST_PLAYED, this::setFirstPlayed);
        tag.listenForLong(TAG_LAST_PLAYED, this::setLastPlayed);
        tag.listenForString(TAG_LEVEL, this::setLevel);
        tag.listenForString(TAG_SPAWN_LEVEL, this::setSpawnLevel);

        if (tag.contains(TAG_SPAWN_X) && tag.contains(TAG_SPAWN_Y) && tag.contains(TAG_SPAWN_Z)) {
            this.setSpawnLocation(Vector3i.from(
                    tag.getInt(TAG_SPAWN_X),
                    tag.getInt(TAG_SPAWN_Y),
                    tag.getInt(TAG_SPAWN_Z)
            ));
        }
        tag.listenForInt(TAG_GAME_TYPE, this::setGamemode);
        tag.listenForString(TAG_LAST_ADDRESS, this::setLastAddress);
        tag.listenForCompound(TAG_ACHIEVEMENTS, achievementsTag -> {
            achievementsTag.getValue().forEach((achievement, tag1) -> {
                if (tag1 instanceof ByteTag && ((ByteTag) tag1).getAsBoolean()) {
                    this.achievements.add(achievement);
                }
            });
        });
    }

    public void saveData(CompoundTagBuilder tag) {
        if (this.firstPlayed > 0 && this.lastPlayed > 0) {
            tag.longTag(TAG_FIRST_PLAYED, this.firstPlayed);
            tag.longTag(TAG_LAST_PLAYED, this.lastPlayed);
        }
        if (this.level != null) {
            tag.stringTag(TAG_LEVEL, this.level);
        }
        if (this.spawnLevel != null && spawnLocation != null) {
            tag.stringTag(TAG_SPAWN_LEVEL, this.spawnLevel)
                    .intTag(TAG_SPAWN_X, this.spawnLocation.getX())
                    .intTag(TAG_SPAWN_Y, this.spawnLocation.getY())
                    .intTag(TAG_SPAWN_Z, this.spawnLocation.getZ());
        }

    }

    public long getFirstPlayed() {
        return firstPlayed;
    }

    public void setFirstPlayed(long firstPlayed) {
        this.firstPlayed = firstPlayed;
    }

    public long getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(long lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSpawnLevel() {
        return spawnLevel;
    }

    public void setSpawnLevel(String spawnLevel) {
        this.spawnLevel = spawnLevel;
    }

    public Vector3i getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Vector3i spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public int getGamemode() {
        return gamemode;
    }

    public void setGamemode(int gamemode) {
        this.gamemode = gamemode;
    }

    public InetAddress getLastAddress() {
        return lastAddress;
    }

    public void setLastAddress(InetAddress lastAddress) {
        this.lastAddress = lastAddress;
    }

    private void setLastAddress(String lastAddress) {
        try {
            this.lastAddress = InetAddress.getByName(lastAddress);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid player address", e);
        }
    }

    public Set<String> getAchievements() {
        return achievements;
    }
}
