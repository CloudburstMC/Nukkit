package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import lombok.extern.log4j.Log4j2;

import java.util.IdentityHashMap;
import java.util.Map;

import static cn.nukkit.block.BlockIds.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
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
        return colorMap.get(this.getId())[this.getMeta() & 0x07];
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
    public Item toItem() {
        return Item.get(this.id, this.getMeta() & 0x07);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        log.info("OnPlace: \nTarget: {}\nBlock: {}\nFace: {}\nClickPos: {}", target.toString(), block.toString(), face.toString(), clickPos.toString());
        int meta = this.getMeta() & 0x07;
        boolean isTop;
        BlockDoubleSlab dSlab = (BlockDoubleSlab) BlockRegistry.get().getBlock(this.getDoubleSlab(), meta);

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
            } else if (checkSlab(block) && !((BlockSlab) block).isTopSlab()) {
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
        log.info("Placing block: {}:{} at {}", this.id, this.getMeta(), this.position);
        return this.getLevel().setBlock(block.getPosition(), this, true, true);
    }

    private boolean isTopSlab() {
        return (this.getMeta() & 0x08) == 0x08;
    }

    private boolean checkSlab(Block other) {
        return other instanceof BlockSlab && ((other.getMeta() & 0x07) == (this.getMeta() & 0x07));
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
