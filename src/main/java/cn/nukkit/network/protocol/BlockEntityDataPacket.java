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
    public void handle(Player player) {
        if (!player.spawned || !player.isAlive()) {
            return;
        }
        player.craftingType = CRAFTING_SMALL;
        player.resetCraftingGridType();

        Vector3 pos = new Vector3(this.x, this.y, this.z);
        if (pos.distanceSquared(player) > 10000) {
            return;
        }

        BlockEntity t = player.level.getBlockEntity(pos);
        if (t instanceof BlockEntitySpawnable) {
            CompoundTag nbt;
            try {
                nbt = NBTIO.read(this.namedTag, ByteOrder.LITTLE_ENDIAN, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (!((BlockEntitySpawnable) t).updateCompoundTag(nbt, player)) {
                ((BlockEntitySpawnable) t).spawnTo(player);
            }
        }
    }
}