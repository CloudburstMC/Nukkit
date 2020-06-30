package cn.nukkit.entity.impl.misc;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.misc.FallingBlock;
import cn.nukkit.event.entity.EntityBlockChangeEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.registry.BlockRegistry;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.block.BlockIds.ANVIL;
import static com.nukkitx.protocol.bedrock.data.entity.EntityData.VARIANT;

/**
 * @author MagicDroidX
 */
public class EntityFallingBlock extends BaseEntity implements FallingBlock {

    public EntityFallingBlock(EntityType<FallingBlock> type, Location location) {
        super(type, location);
    }

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
    public float getGravity() {
        return 0.04f;
    }

    @Override
    public float getDrag() {
        return 0.02f;
    }

    @Override
    protected float getBaseOffset() {
        return 0.49f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        int id;
        int meta;
        BlockRegistry registry = BlockRegistry.get();
        if (tag.contains("Tile") && tag.contains("Data")) {
            id = tag.getByte("Tile") & 0xff;
            meta = tag.getByte("Data");
        } else {
            CompoundTag plantTag = tag.getCompound("FallingBlock");
            id = registry.getLegacyId(plantTag.getString("name"));
            meta = plantTag.getShort("val");
        }
        if (id == 0) {
            close();
            return;
        }

        this.data.setInt(VARIANT, registry.getRuntimeId(id, meta));
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        Block block = getBlock();

        tag.tag(CompoundTag.builder()
                .stringTag("name", block.getId().toString())
                .shortTag("val", (short) block.getMeta())
                .build("FallingBlock"));
    }

    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    @Override
    public boolean onUpdate(int currentTick) {

        if (closed) {
            return false;
        }

        this.timing.startTiming();

        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0 && !justCreated) {
            return true;
        }

        lastUpdate = currentTick;

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (isAlive()) {
            this.motion = this.motion.sub(0, getGravity(), 0);

            move(this.motion);

            float friction = 1 - getDrag();

            this.motion = this.motion.mul(friction, 1 - this.getDrag(), friction);

            Vector3i pos = this.getPosition().sub(0.5, 0, 0.5).round().toInt();

            if (onGround) {
                close();
                Block block = level.getBlock(pos);
                if (block.getId() != AIR && block.isTransparent() && !block.canBeReplaced()) {
                    if (this.level.getGameRules().get(GameRules.DO_ENTITY_DROPS)) {
                        getLevel().dropItem(this.getPosition(), this.getBlock().toItem());
                    }
                } else {
                    EntityBlockChangeEvent event = new EntityBlockChangeEvent(this, block, this.getBlock());
                    server.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        getLevel().setBlock(pos, event.getTo(), true);

                        if (event.getTo().getId() == ANVIL) {
                            getLevel().addSound(pos, Sound.RANDOM_ANVIL_LAND);
                        }
                    }
                }
                hasUpdate = true;
            }

            updateMovement();
        }

        this.timing.stopTiming();

        return hasUpdate || !onGround || this.motion.length() > 0.00001;
    }

    public Block getBlock() {
        return BlockRegistry.get().getBlock(this.data.getInt(VARIANT));
    }

    @Override
    public void setBlock(Block block) {
        int runtimeId = BlockRegistry.get().getRuntimeId(block);
        this.data.setInt(VARIANT, runtimeId);
    }

    @Override
    public boolean canBeMovedByCurrents() {
        return false;
    }
}
