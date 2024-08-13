package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;

public class BoatDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (!(target instanceof BlockWater)) {
            if (target.getId() != BlockID.AIR || !(target.down() instanceof BlockWater)) {
                return super.dispense(block, face, item);
            }
        }

        Location pos = target.getLocation().setYaw(face.getHorizontalAngle());

        EntityBoat boat = (EntityBoat) Entity.createEntity(EntityBoat.NETWORK_ID, block.level.getChunk(pos.getChunkX(), pos.getChunkZ()),
                Entity.getDefaultNBT(pos)
                        .putByte("woodID", item.getDamage())
        );

        boat.spawnToAll();

        return null;
    }
}
