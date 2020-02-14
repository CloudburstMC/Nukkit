package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

/**
 * Created by CreeperFace on 15.4.2017.
 */
public class BlockDispenser extends BlockSolid implements Faceable {

    public BlockDispenser(Identifier id) {
        super(id);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(id, 0);
    }

    @Override
    public int getComparatorInputOverride() {
        /*BlockEntity blockEntity = this.level.getBlockEntity(this.getPosition());

        if(blockEntity instanceof BlockEntityDispenser) {
            //return ContainerInventory.calculateRedstone(((BlockEntityDispenser) blockEntity).getInventory()); TODO: dispenser
        }*/

        return super.getComparatorInputOverride();
    }

    public BlockFace getFacing() {
        return BlockFace.fromIndex(this.getMeta() & 7);
    }

    public boolean isTriggered() {
        return (this.getMeta() & 8) > 0;
    }

    public void setTriggered(boolean value) {
        int i = 0;
        i |= getFacing().getIndex();

        if (value) {
            i |= 8;
        }

        this.setMeta(i);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    public Vector3f getDispensePosition() {
        BlockFace facing = getFacing();
        float x = this.getX() + 0.7f * facing.getXOffset();
        float y = this.getY() + 0.7f * facing.getYOffset();
        float z = this.getZ() + 0.7f * facing.getZOffset();

        return Vector3f.from(x, y, z);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x07);
    }
}
