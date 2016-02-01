package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.Liquid;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityBlockChangeEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * Created on 15-10-26.
 */
public class EntityFallingBlock extends Entity {

    public static final int NETWORK_ID = 66;

    public static final int DATA_BLOCK_INFO = 20;

    @Override
    public float getWidth() {
        return 0.98f;
    }

    @Override
    public float getLength() {
        return 0.98f;
    }

    @Override
    public float getHeight() {
        return 0.98f;
    }

    @Override
    protected float getGravity() {
        return 0.04f;
    }

    @Override
    protected float getDrag() {
        return 0.02f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    protected int blockId;
    protected int damage;

    public EntityFallingBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (namedTag != null) {
            if (namedTag.contains("TileID")) {
                blockId = namedTag.getInt("TileID");
            } else if (namedTag.contains("Tile")) {
                blockId = namedTag.getInt("Tile");
                namedTag.putInt("TileID", blockId);
            }

            if (namedTag.contains("Data")) {
                damage = namedTag.getByte("Data") & 0xff;
            }
        }

        if (blockId == 0) {
            close();
            return;
        }

        setDataProperty(new IntEntityData(DATA_BLOCK_INFO, this.getBlock() | this.getDamage() << 8));
    }

    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public void attack(EntityDamageEvent source) {
        if (source.getCause() == EntityDamageEvent.CAUSE_VOID) {
            super.attack(source);
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {

        if (closed) {
            return false;
        }

        // TODO timings.startTiming();

        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0 && !justCreated) {
            return true;
        }

        lastUpdate = currentTick;

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (isAlive()) {
            Vector3 pos = new Vector3(x - 0.5, y, z - 0.5).round();

            if (ticksLived == 1) {
                Block block = level.getBlock(pos);
                if (block.getId() != blockId) {
                    kill();
                    return true;
                }
                level.setBlock(pos, Block.get(0), true);
            }

            motionY -= getGravity();

            move(motionX, motionY, motionZ);

            float friction = 1 - getDrag();

            motionX *= friction;
            motionY *= 1 - getDrag();
            motionZ *= friction;

            pos = (new Vector3(x - 0.5, y, z - 0.5)).floor();

            if (onGround) {
                kill();
                Block block = level.getBlock(pos);
                if (block.getId() > 0 && !block.isSolid() && !(block instanceof Liquid)) {
                    getLevel().dropItem(this, Item.get(this.getBlock(), this.getDamage(), 1));
                } else {
                    EntityBlockChangeEvent event = new EntityBlockChangeEvent(this, block, Block.get(getBlock(), getDamage()));
                    server.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        getLevel().setBlock(pos, event.getTo(), true);
                    }
                }
                hasUpdate = true;
            }

            updateMovement();
        }
        return hasUpdate || !onGround || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001;
    }

    public int getBlock() {
        return blockId;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void saveNBT() {
        namedTag.putInt("TileID", blockId);
        namedTag.putByte("Data", (byte) damage);
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket packet = new AddEntityPacket();
        packet.type = EntityFallingBlock.NETWORK_ID;
        packet.eid = getId();
        packet.x = (float) x;
        packet.y = (float) y;
        packet.z = (float) z;
        packet.speedX = (float) motionX;
        packet.speedY = (float) motionY;
        packet.speedZ = (float) motionZ;
        packet.yaw = (float) yaw;
        packet.pitch = (float) pitch;
        packet.metadata = dataProperties;
        player.dataPacket(packet);
        super.spawnTo(player);
    }

}
