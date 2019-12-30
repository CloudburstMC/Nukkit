package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;

/**
 * http://minecraft.gamepedia.com/End_Rod
 *
 * @author PikyCZ
 */
public class BlockEndRod extends BlockTransparent implements Faceable {

    public BlockEndRod(Identifier id) {
        super(id);
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
    public int getLightLevel() {
        return 14;
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getMinX() {
        return this.x + 0.4;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.4;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.6;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.6;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        int[] faces = {0, 1, 3, 2, 5, 4};
        this.setDamage(faces[player != null ? face.getIndex() : 0]);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(id, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }

}
