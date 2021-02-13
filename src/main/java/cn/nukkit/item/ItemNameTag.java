package cn.nukkit.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.mob.EntityEnderDragon;

public class ItemNameTag extends Item {

    public ItemNameTag() {
        this(0, 1);
    }

    public ItemNameTag(Integer meta) {
        this(meta, 1);
    }

    public ItemNameTag(Integer meta, int count) {
        super(NAME_TAG, meta, count, "Name Tag");
    }

    @Override
    public boolean useOn(Entity entity) {
        if(this.hasCustomName()) {
            if (!(entity instanceof EntityHuman || entity instanceof EntityEnderDragon)) {
                entity.setNameTag(this.getCustomName());
                entity.setNameTagVisible(true);

                // Set entity as persistent? Correct me if that's already implemented.
                return true;
            }
        }
        return false;
    }
}
