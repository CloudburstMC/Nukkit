package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

import java.io.IOException;
import java.nio.ByteOrder;

import static cn.nukkit.Player.CRAFTING_SMALL;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockEntityDataPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.BLOCK_ENTITY_DATA_PACKET;

    public int x;
    public int y;
    public int z;
    public byte[] namedTag;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        BlockVector3 v = this.getBlockVector3();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.namedTag = this.get();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBlockVector3(this.x, this.y, this.z);
        this.put(this.namedTag);
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}