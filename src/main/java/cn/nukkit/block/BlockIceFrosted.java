package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import java.util.ArrayList;
import java.util.List;

public class BlockIceFrosted extends BlockIce {

    public BlockIceFrosted() {
    }

    @Override
    public int getId() {
        return FROSTED_ICE;
    }

    @Override
    public String getName() {
        return "Frosted Ice";
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, new BlockWaterStill(), true);
        List<Block> nearFrosted = new ArrayList<Block>();
        int coordX1 = (int) this.x - 1;
        int coordX2 = (int) this.x + 1;
        int coordZ1 = (int) this.z - 1;
        int coordZ2 = (int) this.z + 1;
        for (int coordX = coordX1; coordX < coordX2 + 1; coordX++) {
            for (int coordZ = coordZ1; coordZ < coordZ2 + 1; coordZ++) {
                Block nearBlock = this.getLevel().getBlock(coordX, (int) this.y, coordZ);
                if (nearBlock instanceof BlockIceFrosted) {
                    nearFrosted.add(nearBlock);
                }

            }
        }
        if (nearFrosted.size() < 2) {
            for (Block iceBlock : nearFrosted) {
                this.getLevel().setBlock(iceBlock, new BlockWaterStill(), true);
            }
        }
        return true;
    }

    //@Override
    //public int onUpdate(int type) {
        //TODO: melt
    //}

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockAir());
    }

    @Override
    public boolean canSilkTouch() {
        return false;
    }
}
