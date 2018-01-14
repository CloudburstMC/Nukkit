package cn.nukkit.server.entity.item;

import cn.nukkit.server.level.format.FullChunk;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.util.MinecartType;

/**
 * Created by Snake1999 on 2016/1/30.
 * Package cn.nukkit.server.entity.item in project Nukkit.
 */
public class EntityMinecartEmpty extends EntityMinecartAbstract {

    public static final int NETWORK_ID = 84;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityMinecartEmpty(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(0);
    }

    @Override
    protected void activate(int x, int y, int z, boolean flag) {
        if (flag) {
            if (this.riding != null) {
                mountEntity(riding);
            }
            // looks like MCPE and MCPC not same XD
            // removed rolling feature from here because of MCPE logic?
        }
    }
}
