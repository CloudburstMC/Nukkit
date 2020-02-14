package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import java.util.IdentityHashMap;
import java.util.Map;

import static cn.nukkit.block.BlockIds.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockSlab extends BlockTransparent {
    protected static Map<Identifier, BlockColor[]> colorMap = new IdentityHashMap<>();

    static {
        BlockColor[] slabColors = new BlockColor[]{
                BlockColor.STONE_BLOCK_COLOR,
                BlockColor.SAND_BLOCK_COLOR,
                BlockColor.WOOD_BLOCK_COLOR,
                BlockColor.STONE_BLOCK_COLOR,
                BlockColor.STONE_BLOCK_COLOR,
                BlockColor.QUARTZ_BLOCK_COLOR,
                BlockColor.NETHERRACK_BLOCK_COLOR
        };
        colorMap.put(STONE_SLAB, slabColors.clone());
        slabColors = new BlockColor[]{
                BlockColor.ORANGE_BLOCK_COLOR,
                BlockColor.PURPLE_BLOCK_COLOR,
                BlockColor.CYAN_BLOCK_COLOR,
                BlockColor.CYAN_BLOCK_COLOR,
                BlockColor.CYAN_BLOCK_COLOR,
                BlockColor.STONE_BLOCK_COLOR,
                BlockColor.SAND_BLOCK_COLOR,
                BlockColor.NETHERRACK_BLOCK_COLOR
        };
        colorMap.put(STONE_SLAB2, slabColors.clone());
        slabColors = new BlockColor[]{
                BlockColor.WHITE_BLOCK_COLOR,
                BlockColor.ORANGE_BLOCK_COLOR,
                BlockColor.STONE_BLOCK_COLOR,
                BlockColor.STONE_BLOCK_COLOR,
                BlockColor.WHITE_BLOCK_COLOR,
                BlockColor.WHITE_BLOCK_COLOR,
                BlockColor.PINK_BLOCK_COLOR,
                BlockColor.PINK_BLOCK_COLOR
        };
        colorMap.put(STONE_SLAB3, slabColors.clone());
        slabColors = new BlockColor[]{
                BlockColor.STONE_BLOCK_COLOR,
                BlockColor.QUARTZ_BLOCK_COLOR,
                BlockColor.STONE_BLOCK_COLOR,
                BlockColor.SAND_BLOCK_COLOR,
                BlockColor.ORANGE_BLOCK_COLOR
        };
        colorMap.put(STONE_SLAB4, slabColors.clone());
        slabColors = new BlockColor[]{
                BlockColor.WOOD_BLOCK_COLOR,
                BlockColor.SPRUCE_BLOCK_COLOR,
                BlockColor.SAND_BLOCK_COLOR,
                BlockColor.DIRT_BLOCK_COLOR,
                BlockColor.ORANGE_BLOCK_COLOR,
                BlockColor.BROWN_BLOCK_COLOR
        };
        colorMap.put(WOODEN_SLAB, slabColors);
    }

    public BlockSlab(Identifier id) {
        super(id);
    }

    @Override
    public BlockColor getColor() {
        return colorMap.get(this.getId())[this.getDamage() & 0x07];
    }

    @Override
    public double getMinY() {
        return this.isTopSlab() ? this.y + 0.5 : this.y;
    }

    @Override
    public double getMaxY() {
        return this.isTopSlab() ? this.y + 1 : this.y + 0.5;
    }

    @Override
    public double getResistance() {
        return this.getId() == BlockIds.WOODEN_SLAB ? 15 : 30;
    }

    @Override
    public int getToolType() {
        return this.getId() == BlockIds.WOODEN_SLAB ? ItemTool.TYPE_AXE : ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return this.getId() == BlockIds.WOODEN_SLAB;
    }

    @Override
    public int getBurnChance() {
        return this.getId() == WOODEN_SLAB ? 5 : 0;
    }

    @Override
    public int getBurnAbility() {
        return this.getId() == WOODEN_SLAB ? 20 : 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        this.setDamage(this.getDamage() & 0x07);
        if (face == BlockFace.DOWN) {
            if (target instanceof BlockSlab && (target.getDamage() & 0x08) == 0x08 && (target.getDamage() & 0x07) == (this.getDamage() & 0x07)) {
                this.getLevel().setBlock(target, Block.get(getDoubleSlab(), this.getDamage()), true);

                return true;
            } else if (block instanceof BlockSlab && (block.getDamage() & 0x07) == (this.getDamage() & 0x07)) {
                this.getLevel().setBlock(block, Block.get(getDoubleSlab(), this.getDamage()), true);

                return true;
            } else {
                this.setDamage(this.getDamage() | 0x08);
            }
        } else if (face == BlockFace.UP) {
            if (target instanceof BlockSlab && (target.getDamage() & 0x08) == 0 && (target.getDamage() & 0x07) == (this.getDamage() & 0x07)) {
                this.getLevel().setBlock(target, Block.get(getDoubleSlab(), this.getDamage()), true);

                return true;
            } else if (block instanceof BlockSlab && (block.getDamage() & 0x07) == (this.getDamage() & 0x07)) {
                this.getLevel().setBlock(block, Block.get(getDoubleSlab(), this.getDamage()), true);

                return true;
            }
            //TODO: check for collision
        } else {
            if (block instanceof BlockSlab) {
                if ((block.getDamage() & 0x07) == (this.getDamage() & 0x07)) {
                    this.getLevel().setBlock(block, Block.get(getDoubleSlab(), this.getDamage()), true);

                    return true;
                }

                return false;
            } else {
                if (clickPos.getY() > 0.5) {
                    this.setDamage(this.getDamage() | 0x08);
                }
            }
        }

        if (block instanceof BlockSlab && (target.getDamage() & 0x07) != (this.getDamage() & 0x07)) {
            return false;
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    protected Identifier getDoubleSlab() {
        if (this.id == STONE_SLAB) {
            return DOUBLE_STONE_SLAB;
        } else if (this.id == STONE_SLAB2) {
            return DOUBLE_STONE_SLAB2;
        } else if (this.id == STONE_SLAB3) {
            return DOUBLE_STONE_SLAB3;
        } else if (this.id == STONE_SLAB4) {
            return DOUBLE_STONE_SLAB4;
        } else if (this.id == WOODEN_SLAB) {
            return DOUBLE_WOODEN_SLAB;
        }
        return null;
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
