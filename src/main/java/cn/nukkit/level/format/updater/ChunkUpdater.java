package cn.nukkit.level.format.updater;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.generic.BaseChunk;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ChunkUpdater {
    @SuppressWarnings("java:S3400")
    public int getContentVersion() {
        return 8;
    }

    @PowerNukkitOnly("Needed for level backward compatibility")
    @Since("1.3.0.0-PN")
    public void backwardCompatibilityUpdate(Level level, BaseChunk chunk) {
        boolean updated = false;
        for (ChunkSection section : chunk.getSections()) {
            if (section.getContentVersion() >= getContentVersion()) {
                continue;
            }
            
            if (section.getContentVersion() < 5) {
                updated = updateToV8FromV0(level, chunk, updated, section, section.getContentVersion());
            } else if (section.getContentVersion() == 5 || section.getContentVersion() == 7) {
                updated = updateBeehiveToV8(chunk, updated, section, false);
            } else if (section.getContentVersion() == 6) {
                updated = updateBeehiveToV8(chunk, updated, section, true);
            }
        }

        if (updated) {
            chunk.setChanged();
        }
    }
    
    private boolean updateBeehiveToV8(BaseChunk chunk, boolean updated, ChunkSection section, boolean updateDirection) {
        if (walk(chunk, section, new BeehiveUpdater(section, updateDirection))) {
            updated = true;
        }
        section.setContentVersion(8);
        return updated;
    }

    private boolean updateToV8FromV0(Level level, BaseChunk chunk, boolean updated, ChunkSection section, int contentVersion) {
        WallUpdater wallUpdater = new WallUpdater(level, section);
        boolean sectionUpdated = walk(chunk, section, new GroupedUpdaters(
                new MesaBiomeUpdater(section),
                new NewLeafUpdater(section),
                new BeehiveUpdater(section, true),
                contentVersion < 4? wallUpdater : null,
                contentVersion < 1? new StemUpdater(level, section, BlockID.MELON_STEM, BlockID.MELON_BLOCK) : null,
                contentVersion < 1? new StemUpdater(level, section, BlockID.PUMPKIN_STEM, BlockID.PUMPKIN) : null,
                contentVersion < 5? new OldWoodBarkUpdater(section, BlockID.LOG,  0b000) : null,
                contentVersion < 5? new OldWoodBarkUpdater(section, BlockID.LOG2, 0b100) : null,
                contentVersion < 5? new DoorUpdater(chunk, section) : null
        ));

        updated = updated || sectionUpdated;

        int attempts = 0;
        while (sectionUpdated) {
            if (attempts++ >= 5) {
                int x = chunk.getX() << 4 | 0x6;
                int y = section.getY() << 4 | 0x6;
                int z = chunk.getZ() << 4 | 0x6;
                Server.getInstance().getLogger().warning("The chunk section at x:"+x+", y:"+y+", z:"+z+" failed to complete the backward compatibility update 1 after "+attempts+" attempts");
                break;
            }
            sectionUpdated = walk(chunk, section, wallUpdater);
        }

        section.setContentVersion(8);
        return updated;
    }

    private boolean walk(BaseChunk chunk, ChunkSection section, Updater updater) {
        int offsetX = chunk.getX() << 4;
        int offsetZ = chunk.getZ() << 4;
        int offsetY = section.getY() << 4;
        boolean updated = false;
        for (int x = 0; x <= 0xF; x++) {
            for (int z = 0; z <= 0xF; z++) {
                for (int y = 0; y <= 0xF; y++) {
                    BlockState state = section.getBlockState(x, y, z, 0);
                    updated |= updater.update(offsetX, offsetY, offsetZ, x, y, z, state);
                }
            }
        }
        return updated;
    }
}
