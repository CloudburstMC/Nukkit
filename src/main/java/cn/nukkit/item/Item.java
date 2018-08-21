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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Item implements Cloneable, BlockID, ItemID {
    //Normal Item IDs

    protected static String UNKNOWN_STR = "Unknown";
    public static Class[] list = null;

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
            list = new Class[65535];
            list[IRON_SHOVEL] = ItemShovelIron.class; //256
            list[IRON_PICKAXE] = ItemPickaxeIron.class; //257
            list[IRON_AXE] = ItemAxeIron.class; //258
            list[FLINT_AND_STEEL] = ItemFlintSteel.class; //259
            list[APPLE] = ItemApple.class; //260
            list[BOW] = ItemBow.class; //261
            list[ARROW] = ItemArrow.class; //262
            list[COAL] = ItemCoal.class; //263
            list[DIAMOND] = ItemDiamond.class; //264
            list[IRON_INGOT] = ItemIngotIron.class; //265
            list[GOLD_INGOT] = ItemIngotGold.class; //266
            list[IRON_SWORD] = ItemSwordIron.class; //267
            list[WOODEN_SWORD] = ItemSwordWood.class; //268
            list[WOODEN_SHOVEL] = ItemShovelWood.class; //269
            list[WOODEN_PICKAXE] = ItemPickaxeWood.class; //270
            list[WOODEN_AXE] = ItemAxeWood.class; //271
            list[STONE_SWORD] = ItemSwordStone.class; //272
            list[STONE_SHOVEL] = ItemShovelStone.class; //273
            list[STONE_PICKAXE] = ItemPickaxeStone.class; //274
            list[STONE_AXE] = ItemAxeStone.class; //275
            list[DIAMOND_SWORD] = ItemSwordDiamond.class; //276
            list[DIAMOND_SHOVEL] = ItemShovelDiamond.class; //277
            list[DIAMOND_PICKAXE] = ItemPickaxeDiamond.class; //278
            list[DIAMOND_AXE] = ItemAxeDiamond.class; //279
            list[STICK] = ItemStick.class; //280
            list[BOWL] = ItemBowl.class; //281
            list[MUSHROOM_STEW] = ItemMushroomStew.class; //282
            list[GOLD_SWORD] = ItemSwordGold.class; //283
            list[GOLD_SHOVEL] = ItemShovelGold.class; //284
            list[GOLD_PICKAXE] = ItemPickaxeGold.class; //285
            list[GOLD_AXE] = ItemAxeGold.class; //286
            list[STRING] = ItemString.class; //287
            list[FEATHER] = ItemFeather.class; //288
            list[GUNPOWDER] = ItemGunpowder.class; //289
            list[WOODEN_HOE] = ItemHoeWood.class; //290
            list[STONE_HOE] = ItemHoeStone.class; //291
            list[IRON_HOE] = ItemHoeIron.class; //292
            list[DIAMOND_HOE] = ItemHoeDiamond.class; //293
            list[GOLD_HOE] = ItemHoeGold.class; //294
            list[WHEAT_SEEDS] = ItemSeedsWheat.class; //295
            list[WHEAT] = ItemWheat.class; //296
            list[BREAD] = ItemBread.class; //297
            list[LEATHER_CAP] = ItemHelmetLeather.class; //298
            list[LEATHER_TUNIC] = ItemChestplateLeather.class; //299
            list[LEATHER_PANTS] = ItemLeggingsLeather.class; //300
            list[LEATHER_BOOTS] = ItemBootsLeather.class; //301
            list[CHAIN_HELMET] = ItemHelmetChain.class; //302
            list[CHAIN_CHESTPLATE] = ItemChestplateChain.class; //303
            list[CHAIN_LEGGINGS] = ItemLeggingsChain.class; //304
            list[CHAIN_BOOTS] = ItemBootsChain.class; //305
            list[IRON_HELMET] = ItemHelmetIron.class; //306
            list[IRON_CHESTPLATE] = ItemChestplateIron.class; //307
            list[IRON_LEGGINGS] = ItemLeggingsIron.class; //308
            list[IRON_BOOTS] = ItemBootsIron.class; //309
            list[DIAMOND_HELMET] = ItemHelmetDiamond.class; //310
            list[DIAMOND_CHESTPLATE] = ItemChestplateDiamond.class; //311
            list[DIAMOND_LEGGINGS] = ItemLeggingsDiamond.class; //312
            list[DIAMOND_BOOTS] = ItemBootsDiamond.class; //313
            list[GOLD_HELMET] = ItemHelmetGold.class; //314
            list[GOLD_CHESTPLATE] = ItemChestplateGold.class; //315
            list[GOLD_LEGGINGS] = ItemLeggingsGold.class; //316
            list[GOLD_BOOTS] = ItemBootsGold.class; //317
            list[FLINT] = ItemFlint.class; //318
            list[RAW_PORKCHOP] = ItemPorkchopRaw.class; //319
            list[COOKED_PORKCHOP] = ItemPorkchopCooked.class; //320
            list[PAINTING] = ItemPainting.class; //321
            list[GOLDEN_APPLE] = ItemAppleGold.class; //322
            list[SIGN] = ItemSign.class; //323
            list[WOODEN_DOOR] = ItemDoorWood.class; //324
            list[BUCKET] = ItemBucket.class; //325

            list[MINECART] = ItemMinecart.class; //328
            list[SADDLE] = ItemSaddle.class; //329
            list[IRON_DOOR] = ItemDoorIron.class; //330
            list[REDSTONE] = ItemRedstone.class; //331
            list[SNOWBALL] = ItemSnowball.class; //332
            list[BOAT] = ItemBoat.class; //333
            list[LEATHER] = ItemLeather.class; //334

            list[BRICK] = ItemBrick.class; //336
            list[CLAY] = ItemClay.class; //337
            list[SUGARCANE] = ItemSugarcane.class; //338
            list[PAPER] = ItemPaper.class; //339
            list[BOOK] = ItemBook.class; //340
            list[SLIMEBALL] = ItemSlimeball.class; //341
            list[MINECART_WITH_CHEST] = ItemMinecartChest.class; //342

            list[EGG] = ItemEgg.class; //344
            list[COMPASS] = ItemCompass.class; //345
            list[FISHING_ROD] = ItemFishingRod.class; //346
            list[CLOCK] = ItemClock.class; //347
            list[GLOWSTONE_DUST] = ItemGlowstoneDust.class; //348
            list[RAW_FISH] = ItemFish.class; //349
            list[COOKED_FISH] = ItemFishCooked.class; //350
            list[DYE] = ItemDye.class; //351
            list[BONE] = ItemBone.class; //352
            list[SUGAR] = ItemSugar.class; //353
            list[CAKE] = ItemCake.class; //354
            list[BED] = ItemBed.class; //355
            list[REPEATER] = ItemRedstoneRepeater.class; //356
            list[COOKIE] = ItemCookie.class; //357
            list[MAP] = ItemMap.class; //358
            list[SHEARS] = ItemShears.class; //359
            list[MELON] = ItemMelon.class; //360
            list[PUMPKIN_SEEDS] = ItemSeedsPumpkin.class; //361
            list[MELON_SEEDS] = ItemSeedsMelon.class; //362
            list[RAW_BEEF] = ItemBeefRaw.class; //363
            list[STEAK] = ItemSteak.class; //364
            list[RAW_CHICKEN] = ItemChickenRaw.class; //365
            list[COOKED_CHICKEN] = ItemChickenCooked.class; //366
            list[ROTTEN_FLESH] = ItemRottenFlesh.class; //367
            list[ENDER_PEARL] = ItemEnderPearl.class; //368
            list[BLAZE_ROD] = ItemBlazeRod.class; //369
            list[GHAST_TEAR] = ItemGhastTear.class; //370
            list[GOLD_NUGGET] = ItemNuggetGold.class; //371
            list[NETHER_WART] = ItemNetherWart.class; //372
            list[POTION] = ItemPotion.class; //373
            list[GLASS_BOTTLE] = ItemGlassBottle.class; //374
            list[SPIDER_EYE] = ItemSpiderEye.class; //375
            list[FERMENTED_SPIDER_EYE] = ItemSpiderEyeFermented.class; //376
            list[BLAZE_POWDER] = ItemBlazePowder.class; //377
            list[MAGMA_CREAM] = ItemMagmaCream.class; //378
            list[BREWING_STAND] = ItemBrewingStand.class; //379
            list[CAULDRON] = ItemCauldron.class; //380
            list[ENDER_EYE] = ItemEnderEye.class; //381
            list[GLISTERING_MELON] = ItemMelonGlistering.class; //382
            list[SPAWN_EGG] = ItemSpawnEgg.class; //383
            list[EXPERIENCE_BOTTLE] = ItemExpBottle.class; //384
            list[FIRE_CHARGE] = ItemFireCharge.class; //385
            //TODO: list[BOOK_AND_QUILL] = ItemBookAndQuill.class; //386
            list[WRITTEN_BOOK] = ItemBookWritten.class; //387
            list[EMERALD] = ItemEmerald.class; //388
            list[ITEM_FRAME] = ItemItemFrame.class; //389
            list[FLOWER_POT] = ItemFlowerPot.class; //390
            list[CARROT] = ItemCarrot.class; //391
            list[BAKED_POTATO] = ItemPotatoBaked.class; //393
            list[POISONOUS_POTATO] = ItemPotatoPoisonous.class; //394
            //TODO: list[EMPTY_MAP] = ItemEmptyMap.class; //395
            //TODO: list[GOLDEN_CARROT] = ItemCarrotGolden.class; //396
            list[SKULL] = ItemSkull.class; //397
            list[CARROT_ON_A_STICK] = ItemCarrotOnAStick.class; //398
            list[NETHER_STAR] = ItemNetherStar.class; //399
            list[PUMPKIN_PIE] = ItemPumpkinPie.class; //400
            list[FIREWORKS] = ItemFirework.class; //401

            list[ENCHANTED_BOOK] = ItemBookEnchanted.class; //403
            list[COMPARATOR] = ItemRedstoneComparator.class; //404
            list[NETHER_BRICK] = ItemNetherBrick.class; //405
            list[QUARTZ] = ItemQuartz.class; //406
            list[MINECART_WITH_TNT] = ItemMinecartTNT.class; //407
            list[MINECART_WITH_HOPPER] = ItemMinecartHopper.class; //408
            list[PRISMARINE_SHARD] = ItemPrismarineShard.class; //409
            list[HOPPER] = ItemHopper.class;
            list[RAW_RABBIT] = ItemRabbitRaw.class; //411
            list[COOKED_RABBIT] = ItemRabbitCooked.class; //412
            list[RABBIT_STEW] = ItemRabbitStew.class; //413
            list[RABBIT_FOOT] = ItemRabbitFoot.class; //414
            //TODO: list[RABBIT_HIDE] = ItemRabbitHide.class; //415
            list[LEATHER_HORSE_ARMOR] = ItemHorseArmorLeather.class; //416
            list[IRON_HORSE_ARMOR] = ItemHorseArmorIron.class; //417
            list[GOLD_HORSE_ARMOR] = ItemHorseArmorGold.class; //418
            list[DIAMOND_HORSE_ARMOR] = ItemHorseArmorDiamond.class; //419
            //TODO: list[LEAD] = ItemLead.class; //420
            //TODO: list[NAME_TAG] = ItemNameTag.class; //421
            list[PRISMARINE_CRYSTALS] = ItemPrismarineCrystals.class; //422
            list[RAW_MUTTON] = ItemMuttonRaw.class; //423
            list[COOKED_MUTTON] = ItemMuttonCooked.class; //424

            list[END_CRYSTAL] = ItemEndCrystal.class; //426
            list[SPRUCE_DOOR] = ItemDoorSpruce.class; //427
            list[BIRCH_DOOR] = ItemDoorBirch.class; //428
            list[JUNGLE_DOOR] = ItemDoorJungle.class; //429
            list[ACACIA_DOOR] = ItemDoorAcacia.class; //430
            list[DARK_OAK_DOOR] = ItemDoorDarkOak.class; //431
            list[CHORUS_FRUIT] = ItemChorusFruit.class; //432
            //TODO: list[POPPED_CHORUS_FRUIT] = ItemChorusFruitPopped.class; //433

            //TODO: list[DRAGON_BREATH] = ItemDragonBreath.class; //437
            list[SPLASH_POTION] = ItemPotionSplash.class; //438

            list[LINGERING_POTION] = ItemPotionLingering.class; //441

            list[ELYTRA] = ItemElytra.class; //444

            //TODO: list[SHULKER_SHELL] = ItemShulkerShell.class; //445

            list[BEETROOT] = ItemBeetroot.class; //457
            list[BEETROOT_SEEDS] = ItemSeedsBeetroot.class; //458
            list[BEETROOT_SOUP] = ItemBeetrootSoup.class; //459
            list[RAW_SALMON] = ItemSalmon.class; //460
            list[CLOWNFISH] = ItemClownfish.class; //461
            list[PUFFERFISH] = ItemPufferfish.class; //462
            list[COOKED_SALMON] = ItemSalmonCooked.class; //463

            list[GOLDEN_APPLE_ENCHANTED] = ItemAppleGoldEnchanted.class; //466
            list[RECORD_11] = ItemRecord11.class;
            list[RECORD_CAT] = ItemRecordCat.class;
            list[RECORD_13] = ItemRecord13.class;
            list[RECORD_BLOCKS] = ItemRecordBlocks.class;
            list[RECORD_CHIRP] = ItemRecordChirp.class;
            list[RECORD_FAR] = ItemRecordFar.class;
            list[RECORD_WARD] = ItemRecordWard.class;
            list[RECORD_MALL] = ItemRecordMall.class;
            list[RECORD_MELLOHI] = ItemRecordMellohi.class;
            list[RECORD_STAL] = ItemRecordStal.class;
            list[RECORD_STRAD] = ItemRecordStrad.class;
            list[RECORD_WAIT] = ItemRecordWait.class;

            for (int i = 0; i < 256; ++i) {
                if (Block.list[i] != null) {
                    list[i] = Block.list[i];
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
            Class c = list[id];
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
            if (enchTag instanceof ListTag) {
                return true;
            }
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

        return enchantments.stream().toArray(Enchantment[]::new);
    }

    public boolean hasCustomName() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = this.getNamedTag();
        if (tag.contains("display")) {
            Tag tag1 = tag.get("display");
            if (tag1 instanceof CompoundTag && ((CompoundTag) tag1).contains("Name") && ((CompoundTag) tag1).get("Name") instanceof StringTag) {
                return true;
            }
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
