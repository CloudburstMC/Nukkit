package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockSlab extends BlockTransparent {

    public BlockSlab(Identifier id) {
        super(id);
    }

    @Override
    public float getMinY() {
        return ((this.getMeta() & 0x08) > 0) ? this.getY() + 0.5f : this.getY();
    }

    @Override
    public float getMaxY() {
        return ((this.getMeta() & 0x08) > 0) ? this.getY() + 1 : this.getY() + 0.5f;
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
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        this.setMeta(this.getMeta() & 0x07);
        if (face == BlockFace.DOWN) {
            if (target instanceof BlockSlab && (target.getMeta() & 0x08) == 0x08 && (target.getMeta() & 0x07) == (this.getMeta() & 0x07)) {
                this.getLevel().setBlock(target.getPosition(), Block.get(getDoubleSlab(), this.getMeta()), true);

                return true;
            } else if (block instanceof BlockSlab && (block.getMeta() & 0x07) == (this.getMeta() & 0x07)) {
                this.getLevel().setBlock(block.getPosition(), Block.get(getDoubleSlab(), this.getMeta()), true);

                return true;
            } else {
                this.setMeta(this.getMeta() | 0x08);
            }
        } else if (face == BlockFace.UP) {
            if (target instanceof BlockSlab && (target.getMeta() & 0x08) == 0 && (target.getMeta() & 0x07) == (this.getMeta() & 0x07)) {
                this.getLevel().setBlock(target.getPosition(), Block.get(getDoubleSlab(), this.getMeta()), true);

                return true;
            } else if (block instanceof BlockSlab && (block.getMeta() & 0x07) == (this.getMeta() & 0x07)) {
                this.getLevel().setBlock(block.getPosition(), Block.get(getDoubleSlab(), this.getMeta()), true);

                return true;
            }
            //TODO: check for collision
        } else {
            if (block instanceof BlockSlab) {
                if ((block.getMeta() & 0x07) == (this.getMeta() & 0x07)) {
                    this.getLevel().setBlock(block.getPosition(), Block.get(getDoubleSlab(), this.getMeta()), true);

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

    protected abstract Identifier getDoubleSlab();

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
