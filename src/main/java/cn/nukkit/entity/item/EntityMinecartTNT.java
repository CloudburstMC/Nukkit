package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.BlockTNT;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMinecartTNT;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.MinecartType;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Author: Adam Matthew [larryTheCoder]
 * <p>
 * Nukkit Project.
 */
public class EntityMinecartTNT extends EntityMinecartAbstract implements EntityExplosive {

    public static final int NETWORK_ID = 97;
    private int fuse;
    private boolean activated = false;

    public EntityMinecartTNT(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        super.setDisplayBlock(new BlockTNT(), false);
    }

    @Override
    public boolean isRideable() {
        return false;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        if (namedTag.contains("TNTFuse")) {
            fuse = namedTag.getByte("TNTFuse");
        } else {
            fuse = 80;
        }
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_CHARGED, false);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        this.timing.startTiming();

        if (fuse < 80) {
            int tickDiff = currentTick - lastUpdate;

            lastUpdate = currentTick;

            if (fuse % 5 == 0) {
                setDataProperty(new IntEntityData(DATA_FUSE_LENGTH, fuse));
            }

            fuse -= tickDiff;

            if (isAlive() && fuse <= 0) {
                if (this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
                    this.explode(ThreadLocalRandom.current().nextInt(5));
                }
                this.close();
                return false;
            }
        }

        this.timing.stopTiming();

        return super.onUpdate(currentTick);
    }

    @Override
    public void activate(int x, int y, int z, boolean flag) {
        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_IGNITE);
        this.fuse = 79;
    }

    @Override
    public void explode() {
        explode(0);
    }

    public void explode(double square) {
        double root = Math.sqrt(square);

        if (root > 5.0D) {
            root = 5.0D;
        }

        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, (4.0D + ThreadLocalRandom.current().nextDouble() * 1.5D * root));
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Explosion explosion = new Explosion(this, event.getForce(), this);
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
        this.close();
    }

    @Override
    public void dropItem() {
        level.dropItem(this, new ItemMinecartTNT());
    }

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(3);
    }

    @Override
    public int getNetworkId() {
        return EntityMinecartTNT.NETWORK_ID;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        super.namedTag.putInt("TNTFuse", this.fuse);
    }
    
    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean interact = super.onInteract(player, item, clickedPos);
        if (item.getId() == Item.FLINT_AND_STEEL || item.getId() == Item.FIRE_CHARGE) {
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_IGNITE);
            this.fuse = 79;
            return true;
        }

        return interact;
    }

    @Override
    public boolean mountEntity(Entity entity, byte mode) {
        return false;
    }
}
