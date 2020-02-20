package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import java.util.Arrays;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockSlab extends BlockTransparent {

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
        return id -> new BlockSlab(id, doubleSlabId, Arrays.copyOf(colors, 8));
    }

    @Override
    public BlockColor getColor() {
        return colors[this.getMeta() & 0x07];
    }

    @Override
    public float getMinY() {
        return this.isTopSlab() ? (this.getY() + 0.5f) : this.getY();
    }

    @Override
    public float getMaxY() {
        return this.isTopSlab() ? (this.getY() + 1f) : (this.getY() + 0.5f);
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem() {
        return Item.get(this.id, this.getMeta() & 0x07);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        int meta = this.getMeta() & 0x07;
        boolean isTop;
        BlockDoubleSlab dSlab = (BlockDoubleSlab) BlockRegistry.get().getBlock(this.doubleSlabId, meta);

        if (face == BlockFace.DOWN) {
            if (checkSlab(target) && ((BlockSlab) target).isTopSlab()) {
                if (this.getLevel().setBlock(target.getPosition(), dSlab, true, false)) {
                    dSlab.playPlaceSound();
                    return true;
                }
                return false;
            } else if (checkSlab(block) && !((BlockSlab) block).isTopSlab()) {
                if (this.getLevel().setBlock(block.getPosition(), dSlab, true, false)) {
                    dSlab.playPlaceSound();
                    return true;
                }
                return false;
            } else {
                isTop = true;
            }
        } else if (face == BlockFace.UP) {
            if (checkSlab(target) && !((BlockSlab) target).isTopSlab()) {
                if (this.getLevel().setBlock(target.getPosition(), dSlab, true, false)) {
                    dSlab.playPlaceSound();
                    return true;
                }
                return false;
            } else if (checkSlab(block) && ((BlockSlab) block).isTopSlab()) {
                if (this.getLevel().setBlock(block.getPosition(), dSlab, true, false)) {
                    dSlab.playPlaceSound();
                    return true;
                }
                return false;
            } else {
                isTop = false;
            }
        } else { // Horizontal face
            isTop = clickPos.getY() >= 0.5f;
            if (checkSlab(block)
                    && ((isTop && !((BlockSlab) block).isTopSlab())
                    || (!isTop && ((BlockSlab) block).isTopSlab()))) {
                if (this.getLevel().setBlock(block.getPosition(), dSlab, true, false)) {
                    dSlab.playPlaceSound();
                    return true;
                }
                return false;
            }
        }

        if (block instanceof BlockSlab && (target.getMeta() & 0x07) != (this.getMeta() & 0x07)) {
            return false;
        }
        this.setMeta(meta + (isTop ? 0x08 : 0));
        return this.getLevel().setBlock(block.getPosition(), this, true, true);
    }

    private boolean isTopSlab() {
        return (this.getMeta() & 0x08) == 0x08;
    }

    private boolean checkSlab(Block other) {
        return other instanceof BlockSlab && ((other.getMeta() & 0x07) == (this.getMeta() & 0x07));
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
