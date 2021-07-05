package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.*;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockproperty.UnknownRuntimeIdException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.Fuel;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.sideeffect.SideEffect;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Modifier;
import java.nio.ByteOrder;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
public class Item implements Cloneable, BlockID, ItemID {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final Item[] EMPTY_ARRAY = new Item[0];
    
    /**
     * Groups:
     * <ol>
     *     <li>namespace (optional)</li>
     *     <li>item name (choice)</li>
     *     <li>damage (optional, for item name)</li>
     *     <li>numeric id (choice)</li>
     *     <li>damage (optional, for numeric id)</li>
     * </ol>
     */
    private static final Pattern ITEM_STRING_PATTERN = Pattern.compile(
            //       1:namespace    2:name           3:damage   4:num-id    5:damage
            "^(?:(?:([a-z_]\\w*):)?([a-z._]\\w*)(?::(-?\\d+))?|(-?\\d+)(?::(-?\\d+))?)$");

    protected static String UNKNOWN_STR = "Unknown";
    public static Class[] list = null;
    
    private static Map<String, Integer> itemIds = Arrays.stream(ItemID.class.getDeclaredFields())
            .filter(field-> field.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL))
            .filter(field -> field.getType().equals(int.class))
            .collect(Collectors.toMap(
                    field -> field.getName().toLowerCase(),
                    field -> {
                        try {
                            return field.getInt(null);
                        } catch (IllegalAccessException e) {
                            throw new InternalError(e);
                        }
                    },
                    (e1, e2) -> e1, LinkedHashMap::new
            ));

    private static Map<String, Integer> blockIds = Arrays.stream(BlockID.class.getDeclaredFields())
            .filter(field-> field.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL))
            .filter(field -> field.getType().equals(int.class))
            .collect(Collectors.toMap(
                    field -> field.getName().toLowerCase(),
                    field -> {
                        try {
                            int blockId = field.getInt(null);
                            if (blockId > 255) {
                                return 255 - blockId;
                            }
                            return blockId;
                        } catch (IllegalAccessException e) {
                            throw new InternalError(e);
                        }
                    },
                    (e1, e2) -> e1, LinkedHashMap::new
            ));

    protected Block block = null;
    protected final int id;
    protected int meta;
    protected boolean hasMeta = true;
    private byte[] tags = EmptyArrays.EMPTY_BYTES;
    private transient CompoundTag cachedNBT = null;
    public int count;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Unused", replaceWith = "meta or getDamage()")
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
        //this.id = id & 0xffff;
        this.id = id;
        if (meta != null && meta >= 0) {
            this.meta = meta & 0xffff;
        } else {
            this.hasMeta = false;
        }
        this.count = count;
        this.name = name != null? name.intern() : null;
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
            list[KELP] = ItemKelp.class; //335

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
            list[BOOK_AND_QUILL] = ItemBookAndQuill.class; //386
            list[WRITTEN_BOOK] = ItemBookWritten.class; //387
            list[EMERALD] = ItemEmerald.class; //388
            list[ITEM_FRAME] = ItemItemFrame.class; //389
            list[FLOWER_POT] = ItemFlowerPot.class; //390
            list[CARROT] = ItemCarrot.class; //391
            list[POTATO] = ItemPotato.class; //392
            list[BAKED_POTATO] = ItemPotatoBaked.class; //393
            list[POISONOUS_POTATO] = ItemPotatoPoisonous.class; //394
            list[EMPTY_MAP] = ItemEmptyMap.class; //395
            list[GOLDEN_CARROT] = ItemCarrotGolden.class; //396
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
            list[RABBIT_HIDE] = ItemRabbitHide.class; //415
            list[LEATHER_HORSE_ARMOR] = ItemHorseArmorLeather.class; //416
            list[IRON_HORSE_ARMOR] = ItemHorseArmorIron.class; //417
            list[GOLD_HORSE_ARMOR] = ItemHorseArmorGold.class; //418
            list[DIAMOND_HORSE_ARMOR] = ItemHorseArmorDiamond.class; //419
            list[LEAD] = ItemLead.class; //420
            list[NAME_TAG] = ItemNameTag.class; //421
            list[PRISMARINE_CRYSTALS] = ItemPrismarineCrystals.class; //422
            list[RAW_MUTTON] = ItemMuttonRaw.class; //423
            list[COOKED_MUTTON] = ItemMuttonCooked.class; //424
            list[ARMOR_STAND] = ItemArmorStand.class; //425
            list[END_CRYSTAL] = ItemEndCrystal.class; //426
            list[SPRUCE_DOOR] = ItemDoorSpruce.class; //427
            list[BIRCH_DOOR] = ItemDoorBirch.class; //428
            list[JUNGLE_DOOR] = ItemDoorJungle.class; //429
            list[ACACIA_DOOR] = ItemDoorAcacia.class; //430
            list[DARK_OAK_DOOR] = ItemDoorDarkOak.class; //431
            list[CHORUS_FRUIT] = ItemChorusFruit.class; //432
            list[POPPED_CHORUS_FRUIT] = ItemChorusFruitPopped.class; //433
            list[BANNER_PATTERN] = ItemBannerPattern.class; //434

            list[DRAGON_BREATH] = ItemDragonBreath.class; //437
            list[SPLASH_POTION] = ItemPotionSplash.class; //438

            list[LINGERING_POTION] = ItemPotionLingering.class; //441

            list[ELYTRA] = ItemElytra.class; //444

            list[SHULKER_SHELL] = ItemShulkerShell.class; //445
            list[BANNER] = ItemBanner.class; //446

            list[TOTEM] = ItemTotem.class; //450
            
            list[IRON_NUGGET] = ItemNuggetIron.class; //452
            
            list[TRIDENT] = ItemTrident.class; //455

            list[BEETROOT] = ItemBeetroot.class; //457
            list[BEETROOT_SEEDS] = ItemSeedsBeetroot.class; //458
            list[BEETROOT_SOUP] = ItemBeetrootSoup.class; //459
            list[RAW_SALMON] = ItemSalmon.class; //460
            list[CLOWNFISH] = ItemClownfish.class; //461
            list[PUFFERFISH] = ItemPufferfish.class; //462
            list[COOKED_SALMON] = ItemSalmonCooked.class; //463
            list[DRIED_KELP] = ItemDriedKelp.class; //464

            list[GOLDEN_APPLE_ENCHANTED] = ItemAppleGoldEnchanted.class; //466
            
            list[TURTLE_SHELL] = ItemTurtleShell.class; //469

            list[CROSSBOW] = ItemCrossbow.class; //471
            list[SPRUCE_SIGN] = ItemSpruceSign.class; //472
            list[BIRCH_SIGN] = ItemBirchSign.class; //473
            list[JUNGLE_SIGN] = ItemJungleSign.class; //474
            list[ACACIA_SIGN] = ItemAcaciaSign.class; //475
            list[DARKOAK_SIGN] = ItemDarkOakSign.class; //476
            list[SWEET_BERRIES] = ItemSweetBerries.class; //477

            list[RECORD_13] = ItemRecord13.class; //500
            list[RECORD_CAT] = ItemRecordCat.class; //501
            list[RECORD_BLOCKS] = ItemRecordBlocks.class; //502
            list[RECORD_CHIRP] = ItemRecordChirp.class; //503
            list[RECORD_FAR] = ItemRecordFar.class; //504
            list[RECORD_MALL] = ItemRecordMall.class; //505
            list[RECORD_MELLOHI] = ItemRecordMellohi.class; //506
            list[RECORD_STAL] = ItemRecordStal.class; //507
            list[RECORD_STRAD] = ItemRecordStrad.class; //508
            list[RECORD_WARD] = ItemRecordWard.class; //509
            list[RECORD_11] = ItemRecord11.class; //510
            list[RECORD_WAIT] = ItemRecordWait.class; //511

            list[SHIELD] = ItemShield.class; //513

            list[CAMPFIRE] = ItemCampfire.class; //720

            list[SUSPICIOUS_STEW] = ItemSuspiciousStew.class; //734

            list[HONEYCOMB] = ItemHoneycomb.class; //736
            list[HONEY_BOTTLE] = ItemHoneyBottle.class; //737
                        
            list[LODESTONECOMPASS] = ItemCompassLodestone.class; //741;
            list[NETHERITE_INGOT] = ItemIngotNetherite.class; //742
            list[NETHERITE_SWORD] = ItemSwordNetherite.class; //743
            list[NETHERITE_SHOVEL] = ItemShovelNetherite.class; //744
            list[NETHERITE_PICKAXE] = ItemPickaxeNetherite.class; //745
            list[NETHERITE_AXE] = ItemAxeNetherite.class; //746
            list[NETHERITE_HOE] = ItemHoeNetherite.class; //747
            list[NETHERITE_HELMET] = ItemHelmetNetherite.class; //748
            list[NETHERITE_CHESTPLATE] = ItemChestplateNetherite.class; //749
            list[NETHERITE_LEGGINGS] = ItemLeggingsNetherite.class; //750
            list[NETHERITE_BOOTS] = ItemBootsNetherite.class; //751
            list[NETHERITE_SCRAP] = ItemScrapNetherite.class; //752
            list[CRIMSON_SIGN] = ItemCrimsonSign.class; //753
            list[WARPED_SIGN] = ItemWarpedSign.class; //754
            list[CRIMSON_DOOR] = ItemDoorCrimson.class; //755
            list[WARPED_DOOR] = ItemDoorWarped.class; //756
            list[WARPED_FUNGUS_ON_A_STICK] = ItemWarpedFungusOnAStick.class; //757
            list[CHAIN] = ItemChain.class; //758
            list[RECORD_PIGSTEP] = ItemRecordPigstep.class; //759
            list[NETHER_SPROUTS] = ItemNetherSprouts.class; //760

            list[SOUL_CAMPFIRE] = ItemCampfireSoul.class; //801
            
            for (int i = 0; i < 256; ++i) {
                if (Block.list[i] != null) {
                    list[i] = Block.list[i];
                }
            }
        }

        initCreativeItems();
    }

    private static List<String> itemList;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static List<String> rebuildItemList() {
        return itemList = Collections.unmodifiableList(Stream.of(
                BlockStateRegistry.getPersistenceNames().stream()
                        .map(name-> name.substring(name.indexOf(':') + 1)),
                itemIds.keySet().stream()
        ).flatMap(Function.identity()).distinct().collect(Collectors.toList()));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static List<String> getItemList() {
        List<String> itemList = Item.itemList;
        if (itemList == null) {
            return rebuildItemList();
        }
        return itemList;
    }

    private static final ArrayList<Item> creative = new ArrayList<>();

    @SneakyThrows(IOException.class)
    @SuppressWarnings("unchecked")
    private static void initCreativeItems() {
        clearCreativeItems();

        Config config = new Config(Config.JSON);
        try(InputStream resourceAsStream = Server.class.getClassLoader().getResourceAsStream("creativeitems.json")) {
            config.load(resourceAsStream);
        }
        List<Map> list = config.getMapList("items");

        for (Map map : list) {
            try {
                Item item = loadCreativeItemEntry(map);
                if (item != null) {
                    addCreativeItem(item);
                }
            } catch (Exception e) {
                log.error("Error while registering a creative item", e);
            }
        }
    }

    private static Item loadCreativeItemEntry(Map<String, Object> data) {
        String nbt = (String) data.get("nbt_b64");
        byte[] nbtBytes = nbt != null ? Base64.getDecoder().decode(nbt) : EmptyArrays.EMPTY_BYTES;

        String id = data.get("id").toString();
        Item item = null;
        if (data.containsKey("damage")) {
            int meta = Utils.toInt(data.get("damage"));
            item = fromString(id + ":" + meta);
        } else if (data.containsKey("blockRuntimeId")) {
            Integer blockId = BlockStateRegistry.getBlockId(id);
            if (blockId == null || blockId > BlockID.QUARTZ_BRICKS) { //TODO Remove this after the support is added
                return null;
            }
            int blockRuntimeId = -1;
            try {
                blockRuntimeId = ((Number) data.get("blockRuntimeId")).intValue();
                BlockState blockState = BlockStateRegistry.getBlockStateByRuntimeId(blockRuntimeId);
                if (blockState != null) {
                    item = blockState.asItemBlock();
                } else {
                    log.warn("Block state not found for the creative item {} with runtimeId {}", id, blockRuntimeId);
                }
            } catch (Throwable e) {
                log.error("Error loading the creative item {} with runtimeId {}", id, blockRuntimeId, e);
                return null;
            }
        }

        if (item == null) {
            item = fromString(id);
        }

        item.setCompoundTag(nbtBytes);
        return item;
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

    public static Item getBlock(int id) {
        return getBlock(id, 0);
    }

    public static Item getBlock(int id, Integer meta) {
        return getBlock(id, meta, 1);
    }

    public static Item getBlock(int id, Integer meta, int count) {
        return getBlock(id, meta, count, EmptyArrays.EMPTY_BYTES);
    }

    public static Item getBlock(int id, Integer meta, int count, byte[] tags) {
        if (id > 255) {
            id = 255 - id;
        }
        return get(id, meta, count, tags);
    }

    public static Item get(int id) {
        return get(id, 0);
    }

    public static Item get(int id, Integer meta) {
        return get(id, meta, 1);
    }

    public static Item get(int id, Integer meta, int count) {
        return get(id, meta, count, EmptyArrays.EMPTY_BYTES);
    }

    @PowerNukkitDifference(
            info = "Prevents players from getting invalid items by limiting the return to the maximum damage defined in Block.getMaxItemDamage()",
            since = "1.4.0.0-PN")
    public static Item get(int id, Integer meta, int count, byte[] tags) {
        try {
            Class c = null;
            if (id < 0) {
                int blockId = 255 - id;
                c = Block.list[blockId];
            } else {
                c = list[id];
            }
            Item item;

            if (id < 256) {
                int blockId = id < 0? 255 - id : id;
                if (meta == 0) {
                    item = new ItemBlock(Block.get(blockId), 0, count);
                } else if (meta == -1) {
                    // Special case for item instances used in fuzzy recipes
                    item = new ItemBlock(Block.get(blockId), -1);
                } else {
                    BlockState state = BlockState.of(blockId, meta);
                    try {
                        state.validate();
                        item = state.asItemBlock(count);
                    } catch (InvalidBlockPropertyMetaException | InvalidBlockStateException e) {
                        log.warn("Attempted to get an ItemBlock with invalid block state in memory: {}, trying to repair the block state...", state);
                        log.catching(org.apache.logging.log4j.Level.DEBUG, e);
                        Block repaired = state.getBlockRepairing(null, 0, 0, 0);
                        item = repaired.asItemBlock(count);
                        log.error("Attempted to get an illegal item block {}:{} ({}), the meta was changed to {}",
                                id, meta, blockId, item.getDamage(), e);
                    } catch (UnknownRuntimeIdException e) {
                        log.warn("Attempted to get an illegal item block {}:{} ({}), the runtime id was unknown and the meta was changed to 0",
                                id, meta, blockId, e);
                        item = BlockState.of(id).asItemBlock(count);
                    }
                }
            } else if (c == null) {
                item = new Item(id, meta, count);
            } else {
                if (meta == -1) {
                    item = ((Item) c.getConstructor(Integer.class, int.class).newInstance(0, count)).createFuzzyCraftingRecipe();
                } else {
                    item = ((Item) c.getConstructor(Integer.class, int.class).newInstance(meta, count));
                }
            }

            if (tags.length != 0) {
                item.setCompoundTag(tags);
            }
            
            return item;
        } catch (Exception e) {
            log.error("Error getting the item {}:{}{}! Returning an unsafe item stack!", 
                    id, meta, id < 0? " ("+(255 - id)+")":"", e);
            return new Item(id, meta, count).setCompoundTag(tags);
        }
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Improve namespaced name handling and allows to get custom blocks by name")
    public static Item fromString(String str) {
        String normalized = str.trim().replace(' ', '_').toLowerCase();
        Matcher matcher = ITEM_STRING_PATTERN.matcher(normalized);
        if (!matcher.matches()) {
            return get(AIR);
        }
        
        String name = matcher.group(2);
        OptionalInt meta = OptionalInt.empty();
        String metaGroup;
        if (name != null) {
            metaGroup = matcher.group(3);
        } else {
            metaGroup = matcher.group(5);
        }
        if (metaGroup != null) {
            meta = OptionalInt.of(Short.parseShort(metaGroup));
        }

        String numericIdGroup = matcher.group(4);
        if (name != null) {
            String namespaceGroup = matcher.group(1);
            String namespacedId;
            if (namespaceGroup != null) {
                namespacedId = namespaceGroup + ":" + name;
            } else {
                namespacedId = "minecraft:" + name;
            }
            MinecraftItemID minecraftItemId = MinecraftItemID.getByNamespaceId(namespacedId);
            if (minecraftItemId != null) {
                Item item = minecraftItemId.get(1);
                if (meta.isPresent()) {
                    int damage = meta.getAsInt();
                    if (damage < 0) {
                        item = item.createFuzzyCraftingRecipe();
                    } else {
                        item.setDamage(damage);
                    }
                }
                return item;
            } else if (namespaceGroup != null && !namespaceGroup.equals("minecraft:")) {
                return get(AIR);
            }
        } else if (numericIdGroup != null) {
            int id = Integer.parseInt(numericIdGroup);
            return get(id, meta.orElse(0));
        }
        
        if (name == null) {
            return get(AIR);
        }

        int id = 0;

        try {
            id = ItemID.class.getField(name.toUpperCase()).getInt(null);
        } catch (Exception ignore1) {
            try {
                id = BlockID.class.getField(name.toUpperCase()).getInt(null);
                if (id > 255) {
                    id = 255 - id;
                }
            } catch (Exception ignore2) {

            }
        }

        return get(id, meta.orElse(0));
    }

    public static Item fromJson(Map<String, Object> data) {
        return fromJson(data, false);
    }

    private static Item fromJson(Map<String, Object> data, boolean ignoreNegativeItemId) {
        String nbt = (String) data.get("nbt_b64");
        byte[] nbtBytes;
        if (nbt != null) {
            nbtBytes = Base64.getDecoder().decode(nbt);
        } else { // Support old format for backwards compat
            nbt = (String) data.getOrDefault("nbt_hex", null);
            if (nbt == null) {
                nbtBytes = EmptyArrays.EMPTY_BYTES;
            } else {
                nbtBytes = Utils.parseHexBinary(nbt);
            }
        }

        int id = Utils.toInt(data.get("id"));
        if (ignoreNegativeItemId && id < 0) return null;

        return get(id, Utils.toInt(data.getOrDefault("damage", 0)), Utils.toInt(data.getOrDefault("count", 1)), nbtBytes);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static Item fromJsonNetworkId(Map<String, Object> data) {
        String nbt = (String) data.get("nbt_b64");
        byte[] nbtBytes;
        if (nbt != null) {
            nbtBytes = Base64.getDecoder().decode(nbt);
        } else { // Support old format for backwards compat
            nbt = (String) data.getOrDefault("nbt_hex", null);
            if (nbt == null) {
                nbtBytes = EmptyArrays.EMPTY_BYTES;
            } else {
                nbtBytes = Utils.parseHexBinary(nbt);
            }
        }

        int networkId = Utils.toInt(data.get("id"));
        RuntimeItemMapping mapping = RuntimeItems.getRuntimeMapping();
        int legacyFullId = mapping.getLegacyFullId(networkId);
        int id = RuntimeItems.getId(legacyFullId);
        OptionalInt meta = RuntimeItems.hasData(legacyFullId)? OptionalInt.of(RuntimeItems.getData(legacyFullId)) : OptionalInt.empty();
        if (data.containsKey("damage")) {
            int jsonMeta = Utils.toInt(data.get("damage"));
            if (jsonMeta != Short.MAX_VALUE) {
                if (meta.isPresent() && jsonMeta != meta.getAsInt()) {
                    throw new IllegalArgumentException(
                            "Conflicting damage value for " + mapping.getNamespacedIdByNetworkId(networkId) + ". " +
                                    "From json: " + jsonMeta + ", from mapping: " + meta.getAsInt()
                    );
                }
                meta = OptionalInt.of(jsonMeta);
            } else if (!meta.isPresent()) {
                meta = OptionalInt.of(-1);
            }
        }
        return get(id, meta.orElse(0), Utils.toInt(data.getOrDefault("count", 1)), nbtBytes);
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

    /**
     * Convenience method to check if the item stack has positive level on a specific enchantment by it's id.
     * @param id The enchantment ID from {@link Enchantment} constants.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean hasEnchantment(int id) {
        return getEnchantmentLevel(id) > 0;
    }

    /**
     * Find the enchantment level by the enchantment id.
     * @param id The enchantment ID from {@link Enchantment} constants.
     * @return {@code 0} if the item don't have that enchantment or the current level of the given enchantment.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getEnchantmentLevel(int id) {
        if (!this.hasEnchantments()) {
            return 0;
        }

        for (CompoundTag entry : this.getNamedTag().getList("ench", CompoundTag.class).getAll()) {
            if (entry.getShort("id") == id) {
                return entry.getShort("lvl");
            }
        }
        
        return 0;
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
                    e.setLevel(entry.getShort("lvl"), false);
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
            return Enchantment.EMPTY_ARRAY;
        }

        List<Enchantment> enchantments = new ArrayList<>();

        ListTag<CompoundTag> ench = this.getNamedTag().getList("ench", CompoundTag.class);
        for (CompoundTag entry : ench.getAll()) {
            Enchantment e = Enchantment.getEnchantment(entry.getShort("id"));
            if (e != null) {
                e.setLevel(entry.getShort("lvl"), false);
                enchantments.add(e);
            }
        }

        return enchantments.toArray(Enchantment.EMPTY_ARRAY);
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Nonnull
    public SideEffect[] getAttackSideEffects(@Nonnull Entity attacker, @Nonnull Entity entity) {
        return Arrays.stream(getEnchantments())
                .flatMap(enchantment -> Arrays.stream(enchantment.getAttackSideEffects(attacker, entity)))
                .filter(Objects::nonNull)
                .toArray(SideEffect[]::new)
        ;
    }

    @Since("1.4.0.0-PN")
    public int getRepairCost() {
        if (this.hasCompoundTag()) {
            CompoundTag tag = this.getNamedTag();
            if (tag.contains("RepairCost")) {
                Tag repairCost = tag.get("RepairCost");
                if (repairCost instanceof IntTag) {
                    return ((IntTag) repairCost).data;
                }
            }
        }
        return 0;
    }

    @Since("1.4.0.0-PN")
    public Item setRepairCost(int cost) {
        if (cost <= 0 && this.hasCompoundTag()) {
            return this.setNamedTag(this.getNamedTag().remove("RepairCost"));
        }

        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }
        return this.setNamedTag(tag.putInt("RepairCost", cost));
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

        return lines.toArray(EmptyArrays.EMPTY_STRINGS);
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
        return this.setCompoundTag(EmptyArrays.EMPTY_BYTES);
    }

    public static CompoundTag parseCompoundTag(byte[] tag) {
        try {
            return NBTIO.read(tag, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public byte[] writeCompoundTag(CompoundTag tag) {
        try {
            tag.setName("");
            return NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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
            return Block.get(BlockID.AIR);
        }
    }

    @Since("1.4.0.0-PN")
    @API(definition = API.Definition.INTERNAL, usage = API.Usage.INCUBATING)
    public Block getBlockUnsafe() {
        return this.block;
    }

    public int getId() {
        return id;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final int getNetworkFullId() throws UnknownNetworkIdException {
        try {
            return RuntimeItems.getRuntimeMapping().getNetworkFullId(this);
        } catch (IllegalArgumentException e) {
            throw new UnknownNetworkIdException(this, e);
        }
    }

    @Since("1.4.0.0-PN")
    public final int getNetworkId() throws UnknownNetworkIdException {
        return RuntimeItems.getNetworkId(getNetworkFullId());
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public String getNamespaceId() {
        RuntimeItemMapping runtimeMapping = RuntimeItems.getRuntimeMapping();
        return runtimeMapping.getNamespacedIdByNetworkId(
                RuntimeItems.getNetworkId(runtimeMapping.getNetworkFullId(this))
        );
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getBlockId() {
        if (block != null) {
            return block.getId();
        } else {
            return -1;
        }
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
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Item createFuzzyCraftingRecipe() {
        Item item = clone();
        item.hasMeta = false;
        return item;
    }

    public int getMaxStackSize() {
        return block == null? 64 : block.getItemMaxStackSize();
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

    /**
     * If the item is resistant to lava and fire and can float on lava like if it was on water.
     * @since 1.4.0.0-PN
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isLavaResistant() {
        return false;
    }
    
    public boolean onUse(Player player, int ticksUsed) {
        return false;
    }

    public boolean onRelease(Player player, int ticksUsed) {
        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean damageWhenBreaking() { return true; }

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

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final Item decrement(int amount) {
        return increment(-amount);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final Item increment(int amount) {
        if (count + amount <= 0) {
            return getBlock(BlockID.AIR);
        }
        Item cloned = clone();
        cloned.count += amount;
        return cloned;
    }

    /**
     * When true, this item can be used to reduce growing times like a bone meal.
     * @return {@code true} if it can act like a bone meal
     */
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public boolean isFertilizer() {
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

    /**
     * Same as {@link #equals(Item, boolean)} but the enchantment order of the items does not affect the result.
     * @since 1.2.1.0-PN
     */
    public final boolean equalsIgnoringEnchantmentOrder(Item item, boolean checkDamage) {
        if (!this.equals(item, checkDamage, false)) {
            return false;
        }

        if (Arrays.equals(this.getCompoundTag(), item.getCompoundTag())) {
            return true;
        }

        if (!this.hasCompoundTag() || !item.hasCompoundTag()) {
            return false;
        }

        CompoundTag thisTags = this.getNamedTag();
        CompoundTag otherTags = item.getNamedTag();
        if (thisTags.equals(otherTags)) {
            return true;
        }

        if (!thisTags.contains("ench") || !otherTags.contains("ench")
                || !(thisTags.get("ench") instanceof ListTag)
                || !(otherTags.get("ench") instanceof ListTag)
                || thisTags.getList("ench").size() != otherTags.getList("ench").size()) {
            return false;
        }

        ListTag<CompoundTag> thisEnchantmentTags = thisTags.getList("ench", CompoundTag.class);
        ListTag<CompoundTag> otherEnchantmentTags = otherTags.getList("ench", CompoundTag.class);

        int size = thisEnchantmentTags.size();
        Int2IntMap enchantments = new Int2IntArrayMap(size);
        enchantments.defaultReturnValue(Integer.MIN_VALUE);

        for (int i = 0; i < size; i++) {
            CompoundTag tag = thisEnchantmentTags.get(i);
            enchantments.put(tag.getShort("id"), tag.getShort("lvl"));
        }

        for (int i = 0; i < size; i++) {
            CompoundTag tag = otherEnchantmentTags.get(i);
            if (enchantments.get(tag.getShort("id")) != tag.getShort("lvl")) {
                return false;
            }
        }

        return true;
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
            item.cachedNBT = null;
            return item;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
