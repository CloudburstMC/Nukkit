package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class AddPlayerPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.ADD_PLAYER_PACKET;

    @Override
    public short pid() {
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
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeUuid(buffer, this.uuid);
        Binary.writeString(buffer, this.username);
        Binary.writeEntityUniqueId(buffer, this.entityUniqueId);
        Binary.writeEntityRuntimeId(buffer, this.entityRuntimeId);
        Binary.writeString(buffer, this.platformChatId);
        Binary.writeVector3f(buffer, this.x, this.y, this.z);
        Binary.writeVector3f(buffer, this.speedX, this.speedY, this.speedZ);
        buffer.writeFloatLE(this.pitch);
        buffer.writeFloatLE(this.yaw); //TODO headrot
        buffer.writeFloatLE(this.yaw);
        Binary.writeItem(buffer, this.item);
        Binary.writeMetadata(buffer, this.metadata);
        Binary.writeUnsignedVarInt(buffer, 0); //TODO: Adventure settings
        Binary.writeUnsignedVarInt(buffer, 0);
        Binary.writeUnsignedVarInt(buffer, 0);
        Binary.writeUnsignedVarInt(buffer, 0);
        Binary.writeUnsignedVarInt(buffer, 0);
        buffer.writeLongLE(entityUniqueId);
        Binary.writeUnsignedVarInt(buffer, 0); //TODO: Entity links
        Binary.writeString(buffer, deviceId);
    }
}
