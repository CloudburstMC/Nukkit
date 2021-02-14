package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.event.block.ComposterEmptyEvent;
import cn.nukkit.event.block.ComposterFillEvent;
import cn.nukkit.item.*;
import cn.nukkit.level.Sound;
import cn.nukkit.math.MathHelper;
import cn.nukkit.utils.DyeColor;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockComposter extends BlockSolidMeta implements ItemID {

    private static Int2IntOpenHashMap compostableItems = new Int2IntOpenHashMap();
    private static final IntBlockProperty COMPOSTER_FILL_LEVEL = new IntBlockProperty("composter_fill_level", false, 8);
    public static final BlockProperties PROPERTIES = new BlockProperties(COMPOSTER_FILL_LEVEL);
    static {
        registerDefaults();
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockComposter() {
        this(0);
    }

    public BlockComposter(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COMPOSTER;
    }

    @Override
    public String getName() {
        return "Composter";
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 0.6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        return getPropertyValue(COMPOSTER_FILL_LEVEL);
    }

    public boolean incrementLevel() {
        int fillLevel = getPropertyValue(COMPOSTER_FILL_LEVEL) + 1;
        setPropertyValue(COMPOSTER_FILL_LEVEL, fillLevel);
        this.level.setBlock(this, this, true, true);
        return fillLevel == 8;
    }

    public boolean isFull() {
        return getPropertyValue(COMPOSTER_FILL_LEVEL) == 8;
    }

    public boolean isEmpty() {
        return getPropertyValue(COMPOSTER_FILL_LEVEL) == 0;
    }

    @PowerNukkitDifference(info = "Player is null when is called from BlockEntityHopper")
    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (item.getCount() <= 0 || item.getId() == Item.AIR) {
            return false;
        }

        if (isFull()) {
            ComposterEmptyEvent event = new ComposterEmptyEvent(this, player, item, MinecraftItemID.BONE_MEAL.get(1), 0);
            this.level.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                setDamage(event.getNewLevel());
                this.level.setBlock(this, this, true, true);
                this.level.dropItem(add(0.5, 0.85, 0.5), event.getDrop(), event.getMotion(), false, 10);
                this.level.addSound(add(0.5 , 0.5, 0.5), Sound.BLOCK_COMPOSTER_EMPTY);
            }
            return true;
        }

        int chance = getChance(item);
        if (chance <= 0) {
            return false;
        }

        boolean success = new Random().nextInt(100) < chance;
        ComposterFillEvent event = new ComposterFillEvent(this, player, item, chance, success);
        this.level.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return true;
        }

        if (player != null && !player.isCreative()) {
            item.setCount(item.getCount() - 1);
        }

        if (event.isSuccess()) {
            if (incrementLevel()) {
                level.addSound(this.add(0.5, 0.5, 0.5), Sound.BLOCK_COMPOSTER_READY);
            } else {
                level.addSound(this.add(0.5, 0.5, 0.5), Sound.BLOCK_COMPOSTER_FILL_SUCCESS);
            }
        } else {
            level.addSound(this.add(0.5, 0.5, 0.5), Sound.BLOCK_COMPOSTER_FILL);
        }

        return true;
    }

    public Item empty() {
        return empty(null, null);
    }

    public Item empty(@Nullable Item item, @Nullable Player player) {
        ComposterEmptyEvent event = new ComposterEmptyEvent(this, player, item, new ItemDye(DyeColor.WHITE), 0);
        this.level.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            setPropertyValue(COMPOSTER_FILL_LEVEL, event.getNewLevel());
            this.level.setBlock(this, this, true, true);
            if (item != null) {
                this.level.dropItem(add(0.5, 0.85, 0.5), event.getDrop(), event.getMotion(), false, 10);
            }
            this.level.addSound(add(0.5 , 0.5, 0.5), Sound.BLOCK_COMPOSTER_EMPTY);
            return event.getDrop();
        }
        return null;
    }

    public static void registerItem(int chance, int itemId) {
        registerItem(chance, itemId, 0);
    }

    public static void registerItem(int chance, int itemId, int meta) {
        compostableItems.put(itemId << 6 | meta & 0x3F, chance);
    }

    public static void registerItems(int chance, int... itemIds) {
        for (int itemId : itemIds) {
            registerItem(chance, itemId, 0);
        }
    }

    public static void registerBlocks(int chance, int... blockIds) {
        for (int blockId : blockIds) {
            registerBlock(chance, blockId, 0);
        }
    }

    public static void registerBlock(int chance, int blockId) {
        registerBlock(chance, blockId, 0);
    }

    public static void registerBlock(int chance, int blockId, int meta) {
        if (blockId > 255) {
            blockId = 255 - blockId;
        }
        registerItem(chance, blockId, meta);
    }

    public static void register(int chance, Item item) {
        registerItem(chance, item.getId(), item.getDamage());
    }

    public static int getChance(Item item) {
        int chance = compostableItems.get(item.getId() << 6 | item.getDamage());
        if (chance == 0) {
            chance = compostableItems.get(item.getId() << 6);
        }
        return chance;
    }

    private static void registerDefaults() {
        registerItems(30, KELP, BEETROOT_SEEDS, DRIED_KELP, MELON_SEEDS, PUMPKIN_SEEDS, SWEET_BERRIES, WHEAT_SEEDS);
        registerItems(50, MELON_SLICE, SUGAR_CANE, NETHER_SPROUTS);
        registerItems(65, APPLE, BEETROOT, CARROT, COCOA, POTATO, WHEAT);
        registerItems(85, BAKED_POTATOES, BREAD, COOKIE);
        registerItems(100, CAKE, PUMPKIN_PIE);

        registerBlocks(30, BLOCK_KELP, LEAVES, LEAVES2, SAPLINGS, SEAGRASS, SWEET_BERRY_BUSH);
        registerBlocks(50, GRASS, CACTUS, DRIED_KELP_BLOCK, VINES, NETHER_SPROUTS_BLOCK, 
                                  TWISTING_VINES, WEEPING_VINES);
        registerBlocks(65, DANDELION, RED_FLOWER, DOUBLE_PLANT, WITHER_ROSE, LILY_PAD, MELON_BLOCK,
                                  PUMPKIN, CARVED_PUMPKIN, SEA_PICKLE, BROWN_MUSHROOM, RED_MUSHROOM, 
                                  WARPED_ROOTS, CRIMSON_ROOTS, SHROOMLIGHT);
        registerBlocks(85, HAY_BALE, BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK, MUSHROOM_STEW);
        registerBlocks(100, CAKE_BLOCK);

        registerBlock(50, TALL_GRASS, 0);
        registerBlock(50, TALL_GRASS, 1);
        registerBlock(65, TALL_GRASS, 2);
        registerBlock(65, TALL_GRASS, 3);
    }
}
