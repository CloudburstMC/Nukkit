package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Directional;
import com.nukkitx.api.util.data.BlockFace;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @author CreeperFace
 */
@NoArgsConstructor
public class TorchSerializer implements Serializer<Directional> {

    public static final Serializer INSTANCE = new TorchSerializer();

    private static final List<BlockFace> VALUES = Arrays.asList(BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.TOP);

    @Override
    public short readMetadata(Directional metadata) {
        return (short) GenericMath.clamp(VALUES.indexOf(metadata.getFace()), 1, 5);
    }

    @Override
    public Directional writeMetadata(ItemType type, short metadata) {
        return Directional.of(VALUES.get(Math.min(VALUES.size(), metadata) - 1));
    }
}
