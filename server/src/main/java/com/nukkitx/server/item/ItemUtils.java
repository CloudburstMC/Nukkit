package com.nukkitx.server.item;

import com.google.common.base.Preconditions;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.ItemStackBuilder;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.ItemTypes;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.*;
import com.nukkitx.protocol.bedrock.data.ItemData;
import com.nukkitx.server.metadata.MetadataSerializers;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ItemUtils {
    public static final NukkitItemStack AIR = new NukkitItemStack(BlockTypes.AIR, 0, null);

    public static ItemStack getItemOrEmpty(@Nullable ItemStack item) {
        if (item == null || ItemStack.isInvalid(item)) {
            return AIR;
        }
        return item;
    }

    public static void checkValidItem(ItemStack item) {
        Preconditions.checkNotNull(item, "item");
        Preconditions.checkArgument(item.getAmount() > 0 && item.getItemType() != BlockTypes.AIR, "Invalid item");
    }

    public static ItemStack createItemInstance(Map<String, Tag<?>> map) {
        ByteTag countTag = (ByteTag) map.get("Count");
        ShortTag damageTag = (ShortTag) map.get("Damage");
        ShortTag idTag = (ShortTag) map.get("id");
        ItemType type = ItemTypes.byId(idTag.getValue());

        ItemStackBuilder builder = new NukkitItemStackBuilder()
                .itemType(type)
                .amount(countTag.getValue())
                .itemData(MetadataSerializers.deserializeMetadata(type, damageTag.getValue()));

        Tag<?> tagTag = map.get("tag");
        if (tagTag instanceof CompoundTag) {
            applyItemData(builder, (CompoundTag) tagTag);
        }

        return builder.build();
    }

    public static void applyItemData(ItemStackBuilder builder, CompoundTag tag) {
        Tag<?> displayTag = tag.get("display");
        if (displayTag instanceof CompoundTag) {
            ((CompoundTag) displayTag).get("Name");
            Tag<?> nameTag = ((CompoundTag) displayTag).get("Name");
            if (nameTag instanceof StringTag) {
                builder.name(((StringTag) nameTag).getValue());
            }
        }
    }

    public static ItemStack[] createItemStacks(ListTag<CompoundTag> tag, int knownSize) {
        ItemStack[] all = new ItemStack[knownSize];
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

    public static List<CompoundTag> createNBT(ItemStack... items) {
        return createNBT(true, items);
    }

    public static List<CompoundTag> createNBT(boolean saveSlot, ItemStack... items) {
        List<CompoundTag> list = new ArrayList<>();

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];

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

    public static CompoundTagBuilder toFullNBT(ItemStack item) {
        return CompoundTagBuilder.builder()
                .byteTag("Count", (byte) item.getAmount())
                .shortTag("Damage", MetadataSerializers.serializeMetadata(item))
                .shortTag("id", (short) item.getItemType().getId())
                .tag(toSpecificNBT(item));
    }

    public static CompoundTag toSpecificNBT(ItemStack item) {
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

    @Nonnull
    public ItemData[] toNetwork(@Nonnull ItemStack... items) {
        Preconditions.checkNotNull(items, "items");
        ItemData[] networkItems = new ItemData[items.length];

        for (int i = 0; i < items.length; i++) {
            networkItems[i] = toNetwork(items[i]);
        }
        return networkItems;
    }

    @Nonnull
    public ItemData toNetwork(@Nullable ItemStack item) {
        if (item == null) {
            return ItemData.AIR;
        }
        return ItemData.of(
                item.getItemType().getId(),
                MetadataSerializers.serializeMetadata(item),
                item.getAmount(),
                toSpecificNBT(item)
        );
    }

    public ItemStack fromNetwork(@Nonnull ItemData item) {
        Preconditions.checkNotNull(item, "item");

        ItemType type = ItemTypes.byId(item.getId());

        ItemStackBuilder builder = new NukkitItemStackBuilder()
                .itemType(type)
                .amount(item.getCount())
                .itemData(MetadataSerializers.deserializeMetadata(type, item.getDamage()));
        if (item.getTag() != null) {
            applyItemData(builder, item.getTag());
        }
        return builder.build();
    }
}
