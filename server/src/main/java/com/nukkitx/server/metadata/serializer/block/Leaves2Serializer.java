package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Leaves;
import com.nukkitx.api.metadata.block.Leaves2;
import com.nukkitx.api.metadata.data.TreeSpecies;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Leaves2Serializer implements Serializer<Leaves2> {

    public static final Serializer INSTANCE = new Leaves2Serializer();

    @Override
    public short readMetadata(Leaves2 metadata) {
        int woodType = GenericMath.clamp(metadata.getSpecies().ordinal() - 4, 0, 1);

        return (short) (woodType | (metadata.getDecayState().ordinal() << 2));
    }

    @Override
    public Leaves2 writeMetadata(ItemType type, short metadata) {
        return Leaves2.of(TreeSpecies.values()[GenericMath.clamp((metadata & 0x03) + 4, 0, 1)], Leaves.DecayState.values()[(metadata >> 2) & 0x03]);
    }
}
