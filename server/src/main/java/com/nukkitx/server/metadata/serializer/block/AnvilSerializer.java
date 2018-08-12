package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Anvil;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.block.AnvilBlock;
import com.nukkitx.api.metadata.item.AnvilItem;
import com.nukkitx.server.math.DirectionHelper;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnvilSerializer implements Serializer {

    public static final Serializer INSTANCE = new AnvilSerializer();

    @Override
    public short readMetadata(BlockState state) {
        AnvilBlock anvil = state.ensureMetadata(AnvilBlock.class);

        return (short) ((anvil.getDamage().ordinal() << 2) | (DirectionHelper.toMeta(anvil.getDirection(), DirectionHelper.SeqType.TYPE_2))); //TODO: cehck seq type
    }

    @Override
    public short readMetadata(ItemInstance item) {
        AnvilItem anvil = item.ensureMetadata(AnvilItem.class);

        return (short) anvil.getDamage().ordinal();
    }

    @Override
    public Metadata writeMetadata(ItemType type, short metadata) {
        int dmgVal = GenericMath.clamp(type.isBlock() ? (metadata & 0x0c) >> 2 : metadata & 0x03, 0, Anvil.Damage.values().length - 1);
        Anvil.Damage dmg = AnvilBlock.Damage.values()[dmgVal];

        return type.isBlock() ? AnvilBlock.of(DirectionHelper.fromMeta(metadata & 0x03, DirectionHelper.SeqType.TYPE_2), dmg) : AnvilItem.of(dmg);
    }
}
