package cn.nukkit.level;

import cn.nukkit.block.BlockID;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

@Data
@Log4j2
public class BlockPalette {

    private final int protocol;
    private final Int2IntMap legacyToRuntimeId = new Int2IntOpenHashMap();
    private final Int2IntMap runtimeIdToLegacy = new Int2IntOpenHashMap();
    private final Map<CompoundTag, Integer> stateToLegacy = new HashMap<>();
    private volatile boolean locked;

    public BlockPalette(int protocol) {
        this.protocol = protocol;
        this.legacyToRuntimeId.defaultReturnValue(-1);
        this.runtimeIdToLegacy.defaultReturnValue(-1);
    }

    public void clearStates() {
        this.locked = false;
        this.legacyToRuntimeId.clear();
        this.runtimeIdToLegacy.clear();
        this.stateToLegacy.clear();
    }

    public void registerState(int blockId, int data, int runtimeId, CompoundTag blockState) {
        if (this.locked) {
            throw new IllegalStateException("Block palette is already locked!");
        }

        int legacyId = blockId << 6 | data;
        this.legacyToRuntimeId.put(legacyId, runtimeId);
        this.runtimeIdToLegacy.putIfAbsent(runtimeId, legacyId);
        this.stateToLegacy.putIfAbsent(blockState, legacyId);
    }

    public void lock() {
        this.locked = true;
    }

    public int getRuntimeId(int id, int meta) {
        int legacyId = id << 6 | meta;
        int runtimeId = this.legacyToRuntimeId.get(legacyId);
        if (runtimeId == -1) {
            runtimeId = legacyToRuntimeId.get(id << 6);
            if (runtimeId == -1) {
                runtimeId = legacyToRuntimeId.get(BlockID.INFO_UPDATE << 6);
                log.info("Missing block runtime id mappings for {}:{}", id, meta);
            }
        }
        return runtimeId;
    }

    public int getRuntimeId(int legacyId) {
        int runtimeId = this.legacyToRuntimeId.get(legacyId);
        if (runtimeId == -1) {
            log.info("Missing block runtime id mappings for {}", legacyId);
            return legacyToRuntimeId.get(BlockID.INFO_UPDATE << 6);
        }
        return runtimeId;
    }

    public int getLegacyFullId(int runtimeId) {
        return this.runtimeIdToLegacy.get(runtimeId);
    }

    public int getLegacyFullId(CompoundTag state) {
        return this.stateToLegacy.getOrDefault(state, -1);
    }

    public Int2IntMap getLegacyToRuntimeIdMap() {
        return Int2IntMaps.unmodifiable(this.legacyToRuntimeId);
    }
}
