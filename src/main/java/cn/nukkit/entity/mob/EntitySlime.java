package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * @author PikyCZ
 */
public class EntitySlime extends EntityMob {

    public static final int NETWORK_ID = 37;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntitySlime(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(4);
    }

    @Override
    public float getWidth() {
        return 1.02f;
    }

    @Override
    public float getHeight() {
        return 1.02f;
    }

    @Override
    public String getName() {
        return "Slime";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.SLIMEBALL)};
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
