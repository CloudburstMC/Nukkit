package cn.nukkit.item;

import cn.nukkit.block.BlockIds;
import cn.nukkit.registry.ItemRegistry;
import cn.nukkit.utils.Identifier;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.Tag;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemUtils {

    public static CompoundTag serializeItem(Item item) {
        return serializeItem(item, -1);
    }

    public static CompoundTag serializeItem(Item item, int slot) {
        CompoundTagBuilder tag = CompoundTag.builder()
                .stringTag("Name", item.getId().toString())
                .byteTag("Count", (byte) item.getCount())
                .shortTag("Damage", (short) item.getMeta());
        if (slot >= 0) {
            tag.byteTag("Slot", (byte) slot);
        }

        CompoundTagBuilder nbt = item.getTag().toBuilder();
        item.saveAdditionalData(nbt);
        tag.tag(nbt.build("tag"));

        return tag.buildRootTag();
    }

    public static Item deserializeItem(CompoundTag tag) {
        if (!(tag.contains("Name") || tag.contains("id")) && !tag.contains("Count")) {
            return Item.get(BlockIds.AIR);
        }

        Item item;
        try {
            Identifier identifier;
            if (tag.contains("Name")) {
                identifier = Identifier.fromString(tag.getString("Name"));
            } else {
                identifier = ItemRegistry.get().fromLegacy(tag.getShort("id"));
            }
            item = Item.get(identifier, !tag.contains("Damage") ? 0 : tag.getShort("Damage"), tag.getByte("Count"));
        } catch (Exception e) {
            item = Item.fromString(tag.getString("id"));
            item.setMeta(!tag.contains("Damage") ? 0 : tag.getShort("Damage"));
            item.setCount(tag.getByte("Count"));
        }

        Tag<?> tagTag = tag.get("tag");
        if (tagTag instanceof CompoundTag) {
            item.loadAdditionalData((CompoundTag) tagTag);
        }

        return item;
    }
}
