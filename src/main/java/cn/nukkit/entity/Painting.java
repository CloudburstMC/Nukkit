package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddPaintingPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Painting extends Hanging {
    public static final int NETWORK_ID = 83;

    public Painting(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void spawnTo(Player player) {
        AddPaintingPacket pk = new AddPaintingPacket();
        pk.eid = this.getId();
        pk.x = (int) this.x;
        pk.y = (int) this.y;
        pk.z = (int) this.z;
        pk.direction = this.getDirection();
        pk.title = this.namedTag.getString("Motive");

        super.spawnTo(player);
    }
}
