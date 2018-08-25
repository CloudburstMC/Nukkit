package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Leaves;
import com.nukkitx.api.metadata.data.TreeSpecies;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LeavesSerializer implements Serializer<Leaves> {

    public static final Serializer INSTANCE = new LeavesSerializer();

    @Override
    public short readMetadata(Leaves metadata) {
        int woodType = GenericMath.clamp(metadata.getSpecies().ordinal(), 0, 3);

        return (short) (woodType | (metadata.getDecayState().ordinal() << 2));
    }

    @Override
    public Leaves writeMetadata(ItemType type, short metadata) {
        return Leaves.of(TreeSpecies.values()[metadata & 0x03], Leaves.DecayState.values()[(metadata >> 2) & 0x03]);
    }
}
