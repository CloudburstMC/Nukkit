package cn.nukkit.network.protocol;

import cn.nukkit.inventory.FurnaceRecipe;
import cn.nukkit.inventory.ShapedRecipe;
import cn.nukkit.inventory.ShapelessRecipe;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentEntry;
import cn.nukkit.item.enchantment.EnchantmentList;
import cn.nukkit.utils.BinaryStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Nukkit Project Team
 */
public class CraftingDataPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.CRAFTING_DATA_PACKET;

    public static final int ENTRY_SHAPELESS = 0;
    public static final int ENTRY_SHAPED = 1;
    public static final int ENTRY_FURNACE = 2;
    public static final int ENTRY_FURNACE_DATA = 3;
    public static final int ENTRY_ENCHANT_LIST = 4;

    public List<Object> entries;
    public boolean cleanRecipes;

    private static int writeEntry(Object entry, BinaryStream stream) {
        if (entry instanceof ShapelessRecipe) {
            return writeShapelessRecipe(((ShapelessRecipe) entry), stream);
        } else if (entry instanceof ShapedRecipe) {
            return writeShapedRecipe(((ShapedRecipe) entry), stream);
        } else if (entry instanceof FurnaceRecipe) {
            return writeFurnaceRecipe(((FurnaceRecipe) entry), stream);
        } else if (entry instanceof EnchantmentList) {
            return writeEnchantList(((EnchantmentList) entry), stream);
        }
        return -1;
    }

    private static int writeEntry(ShapelessRecipe entry, BinaryStream stream) {
        if (entry != null) {
            return writeShapelessRecipe(entry, stream);
        }
        return -1;
    }

    private static int writeEntry(ShapedRecipe entry, BinaryStream stream) {
        if (entry != null) {
            return writeShapedRecipe(entry, stream);
        }
        return -1;
    }

    private static int writeEntry(FurnaceRecipe entry, BinaryStream stream) {
        if (entry != null) {
            return writeFurnaceRecipe(entry, stream);
        }
        return -1;
    }

    private static int writeEntry(EnchantmentList entry, BinaryStream stream) {
        if (entry != null) {
            return writeEnchantList(entry, stream);
        }
        return -1;
    }

    private static int writeShapelessRecipe(ShapelessRecipe recipe, BinaryStream stream) {
        stream.putInt(recipe.getIngredientCount());

        for (Item item : recipe.getIngredientList()) {
            stream.putSlot(item);
        }

        stream.putInt(1);
        stream.putSlot(recipe.getResult());
        stream.putUUID(recipe.getId());

        return CraftingDataPacket.ENTRY_SHAPELESS;
    }

    private static int writeShapedRecipe(ShapedRecipe recipe, BinaryStream stream) {
        stream.putInt(recipe.getWidth());
        stream.putInt(recipe.getHeight());

        for (int z = 0; z < recipe.getHeight(); ++z) {
            for (int x = 0; x < recipe.getWidth(); ++x) {
                stream.putSlot(recipe.getIngredient(x, z));
            }
        }

        stream.putInt(1);
        stream.putSlot(recipe.getResult());

        stream.putUUID(recipe.getId());

        return CraftingDataPacket.ENTRY_SHAPED;
    }

    private static int writeFurnaceRecipe(FurnaceRecipe recipe, BinaryStream stream) {
        if (recipe.getInput().getDamage() != 0) { //Data recipe
            stream.putInt((recipe.getInput().getId() << 16) | (recipe.getInput().getDamage()));
            stream.putSlot(recipe.getResult());

            return CraftingDataPacket.ENTRY_FURNACE_DATA;
        } else {
            stream.putInt(recipe.getInput().getId());
            stream.putSlot(recipe.getResult());

            return CraftingDataPacket.ENTRY_FURNACE;
        }
    }

    private static int writeEnchantList(EnchantmentList list, BinaryStream stream) {
        stream.putByte((byte) list.getSize());
        for (int i = 0; i < list.getSize(); ++i) {
            EnchantmentEntry entry = list.getSlot(i);
            stream.putInt(entry.getCost());
            stream.putByte((byte) entry.getEnchantments().length);
            for (Enchantment enchantment : entry.getEnchantments()) {
                stream.putInt(enchantment.getId());
                stream.putInt(enchantment.getLevel());
            }
            stream.putString(entry.getRandomName());
        }
        return CraftingDataPacket.ENTRY_ENCHANT_LIST;
    }

    public void addShapelessRecipe(ShapelessRecipe... recipe) {
        Collections.addAll(entries, (Object[]) recipe);
    }

    public void addShapedRecipe(ShapedRecipe... recipe) {
        Collections.addAll(entries, (Object[]) recipe);
    }

    public void addFurnaceRecipe(FurnaceRecipe... recipe) {
        Collections.addAll(entries, (Object[]) recipe);
    }

    public void addEnchantList(EnchantmentList... list) {
        Collections.addAll(entries, (Object[]) list);
    }

    @Override
    public DataPacket clean() {
        entries = new ArrayList<>();
        return super.clean();
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        reset();
        putInt(entries.size());

        BinaryStream writer = new BinaryStream();

        for (Object entry : entries) {
            int entryType = writeEntry(entry, writer);
            if (entryType != 0) {
                putInt(entryType);
                putInt(writer.getCount());
                put(writer.getBuffer());
            } else {
                putInt(-1);
                putInt(0);
            }
            writer.reset();
        }

        putByte((byte) (cleanRecipes ? 1 : 0));
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
