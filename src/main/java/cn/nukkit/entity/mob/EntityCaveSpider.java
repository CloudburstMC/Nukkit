package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityArthropod;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityCaveSpider extends EntityMob implements EntityArthropod {

    public static final int NETWORK_ID = 40;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityCaveSpider(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(12);
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    @Override
    public String getOriginalName() {
        return "Cave Spider";
    }

    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
