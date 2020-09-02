package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

@PowerNukkitOnly
public class BlockScaffolding extends BlockFallableMeta {

    public BlockScaffolding() {
        // Does nothing
    }

    public BlockScaffolding(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SCAFFOLDING;
    }

    @Override
    public String getName() {
        return "Scaffolding";
    }

    public int getStability() {
        return getDamage() & 0x7;
    }

    public void setStability(int stability) {
        setDamage(stability & 0x7 | (getDamage() & 0x8));
    }

    public boolean getStabilityCheck() {
        return (getDamage() & 0x8) > 0;
    }

    public void setStabilityCheck(boolean check) {
        if (check) {
            setDamage(getDamage() | 0x8);
        } else {
            setDamage(getDamage() & 0x7);
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockScaffolding(0));
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (block instanceof BlockLava) {
            return false;
        }

        Block down = down();
        if (target.getId() != SCAFFOLDING && down.getId() != SCAFFOLDING && down.getId() != AIR && !down.isSolid()) {
            boolean scaffoldOnSide = false;
            for (int i = 0; i < 4; i++) {
                BlockFace sideFace = BlockFace.fromHorizontalIndex(i);
                if (sideFace != face) {
                    Block side = getSide(sideFace);
                    if (side.getId() == SCAFFOLDING) {
                        scaffoldOnSide = true;
                        break;
                    }
                }
            }
            if (!scaffoldOnSide) {
                return false;
            }
        }

        setDamage(0x8);
        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = down();
            if (down.isSolid()) {
                if (getDamage() != 0) {
                    setDamage(0);
                    this.getLevel().setBlock(this, this, true, true);
                }
                return type;
            }

            int stability = 7;
            for (BlockFace face : BlockFace.values()) {
                if (face == BlockFace.UP) {
                    continue;
                }

                Block otherBlock = getSide(face);
                if (otherBlock.getId() == SCAFFOLDING) {
                    BlockScaffolding other = (BlockScaffolding) otherBlock;
                    int otherStability = other.getStability();
                    if (otherStability < stability) {
                        if (face == BlockFace.DOWN) {
                            stability = otherStability;
                        } else {
                            stability = otherStability + 1;
                        }
                    }
                }
            }

            if (stability >= 7) {
                if (getStabilityCheck()) {
                    super.onUpdate(type);
                } else {
                    this.getLevel().scheduleUpdate(this, 0);
                }
                return type;
            }

            setStabilityCheck(false);
            setStability(stability);
            this.getLevel().setBlock(this, this, true, true);
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.getLevel().useBreakOn(this);
            return type;
        }

        return 0;
    }

    @Override
    protected EntityFallingBlock createFallingEntity(CompoundTag customNbt) {
        setDamage(0);
        customNbt.putBoolean("BreakOnLava", true);
        return super.createFallingEntity(customNbt);
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 60;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean canBeClimbed() {
        return true;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new SimpleAxisAlignedBB(x, y + (2.0/16), z, x + 1, y + 1, z + 1);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return this;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return this;
    }

    @Override
    public double getMinY() {
        return this.y + (14.0/16);
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.TRANSPARENT_BLOCK_COLOR;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return side == BlockFace.UP;
    }
}
