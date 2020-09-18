package cn.nukkit.blockentity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockEntityTarget extends BlockEntity {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockEntityTarget(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.TARGET;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setActivePower(int power) {
        namedTag.putInt("activePower", power);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getActivePower() {
        return NukkitMath.clamp(namedTag.getInt("activePower"), 0, 15);
    }
}
