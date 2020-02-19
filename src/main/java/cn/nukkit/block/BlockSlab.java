package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockSlab extends BlockTransparent {

    public static final BlockColor[] COLORS_1 = new BlockColor[]{
            BlockColor.STONE_BLOCK_COLOR,
            BlockColor.SAND_BLOCK_COLOR,
            BlockColor.WOOD_BLOCK_COLOR,
            BlockColor.STONE_BLOCK_COLOR,
            BlockColor.STONE_BLOCK_COLOR,
            BlockColor.QUARTZ_BLOCK_COLOR,
            BlockColor.NETHERRACK_BLOCK_COLOR
    };
    public static final BlockColor[] COLORS_2 = new BlockColor[]{
            BlockColor.ORANGE_BLOCK_COLOR,
            BlockColor.PURPLE_BLOCK_COLOR,
            BlockColor.CYAN_BLOCK_COLOR,
            BlockColor.CYAN_BLOCK_COLOR,
            BlockColor.CYAN_BLOCK_COLOR,
            BlockColor.STONE_BLOCK_COLOR,
            BlockColor.SAND_BLOCK_COLOR,
            BlockColor.NETHERRACK_BLOCK_COLOR
    };
    public static final BlockColor[] COLORS_3 = new BlockColor[]{
            BlockColor.WHITE_BLOCK_COLOR,
            BlockColor.ORANGE_BLOCK_COLOR,
            BlockColor.STONE_BLOCK_COLOR,
            BlockColor.STONE_BLOCK_COLOR,
            BlockColor.WHITE_BLOCK_COLOR,
            BlockColor.WHITE_BLOCK_COLOR,
            BlockColor.PINK_BLOCK_COLOR,
            BlockColor.PINK_BLOCK_COLOR
    };
    public static final BlockColor[] COLORS_4 = new BlockColor[]{
            BlockColor.STONE_BLOCK_COLOR,
            BlockColor.QUARTZ_BLOCK_COLOR,
            BlockColor.STONE_BLOCK_COLOR,
            BlockColor.SAND_BLOCK_COLOR,
            BlockColor.ORANGE_BLOCK_COLOR
    };

    private final Identifier doubleSlabId;
    private final BlockColor[] colors;

    public BlockSlab(Identifier id, Identifier doubleSlabId, BlockColor[] colors) {
        super(id);
        this.doubleSlabId = doubleSlabId;
        this.colors = colors;
    }

    public static BlockFactory factory(Identifier doubleSlabId, BlockColor... colors) {
        return id -> new BlockDoubleSlab(id, doubleSlabId, Arrays.copyOf(colors, 8));
    }

    @Override
    public float getMinY() {
        return this.isTopSlab() ? (this.getY() + 0.5f) : this.getY();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return getToolType() < ItemTool.TYPE_AXE ? 30 : 15;
    }

    @Override
    public float getMaxY() {
        return this.isTopSlab() ? (this.getY() + 1f) : (this.getY() + 0.5f);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        this.setMeta(this.getMeta() & 0x07);
        if (face == BlockFace.DOWN) {
            if (target instanceof BlockSlab && (target.getMeta() & 0x08) == 0x08 && (target.getMeta() & 0x07) == (this.getMeta() & 0x07)) {
                this.getLevel().setBlock(target.getPosition(), Block.get(this.doubleSlabId, this.getMeta()), true);

                return true;
            } else if (block instanceof BlockSlab && (block.getMeta() & 0x07) == (this.getMeta() & 0x07)) {
                this.getLevel().setBlock(block.getPosition(), Block.get(this.doubleSlabId, this.getMeta()), true);

                return true;
            } else {
                this.setMeta(this.getMeta() | 0x08);
            }
        } else if (face == BlockFace.UP) {
            if (target instanceof BlockSlab && (target.getMeta() & 0x08) == 0 && (target.getMeta() & 0x07) == (this.getMeta() & 0x07)) {
                this.getLevel().setBlock(target.getPosition(), Block.get(this.doubleSlabId, this.getMeta()), true);

                return true;
            } else if (block instanceof BlockSlab && (block.getMeta() & 0x07) == (this.getMeta() & 0x07)) {
                this.getLevel().setBlock(block.getPosition(), Block.get(this.doubleSlabId, this.getMeta()), true);

                return true;
            }
            //TODO: check for collision
        } else {
            if (block instanceof BlockSlab) {
                if ((block.getMeta() & 0x07) == (this.getMeta() & 0x07)) {
                    this.getLevel().setBlock(block.getPosition(), Block.get(this.doubleSlabId, this.getMeta()), true);

                    return true;
                }

                return false;
            } else {
                if (clickPos.getY() > 0.5) {
                    this.setMeta(this.getMeta() | 0x08);
                }
            }
        }

        if (block instanceof BlockSlab && (target.getMeta() & 0x07) != (this.getMeta() & 0x07)) {
            return false;
        }
        this.getLevel().setBlock(block.getPosition(), this, true, true);

        return true;
    }

    @Override
    public BlockColor getColor() {
        return this.colors[this.getMeta() & 0x7];
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }

    private boolean isTopSlab() {
        return (this.getMeta() & 0x08) == 0x08;
    }
}
