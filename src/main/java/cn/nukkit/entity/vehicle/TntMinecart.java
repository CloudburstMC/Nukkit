package cn.nukkit.entity.vehicle;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.EntityType;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.player.Player;
import cn.nukkit.utils.MinecartType;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.entity.data.EntityData.FUSE_LENGTH;
import static cn.nukkit.entity.data.EntityFlag.CHARGED;

/**
 * Author: Adam Matthew [larryTheCoder]
 * <p>
 * Nukkit Project.
 */
public class TntMinecart extends AbstractMinecart<TntMinecart> implements EntityExplosive {

    public static final int NETWORK_ID = 97;
    private int fuse;
    private boolean activated = false;

    public TntMinecart(EntityType<TntMinecart> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
        super.setDisplayBlock(Block.get(BlockIds.TNT), false);
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
        this.setFlag(CHARGED, false);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        this.timing.startTiming();

        if (fuse < 80) {
            int tickDiff = currentTick - lastUpdate;

            lastUpdate = currentTick;

            if (fuse % 5 == 0) {
                setIntData(FUSE_LENGTH, fuse);
            }

            fuse -= tickDiff;

            if (isAlive() && fuse <= 0) {
                if (this.level.getGameRules().get(GameRules.TNT_EXPLODES)) {
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
        level.dropItem(this, Item.get(ItemIds.TNT_MINECART));
    }

    @Override
    public MinecartType getMinecartType() {
        return MinecartType.valueOf(3);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        super.namedTag.putInt("TNTFuse", this.fuse);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean interact = super.onInteract(player, item, clickedPos);
        if (item.getId() == ItemIds.FLINT_AND_STEEL || item.getId() == ItemIds.FIREBALL) {
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
