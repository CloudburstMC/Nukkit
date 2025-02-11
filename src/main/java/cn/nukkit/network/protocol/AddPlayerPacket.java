package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Binary;
import lombok.ToString;

import java.util.UUID;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
@ToString
public class AddPlayerPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ADD_PLAYER_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public UUID uuid;
    public String username;
    public long entityUniqueId;
    public long entityRuntimeId;
    public String platformChatId = "";
    public float x;
    public float y;
    public float z;
    public float speedX;
    public float speedY;
    public float speedZ;
    public float pitch;
    public float yaw;
    public float headYaw = -1;
    public Item item;
    public int gameType = Server.getInstance().getGamemode();
    public EntityMetadata metadata = new EntityMetadata();
    public String deviceId = "";
    public int buildPlatform = -1;

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUUID(this.uuid);
        this.putString(this.username);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putString(this.platformChatId);
        this.putVector3f(this.x, this.y, this.z);
        this.putVector3f(this.speedX, this.speedY, this.speedZ);
        this.putLFloat(this.pitch);
        this.putLFloat(this.yaw);
        this.putLFloat(this.headYaw == -1 ? this.yaw : this.headYaw);
        this.putSlot(this.item);
        this.putVarInt(this.gameType);
        this.put(Binary.writeMetadata(this.metadata));
        this.putUnsignedVarInt(0); // Entity properties int
        this.putUnsignedVarInt(0); // Entity properties float
        this.putLLong(entityUniqueId);
        this.putUnsignedVarInt(0); // getPlayerPermission().ordinal()
        this.putUnsignedVarInt(0); // getCommandPermission().ordinal()
        this.putUnsignedVarInt(1); // getAbilityLayers().size()
        this.putLShort(1); // getLayerType().ordinal() == BASE
        this.putLInt(262143); // getAbilitiesSet()
        this.putLInt(63); // getAbilityValues()
        this.putLFloat(0.1f); // getFlySpeed()
        this.putLFloat(1.0f); // getVerticalFlySpeed()
        this.putLFloat(0.05f); // getWalkSpeed()
        this.putUnsignedVarInt(0); // Entity links
        this.putString(deviceId);
        this.putLInt(buildPlatform);
    }
}
