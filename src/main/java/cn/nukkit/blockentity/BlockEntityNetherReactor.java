package cn.nukkit.blockentity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockNetherReactor;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.MainLogger;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockEntityNetherReactor extends BlockEntitySpawnable {
    private BlockNetherReactor.NetherReactorProgress progress;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockEntityNetherReactor(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.NETHER_REACTOR;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockNetherReactor.NetherReactorProgress getProgress() {
        return progress;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setProgress(BlockNetherReactor.NetherReactorProgress progress) {
        this.progress = progress;
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        progress = BlockNetherReactor.NetherReactorProgress.READY;
        if (namedTag.containsShort("Progress")) {
            try {
                progress = BlockNetherReactor.NetherReactorProgress.getFromData(namedTag.getShort("Progress"));
            } catch (IllegalArgumentException e) {
                MainLogger.getLogger().warning("Failed to load the nether reactor progress at "+getLocation(), e);
            }
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        namedTag.putShort("Progress", getProgress().ordinal());
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.NETHER_REACTOR)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putShort("Progress", getProgress().ordinal());
    }
}
