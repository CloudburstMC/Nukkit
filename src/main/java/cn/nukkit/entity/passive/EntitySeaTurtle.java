package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * @author wode490390
 */
public class EntitySeaTurtle extends EntityAnimal {

    public static final int NETWORK_ID = 74;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntitySeaTurtle(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(30);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.36f;
        }
        return 1.2f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.12f;
        }
        return 0.4f;
    }

    @Override
    public String getName() {
        return "Sea Turtle";
    }

    @Override
    public void spawnTo(Player player) {
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
        player.dataPacket(pk);

        super.spawnTo(player);
    }
}
