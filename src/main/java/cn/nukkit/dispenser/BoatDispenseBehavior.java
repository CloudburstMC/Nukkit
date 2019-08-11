package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

public class BoatDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Vector3 pos = block.getSide(face).multiply(1.125);

        Block target = block.getSide(face);

        if (target instanceof BlockWater) {
            pos.y += 1;
        } else if (target.getId() != BlockID.AIR || !(target.down() instanceof BlockWater)) {
            return super.dispense(block, face, item);
        }

        pos = target.getLocation().setYaw(face.getHorizontalAngle());

        EntityBoat boat = new EntityBoat(block.level.getChunk(target.getChunkX(), target.getChunkZ()),
                Entity.getDefaultNBT(pos)
                        .putByte("woodID", item.getDamage())
        );

        boat.spawnToAll();

        return null;
    }

}
