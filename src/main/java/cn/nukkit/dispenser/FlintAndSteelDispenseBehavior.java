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
            Block down = target.down();
            if (down.getId() != BlockID.OBSIDIAN || !down.level.createPortal(down, false)) {
                boolean soulFire = down.getId() == Block.SOUL_SAND || down.getId() == Block.SOUL_SOIL;
                block.level.setBlock(target, Block.get(soulFire ? BlockID.SOUL_FIRE : BlockID.FIRE));
            }
            item.useOn(target);
        } else if (target.getId() == BlockID.TNT) {
            target.onActivate(item);
            item.useOn(target);
        }

        return item;
    }
}
