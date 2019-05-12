package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.Fuel;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.*;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Item implements Cloneable, BlockID, ItemID {
    //Normal Item IDs

    protected static String UNKNOWN_STR = "Unknown";
    public static Map<Integer, Class> list = null;

    protected Block block = null;
    protected final int id;
    protected int meta;
    protected boolean hasMeta = true;
    private byte[] tags = new byte[0];
    private CompoundTag cachedNBT = null;
    public int count;
    protected int durability = 0;
    protected String name;

    public Item(int id) {
        this(id, 0, 1, UNKNOWN_STR);
    }

    public Item(int id, Integer meta) {
        this(id, meta, 1, UNKNOWN_STR);
    }

    public Item(int id, Integer meta, int count) {
        this(id, meta, count, UNKNOWN_STR);
    }

    public Item(int id, Integer meta, int count, String name) {
        this.id = id & 0xffff;
        if (meta != null && meta >= 0) {
            this.meta = meta & 0xffff;
        } else {
            this.hasMeta = false;
        }
        this.count = count;
        this.name = name;
        /*f (this.block != null && this.id <= 0xff && Block.list[id] != null) { //probably useless
            this.block = Block.get(this.id, this.meta);
            this.name = this.block.getName();
        }*/
    }

    public boolean hasMeta() {
        return hasMeta;
    }

    public boolean canBeActivated() {
        return false;
    }

    public static void init() {
        if (list == null) {
            list = new HashMap<>();
            list.put(IRON_SHOVEL, ItemShovelIron.class); //256
            list.put(IRON_PICKAXE, ItemPickaxeIron.class); //257
            list.put(IRON_AXE, ItemAxeIron.class); //258
            list.put(FLINT_AND_STEEL, ItemFlintSteel.class); //259
            list.put(APPLE, ItemApple.class); //260
            list.put(BOW, ItemBow.class); //261
            list.put(ARROW, ItemArrow.class); //262
            list.put(COAL, ItemCoal.class); //263
            list.put(DIAMOND, ItemDiamond.class); //264
            list.put(IRON_INGOT, ItemIngotIron.class); //265
            list.put(GOLD_INGOT, ItemIngotGold.class); //266
            list.put(IRON_SWORD, ItemSwordIron.class); //267
            list.put(WOODEN_SWORD, ItemSwordWood.class); //268
            list.put(WOODEN_SHOVEL, ItemShovelWood.class); //269
            list.put(WOODEN_PICKAXE, ItemPickaxeWood.class); //270
            list.put(WOODEN_AXE, ItemAxeWood.class); //271
            list.put(STONE_SWORD, ItemSwordStone.class); //272
            list.put(STONE_SHOVEL, ItemShovelStone.class); //273
            list.put(STONE_PICKAXE, ItemPickaxeStone.class); //274
            list.put(STONE_AXE, ItemAxeStone.class); //275
            list.put(DIAMOND_SWORD, ItemSwordDiamond.class); //276
            list.put(DIAMOND_SHOVEL, ItemShovelDiamond.class); //277
            list.put(DIAMOND_PICKAXE, ItemPickaxeDiamond.class); //278
            list.put(DIAMOND_AXE, ItemAxeDiamond.class); //279
            list.put(STICK, ItemStick.class); //280
            list.put(BOWL, ItemBowl.class); //281
            list.put(MUSHROOM_STEW, ItemMushroomStew.class); //282
            list.put(GOLD_SWORD, ItemSwordGold.class); //283
            list.put(GOLD_SHOVEL, ItemShovelGold.class); //284
            list.put(GOLD_PICKAXE, ItemPickaxeGold.class); //285
            list.put(GOLD_AXE, ItemAxeGold.class); //286
            list.put(STRING, ItemString.class); //287
            list.put(FEATHER, ItemFeather.class); //288
            list.put(GUNPOWDER, ItemGunpowder.class); //289
            list.put(WOODEN_HOE, ItemHoeWood.class); //290
            list.put(STONE_HOE, ItemHoeStone.class); //291
            list.put(IRON_HOE, ItemHoeIron.class); //292
            list.put(DIAMOND_HOE, ItemHoeDiamond.class); //293
            list.put(GOLD_HOE, ItemHoeGold.class); //294
            list.put(WHEAT_SEEDS, ItemSeedsWheat.class); //295
            list.put(WHEAT, ItemWheat.class); //296
            list.put(BREAD, ItemBread.class); //297
            list.put(LEATHER_CAP, ItemHelmetLeather.class); //298
            list.put(LEATHER_TUNIC, ItemChestplateLeather.class); //299
            list.put(LEATHER_PANTS, ItemLeggingsLeather.class); //300
            list.put(LEATHER_BOOTS, ItemBootsLeather.class); //301
            list.put(CHAIN_HELMET, ItemHelmetChain.class); //302
            list.put(CHAIN_CHESTPLATE, ItemChestplateChain.class); //303
            list.put(CHAIN_LEGGINGS, ItemLeggingsChain.class); //304
            list.put(CHAIN_BOOTS, ItemBootsChain.class); //305
            list.put(IRON_HELMET, ItemHelmetIron.class); //306
            list.put(IRON_CHESTPLATE, ItemChestplateIron.class); //307
            list.put(IRON_LEGGINGS, ItemLeggingsIron.class); //308
            list.put(IRON_BOOTS, ItemBootsIron.class); //309
            list.put(DIAMOND_HELMET, ItemHelmetDiamond.class); //310
            list.put(DIAMOND_CHESTPLATE, ItemChestplateDiamond.class); //311
            list.put(DIAMOND_LEGGINGS, ItemLeggingsDiamond.class); //312
            list.put(DIAMOND_BOOTS, ItemBootsDiamond.class); //313
            list.put(GOLD_HELMET, ItemHelmetGold.class); //314
            list.put(GOLD_CHESTPLATE, ItemChestplateGold.class); //315
            list.put(GOLD_LEGGINGS, ItemLeggingsGold.class); //316
            list.put(GOLD_BOOTS, ItemBootsGold.class); //317
            list.put(FLINT, ItemFlint.class); //318
            list.put(RAW_PORKCHOP, ItemPorkchopRaw.class); //319
            list.put(COOKED_PORKCHOP, ItemPorkchopCooked.class); //320
            list.put(PAINTING, ItemPainting.class); //321
            list.put(GOLDEN_APPLE, ItemAppleGold.class); //322
            list.put(SIGN, ItemSign.class); //323
            list.put(WOODEN_DOOR, ItemDoorWood.class); //324
            list.put(BUCKET, ItemBucket.class); //325

            list.put(MINECART, ItemMinecart.class); //328
            list.put(SADDLE, ItemSaddle.class); //329
            list.put(IRON_DOOR, ItemDoorIron.class); //330
            list.put(REDSTONE, ItemRedstone.class); //331
            list.put(SNOWBALL, ItemSnowball.class); //332
            list.put(BOAT, ItemBoat.class); //333
            list.put(LEATHER, ItemLeather.class); //334

            list.put(BRICK, ItemBrick.class); //336
            list.put(CLAY, ItemClay.class); //337
            list.put(SUGARCANE, ItemSugarcane.class); //338
            list.put(PAPER, ItemPaper.class); //339
            list.put(BOOK, ItemBook.class); //340
            list.put(SLIMEBALL, ItemSlimeball.class); //341
            list.put(MINECART_WITH_CHEST, ItemMinecartChest.class); //342

            list.put(EGG, ItemEgg.class); //344
            list.put(COMPASS, ItemCompass.class); //345
            list.put(FISHING_ROD, ItemFishingRod.class); //346
            list.put(CLOCK, ItemClock.class); //347
            list.put(GLOWSTONE_DUST, ItemGlowstoneDust.class); //348
            list.put(RAW_FISH, ItemFish.class); //349
            list.put(COOKED_FISH, ItemFishCooked.class); //350
            list.put(DYE, ItemDye.class); //351
            list.put(BONE, ItemBone.class); //352
            list.put(SUGAR, ItemSugar.class); //353
            list.put(CAKE, ItemCake.class); //354
            list.put(BED, ItemBed.class); //355
            list.put(REPEATER, ItemRedstoneRepeater.class); //356
            list.put(COOKIE, ItemCookie.class); //357
            list.put(MAP, ItemMap.class); //358
            list.put(SHEARS, ItemShears.class); //359
            list.put(MELON, ItemMelon.class); //360
            list.put(PUMPKIN_SEEDS, ItemSeedsPumpkin.class); //361
            list.put(MELON_SEEDS, ItemSeedsMelon.class); //362
            list.put(RAW_BEEF, ItemBeefRaw.class); //363
            list.put(STEAK, ItemSteak.class); //364
            list.put(RAW_CHICKEN, ItemChickenRaw.class); //365
            list.put(COOKED_CHICKEN, ItemChickenCooked.class); //366
            list.put(ROTTEN_FLESH, ItemRottenFlesh.class); //367
            list.put(ENDER_PEARL, ItemEnderPearl.class); //368
            list.put(BLAZE_ROD, ItemBlazeRod.class); //369
            list.put(GHAST_TEAR, ItemGhastTear.class); //370
            list.put(GOLD_NUGGET, ItemNuggetGold.class); //371
            list.put(NETHER_WART, ItemNetherWart.class); //372
            list.put(POTION, ItemPotion.class); //373
            list.put(GLASS_BOTTLE, ItemGlassBottle.class); //374
            list.put(SPIDER_EYE, ItemSpiderEye.class); //375
            list.put(FERMENTED_SPIDER_EYE, ItemSpiderEyeFermented.class); //376
            list.put(BLAZE_POWDER, ItemBlazePowder.class); //377
            list.put(MAGMA_CREAM, ItemMagmaCream.class); //378
            list.put(BREWING_STAND, ItemBrewingStand.class); //379
            list.put(CAULDRON, ItemCauldron.class); //380
            list.put(ENDER_EYE, ItemEnderEye.class); //381
            list.put(GLISTERING_MELON, ItemMelonGlistering.class); //382
            list.put(SPAWN_EGG, ItemSpawnEgg.class); //383
            list.put(EXPERIENCE_BOTTLE, ItemExpBottle.class); //384
            list.put(FIRE_CHARGE, ItemFireCharge.class); //385
            //TODO: list.put(BOOK_AND_QUILL, ItemBookAndQuill.class); //386
            list.put(WRITTEN_BOOK, ItemBookWritten.class); //387
            list.put(EMERALD, ItemEmerald.class); //388
            list.put(ITEM_FRAME, ItemItemFrame.class); //389
            list.put(FLOWER_POT, ItemFlowerPot.class); //390
            list.put(CARROT, ItemCarrot.class); //391
            list.put(POTATO, ItemPotato.class); //392
            list.put(BAKED_POTATO, ItemPotatoBaked.class); //393
            list.put(POISONOUS_POTATO, ItemPotatoPoisonous.class); //394
            //TODO: list.put(EMPTY_MAP, ItemEmptyMap.class); //395
            //TODO: list.put(GOLDEN_CARROT, ItemCarrotGolden.class); //396
            list.put(SKULL, ItemSkull.class); //397
            list.put(CARROT_ON_A_STICK, ItemCarrotOnAStick.class); //398
            list.put(NETHER_STAR, ItemNetherStar.class); //399
            list.put(PUMPKIN_PIE, ItemPumpkinPie.class); //400
            list.put(FIREWORKS, ItemFirework.class); //401

            list.put(ENCHANTED_BOOK, ItemBookEnchanted.class); //403
            list.put(COMPARATOR, ItemRedstoneComparator.class); //404
            list.put(NETHER_BRICK, ItemNetherBrick.class); //405
            list.put(QUARTZ, ItemQuartz.class); //406
            list.put(MINECART_WITH_TNT, ItemMinecartTNT.class); //407
            list.put(MINECART_WITH_HOPPER, ItemMinecartHopper.class); //408
            list.put(PRISMARINE_SHARD, ItemPrismarineShard.class); //409
            list.put(HOPPER, ItemHopper.class);
            list.put(RAW_RABBIT, ItemRabbitRaw.class); //411
            list.put(COOKED_RABBIT, ItemRabbitCooked.class); //412
            list.put(RABBIT_STEW, ItemRabbitStew.class); //413
            list.put(RABBIT_FOOT, ItemRabbitFoot.class); //414
            //TODO: list.put(RABBIT_HIDE, ItemRabbitHide.class); //415
            list.put(LEATHER_HORSE_ARMOR, ItemHorseArmorLeather.class); //416
            list.put(IRON_HORSE_ARMOR, ItemHorseArmorIron.class); //417
            list.put(GOLD_HORSE_ARMOR, ItemHorseArmorGold.class); //418
            list.put(DIAMOND_HORSE_ARMOR, ItemHorseArmorDiamond.class); //419
            //TODO: list.put(LEAD, ItemLead.class); //420
            //TODO: list.put(NAME_TAG, ItemNameTag.class); //421
            list.put(PRISMARINE_CRYSTALS, ItemPrismarineCrystals.class); //422
            list.put(RAW_MUTTON, ItemMuttonRaw.class); //423
            list.put(COOKED_MUTTON, ItemMuttonCooked.class); //424

            list.put(END_CRYSTAL, ItemEndCrystal.class); //426
            list.put(SPRUCE_DOOR, ItemDoorSpruce.class); //427
            list.put(BIRCH_DOOR, ItemDoorBirch.class); //428
            list.put(JUNGLE_DOOR, ItemDoorJungle.class); //429
            list.put(ACACIA_DOOR, ItemDoorAcacia.class); //430
            list.put(DARK_OAK_DOOR, ItemDoorDarkOak.class); //431
            list.put(CHORUS_FRUIT, ItemChorusFruit.class); //432
            //TODO: list.put(POPPED_CHORUS_FRUIT, ItemChorusFruitPopped.class); //433

            //TODO: list.put(DRAGON_BREATH, ItemDragonBreath.class); //437
            list.put(SPLASH_POTION, ItemPotionSplash.class); //438

            list.put(LINGERING_POTION, ItemPotionLingering.class); //441

            list.put(ELYTRA, ItemElytra.class); //444

            //TODO: list.put(SHULKER_SHELL, ItemShulkerShell.class); //445
            list.put(BANNER, ItemBanner.class); //446

            list.put(TRIDENT, ItemTrident.class); //455

            list.put(BEETROOT, ItemBeetroot.class); //457
            list.put(BEETROOT_SEEDS, ItemSeedsBeetroot.class); //458
            list.put(BEETROOT_SOUP, ItemBeetrootSoup.class); //459
            list.put(RAW_SALMON, ItemSalmon.class); //460
            list.put(CLOWNFISH, ItemClownfish.class); //461
            list.put(PUFFERFISH, ItemPufferfish.class); //462
            list.put(COOKED_SALMON, ItemSalmonCooked.class); //463
            list.put(DRIED_KELP, ItemDriedKelp.class); //464

            list.put(GOLDEN_APPLE_ENCHANTED, ItemAppleGoldEnchanted.class); //466

            list.put(TURTLE_SHELL, ItemTurtleShell.class); //469

            list.put(RECORD_11, ItemRecord11.class);
            list.put(RECORD_CAT, ItemRecordCat.class);
            list.put(RECORD_13, ItemRecord13.class);
            list.put(RECORD_BLOCKS, ItemRecordBlocks.class);
            list.put(RECORD_CHIRP, ItemRecordChirp.class);
            list.put(RECORD_FAR, ItemRecordFar.class);
            list.put(RECORD_WARD, ItemRecordWard.class);
            list.put(RECORD_MALL, ItemRecordMall.class);
            list.put(RECORD_MELLOHI, ItemRecordMellohi.class);
            list.put(RECORD_STAL, ItemRecordStal.class);
            list.put(RECORD_STRAD, ItemRecordStrad.class);
            list.put(RECORD_WAIT, ItemRecordWait.class);

            for (int i = 0; i < 256; ++i) {
                if (Block.list.get(i) != null) {
                    list.put(i, Block.list.get(i));
                }
            }
        }

        initCreativeItems();
    }

    private static final ArrayList<Item> creative = new ArrayList<>();

    private static void initCreativeItems() {
        clearCreativeItems();
        Server server = Server.getInstance();

        String path = server.getDataPath() + "creativeitems.json";
        if (!new File(path).exists()) {
            try {
                Utils.writeFile(path, Server.class.getClassLoader().getResourceAsStream("creativeitems.json"));
            } catch (IOException e) {
                MainLogger.getLogger().logException(e);
                return;
            }
        }
        List<Map> list = new Config(path, Config.YAML).getMapList("items");

        for (Map map : list) {
            try {
                int id = (int) map.get("id");
                int damage = (int) map.getOrDefault("damage", 0);
                String hex = (String) map.get("nbt_hex");
                byte[] nbt = hex != null ? Utils.parseHexBinary(hex) : new byte[0];

                addCreativeItem(Item.get(id, damage, 1, nbt));
            } catch (Exception e) {
                MainLogger.getLogger().logException(e);
            }
        }
    }

    public static void clearCreativeItems() {
        Item.creative.clear();
    }

    public static ArrayList<Item> getCreativeItems() {
        return new ArrayList<>(Item.creative);
    }

    public static void addCreativeItem(Item item) {
        Item.creative.add(item.clone());
    }

    public static void removeCreativeItem(Item item) {
        int index = getCreativeItemIndex(item);
        if (index != -1) {
            Item.creative.remove(index);
        }
    }

    public static boolean isCreativeItem(Item item) {
        for (Item aCreative : Item.creative) {
            if (item.equals(aCreative, !item.isTool())) {
                return true;
            }
        }
        return false;
    }

    public static Item getCreativeItem(int index) {
        return (index >= 0 && index < Item.creative.size()) ? Item.creative.get(index) : null;
    }

    public static int getCreativeItemIndex(Item item) {
        for (int i = 0; i < Item.creative.size(); i++) {
            if (item.equals(Item.creative.get(i), !item.isTool())) {
                return i;
            }
        }
        return -1;
    }

    public static Item get(int id) {
        return get(id, 0);
    }

    public static Item get(int id, Integer meta) {
        return get(id, meta, 1);
    }

    public static Item get(int id, Integer meta, int count) {
        return get(id, meta, count, new byte[0]);
    }

    public static Item get(int id, Integer meta, int count, byte[] tags) {
        try {
            Class c = list.get(id);
            Item item;

            if (c == null) {
                item = new Item(id, meta, count);
            } else if (id < 256) {
                if (meta >= 0) {
                    item = new ItemBlock(Block.get(id, meta), meta, count);
                } else {
                    item = new ItemBlock(Block.get(id), meta, count);
                }
            } else {
                item = ((Item) c.getConstructor(Integer.class, int.class).newInstance(meta, count));
            }

            if (tags.length != 0) {
                item.setCompoundTag(tags);
            }

            return item;
        } catch (Exception e) {
            return new Item(id, meta, count).setCompoundTag(tags);
        }
    }

    public static Item fromString(String str) {
        String[] b = str.trim().replace(' ', '_').replace("minecraft:", "").split(":");

        int id = 0;
        int meta = 0;

        Pattern integerPattern = Pattern.compile("^[1-9]\\d*$");
        if (integerPattern.matcher(b[0]).matches()) {
            id = Integer.valueOf(b[0]);
        } else {
            try {
                id = Item.class.getField(b[0].toUpperCase()).getInt(null);
            } catch (Exception ignore) {
            }
        }

        id = id & 0xFFFF;
        if (b.length != 1) meta = Integer.valueOf(b[1]) & 0xFFFF;

        return get(id, meta);
    }

    public static Item fromJson(Map<String, Object> data) {
        String nbt = (String) data.getOrDefault("nbt_hex", "");

        return get(Utils.toInt(data.get("id")), Utils.toInt(data.getOrDefault("damage", 0)), Utils.toInt(data.getOrDefault("count", 1)), nbt.isEmpty() ? new byte[0] : Utils.parseHexBinary(nbt));
    }

    public static Item[] fromStringMultiple(String str) {
        String[] b = str.split(",");
        Item[] items = new Item[b.length - 1];
        for (int i = 0; i < b.length; i++) {
            items[i] = fromString(b[i]);
        }
        return items;
    }

    public Item setCompoundTag(CompoundTag tag) {
        this.setNamedTag(tag);
        return this;
    }

    public Item setCompoundTag(byte[] tags) {
        this.tags = tags;
        this.cachedNBT = null;
        return this;
    }

    public byte[] getCompoundTag() {
        return tags;
    }

    public boolean hasCompoundTag() {
        return this.tags != null && this.tags.length > 0;
    }

    public boolean hasCustomBlockData() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = this.getNamedTag();
        return tag.contains("BlockEntityTag") && tag.get("BlockEntityTag") instanceof CompoundTag;

    }

    public Item clearCustomBlockData() {
        if (!this.hasCompoundTag()) {
            return this;
        }
        CompoundTag tag = this.getNamedTag();

        if (tag.contains("BlockEntityTag") && tag.get("BlockEntityTag") instanceof CompoundTag) {
            tag.remove("BlockEntityTag");
            this.setNamedTag(tag);
        }

        return this;
    }

    public Item setCustomBlockData(CompoundTag compoundTag) {
        CompoundTag tags = compoundTag.copy();
        tags.setName("BlockEntityTag");

        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }

        tag.putCompound("BlockEntityTag", tags);
        this.setNamedTag(tag);

        return this;
    }

    public CompoundTag getCustomBlockData() {
        if (!this.hasCompoundTag()) {
            return null;
        }

        CompoundTag tag = this.getNamedTag();

        if (tag.contains("BlockEntityTag")) {
            Tag bet = tag.get("BlockEntityTag");
            if (bet instanceof CompoundTag) {
                return (CompoundTag) bet;
            }
        }

        return null;
    }

    public boolean hasEnchantments() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = this.getNamedTag();

        if (tag.contains("ench")) {
            Tag enchTag = tag.get("ench");
            return enchTag instanceof ListTag;
        }

        return false;
    }

    public Enchantment getEnchantment(int id) {
        return getEnchantment((short) (id & 0xffff));
    }

    public Enchantment getEnchantment(short id) {
        if (!this.hasEnchantments()) {
            return null;
        }

        for (CompoundTag entry : this.getNamedTag().getList("ench", CompoundTag.class).getAll()) {
            if (entry.getShort("id") == id) {
                Enchantment e = Enchantment.getEnchantment(entry.getShort("id"));
                if (e != null) {
                    e.setLevel(entry.getShort("lvl"));
                    return e;
                }
            }
        }

        return null;
    }

    public void addEnchantment(Enchantment... enchantments) {
        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }

        ListTag<CompoundTag> ench;
        if (!tag.contains("ench")) {
            ench = new ListTag<>("ench");
            tag.putList(ench);
        } else {
            ench = tag.getList("ench", CompoundTag.class);
        }

        for (Enchantment enchantment : enchantments) {
            boolean found = false;

            for (int k = 0; k < ench.size(); k++) {
                CompoundTag entry = ench.get(k);
                if (entry.getShort("id") == enchantment.getId()) {
                    ench.add(k, new CompoundTag()
                            .putShort("id", enchantment.getId())
                            .putShort("lvl", enchantment.getLevel())
                    );
                    found = true;
                    break;
                }
            }

            if (!found) {
                ench.add(new CompoundTag()
                        .putShort("id", enchantment.getId())
                        .putShort("lvl", enchantment.getLevel())
                );
            }
        }

        this.setNamedTag(tag);
    }

    public Enchantment[] getEnchantments() {
        if (!this.hasEnchantments()) {
            return new Enchantment[0];
        }

        List<Enchantment> enchantments = new ArrayList<>();

        ListTag<CompoundTag> ench = this.getNamedTag().getList("ench", CompoundTag.class);
        for (CompoundTag entry : ench.getAll()) {
            Enchantment e = Enchantment.getEnchantment(entry.getShort("id"));
            if (e != null) {
                e.setLevel(entry.getShort("lvl"));
                enchantments.add(e);
            }
        }

        return enchantments.toArray(new Enchantment[0]);
    }

    public boolean hasCustomName() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = this.getNamedTag();
        if (tag.contains("display")) {
            Tag tag1 = tag.get("display");
            return tag1 instanceof CompoundTag && ((CompoundTag) tag1).contains("Name") && ((CompoundTag) tag1).get("Name") instanceof StringTag;
        }

        return false;
    }

    public String getCustomName() {
        if (!this.hasCompoundTag()) {
            return "";
        }

        CompoundTag tag = this.getNamedTag();
        if (tag.contains("display")) {
            Tag tag1 = tag.get("display");
            if (tag1 instanceof CompoundTag && ((CompoundTag) tag1).contains("Name") && ((CompoundTag) tag1).get("Name") instanceof StringTag) {
                return ((CompoundTag) tag1).getString("Name");
            }
        }

        return "";
    }

    public Item setCustomName(String name) {
        if (name == null || name.equals("")) {
            this.clearCustomName();
        }

        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }
        if (tag.contains("display") && tag.get("display") instanceof CompoundTag) {
            tag.getCompound("display").putString("Name", name);
        } else {
            tag.putCompound("display", new CompoundTag("display")
                    .putString("Name", name)
            );
        }
        this.setNamedTag(tag);
        return this;
    }

    public Item clearCustomName() {
        if (!this.hasCompoundTag()) {
            return this;
        }

        CompoundTag tag = this.getNamedTag();

        if (tag.contains("display") && tag.get("display") instanceof CompoundTag) {
            tag.getCompound("display").remove("Name");
            if (tag.getCompound("display").isEmpty()) {
                tag.remove("display");
            }

            this.setNamedTag(tag);
        }

        return this;
    }

    public String[] getLore() {
        Tag tag = this.getNamedTagEntry("display");
        ArrayList<String> lines = new ArrayList<>();

        if (tag instanceof CompoundTag) {
            CompoundTag nbt = (CompoundTag) tag;
            ListTag<StringTag> lore = nbt.getList("Lore", StringTag.class);

            if (lore.size() > 0) {
                for (StringTag stringTag : lore.getAll()) {
                    lines.add(stringTag.data);
                }
            }
        }

        return lines.toArray(new String[0]);
    }

    public Item setLore(String... lines) {
        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }
        ListTag<StringTag> lore = new ListTag<>("Lore");

        for (String line : lines) {
            lore.add(new StringTag("", line));
        }

        if (!tag.contains("display")) {
            tag.putCompound("display", new CompoundTag("display").putList(lore));
        } else {
            tag.getCompound("display").putList(lore);
        }

        this.setNamedTag(tag);
        return this;
    }

    public Tag getNamedTagEntry(String name) {
        CompoundTag tag = this.getNamedTag();
        if (tag != null) {
            return tag.contains(name) ? tag.get(name) : null;
        }

        return null;
    }

    public CompoundTag getNamedTag() {
        if (!this.hasCompoundTag()) {
            return null;
        }

        if (this.cachedNBT == null) {
            this.cachedNBT = parseCompoundTag(this.tags);
        }

        if (this.cachedNBT != null) {
            this.cachedNBT.setName("");
        }

        return this.cachedNBT;
    }

    public Item setNamedTag(CompoundTag tag) {
        if (tag.isEmpty()) {
            return this.clearNamedTag();
        }
        tag.setName(null);

        this.cachedNBT = tag;
        this.tags = writeCompoundTag(tag);

        return this;
    }

    public Item clearNamedTag() {
        return this.setCompoundTag(new byte[0]);
    }

    public static CompoundTag parseCompoundTag(byte[] tag) {
        try {
            return NBTIO.read(tag, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] writeCompoundTag(CompoundTag tag) {
        try {
            tag.setName("");
            return NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isNull() {
        return this.count <= 0 || this.id == AIR;
    }

    final public String getName() {
        return this.hasCustomName() ? this.getCustomName() : this.name;
    }

    final public boolean canBePlaced() {
        return ((this.block != null) && this.block.canBePlaced());
    }

    public Block getBlock() {
        if (this.block != null) {
            return this.block.clone();
        } else {
            return new BlockAir();
        }
    }

    public int getId() {
        return id;
    }

    public int getDamage() {
        return meta;
    }

    public void setDamage(Integer meta) {
        if (meta != null) {
            this.meta = meta & 0xffff;
        } else {
            this.hasMeta = false;
        }
    }

    public int getMaxStackSize() {
        return 64;
    }

    final public Short getFuelTime() {
        if (!Fuel.duration.containsKey(id)) {
            return null;
        }
        if (this.id != BUCKET || this.meta == 10) {
            return Fuel.duration.get(this.id);
        }
        return null;
    }

    public boolean useOn(Entity entity) {
        return false;
    }

    public boolean useOn(Block block) {
        return false;
    }

    public boolean isTool() {
        return false;
    }

    public int getMaxDurability() {
        return -1;
    }

    public int getTier() {
        return 0;
    }

    public boolean isPickaxe() {
        return false;
    }

    public boolean isAxe() {
        return false;
    }

    public boolean isSword() {
        return false;
    }

    public boolean isShovel() {
        return false;
    }

    public boolean isHoe() {
        return false;
    }

    public boolean isShears() {
        return false;
    }

    public boolean isArmor() {
        return false;
    }

    public boolean isHelmet() {
        return false;
    }

    public boolean isChestplate() {
        return false;
    }

    public boolean isLeggings() {
        return false;
    }

    public boolean isBoots() {
        return false;
    }

    public int getEnchantAbility() {
        return 0;
    }

    public int getAttackDamage() {
        return 1;
    }

    public int getArmorPoints() {
        return 0;
    }

    public int getToughness() {
        return 0;
    }

    public boolean isUnbreakable() {
        return false;
    }

    @Override
    final public String toString() {
        return "Item " + this.name + " (" + this.id + ":" + (!this.hasMeta ? "?" : this.meta) + ")x" + this.count + (this.hasCompoundTag() ? " tags:0x" + Binary.bytesToHexString(this.getCompoundTag()) : "");
    }

    public int getDestroySpeed(Block block, Player player) {
        return 1;
    }

    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        return false;
    }

    /**
     * Called when a player uses the item on air, for example throwing a projectile.
     * Returns whether the item was changed, for example count decrease or durability change.
     *
     * @param player player
     * @param directionVector direction
     * @return item changed
     */
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return false;
    }

    /**
     * Called when a player is using this item and releases it. Used to handle bow shoot actions.
     * Returns whether the item was changed, for example count decrease or durability change.
     *
     * @param player player
     * @return item changed
     */
    public boolean onReleaseUsing(Player player) {
        return false;
    }

    @Override
    public final boolean equals(Object item) {
        return item instanceof Item && this.equals((Item) item, true);
    }

    public final boolean equals(Item item, boolean checkDamage) {
        return equals(item, checkDamage, true);
    }

    public final boolean equals(Item item, boolean checkDamage, boolean checkCompound) {
        if (this.getId() == item.getId() && (!checkDamage || this.getDamage() == item.getDamage())) {
            if (checkCompound) {
                if (Arrays.equals(this.getCompoundTag(), item.getCompoundTag())) {
                    return true;
                } else if (this.hasCompoundTag() && item.hasCompoundTag()) {
                    return this.getNamedTag().equals(item.getNamedTag());
                }
            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether the specified item stack has the same ID, damage, NBT and count as this item stack.
     *
     * @param other item
     * @return equal
     */
    public final boolean equalsExact(Item other) {
        return this.equals(other, true, true) && this.count == other.count;
    }

    @Deprecated
    public final boolean deepEquals(Item item) {
        return equals(item, true);
    }

    @Deprecated
    public final boolean deepEquals(Item item, boolean checkDamage) {
        return equals(item, checkDamage, true);
    }

    @Deprecated
    public final boolean deepEquals(Item item, boolean checkDamage, boolean checkCompound) {
        return equals(item, checkDamage, checkCompound);
    }

    @Override
    public Item clone() {
        try {
            Item item = (Item) super.clone();
            item.tags = this.tags.clone();
            return item;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
