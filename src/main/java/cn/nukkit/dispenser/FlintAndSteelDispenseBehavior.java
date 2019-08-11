package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

public class FlintAndSteelDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (target.getId() == BlockID.AIR) {
            block.level.setBlock(target, Block.get(BlockID.FIRE));
        } else if (target.getId() == BlockID.TNT) {
            target.onActivate(item);
        } else {
            this.success = false;
        }

        return null;
    }
}
