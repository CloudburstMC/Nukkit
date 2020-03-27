package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.vehicle.Boat;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.EntityRegistry;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;

public class BoatDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(Vector3i position, BlockDispenser block, BlockFace face, Item item) {
        Vector3f pos = face.getOffset(position).toFloat().mul(1.125);

        Block target = block.getSide(face);

        if (target instanceof BlockWater) {
            pos = pos.add(0, 1, 0); //TODO: :thinking:
        } else if (target.getId() != BlockIds.AIR || !(target.down() instanceof BlockWater)) {
            return super.dispense(position, block, face, item);
        }

        Location loc = Location.from(target.getPosition(), face.getHorizontalAngle(), 0, target.getLevel());

        Boat boat = EntityRegistry.get().newEntity(EntityTypes.BOAT, loc);
        boat.setWoodType(item.getMeta());
        boat.spawnToAll();

        return null;
    }

}
