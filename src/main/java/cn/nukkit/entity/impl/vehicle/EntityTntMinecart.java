package cn.nukkit.entity.impl.vehicle;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.vehicle.TntMinecart;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.Location;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.player.Player;
import cn.nukkit.utils.MinecartType;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.SoundEvent;
import com.nukkitx.protocol.bedrock.data.entity.EntityLinkData;

import java.util.concurrent.ThreadLocalRandom;

import static com.nukkitx.protocol.bedrock.data.entity.EntityData.FUSE_LENGTH;
import static com.nukkitx.protocol.bedrock.data.entity.EntityFlag.CHARGED;

/**
 * Author: Adam Matthew [larryTheCoder]
 * <p>
 * Nukkit Project.
 */
public class EntityTntMinecart extends EntityAbstractMinecart implements TntMinecart, EntityExplosive {

    public EntityTntMinecart(EntityType<TntMinecart> type, Location location) {
        super(type, location);
    }

    @Override
    public boolean isRideable() {
        return false;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setDisplayBlock(Block.get(BlockIds.TNT));
        this.setDisplay(true);
        this.data.setInt(FUSE_LENGTH, 80);
        this.data.setFlag(CHARGED, false);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        this.timing.startTiming();

        int fuse = this.data.getInt(FUSE_LENGTH);

        if (fuse < 80) {
            int tickDiff = currentTick - lastUpdate;

            lastUpdate = currentTick;

            if (fuse <= 5 || fuse % 5 == 0) {
                this.data.setInt(FUSE_LENGTH, fuse);
            }

            fuse -= tickDiff;

            if (isAlive() && fuse <= 0) {
                if (this.getLevel().getGameRules().get(GameRules.TNT_EXPLODES)) {
                    this.explode(ThreadLocalRandom.current().nextInt(5));
                }
                this.close();
                return false;
            }
        }

        this.timing.stopTiming();

        return super.onUpdate(currentTick) || fuse < 80;
    }

    @Override
    public void activate(int x, int y, int z, boolean flag) {
        this.getLevel().addLevelSoundEvent(this.getPosition(), SoundEvent.IGNITE);
        this.data.setInt(FUSE_LENGTH, 79);
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
        Explosion explosion = new Explosion(this.getLevel(), this.getPosition(), event.getForce(), this);
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
        this.close();
    }

    @Override
    public void dropItem() {
        this.getLevel().dropItem(this.getPosition(), Item.get(ItemIds.TNT_MINECART));
    }

    @Override
    public MinecartType getMinecartType() {
        return MinecartType.valueOf(3);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3f clickedPos) {
        boolean interact = super.onInteract(player, item, clickedPos);
        if (item.getId() == ItemIds.FLINT_AND_STEEL || item.getId() == ItemIds.FIREBALL) {
            this.getLevel().addLevelSoundEvent(this.getPosition(), SoundEvent.IGNITE);
            this.data.setInt(FUSE_LENGTH, 79);
            return true;
        }

        return interact;
    }

    @Override
    public boolean mount(Entity entity, EntityLinkData.Type mode) {
        return false;
    }
}
