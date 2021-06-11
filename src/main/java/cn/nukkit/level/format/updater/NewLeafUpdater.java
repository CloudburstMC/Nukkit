package cn.nukkit.level.format.updater;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.ChunkSection;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class NewLeafUpdater implements Updater {
    private final ChunkSection section;
    private boolean forceOldSystem;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public NewLeafUpdater(ChunkSection section) {
        this.section = section;
    }

    @Override
    public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state) {
        if (state.getBlockId() == BlockID.LEAVES2) {
            @SuppressWarnings("deprecation") 
            int legacyDamage = state.getLegacyDamage();
            if ((legacyDamage & 0xE) == 0x0) {
                // No flags, no conversion is needed
                return false;
            }
            
            boolean newSystemForSure = (legacyDamage & 0x8) == 0x8; // New check decay, invalid on old system
            boolean oldSystemForSure = (legacyDamage & 0x2) == 0x2; // Old check decay, invalid on new system
            if (newSystemForSure && oldSystemForSure) {
                // Oh god! This shouldn't happen!
                // But it's happening somehow: https://github.com/PowerNukkit/PowerNukkit/issues/482
                // Keeping the type as old system, letting it check decay and making it non-persistent
                int newData = legacyDamage & 0b0001; // Wood type
                newData |= 0b1000; // Check-decay
                BlockState fixed = state.withData(newData);
                section.setBlockState(x, y, z, fixed);
                return true;
            }
            if (newSystemForSure) {
                // Ok, using the right flag positions as indicated in the wiki
                // Nothing needs to be done
                return false;
            }
            if (forceOldSystem || oldSystemForSure) {
                // Using the old incorrect persistent flags, let's fix it and force a check decay
                int newData = legacyDamage & 0b0001; // Wood type
                boolean persistent = (legacyDamage & 0x04) == 0x04;
                if (persistent) {
                    newData |= 0b0100; // Persistent
                } 
                
                if (oldSystemForSure || !persistent) {
                    newData |= 0b1000; // Check Decay
                }
                
                BlockState fixed = state.withData(newData);
                if (newData == legacyDamage) {
                    return false;
                }
                section.setBlockState(x, y, z, fixed);
                return true;
            }
        }
        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isForceOldSystem() {
        return this.forceOldSystem;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setForceOldSystem(boolean forceOldSystem) {
        this.forceOldSystem = forceOldSystem;
    }
}
