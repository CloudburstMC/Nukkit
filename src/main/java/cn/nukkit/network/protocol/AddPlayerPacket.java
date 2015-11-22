package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityData;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Binary;

import java.util.Map;
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
    public long eid;
    public float x;
    public float y;
    public float z;
    public float speedX;
    public float speedY;
    public float speedZ;
    public float pitch;
    public float yaw;
    public Item item;
    public Map<Integer, EntityData> metadata;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putUUID(this.uuid);
        this.putString(this.username);
        this.putLong(this.eid);
        this.putFloat(this.x);
        this.putFloat(this.y);
        this.putFloat(this.z);
        this.putFloat(this.speedX);
        this.putFloat(this.speedY);
        this.putFloat(this.speedZ);
        this.putFloat(this.yaw);
        this.putFloat(this.yaw); //TODO headrot
        this.putFloat(this.pitch);
        this.putSlot(this.item);

        this.put(Binary.writeMetadata(this.metadata));
    }
}
