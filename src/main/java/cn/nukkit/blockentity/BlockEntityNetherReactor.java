package cn.nukkit.blockentity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockproperty.value.NetherReactorState;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.MathHelper;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * This entity allows to manipulate the save state of a nether reactor core, but changing it
 * will cause no visual change. To see the changes in the world it would be necessary to 
 * change the block data value to {@code 0 1 or 3} but that is impossible in the recent versions
 * because Minecraft Bedrock Edition has moved from block data to the block property & block state
 * system and did not create a block property for the old nether reactor core block, making it 
 * impossible for the server to tell the client to render the red and dark versions of the block.
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockEntityNetherReactor extends BlockEntitySpawnable {
    private NetherReactorState reactorState;
    private int progress;
    
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
    public NetherReactorState getReactorState() {
        return reactorState;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setReactorState(NetherReactorState reactorState) {
        this.reactorState = reactorState;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getProgress() {
        return progress;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setProgress(int progress) {
        this.progress = MathHelper.clamp(progress, 0, 900);
    }

    @Override
    protected void initBlockEntity() {
        reactorState = NetherReactorState.READY;
        if (namedTag.containsShort("Progress")) {
            progress = (short) namedTag.getShort("Progress");
        }
        
        if (namedTag.containsByte("HasFinished") && namedTag.getBoolean("HasFinished")) {
            reactorState = NetherReactorState.FINISHED;
        } else if (namedTag.containsByte("IsInitialized") && namedTag.getBoolean("IsInitialized")) {
            reactorState = NetherReactorState.INITIALIZED;
        } else {
            reactorState = NetherReactorState.READY;
        }
        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        NetherReactorState reactorState = getReactorState();
        namedTag.putShort("Progress", getProgress());
        namedTag.putBoolean("HasFinished", reactorState == NetherReactorState.FINISHED);
        namedTag.putBoolean("IsInitialized", reactorState == NetherReactorState.INITIALIZED);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        NetherReactorState reactorState = getReactorState();
        return new CompoundTag()
                .putString("id", BlockEntity.NETHER_REACTOR)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putShort("Progress", getProgress())
                .putBoolean("HasFinished", reactorState == NetherReactorState.FINISHED)
                .putBoolean("IsInitialized", reactorState == NetherReactorState.INITIALIZED);
    }
}
