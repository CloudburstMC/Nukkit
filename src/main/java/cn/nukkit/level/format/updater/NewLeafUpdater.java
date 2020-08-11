package cn.nukkit.level.format.updater;

import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.ChunkSection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NewLeafUpdater implements Updater {
    private final ChunkSection section;

    @Override
    public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state) {
        if (state.getBlockId() == BlockID.LEAVES2) {
            @SuppressWarnings("deprecation") 
            int legacyDamage = state.getLegacyDamage();
            if ((legacyDamage & 0xE) == 0x0) {
                // No flags, no conversion is needed
                return false;
            }
            
            boolean checkDecayForSure = (legacyDamage & 0x8) == 0x8;
            boolean persistentForSure = (legacyDamage & 0x2) == 0x2;
            if (checkDecayForSure && persistentForSure) {
                // Oh god! This shouldn't happen!
                // I think I won't touch it
                return false;
            }
            if (checkDecayForSure) {
                // Ok, using the right flag positions as indicated in the wiki
                // Nothing needs to be done
                return false;
            }
            if (persistentForSure) {
                // Using the wrong persistent flags, let's fix it
                BlockState fixed = state.withData((legacyDamage & 0b0001) | ((legacyDamage & 0b0110) << 1));
                section.setBlockState(x, y, z, fixed);
                return true;
            }
        }
        return false;
    }
}
