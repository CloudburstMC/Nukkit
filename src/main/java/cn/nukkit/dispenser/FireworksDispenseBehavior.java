package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.EntityRegistry;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;

public class FireworksDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(Vector3i position, BlockDispenser block, BlockFace face, Item item) {
        Vector3f pos = face.getOffset(position).toFloat().add(0, 0.2f, 0);

        EntityRegistry.get().newEntity(EntityTypes.FIREWORKS_ROCKET, Location.from(pos, block.getLevel()));
        return null;
    }
}
