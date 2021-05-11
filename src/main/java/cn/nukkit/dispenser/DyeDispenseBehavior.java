package cn.nukkit.dispenser;

import cn.nukkit.block.*;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.DyeColor;

public class DyeDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (DyeColor.getByDyeData(item.getDamage()) == DyeColor.WHITE) {
            if (target instanceof BlockCrops || target instanceof BlockSapling || target instanceof BlockTallGrass
                    || target instanceof BlockDoublePlant || target instanceof BlockMushroom) {
                target.onActivate(item);

            } else {
                this.success = false;
            }

            return null;
        }

        return super.dispense(block, face, item);
    }
}
