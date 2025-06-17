package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.ComposterEmptyEvent;
import cn.nukkit.event.block.ComposterFillEvent;
import cn.nukkit.item.*;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.DyeColor;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.util.concurrent.ThreadLocalRandom;

public class BlockComposter extends BlockTransparentMeta implements ItemID {

    private static final Int2IntOpenHashMap ITEMS = new Int2IntOpenHashMap();

    static {
        registerItems(30, BEETROOT_SEEDS, DRIED_KELP, GLOW_BERRIES, KELP, MELON_SEEDS, PUMPKIN_SEEDS, SWEET_BERRIES, WHEAT_SEEDS);
        registerItems(50, MELON_SLICE, NETHER_SPROUTS, SUGAR_CANE);
        registerItems(65, APPLE, BEETROOT, CARROT, COCOA, POTATO, NETHER_WART, WHEAT);
        registerItems(85, BAKED_POTATOES, BREAD, COOKIE);
        registerItems(100, CAKE, PUMPKIN_PIE);
        registerBlocks(30, GRASS, LEAVES, LEAVES2, SAPLINGS, SEAGRASS, SMALL_DRIPLEAF, MANGROVE_PROPAGULE, MANGROVE_ROOTS);
        registerBlocks(50, CACTUS, DRIED_KELP_BLOCK, AZALEA_LEAVES_FLOWERED, GLOW_LICHEN, TWISTING_VINES, VINES, WEEPING_VINES);
        registerBlocks(65, AZALEA, BIG_DRIPLEAF, DOUBLE_PLANT, DANDELION, RED_FLOWER, WITHER_ROSE, CRIMSON_FUNGUS, WARPED_FUNGUS, LILY_PAD, MELON_BLOCK, MOSS_BLOCK, BROWN_MUSHROOM, RED_MUSHROOM, PUMPKIN, CARVED_PUMPKIN, CRIMSON_ROOTS, WARPED_ROOTS, SEA_PICKLE, SHROOMLIGHT);
        registerBlocks(85, FLOWERING_AZALEA, HAY_BALE, BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK, BLOCK_NETHER_WART_BLOCK, PITCHER_PLANT, TORCHFLOWER, WARPED_WART_BLOCK);
        registerBlock(50, TALL_GRASS, 0);
        registerBlock(50, TALL_GRASS, 1);
        registerBlock(65, TALL_GRASS, 2);
        registerBlock(65, TALL_GRASS, 3);
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
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        return getDamage();
    }

    public boolean incrementLevel() {
        int fillLevel = getDamage() + 1;
        setDamage(fillLevel);
        this.level.setBlock(this, this, true, true);
        return fillLevel == 8;
    }

    public boolean isFull() {
        return getDamage() == 8;
    }

    public boolean isEmpty() {
        return getDamage() == 0;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getCount() <= 0 || item.getId() == Item.AIR) {
            return false;
        }

        if (isFull()) {
            empty(item, player);
            return true;
        }

        int chance = getChance(item);
        if (chance <= 0) {
            return false;
        }

        boolean success = ThreadLocalRandom.current().nextInt(100) < chance;
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

    public Item empty(Player player) {
        return this.empty(null, player);
    }

    public Item empty(Item item, Player player) {
        if (isEmpty()) {
            return null;
        }
        ComposterEmptyEvent event = new ComposterEmptyEvent(this, player, item, new ItemDye(DyeColor.WHITE), 0);
        this.level.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            this.setDamage(event.getNewLevel());
            this.level.setBlock(this, this, true, true);
            if (item != null) {
                this.level.dropItem(add(0.5, 0.85, 0.5), event.getDrop());
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
        ITEMS.put(itemId << 6 | meta & 0x3F, chance);
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
        int chance = ITEMS.get(item.getId() << 6 | item.getDamage());
        if (chance == 0) {
            chance = ITEMS.get(item.getId() << 6);
        }
        return chance;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }
}
