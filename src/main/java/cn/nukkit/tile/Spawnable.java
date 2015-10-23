package cn.nukkit.tile;

import cn.nukkit.Player;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.CompoundTag;
import cn.nukkit.nbt.NbtIo;
import cn.nukkit.network.Network;
import cn.nukkit.network.protocol.TileEntityDataPacket;

import java.io.IOException;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Spawnable extends Tile {
    public Spawnable(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

    }

    public abstract CompoundTag getSpawnCompound();

    public void spawnTo(Player player) {
        if (this.closed) {
            return;
        }

        CompoundTag tag = this.getSpawnCompound();
        TileEntityDataPacket pk = new TileEntityDataPacket();
        pk.x = this.x;
        pk.y = (byte) this.y;
        pk.z = this.z;
        try {
            pk.namedTag = NbtIo.write(tag);
        } catch (IOException e) {
            //ignore
        }
        player.dataPacket(pk.setChannel(Network.CHANNEL_WORLD_EVENTS));
    }

    public void spawnToAll() {
        if (this.closed) {
            return;
        }

        for (Player player : this.getLevel().getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values()) {
            if (player.spawned) {
                this.spawnTo(player);
            }
        }
    }
}
