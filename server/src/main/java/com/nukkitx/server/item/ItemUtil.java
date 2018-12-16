package com.nukkitx.server.item;

import com.google.common.base.Preconditions;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemInstanceBuilder;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.ItemTypes;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.*;
import com.nukkitx.protocol.bedrock.data.Item;
import com.nukkitx.server.metadata.MetadataSerializers;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ItemUtil {

    public static ItemInstance createItemInstance(Map<String, Tag<?>> map) {
        ByteTag countTag = (ByteTag) map.get("Count");
        ShortTag damageTag = (ShortTag) map.get("Damage");
        ShortTag idTag = (ShortTag) map.get("id");
        ItemType type = ItemTypes.byId(idTag.getValue());

        ItemInstanceBuilder builder = new NukkitItemInstanceBuilder()
                .itemType(type)
                .amount(countTag.getValue())
                .itemData(MetadataSerializers.deserializeMetadata(type, damageTag.getValue()));

        Tag<?> tagTag = map.get("tag");
        if (tagTag instanceof CompoundTag) {
            applyItemData(builder, (CompoundTag) tagTag);
        }

        return builder.build();
    }

    public static void applyItemData(ItemInstanceBuilder builder, CompoundTag tag) {
        Tag<?> displayTag = tag.get("display");
        if (displayTag instanceof CompoundTag) {
            ((CompoundTag) displayTag).get("Name");
            Tag<?> nameTag = ((CompoundTag) displayTag).get("Name");
            if (nameTag instanceof StringTag) {
                builder.name(((StringTag) nameTag).getValue());
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
            all[inSlotTag.getValue()] = createItemInstance(slotMap);
        }
        return all;
    }

    public static List<CompoundTag> createNBT(ItemInstance... items) {
        return createNBT(true, items);
    }

    public static List<CompoundTag> createNBT(boolean saveSlot, ItemInstance... items) {
        List<CompoundTag> list = new ArrayList<>();

        for (int i = 0; i < items.length; i++) {
            ItemInstance item = items[i];

            if (item == null) {
                continue;
            }

            CompoundTagBuilder tag = toFullNBT(item);

            if (saveSlot) {
                tag.byteTag("Slot", (byte) i);
            }

            list.add(tag.buildRootTag());
        }

        return list;
    }

    public static CompoundTagBuilder toFullNBT(ItemInstance item) {
        return CompoundTagBuilder.builder()
                .byteTag("Count", (byte) item.getAmount())
                .shortTag("Damage", MetadataSerializers.serializeMetadata(item))
                .shortTag("id", (short) item.getItemType().getId())
                .tag(toSpecificNBT(item));
    }

    public static CompoundTag toSpecificNBT(ItemInstance item) {
        CompoundTagBuilder display = CompoundTagBuilder.builder();
        // Custom Name
        item.getName().ifPresent(name -> display.stringTag("Name", name));

        // Lore
        List<String> lore = item.getLore();
        if (!lore.isEmpty()) {
            List<StringTag> loreLines = new ArrayList<>();
            lore.forEach(s -> loreLines.add(new StringTag("", s)));
            display.listTag("Lore", StringTag.class, loreLines);
        }

        //TODO: Enchantments
        return display.build("display");
    }

    public static int hash(int id, int damage) {
        return damage << 4 | id;
    }

    public Item[] toNetwork(@Nonnull ItemInstance... items) {
        Preconditions.checkNotNull(items, "items");
        Item[] networkItems = new Item[items.length];

        for (int i = 0; i < items.length; i++) {
            networkItems[i] = toNetwork(items[i]);
        }
        return networkItems;
    }

    public Item toNetwork(@Nonnull ItemInstance item) {
        Preconditions.checkNotNull(item, "item");
        return Item.of(
                item.getItemType().getId(),
                MetadataSerializers.serializeMetadata(item),
                item.getAmount(),
                toSpecificNBT(item)
        );
    }

    public ItemInstance fromNetwork(@Nonnull Item item) {
        Preconditions.checkNotNull(item, "item");

        ItemType type = ItemTypes.byId(item.getId());

        ItemInstanceBuilder builder = new NukkitItemInstanceBuilder()
                .itemType(type)
                .amount(item.getCount())
                .itemData(MetadataSerializers.deserializeMetadata(type, item.getDamage()));
        applyItemData(builder, item.getTag());
        return builder.build();
    }
}
