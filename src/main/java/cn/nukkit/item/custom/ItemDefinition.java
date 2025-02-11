package cn.nukkit.item.custom;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEdible;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ItemDefinition {

    // Prefer having properties related to server side behavior in implementation class

    /**
     * Item identifier
     */
    private final String identifier;

    /**
     * Nukkit save ID for the item
     */
    private final int legacyId;

    /**
     * Class which implements the item behavior
     */
    private final Class<? extends Item> implementation;

    /**
     * Item default texture name
     */
    private final String texture;

    /**
     * Override item name given in implementation class constructor
     */
    private final String overrideDisplayName;

    /**
     * Creative inventory page where the item is put into
     */
    private final CreativeCategory creativeCategory;

    /**
     * Group items in creative inventory
     */
    private final String creativeGroup;

    /**
     * Whether item is displayed with enchantment glint even when not enchanted (like enchanted golden apple)
     */
    private final boolean foil;

    /**
     * Whether item can be used to destroy blocks on creative (disable to match sword behavior)
     */
    @Builder.Default private final boolean canDestroyInCreative = true;

    /**
     * Allow plugins to provide properties we haven't implemented yet
     */
    private final List<CompoundTag> customProperties;

    /**
     * Allow plugins to provide components we haven't implemented yet
     */
    private final List<CompoundTag> customComponents;

    /**
     * Get the nbt tag that is sent to client
     */
    public CompoundTag getNetworkData() {
        try {
            Item item = this.implementation.getConstructor(Integer.class, int.class).newInstance(0, 1);
            if (!(item instanceof CustomItem)) {
                throw new IllegalStateException("Implementation of " + this.identifier + " does not implement CustomItem");
            }

            CompoundTag properties = new CompoundTag()
                    .putInt("max_stack_size", item.getMaxStackSize())
                    .putBoolean("hand_equipped", item.isTool())
                    .putBoolean("allow_off_hand", item.allowOffhand())
                    .putBoolean("can_destroy_in_creative", this.canDestroyInCreative);

            properties.putCompound("minecraft:icon", new CompoundTag()
                    .putCompound("textures", new CompoundTag()
                            .putString("default", this.texture)
                    )
            );

            if (this.creativeCategory != null) {
                properties.putInt("creative_category", this.creativeCategory.ordinal());

                if (this.creativeCategory != CreativeCategory.NONE && this.creativeGroup != null) {
                    properties.putString("creative_group", this.creativeGroup);
                }
            }

            if (this.foil) {
                properties.putBoolean("foil", true);
            }

            CompoundTag components = new CompoundTag()
                    .putCompound("item_properties", properties)
                    .putCompound("minecraft:display_name", new CompoundTag()
                            .putString("value", this.overrideDisplayName == null ? item.getName() : this.overrideDisplayName)
                    );

            if (item.getMaxDurability() > 0) {
                components.putCompound("minecraft:durability", new CompoundTag().putInt("max_durability", item.getMaxDurability()));
            }

            if (item.isArmor()) {
                components.putCompound("minecraft:armor", new CompoundTag().putInt("protection", item.getArmorPoints()));

                if (item.isHelmet()) {
                    properties.putString("wearable_slot", "slot.armor.head");
                    components.putCompound("minecraft:wearable", new CompoundTag().putString("slot", "slot.armor.head"));
                } else if (item.isChestplate()) {
                    properties.putString("wearable_slot", "slot.armor.chest");
                    components.putCompound("minecraft:wearable", new CompoundTag().putString("slot", "slot.armor.chest"));
                } else if (item.isLeggings()) {
                    properties.putString("wearable_slot", "slot.armor.legs");
                    components.putCompound("minecraft:wearable", new CompoundTag().putString("slot", "slot.armor.legs"));
                } else if (item.isBoots()) {
                    properties.putString("wearable_slot", "slot.armor.feet");
                    components.putCompound("minecraft:wearable", new CompoundTag().putString("slot", "slot.armor.feet"));
                }
            }

            if (item instanceof ItemEdible) {
                components.putCompound("minecraft:food", new CompoundTag()
                        .putBoolean("can_always_eat", ((ItemEdible) item).canAlwaysEat())
                );

                properties.putInt("use_duration", ((ItemEdible) item).getUseTicks()).putInt("use_animation", ((ItemEdible) item).isDrink() ? 2 : 1);
            }

            if (item.isTool()) {
                properties.putInt("damage", item.getAttackDamage());

                /*if (item.isPickaxe()) {
                    components.putCompound("minecraft:digger", getPickaxeDiggerNBT(item));
                } else if (item.isAxe()) {
                    components.putCompound("minecraft:digger", getAxeDiggerNBT(item));
                } else if (item.isShovel()) {
                    components.putCompound("minecraft:digger", getShovelDiggerNBT(item));
                }*/
            }

            if (this.customProperties != null) {
                for (CompoundTag property : this.customProperties) {
                    components.putCompound(property.getName(), property);
                }
            }

            if (this.customComponents != null) {
                for (CompoundTag component : this.customComponents) {
                    components.putCompound(component.getName(), component);
                }
            }

            return new CompoundTag().putCompound("components", components);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public enum CreativeCategory {
        ALL,
        CONSTRUCTION,
        NATURE,
        EQUIPMENT,
        ITEMS,
        ITEM_COMMAND_ONLY,
        NONE
    }
}
