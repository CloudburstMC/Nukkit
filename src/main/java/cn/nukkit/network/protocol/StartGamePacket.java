package cn.nukkit.network.protocol;

import cn.nukkit.level.GameRules;
import cn.nukkit.network.protocol.types.ExperimentData;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Binary;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.ToString;

import java.io.IOException;
import java.util.UUID;

import java.util.List;

@ToString
public class StartGamePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.START_GAME_PACKET;

    public static final int GAME_PUBLISH_SETTING_NO_MULTI_PLAY = 0;
    public static final int GAME_PUBLISH_SETTING_INVITE_ONLY = 1;
    public static final int GAME_PUBLISH_SETTING_FRIENDS_ONLY = 2;
    public static final int GAME_PUBLISH_SETTING_FRIENDS_OF_FRIENDS = 3;
    public static final int GAME_PUBLISH_SETTING_PUBLIC = 4;

    private static final byte[] EMPTY_COMPOUND_TAG;
    private static final byte[] EMPTY_UUID;

    static {
        try {
            EMPTY_COMPOUND_TAG = NBTIO.writeNetwork(new CompoundTag(""));
            EMPTY_UUID = Binary.writeUUID(new UUID(0, 0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

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
    public boolean worldEditor;
    public int dayCycleStopTime = -1; // -1 = not stopped, any positive value = stopped
    public int eduEditionOffer = 0;
    public boolean hasEduFeaturesEnabled;
    public float rainLevel;
    public float lightningLevel;
    public boolean hasConfirmedPlatformLockedContent;
    public boolean multiplayerGame = true;
    public boolean broadcastToLAN = true;
    public int xblBroadcastIntent = GAME_PUBLISH_SETTING_PUBLIC;
    public int platformBroadcastIntent = GAME_PUBLISH_SETTING_PUBLIC;
    public boolean commandsEnabled;
    public boolean isTexturePacksRequired;
    public GameRules gameRules;
    public boolean bonusChest;
    public boolean hasStartWithMapEnabled;
    public int permissionLevel = 1;
    public int serverChunkTickRange = 4;
    public boolean hasLockedBehaviorPack;
    public boolean hasLockedResourcePack;
    public boolean isFromLockedWorldTemplate;
    public boolean isUsingMsaGamertagsOnly;
    public boolean isFromWorldTemplate;
    public boolean isWorldTemplateOptionLocked;
    public boolean isOnlySpawningV1Villagers;
    public String vanillaVersion = ProtocolInfo.MINECRAFT_VERSION_NETWORK;
    public String levelId = ""; // base64 string, usually the same as world folder name in vanilla
    public String worldName;
    public String premiumWorldTemplateId = "";
    public boolean isTrial;
    public boolean isInventoryServerAuthoritative;
    public long currentTick;
    public int enchantmentSeed;
    public String multiplayerCorrelationId = "";
    public boolean isDisablingPersonas;
    public boolean isDisablingCustomSkins;
    public boolean clientSideGenerationEnabled;
    public byte chatRestrictionLevel;
    public boolean disablePlayerInteractions;
    public boolean emoteChatMuted;
    public boolean hardcore;
    public final List<ExperimentData> experiments = new ObjectArrayList<>(1);

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putVarInt(this.playerGamemode);
        this.putVector3f(this.x, this.y, this.z);
        this.putLFloat(this.yaw);
        this.putLFloat(this.pitch);
        /* Level settings start */
        this.putLLong(this.seed);
        this.putLShort(0x00); // SpawnBiomeType - Default
        this.putString("plains"); // UserDefinedBiomeName
        this.putVarInt(this.dimension);
        this.putVarInt(this.generator);
        this.putVarInt(this.worldGamemode);
        this.putBoolean(this.hardcore);
        this.putVarInt(this.difficulty);
        this.putBlockVector3(this.spawnX, this.spawnY, this.spawnZ);
        this.putBoolean(this.hasAchievementsDisabled);
        this.putBoolean(this.worldEditor);
        this.putBoolean(false); // isCreatedInEditor
        this.putBoolean(false); // isExportedFromEditor
        this.putVarInt(this.dayCycleStopTime);
        this.putVarInt(this.eduEditionOffer);
        this.putBoolean(this.hasEduFeaturesEnabled);
        this.putString(""); // Education Edition Product ID
        this.putLFloat(this.rainLevel);
        this.putLFloat(this.lightningLevel);
        this.putBoolean(this.hasConfirmedPlatformLockedContent);
        this.putBoolean(this.multiplayerGame);
        this.putBoolean(this.broadcastToLAN);
        this.putVarInt(this.xblBroadcastIntent);
        this.putVarInt(this.platformBroadcastIntent);
        this.putBoolean(this.commandsEnabled);
        this.putBoolean(this.isTexturePacksRequired);
        this.putGameRules(this.gameRules);
        this.putExperiments(this.experiments);
        this.putBoolean(this.bonusChest);
        this.putBoolean(this.hasStartWithMapEnabled);
        this.putVarInt(this.permissionLevel);
        this.putLInt(this.serverChunkTickRange);
        this.putBoolean(this.hasLockedBehaviorPack);
        this.putBoolean(this.hasLockedResourcePack);
        this.putBoolean(this.isFromLockedWorldTemplate);
        this.putBoolean(this.isUsingMsaGamertagsOnly);
        this.putBoolean(this.isFromWorldTemplate);
        this.putBoolean(this.isWorldTemplateOptionLocked);
        this.putBoolean(this.isOnlySpawningV1Villagers);
        this.putBoolean(this.isDisablingPersonas);
        this.putBoolean(this.isDisablingCustomSkins);
        this.putBoolean(this.emoteChatMuted);
        this.putString(ProtocolInfo.MINECRAFT_VERSION_NETWORK);
        this.putLInt(16); // Limited world width
        this.putLInt(16); // Limited world height
        this.putBoolean(false); // Nether type
        // EduSharedUriResource
        this.putString(""); // buttonName
        this.putString(""); // linkUri
        this.putBoolean(false); // Experimental Gameplay
        this.putByte(this.chatRestrictionLevel);
        this.putBoolean(this.disablePlayerInteractions);
        this.putString(""); // ServerId
        this.putString(""); // WorldId
        this.putString(""); // ScenarioId
        this.putString(""); // OwnerId
        /* Level settings end */
        this.putString(this.levelId);
        this.putString(this.worldName);
        this.putString(this.premiumWorldTemplateId);
        this.putBoolean(this.isTrial);
        this.putVarInt(0); // RewindHistorySize
        this.putBoolean(true); // isServerAuthoritativeBlockBreaking
        this.putLLong(this.currentTick);
        this.putVarInt(this.enchantmentSeed);
        this.putUnsignedVarInt(0); // No custom blocks
        this.putString(this.multiplayerCorrelationId);
        this.putBoolean(false); // isInventoryServerAuthoritative
        this.putString(""); // serverEngine
        this.put(EMPTY_COMPOUND_TAG); // playerPropertyData
        this.putLLong(0); // blockRegistryChecksum
        this.put(EMPTY_UUID); // worldTemplateId
        this.putBoolean(this.clientSideGenerationEnabled);
        this.putBoolean(false); // blockIdsAreHashed
        this.putBoolean(false); // mTickDeathSystemsEnabled
        this.putBoolean(true); // isServerAuthSounds
    }
}
