package cn.nukkit.network.protocol;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.*;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.Binary;

import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
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
    public Item item;
    public EntityMetadata metadata = new EntityMetadata();
    //public EntityLink links = new EntityLink[0];
    public String deviceId = "";

    @Override
    public void decode() {
        this.uuid = this.getUUID();
        this.username = this.getString();
        this.entityUniqueId = this.getEntityUniqueId();
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.x = this.getLFloat();
        this.y = this.getLFloat();
        this.z = this.getLFloat();
        this.speedX = this.getLFloat();
        this.speedY = this.getLFloat();
        this.speedZ = this.getLFloat();
        this.pitch = this.getLFloat();
        this.yaw = this.getLFloat();
        this.item = this.getSlot();
        this.metadata = this.getMetadata();
        this.getUnsignedVarInt();
        this.getUnsignedVarInt();
        this.getUnsignedVarInt();
        this.getUnsignedVarInt();
        this.getUnsignedVarInt();
        this.getLLong();
        this.getUnsignedVarInt();
        this.deviceId = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUUID(this.uuid);
        this.putString(this.username);
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putString(this.platformChatId);
        this.putVector3f(this.x, this.y, this.z);
        this.putVector3f(this.speedX, this.speedY, this.speedZ);
        this.putLFloat(this.pitch);
        this.putLFloat(this.yaw); //TODO headrot
        this.putLFloat(this.yaw);
        this.putSlot(this.item);
        this.putMetadata(this.metadata);
        this.putUnsignedVarInt(0); //TODO: Adventure settings
        this.putUnsignedVarInt(0);
        this.putUnsignedVarInt(0);
        this.putUnsignedVarInt(0);
        this.putUnsignedVarInt(0);
        this.putLLong(entityUniqueId);
        this.putUnsignedVarInt(0); //TODO: Entity links
        this.putString(deviceId);
    }
}
