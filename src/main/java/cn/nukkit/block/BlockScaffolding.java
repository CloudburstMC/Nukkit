package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockScaffolding extends BlockFallableMeta {

    public BlockScaffolding() {
        this(0);
    }

    public BlockScaffolding(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Scaffolding";
    }

    @Override
    public int getId() {
        return SCAFFOLDING;
    }

    public int getStability() {
        return this.getDamage() & 0x7;
    }

    public void setStability(int stability) {
        this.setDamage(stability & 0x7 | (this.getDamage() & 0x8));
    }

    public boolean getStabilityCheck() {
        return (this.getDamage() & 0x8) > 0;
    }

    public void setStabilityCheck(boolean check) {
        if (check) {
            this.setDamage(getDamage() | 0x8);
        } else {
            this.setDamage(getDamage() & 0x7);
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(SCAFFOLDING));
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block instanceof BlockLava) {
            return false;
        }

        Block down = this.down();
        if (target.getId() != SCAFFOLDING && down.getId() != SCAFFOLDING && down.getId() != AIR && !down.isSolid()) {
            boolean scaffoldOnSide = false;
            for (int i = 0; i < 4; i++) {
                BlockFace sideFace = BlockFace.fromHorizontalIndex(i);
                if (sideFace != face) {
                    Block side = this.getSide(sideFace);
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

        this.setDamage(0x8);
        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = this.down();
            if (down.isSolid()) {
                if (this.getDamage() != 0) {
                    this.setDamage(0);
                    this.getLevel().setBlock(this, this, true, true);
                }
                return type;
            }

            int stability = 7;
            for (BlockFace face : BlockFace.values()) {
                if (face == BlockFace.UP) {
                    continue;
                }

                Block otherBlock = this.getSide(face);
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
                if (this.getStabilityCheck()) {
                    super.onUpdate(type);
                } else {
                    this.getLevel().scheduleUpdate(this, 0);
                }
                return type;
            }

            this.setStabilityCheck(false);
            this.setStability(stability);
            this.getLevel().setBlock(this, this, true, true);
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.getLevel().useBreakOn(this);
            return type;
        }

        return 0;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    @Override
    public double getHardness() {
        return 0;
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
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public double getMinY() {
        return this.y + 0.875;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getBlockUnsafe() instanceof BlockScaffolding) {
            int top = (int) y;

            for (int i = 1; i <= 16; i++) {
                int id = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY() - i, this.getFloorZ());
                if (id != SCAFFOLDING) {
                    break;
                }
            }

            for (int i = 1; i <= 16; i++) {
                int id = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY() + i, this.getFloorZ());
                if (id == SCAFFOLDING) {
                    top++;
                } else {
                    break;
                }
            }

            boolean success = false;

            Block block = this.up(top - (int) y + 1);
            if (block.getId() == BlockID.AIR) {
                success = this.level.setBlock(block, Block.get(SCAFFOLDING));
            }

            if (success) {
                if (player != null && !player.isCreative()) {
                    item.count--;
                }
            }
            return true;
        }
        return false;
    }
}
