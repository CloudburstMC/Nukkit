package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3f;
import lombok.ToString;

import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class AddPlayerPacket extends DataPacket {

    public UUID uuid;
    public String username;
    public long entityUniqueId;
    public long entityRuntimeId;
    public String platformChatId = "";
    public Vector3f position;
    public Vector3f motion;
    public float pitch;
    public float yaw;
    public float headYaw = this.yaw;
    public Item item;
    public EntityMetadata entityMetadata = new EntityMetadata();
    public EntityLink[] entries = new EntityLink[0]
    public String deviceId = "";
    public int buildPlatform = -1;
    public int unknownUnsignedVarInt1;
    public int unknownUnsignedVarInt2;
    public int unknownUnsignedVarInt3;
    public int unknownUnsignedVarInt4;
    public int unknownUnsignedVarInt5;
    public long unknownLLong;

    @Override
    public byte pid() {
        return ProtocolInfo.ADD_PLAYER_PACKET;
    }

    @Override
    public void decode() {
        this.uuid = this.getUUID();
        this.username = this.getString();
        this.entityUniqueId = this.getEntityUniqueId();
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.platformChatId = this.getString();
        this.position = this.getVector3f();
        this.motion = this.getVector3f();
        this.pitch = this.getLFloat();
        this.yaw = this.getLFloat();
        this.headYaw = this.getLFloat();
        this.item = this.getSlot();
        this.entityMetadata = this.getEntityMetadata();
        this.unknownUnsignedVarInt1 = this.getUnsignedVarInt();
        this.unknownUnsignedVarInt2 = this.getUnsignedVarInt();
        this.unknownUnsignedVarInt3 = this.getUnsignedVarInt();
        this.unknownUnsignedVarInt4 = this.getUnsignedVarInt();
        this.unknownUnsignedVarInt5 = this.getUnsignedVarInt();
        this.unknownLLong = this.getLLong();
        int count = (int) this.getUnsignedVarInt();
        this.entries = new EntityLink[count];
        for (int i = 0; i < count; i++) {
            this.entries[i] = this.getEntityLink();
        }
        this.deviceId = this.getString();
        this.buildPlatform = this.getLInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUUID(this.uuid);
        this.putString(this.username);
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putString(this.platformChatId);
        this.putVector3f(this.position);
        this.putVector3f(this.motion);
        this.putLFloat(this.pitch);
        this.putLFloat(this.yaw);
        this.putLFloat(this.headYaw);
        this.putSlot(this.item);
        this.putEntityMetadata(this.entityMetadata);
        this.putUnsignedVarInt(this.unknownUnsignedVarInt1); //TODO: Adventure settings
        this.putUnsignedVarInt(this.unknownUnsignedVarInt2);
        this.putUnsignedVarInt(this.unknownUnsignedVarInt3);
        this.putUnsignedVarInt(this.unknownUnsignedVarInt4);
        this.putUnsignedVarInt(this.unknownUnsignedVarInt5);
        this.putLLong(this.unknownLLong);
        this.putUnsignedVarInt(this.entries.length);
        for (EntityLink entry : this.entries) {
            this.putEntityLink(entry);
        }
        this.putString(this.deviceId);
        this.putLInt(this.buildPlatform);
    }
}
