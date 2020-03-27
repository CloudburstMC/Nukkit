package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockIds;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import com.nukkitx.math.vector.Vector3i;

public class FlintAndSteelDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(Vector3i position, BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (target.getId() == BlockIds.AIR) {
            block.getLevel().setBlock(target.getPosition(), Block.get(BlockIds.FIRE));
        } else if (target.getId() == BlockIds.TNT) {
            target.onActivate(item);
        } else {
            this.success = false;
        }

        return null;
    }
}
