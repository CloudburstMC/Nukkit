package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.gamerule.GameRuleMap;
import cn.nukkit.utils.Binary;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.ToString;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * Created on 15-10-13.
 */
@ToString
public class StartGamePacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.START_GAME_PACKET;

    public static final int GAME_PUBLISH_SETTING_NO_MULTI_PLAY = 0;
    public static final int GAME_PUBLISH_SETTING_INVITE_ONLY = 1;
    public static final int GAME_PUBLISH_SETTING_FRIENDS_ONLY = 2;
    public static final int GAME_PUBLISH_SETTING_FRIENDS_OF_FRIENDS = 3;
    public static final int GAME_PUBLISH_SETTING_PUBLIC = 4;

    private static final ByteBuf ITEM_DATA_PALETTE;

    static {
        InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_item_ids.json");
        if (stream == null) {
            throw new AssertionError("Unable to locate RuntimeID table");
        }
        Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<ItemData>>() {
        }.getType();
        Collection<ItemData> entries = gson.fromJson(reader, collectionType);
        ByteBuf buffer = Unpooled.directBuffer();

        Binary.writeUnsignedVarInt(buffer, entries.size());

        for (ItemData data : entries) {
            Binary.writeString(buffer, data.name);
            buffer.writeShortLE(data.id);
        }

        ITEM_DATA_PALETTE = buffer;
    }

    public GameRuleMap gameRules;

    public long entityUniqueId;
    public long entityRuntimeId;
    public int playerGamemode;
    public float x;
    public float y;
    public float z;
    public float yaw;
    public float pitch;
    public int seed;
    public byte dimension;
    public int generator = 1;
    public int worldGamemode;
    public int difficulty;
    public int spawnX;
    public int spawnY;
    public int spawnZ;
    public boolean hasAchievementsDisabled = true;
    public int dayCycleStopTime = -1; //-1 = not stopped, any positive value = stopped at that time
    public boolean eduMode = false;
    public boolean hasEduFeaturesEnabled = false;
    public float rainLevel;
    public float lightningLevel;
    public boolean hasConfirmedPlatformLockedContent = false;
    public boolean multiplayerGame = true;
    public boolean broadcastToLAN = true;
    public int xblBroadcastIntent = GAME_PUBLISH_SETTING_PUBLIC;
    public int platformBroadcastIntent = GAME_PUBLISH_SETTING_PUBLIC;
    public boolean commandsEnabled;
    public boolean isTexturePacksRequired = false;

    @Override
    public short pid() {
        return NETWORK_ID;
    }
    public boolean bonusChest = false;
    public boolean hasStartWithMapEnabled = false;
    public int permissionLevel = 1;
    public int serverChunkTickRange = 4;
    public boolean hasLockedBehaviorPack = false;
    public boolean hasLockedResourcePack = false;
    public boolean isFromLockedWorldTemplate = false;
    public boolean isUsingMsaGamertagsOnly = false;
    public boolean isFromWorldTemplate = false;
    public boolean isWorldTemplateOptionLocked = false;
    public boolean isOnlySpawningV1Villagers = false;
    public String levelId = ""; //base64 string, usually the same as world folder name in vanilla
    public String worldName;
    public String premiumWorldTemplateId = "";
    public boolean isTrial = false;
    public long currentTick;

    public int enchantmentSeed;

    public String multiplayerCorrelationId = "";

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityUniqueId(buffer, this.entityUniqueId);
        Binary.writeEntityRuntimeId(buffer, this.entityRuntimeId);
        Binary.writeVarInt(buffer, this.playerGamemode);
        Binary.writeVector3f(buffer, this.x, this.y, this.z);
        buffer.writeFloatLE(this.yaw);
        buffer.writeFloatLE(this.pitch);

        Binary.writeVarInt(buffer, this.seed);
        Binary.writeVarInt(buffer, this.dimension);
        Binary.writeVarInt(buffer, this.generator);
        Binary.writeVarInt(buffer, this.worldGamemode);
        Binary.writeVarInt(buffer, this.difficulty);
        Binary.writeBlockVector3(buffer, this.spawnX, this.spawnY, this.spawnZ);
        buffer.writeBoolean(this.hasAchievementsDisabled);
        Binary.writeVarInt(buffer, this.dayCycleStopTime);
        buffer.writeBoolean(this.eduMode);
        buffer.writeBoolean(this.hasEduFeaturesEnabled);
        buffer.writeFloatLE(this.rainLevel);
        buffer.writeFloatLE(this.lightningLevel);
        buffer.writeBoolean(this.hasConfirmedPlatformLockedContent);
        buffer.writeBoolean(this.multiplayerGame);
        buffer.writeBoolean(this.broadcastToLAN);
        Binary.writeVarInt(buffer, this.xblBroadcastIntent);
        Binary.writeVarInt(buffer, this.platformBroadcastIntent);
        buffer.writeBoolean(this.commandsEnabled);
        buffer.writeBoolean(this.isTexturePacksRequired);
        Binary.writeGameRules(buffer, this.gameRules);
        buffer.writeBoolean(this.bonusChest);
        buffer.writeBoolean(this.hasStartWithMapEnabled);
        Binary.writeVarInt(buffer, this.permissionLevel);
        buffer.writeIntLE(this.serverChunkTickRange);
        buffer.writeBoolean(this.hasLockedBehaviorPack);
        buffer.writeBoolean(this.hasLockedResourcePack);
        buffer.writeBoolean(this.isFromLockedWorldTemplate);
        buffer.writeBoolean(this.isUsingMsaGamertagsOnly);
        buffer.writeBoolean(this.isFromWorldTemplate);
        buffer.writeBoolean(this.isWorldTemplateOptionLocked);
        buffer.writeBoolean(this.isOnlySpawningV1Villagers);

        Binary.writeString(buffer, this.levelId);
        Binary.writeString(buffer, this.worldName);
        Binary.writeString(buffer, this.premiumWorldTemplateId);
        buffer.writeBoolean(this.isTrial);
        buffer.writeLongLE(this.currentTick);
        Binary.writeVarInt(buffer, this.enchantmentSeed);
        buffer.writeBytes(GlobalBlockPalette.getCompiledPalette());
        buffer.writeBytes(ITEM_DATA_PALETTE.duplicate());
        Binary.writeString(buffer, this.multiplayerCorrelationId);
    }

    private static class ItemData {
        private String name;
        private int id;
    }
}
