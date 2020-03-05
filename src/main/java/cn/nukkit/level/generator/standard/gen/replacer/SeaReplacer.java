package cn.nukkit.level.generator.standard.gen.replacer;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;

/**
 * Replaces air blocks below a configured sea level with a configured block.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class SeaReplacer implements BlockReplacer {
    public static final Identifier ID = Identifier.fromString("nukkitx:sea");

    @JsonProperty
    private Block block    = BlockRegistry.get().getBlock(BlockIds.WATER, 0);
    @JsonProperty
    private int   seaLevel = 63;

    @Override
    public Block replace(Block prev, int x, int y, int z, double gradX, double gradY, double gradZ, double density) {
        return prev == null && y <= this.seaLevel ? this.block : prev;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @JsonSetter("block")
    private void setBlock(ConstantBlock block) {
        this.block = block.block();
    }

    @JsonSetter("seaLevel")
    private void setSeaLevel(int seaLevel) {
        Preconditions.checkArgument(seaLevel >= 0 && seaLevel < 256, "seaLevel (%s) must be in range 0-255!", seaLevel);
        this.seaLevel = seaLevel;
    }
}
