package cn.nukkit.entity.item;

import cn.nukkit.level.GameRule;
import cn.nukkit.level.Position;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * Created by PetteriM1
 */
public class EntityEndCrystal extends Entity {

    public static final int NETWORK_ID = 71;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityEndCrystal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
    }

    public boolean attack(EntityDamageEvent source){
        if (this.closed) return false;
        close();
        kill();
		Position pos = this.getPosition();
		Explosion explode = new Explosion(pos, 6, this);
        if (this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
            explode.explodeA();
            explode.explodeB();
        }
        return true;
	}

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = EntityEndCrystal.NETWORK_ID;
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

    @Override
	public boolean canCollideWith(Entity entity) {
		return false;
	}
}
