package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFlint;
import cn.nukkit.item.ItemTool;

import java.util.Random;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockGravel extends BlockFallable {


    public BlockGravel() {
    }

    @Override
    public int getId() {
        return GRAVEL;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return "Gravel";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (new Random().nextInt(9) == 0) {
            return new Item[]{
                    new ItemFlint()
            };
        } else {
            return new Item[]{
                    toItem()
            };
        }
    }
    
    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
