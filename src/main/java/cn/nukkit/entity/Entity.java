package cn.nukkit.entity;

import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.CompoundTag;
import cn.nukkit.plugin.Plugin;

import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Entity extends Location implements Metadatable {

    protected int id;

    public CompoundTag namedTag;

    public boolean closed = false;

    public static Entity createEntity(String type, FullChunk chunk, CompoundTag nbt, Object... args) {
        //todo
        return null;
    }

    public void saveNBT() {
        //todo
    }

    public Integer getDirection() {
        double rotation = (this.yaw - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if ((0 <= rotation && rotation < 45) || (315 <= rotation && rotation < 360)) {
            return 2; //North
        } else if (45 <= rotation && rotation < 135) {
            return 3; //East
        } else if (135 <= rotation && rotation < 225) {
            return 0; //South
        } else if (225 <= rotation && rotation < 315) {
            return 1; //West
        } else {
            return null;
        }
    }

    public int getId() {
        return this.id;
    }

    public void spawnToAll() {
        //todo
    }

    public void close() {
        //todo
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        //todo
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        //todo
        return null;
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        //todo
        return false;
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        //todo
    }

}
