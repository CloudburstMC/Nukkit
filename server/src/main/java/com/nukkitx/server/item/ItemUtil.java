package com.nukkitx.server.item;

import com.google.common.base.Preconditions;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemInstanceBuilder;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.ItemTypes;
import com.nukkitx.nbt.tag.*;
import com.nukkitx.server.metadata.MetadataSerializers;

import java.util.Map;

public class ItemUtil {

    public static ItemInstance createItemStack(Map<String, Tag<?>> map) {
        ByteTag countTag = (ByteTag) map.get("Count");
        ShortTag damageTag = (ShortTag) map.get("Damage");
        ShortTag idTag = (ShortTag) map.get("id");
        ItemType type = ItemTypes.byId(idTag.getValue());

        ItemInstanceBuilder builder = new NukkitItemInstanceBuilder()
                .itemType(type)
                .amount(countTag.getValue())
                .itemData(MetadataSerializers.deserializeMetadata(type, damageTag.getValue()));

        Tag<?> tagTag = map.get("tag");
        if (tagTag != null) {
            applyItemData(builder, (Map<String, Tag<?>>) tagTag.getValue());
        }

        return builder.build();
    }

    public static void applyItemData(ItemInstanceBuilder builder, Map<String, Tag<?>> map) {
        if (map.containsKey("display")) {
            Map<String, Tag<?>> displayTag = ((CompoundTag) map.get("display")).getValue();
            if (displayTag.containsKey("Name")) {
                builder.name((String) displayTag.get("Name").getValue());
            }
        }
    }

    public static ItemInstance[] createItemStacks(ListTag<CompoundTag> tag, int knownSize) {
        ItemInstance[] all = new ItemInstance[knownSize];
        for (CompoundTag slotTag : tag.getValue()) {
            Map<String, Tag<?>> slotMap = slotTag.getValue();
            Tag<?> inSlotTagRaw = slotMap.get("Slot");
            Preconditions.checkArgument(inSlotTagRaw != null, "Slot NBT tag is missing from compound");
            Preconditions.checkArgument(inSlotTagRaw instanceof ByteTag, "Slot NBT tag is not a Byte");
            ByteTag inSlotTag = (ByteTag) inSlotTagRaw;
            if (inSlotTag.getValue() < 0 || inSlotTag.getValue() >= knownSize) {
                throw new IllegalArgumentException("Found illegal slot " + inSlotTag.getValue());
            }
            all[inSlotTag.getValue()] = createItemStack(slotMap);
        }
        return all;
    }
}
