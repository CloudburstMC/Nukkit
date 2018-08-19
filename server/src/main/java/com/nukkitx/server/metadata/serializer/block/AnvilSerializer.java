package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Anvil;
import com.nukkitx.api.metadata.block.AnvilBlock;
import com.nukkitx.server.math.DirectionHelper;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnvilSerializer implements Serializer<AnvilBlock> {

    public static final Serializer INSTANCE = new AnvilSerializer();

    @Override
    public short readMetadata(AnvilBlock anvil) {
        return (short) ((anvil.getDamage().ordinal() << 2) | (DirectionHelper.toMeta(anvil.getDirection(), DirectionHelper.SeqType.TYPE_2))); //TODO: cehck seq type
    }

    @Override
    public AnvilBlock writeMetadata(ItemType type, short metadata) {
        int dmgVal = GenericMath.clamp((metadata & 0x0c) >> 2, 0, Anvil.Damage.values().length - 1);
        Anvil.Damage dmg = AnvilBlock.Damage.values()[dmgVal];

        return AnvilBlock.of(DirectionHelper.fromMeta(metadata & 0x03, DirectionHelper.SeqType.TYPE_2), dmg);
    }
}
