package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author good777LUCKY
 */
@Since("1.4.0.0-PN")
@PowerNukkitOnly
public class EntityNPCEntity extends EntityLiving implements EntityNPC, EntityInteractable {

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public static final int NETWORK_ID = 51;

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public EntityNPCEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 2.1f;
    }

    @Override
    public boolean canDoInteraction() {
        return true;
    }

    @Override
    public String getInteractButtonText() {
        return "action.interact.edit";
    }

    @Override
    public String getName() {
        return "NPC";
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(Integer.MAX_VALUE); // Should be Float max value
        this.setHealth(20);
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
    }
}
