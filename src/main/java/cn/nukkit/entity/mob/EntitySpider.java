package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * @author PikyCZ
 */
public class EntitySpider extends EntityMob {

    public static final int NETWORK_ID = 35;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntitySpider(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(16);
    }

    @Override
    public float getWidth() {
        return 1.3f;
    }

    @Override
    public float getHeight() {
        return 1.12f;
    }

    @Override
    public float getEyeHeight() {
        return 1;
    }

    @Override
    public String getName() {
        return "Spider";
    }

    @Override
    public void spawnTo(Player... players) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = this.getNetworkId();
        pk.entityUniqueId = this.getId();
        pk.entityRuntimeId = this.getId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.metadata = this.dataProperties;
        Server.broadcastPacket(players, pk);

        super.spawnTo(players);
    }
}
