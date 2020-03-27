package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.impl.EntityLiving;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.EntityRegistry;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;

public class SpawnEggDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(Vector3i position, BlockDispenser block, BlockFace face, Item item) {
        Vector3f pos = face.getOffset(position).toFloat().add(0.5, 0.7, 0.5);

        Entity entity = EntityRegistry.get().newEntity(EntityRegistry.get().getEntityType(item.getMeta()), Location.from(pos, block.getLevel()));

        this.success = entity != null;

        if (this.success) {
            if (item.hasCustomName() && entity instanceof EntityLiving) {
                entity.setNameTag(item.getCustomName());
            }

            entity.spawnToAll();
            return null;
        }

        return super.dispense(position, block, face, item);
    }
}
