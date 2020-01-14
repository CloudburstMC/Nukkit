package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockSlab extends BlockTransparent {

    public BlockSlab(Identifier id) {
        super(id);
    }

    @Override
    public double getMinY() {
        return ((this.getDamage() & 0x08) > 0) ? this.y + 0.5 : this.y;
    }

    @Override
    public double getMaxY() {
        return ((this.getDamage() & 0x08) > 0) ? this.y + 1 : this.y + 0.5;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return getToolType() < ItemTool.TYPE_AXE ? 30 : 15;
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

    protected abstract Identifier getDoubleSlab();
}