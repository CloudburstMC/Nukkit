package cn.nukkit.level.format.updater;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockDoor;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.blockstate.MutableBlockState;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;

import static cn.nukkit.block.BlockID.*;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class DoorUpdater implements Updater {
    private static final int DOOR_OPEN_BIT = 0x04;
    private static final int DOOR_TOP_BIT = 0x08;
    private static final int DOOR_HINGE_BIT = 0x01;
    
    private final Chunk chunk;
    private final ChunkSection section;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public DoorUpdater(Chunk chunk, ChunkSection section) {
        this.chunk = chunk;
        this.section = section;
    }

    @Override
    public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state) {
        switch (state.getBlockId()) {
            case WOODEN_DOOR_BLOCK:
            case DARK_OAK_DOOR_BLOCK:
            case ACACIA_DOOR_BLOCK:
            case BIRCH_DOOR_BLOCK:
            case JUNGLE_DOOR_BLOCK:
            case SPRUCE_DOOR_BLOCK:
            case IRON_DOOR_BLOCK:
                break;
            default:
                return false;
        }
        
        @SuppressWarnings("deprecation") 
        int legacy = state.getLegacyDamage();
        MutableBlockState mutableState = BlockStateRegistry.createMutableState(state.getBlockId());
        if ((legacy & DOOR_TOP_BIT) > 0) {
            mutableState.setBooleanValue(CommonBlockProperties.UPPER_BLOCK, true);
            mutableState.setBooleanValue(BlockDoor.DOOR_HINGE, (legacy & DOOR_HINGE_BIT) > 0);
            
            int underY = offsetY + y - 1;
            if (underY >= 0) {
                BlockState underState = chunk.getBlockState(x, underY, z);
                if (underState.getBlockId() == state.getBlockId()) {
                    mutableState.setPropertyValue(BlockDoor.DOOR_DIRECTION, underState.getPropertyValue(BlockDoor.DOOR_DIRECTION));
                    mutableState.setBooleanValue(CommonBlockProperties.OPEN, underState.getPropertyValue(CommonBlockProperties.OPEN));
                }
            }
        } else {
            mutableState.setBooleanValue(CommonBlockProperties.UPPER_BLOCK, false);
            mutableState.setPropertyValue(BlockDoor.DOOR_DIRECTION, BlockDoor.DOOR_DIRECTION.getValueForMeta(legacy & 0x3));
            mutableState.setBooleanValue(CommonBlockProperties.OPEN, (legacy & DOOR_OPEN_BIT) > 0);
        }
        
        return section.setBlockState(x, y, z, mutableState.getCurrentState());
    }
}
