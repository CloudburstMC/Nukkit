package cn.nukkit.level.format.leveldb.structure;

import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.format.leveldb.BlockStateMapping;
import org.cloudburstmc.nbt.NbtMap;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class BlockStateSnapshot {
    private final NbtMap vanillaState;
    private final int runtimeId;
    private final int version;

    @Builder.Default
    private boolean custom = false;

    @Builder.Default
    private int legacyId = -1;
    @Builder.Default
    private int legacyData = -1;

    @Builder.Default
    private int runtimeIdNetworkProtocol = -1;

    public int getLegacyId() {
        if (this.legacyId != -1) {
            return this.legacyId;
        }

        int id = BlockStateMapping.get().getLegacyId(this.runtimeId);
        if (this.version == BlockStateMapping.get().getVersion()) {
            // Cache legacyId only if the mapping is same version,
            // plugins might be passing custom states
            // with unknown legacyId to vanilla mapping
            this.legacyId = id;
        }
        return id;
    }

    public int getLegacyData() {
        if (this.legacyData != -1) {
            return this.legacyData;
        }

        int data = BlockStateMapping.get().getLegacyData(this.runtimeId);
        if (this.version == BlockStateMapping.get().getVersion()) {
            this.legacyData = data;
        }
        return data;
    }

    int getRuntimeIdNetworkProtocol() {
        if (this.runtimeIdNetworkProtocol == -1) {
            this.runtimeIdNetworkProtocol = GlobalBlockPalette.getOrCreateRuntimeId(this.getLegacyId(), this.getLegacyData());
        }

        return this.runtimeIdNetworkProtocol;
    }
}
