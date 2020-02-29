package cn.nukkit.registry;

import cn.nukkit.Nukkit;
import cn.nukkit.block.BlockIds;
import cn.nukkit.item.*;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.nukkit.item.ItemIds.*;

@Log4j2
public class ItemRegistry implements Registry {
    private static final ItemRegistry INSTANCE;
    private static final List<ItemData> VANILLA_ITEMS;

    static {
        InputStream stream = RegistryUtils.getOrAssertResource("runtime_item_ids.json");

        try {
            VANILLA_ITEMS = Nukkit.JSON_MAPPER.readValue(stream, new TypeReference<List<ItemData>>() {});
        } catch (IOException e) {
            throw new AssertionError("Unable to load vanilla items", e);
        }

        INSTANCE = new ItemRegistry(BlockRegistry.get()); // Needs to be initialized afterwards
    }

    private final Map<Identifier, ItemFactory> factoryMap = new IdentityHashMap<>();
    private final BiMap<Integer, Identifier> runtimeIdMap = HashBiMap.create();
    private final AtomicInteger runtimeIdAllocator = new AtomicInteger(VANILLA_ITEMS.size());
    private final BlockRegistry blockRegistry;
    private ByteBuf cachedRuntimeItems;
    private volatile boolean closed;

    private ItemRegistry(BlockRegistry blockRegistry) {
        this.blockRegistry = blockRegistry;
        try {
            this.registerVanillaItems();
        } catch (RegistryException e) {
            throw new IllegalStateException("Unable to register vanilla items", e);
        }

        // register missing vanilla items.
        for (ItemData item : VANILLA_ITEMS) {
            if (item.id < 256) {
                continue;
            }
            Identifier identifier = Identifier.fromString(item.name);
            if (!this.factoryMap.containsKey(identifier)) {
                log.debug("Non-implemented item found {}", identifier);
                registerVanilla(identifier, SimpleItem::new, item.id);
            }
        }
    }

    public static ItemRegistry get() {
        return INSTANCE;
    }

    public synchronized void register(Identifier identifier, ItemFactory itemFactory) throws RegistryException {
        Objects.requireNonNull(identifier, "identifier");
        Objects.requireNonNull(itemFactory, "itemFactory");
        checkClosed();
        if (this.factoryMap.putIfAbsent(identifier, itemFactory) != null) {
            throw new RegistryException(identifier + " has already been registered");
        }
        this.runtimeIdMap.put(this.runtimeIdAllocator.getAndIncrement(), identifier);
    }

    private synchronized void registerVanilla(Identifier identifier, ItemFactory itemFactory, int legacyId) throws RegistryException {
        Objects.requireNonNull(identifier, "identifier");
        Objects.requireNonNull(itemFactory, "itemFactory");
        checkClosed();
        this.factoryMap.put(identifier, itemFactory);
        this.runtimeIdMap.put(legacyId, identifier);
        this.runtimeIdAllocator.updateAndGet(prev -> prev <= legacyId ? legacyId + 1 : prev);
    }

    public Item getItem(Identifier identifier) throws RegistryException {
        Objects.requireNonNull(identifier, "identifier");
        ItemFactory itemFactory = this.factoryMap.get(identifier);
        if (itemFactory == null) {
            if (this.blockRegistry.isBlock(identifier)) {
                return new BlockItem(identifier);
            } else {
                throw new RegistryException("Item '" + identifier + "' is not registered");
            }
        }
        return itemFactory.create(identifier);
    }

    public Identifier fromLegacy(int legacyId) throws RegistryException {
        return getIdentifier(legacyId);
    }

    public Identifier getIdentifier(int runtimeId) throws RegistryException {
        Identifier identifier = null;
        if (runtimeId < 255) {
            if (runtimeId < 0) {
                runtimeId = 255 - runtimeId;
            }
            try {
                identifier = this.blockRegistry.getNameFromLegacyId(runtimeId);
            } catch (RegistryException e) {
                // ignore
            }
        } else {
            identifier = runtimeIdMap.get(runtimeId);
        }
        if (identifier == null) {
            throw new RegistryException("Runtime ID " + runtimeId + " does not exist");
        }
        return identifier;
    }

    public int getRuntimeId(Identifier identifier) throws RegistryException {
        int runtimeId = runtimeIdMap.inverse().getOrDefault(identifier, Integer.MAX_VALUE);
        if (runtimeId == Integer.MAX_VALUE) {
            try {
                int blockId = this.blockRegistry.getLegacyId(identifier);
                if (blockId > 255) {
                    blockId = 255 - blockId;
                }
                return blockId;
            } catch (RegistryException e) {
                throw new RegistryException(identifier + " is not of a registered item");
            }
        }
        return runtimeId;
    }

    @Override
    public synchronized void close() throws RegistryException {
        checkClosed();
        this.closed = true;

        ByteBuf buffer = Unpooled.directBuffer();

        List<Identifier> customBlocks = this.blockRegistry.getCustomBlocks();

        Binary.writeUnsignedVarInt(buffer, VANILLA_ITEMS.size() + customBlocks.size());

        for (ItemData data : VANILLA_ITEMS) {
            Binary.writeString(buffer, data.name);
            buffer.writeShortLE(data.id);
        }

        for (Identifier blockId : customBlocks) {
            Binary.writeString(buffer, blockId.toString());
            buffer.writeShortLE(this.getRuntimeId(blockId));
        }
        this.cachedRuntimeItems = buffer;
    }

    private void checkClosed() throws RegistryException {
        if (this.closed) {
            throw new RegistryException("Registration is closed");
        }
    }

    public ByteBuf getCachedRuntimeItems() {
        return cachedRuntimeItems.slice();
    }

    private void registerVanillaItems() throws RegistryException {
        registerVanilla(IRON_SHOVEL, ItemShovelIron::new, 256);
        registerVanilla(IRON_PICKAXE, ItemPickaxeIron::new, 257);
        registerVanilla(IRON_AXE, ItemAxeIron::new, 258);
        registerVanilla(FLINT_AND_STEEL, ItemFlintSteel::new, 259);
        registerVanilla(APPLE, ItemApple::new, 260);
        registerVanilla(BOW, ItemBow::new, 261);
        registerVanilla(ARROW, ItemArrow::new, 262);
        registerVanilla(COAL, SimpleItem::new, 263);
        registerVanilla(DIAMOND, SimpleItem::new, 264);
        registerVanilla(IRON_INGOT, SimpleItem::new, 265);
        registerVanilla(GOLD_INGOT, SimpleItem::new, 266);
        registerVanilla(IRON_SWORD, ItemSwordIron::new, 267);
        registerVanilla(WOODEN_SWORD, ItemSwordWood::new, 268);
        registerVanilla(WOODEN_SHOVEL, ItemShovelWood::new, 269);
        registerVanilla(WOODEN_PICKAXE, ItemPickaxeWood::new, 270);
        registerVanilla(WOODEN_AXE, ItemAxeWood::new, 271);
        registerVanilla(STONE_SWORD, ItemSwordStone::new, 272);
        registerVanilla(STONE_SHOVEL, ItemShovelStone::new, 273);
        registerVanilla(STONE_PICKAXE, ItemPickaxeStone::new, 274);
        registerVanilla(STONE_AXE, ItemAxeStone::new, 275);
        registerVanilla(DIAMOND_SWORD, ItemSwordDiamond::new, 276);
        registerVanilla(DIAMOND_SHOVEL, ItemShovelDiamond::new, 277);
        registerVanilla(DIAMOND_PICKAXE, ItemPickaxeDiamond::new, 278);
        registerVanilla(DIAMOND_AXE, ItemAxeDiamond::new, 279);
        registerVanilla(STICK, SimpleItem::new, 280);
        registerVanilla(BOWL, SimpleItem::new, 281);
        registerVanilla(MUSHROOM_STEW, ItemMushroomStew::new, 282);
        registerVanilla(GOLDEN_SWORD, ItemSwordGold::new, 283);
        registerVanilla(GOLDEN_SHOVEL, ItemShovelGold::new, 284);
        registerVanilla(GOLDEN_PICKAXE, ItemPickaxeGold::new, 285);
        registerVanilla(GOLDEN_AXE, ItemAxeGold::new, 286);
        registerVanilla(STRING, PlaceableItem.factory(BlockIds.TRIPWIRE), 287);
        registerVanilla(FEATHER, SimpleItem::new, 288);
        registerVanilla(GUNPOWDER, SimpleItem::new, 289);
        registerVanilla(WOODEN_HOE, ItemHoeWood::new, 290);
        registerVanilla(STONE_HOE, ItemHoeStone::new, 291);
        registerVanilla(IRON_HOE, ItemHoeIron::new, 292);
        registerVanilla(DIAMOND_HOE, ItemHoeDiamond::new, 293);
        registerVanilla(GOLDEN_HOE, ItemHoeGold::new, 294);
        registerVanilla(WHEAT_SEEDS, PlaceableItem.factory(BlockIds.WHEAT), 295);
        registerVanilla(WHEAT, SimpleItem::new, 296);
        registerVanilla(BREAD, ItemBread::new, 297);
        registerVanilla(LEATHER_HELMET, ItemHelmetLeather::new, 298);
        registerVanilla(LEATHER_CHESTPLATE, ItemChestplateLeather::new, 299);
        registerVanilla(LEATHER_LEGGINGS, ItemLeggingsLeather::new, 300);
        registerVanilla(LEATHER_BOOTS, ItemBootsLeather::new, 301);
        registerVanilla(CHAINMAIL_HELMET, ItemHelmetChain::new, 302);
        registerVanilla(CHAINMAIL_CHESTPLATE, ItemChestplateChain::new, 303);
        registerVanilla(CHAINMAIL_LEGGINGS, ItemLeggingsChain::new, 304);
        registerVanilla(CHAINMAIL_BOOTS, ItemBootsChain::new, 305);
        registerVanilla(IRON_HELMET, ItemHelmetIron::new, 306);
        registerVanilla(IRON_CHESTPLATE, ItemChestplateIron::new, 307);
        registerVanilla(IRON_LEGGINGS, ItemLeggingsIron::new, 308);
        registerVanilla(IRON_BOOTS, ItemBootsIron::new, 309);
        registerVanilla(DIAMOND_HELMET, ItemHelmetDiamond::new, 310);
        registerVanilla(DIAMOND_CHESTPLATE, ItemChestplateDiamond::new, 311);
        registerVanilla(DIAMOND_LEGGINGS, ItemLeggingsDiamond::new, 312);
        registerVanilla(DIAMOND_BOOTS, ItemBootsDiamond::new, 313);
        registerVanilla(GOLDEN_HELMET, ItemHelmetGold::new, 314);
        registerVanilla(GOLDEN_CHESTPLATE, ItemChestplateGold::new, 315);
        registerVanilla(GOLDEN_LEGGINGS, ItemLeggingsGold::new, 316);
        registerVanilla(GOLDEN_BOOTS, ItemBootsGold::new, 317);
        registerVanilla(FLINT, SimpleItem::new, 318);
        registerVanilla(PORKCHOP, ItemPorkchopRaw::new, 319);
        registerVanilla(COOKED_PORKCHOP, ItemPorkchopCooked::new, 320);
        registerVanilla(PAINTING, ItemPainting::new, 321);
        registerVanilla(GOLDEN_APPLE, ItemAppleGold::new, 322);
        registerVanilla(SIGN, SignItem.factory(BlockIds.STANDING_SIGN), 323);
        registerVanilla(WOODEN_DOOR, PlaceableItem.factory(BlockIds.WOODEN_DOOR), 324);
        registerVanilla(BUCKET, ItemBucket::new, 325);

        registerVanilla(MINECART, ItemMinecart::new, 328);
        registerVanilla(SADDLE, ItemSaddle::new, 329);
        registerVanilla(IRON_DOOR, PlaceableItem.factory(BlockIds.IRON_DOOR), 330);
        registerVanilla(REDSTONE, PlaceableItem.factory(BlockIds.REDSTONE_WIRE), 331);
        registerVanilla(SNOWBALL, ItemSnowball::new, 332);
        registerVanilla(BOAT, ItemBoat::new, 333);
        registerVanilla(LEATHER, SimpleItem::new, 334);
        registerVanilla(KELP, PlaceableItem.factory(BlockIds.KELP),335);
        registerVanilla(BRICK, SimpleItem::new, 336);
        registerVanilla(CLAY_BALL, SimpleItem::new, 337);
        registerVanilla(REEDS, PlaceableItem.factory(BlockIds.REEDS), 338);
        registerVanilla(PAPER, SimpleItem::new, 339);
        registerVanilla(BOOK, ItemBook::new, 340);
        registerVanilla(SLIME_BALL, SimpleItem::new, 341);
        registerVanilla(CHEST_MINECART, ItemMinecartChest::new, 342);

        registerVanilla(EGG, ItemEgg::new, 344);
        registerVanilla(COMPASS, SimpleItem::new, 345);
        registerVanilla(FISHING_ROD, ItemFishingRod::new, 346);
        registerVanilla(CLOCK, SimpleItem::new, 347);
        registerVanilla(GLOWSTONE_DUST, SimpleItem::new, 348);
        registerVanilla(FISH, ItemFish::new, 349);
        registerVanilla(COOKED_FISH, ItemFishCooked::new, 350);
        registerVanilla(DYE, ItemDye::new, 351);
        registerVanilla(BONE, SimpleItem::new, 352);
        registerVanilla(SUGAR, SimpleItem::new, 353);
        registerVanilla(CAKE, ItemCake::new, 354);
        registerVanilla(BED, ItemBed::new, 355);
        registerVanilla(REPEATER, PlaceableItem.factory(BlockIds.UNPOWERED_REPEATER), 356);
        registerVanilla(COOKIE, ItemCookie::new, 357);
        registerVanilla(MAP, ItemMap::new, 358);
        registerVanilla(SHEARS, ItemShears::new, 359);
        registerVanilla(MELON, ItemMelon::new, 360);
        registerVanilla(PUMPKIN_SEEDS, PlaceableItem.factory(BlockIds.PUMPKIN_STEM), 361);
        registerVanilla(MELON_SEEDS, PlaceableItem.factory(BlockIds.MELON_STEM), 362);
        registerVanilla(BEEF, ItemBeefRaw::new, 363);
        registerVanilla(COOKED_BEEF, ItemSteak::new, 364);
        registerVanilla(CHICKEN, ItemChickenRaw::new, 365);
        registerVanilla(COOKED_CHICKEN, ItemChickenCooked::new, 366);
        registerVanilla(ROTTEN_FLESH, ItemRottenFlesh::new, 367);
        registerVanilla(ENDER_PEARL, ItemEnderPearl::new, 368);
        registerVanilla(BLAZE_ROD, ItemBlazeRod::new, 369);
        registerVanilla(GHAST_TEAR, SimpleItem::new, 370);
        registerVanilla(GOLD_NUGGET, SimpleItem::new, 371);
        registerVanilla(NETHER_WART, PlaceableItem.factory(BlockIds.NETHER_WART), 372);
        registerVanilla(POTION, ItemPotion::new, 373);
        registerVanilla(GLASS_BOTTLE, ItemGlassBottle::new, 374);
        registerVanilla(SPIDER_EYE, SimpleItem::new, 375);
        registerVanilla(FERMENTED_SPIDER_EYE, SimpleItem::new, 376);
        registerVanilla(BLAZE_POWDER, SimpleItem::new, 377);
        registerVanilla(MAGMA_CREAM, SimpleItem::new, 378);
        registerVanilla(BREWING_STAND, PlaceableItem.factory(BlockIds.BREWING_STAND), 379);
        registerVanilla(CAULDRON, PlaceableItem.factory(BlockIds.CAULDRON), 380);
        registerVanilla(ENDER_EYE, SimpleItem::new, 381);
        registerVanilla(SPECKLED_MELON, SimpleItem::new, 382);
        registerVanilla(SPAWN_EGG, ItemSpawnEgg::new, 383);
        registerVanilla(EXPERIENCE_BOTTLE, ItemExpBottle::new, 384);
        registerVanilla(FIREBALL, ItemFireCharge::new, 385);
        //TODO: registerVanilla(WRITABLE_BOOK, ItemBookAndQuill::new, 386);
        registerVanilla(WRITTEN_BOOK, ItemBookWritten::new, 387);
        registerVanilla(EMERALD, SimpleItem::new, 388);
        registerVanilla(FRAME, PlaceableItem.factory(BlockIds.FRAME), 389);
        registerVanilla(FLOWER_POT, PlaceableItem.factory(BlockIds.FLOWER_POT), 390);
        registerVanilla(CARROT, ItemCarrot::new, 391);
        registerVanilla(POTATO, ItemPotato::new, 392);
        registerVanilla(BAKED_POTATO, ItemPotatoBaked::new, 393);
        registerVanilla(POISONOUS_POTATO, ItemPotatoPoisonous::new, 394);
        //TODO: registerVanilla(EMPTY_MAP, ItemEmptyMap::new, 395);
        registerVanilla(GOLDEN_CARROT, ItemCarrotGolden::new, 396);
        registerVanilla(SKULL, ItemSkull::new, 397);
        registerVanilla(CARROT_ON_A_STICK, ItemCarrotOnAStick::new, 398);
        registerVanilla(NETHER_STAR, SimpleItem::new, 399);
        registerVanilla(PUMPKIN_PIE, ItemPumpkinPie::new, 400);
        registerVanilla(FIREWORKS, ItemFirework::new, 401);

        registerVanilla(ENCHANTED_BOOK, ItemBookEnchanted::new, 403);
        registerVanilla(COMPARATOR, ItemRedstoneComparator::new, 404);
        registerVanilla(NETHERBRICK, SimpleItem::new, 405);
        registerVanilla(QUARTZ, ItemQuartz::new, 406);
        registerVanilla(TNT_MINECART, ItemMinecartTNT::new, 407);
        registerVanilla(HOPPER_MINECART, ItemMinecartHopper::new, 408);
        registerVanilla(PRISMARINE_SHARD, SimpleItem::new, 409);
        registerVanilla(HOPPER, PlaceableItem.factory(BlockIds.HOPPER), 410);
        registerVanilla(RABBIT, ItemRabbitRaw::new, 411);
        registerVanilla(COOKED_RABBIT, ItemRabbitCooked::new, 412);
        registerVanilla(RABBIT_STEW, ItemRabbitStew::new, 413);
        registerVanilla(RABBIT_FOOT, SimpleItem::new, 414);
        registerVanilla(RABBIT_HIDE, SimpleItem::new, 415);
        registerVanilla(HORSE_ARMOR_LEATHER, ItemHorseArmorLeather::new, 416);
        registerVanilla(HORSE_ARMOR_IRON, ItemHorseArmorIron::new, 417);
        registerVanilla(HORSE_ARMOR_GOLD, ItemHorseArmorGold::new, 418);
        registerVanilla(HORSE_ARMOR_DIAMOND, ItemHorseArmorDiamond::new, 419);
        //TODO: registerVanilla(LEAD, ItemLead::new, 420);
        //TODO: registerVanilla(NAME_TAG, ItemNameTag::new, 421);
        registerVanilla(PRISMARINE_CRYSTALS, SimpleItem::new, 422);
        registerVanilla(MUTTON_RAW, ItemMuttonRaw::new, 423);
        registerVanilla(MUTTON_COOKED, ItemMuttonCooked::new, 424);

        registerVanilla(END_CRYSTAL, ItemEndCrystal::new, 426);
        registerVanilla(ARMOR_STAND, SimpleItem::new, 425);
        registerVanilla(SPRUCE_DOOR, PlaceableItem.factory(BlockIds.SPRUCE_DOOR), 427);
        registerVanilla(BIRCH_DOOR, PlaceableItem.factory(BlockIds.BIRCH_DOOR), 428);
        registerVanilla(JUNGLE_DOOR, PlaceableItem.factory(BlockIds.JUNGLE_DOOR), 429);
        registerVanilla(ACACIA_DOOR, PlaceableItem.factory(BlockIds.ACACIA_DOOR), 430);
        registerVanilla(DARK_OAK_DOOR, PlaceableItem.factory(BlockIds.DARK_OAK_DOOR), 431);
        registerVanilla(CHORUS_FRUIT, ItemChorusFruit::new, 432);
        //TODO: registerVanilla(POPPED_CHORUS_FRUIT, ItemChorusFruitPopped::new, 433);

        //TODO: registerVanilla(DRAGON_BREATH, ItemDragonBreath::new, 437);
        registerVanilla(SPLASH_POTION, ItemPotionSplash::new, 438);

        registerVanilla(LINGERING_POTION, ItemPotionLingering::new, 441);

        registerVanilla(ELYTRA, ItemElytra::new, 444);

        //TODO: registerVanilla(SHULKER_SHELL, ItemShulkerShell::new, 445);
        registerVanilla(BANNER, ItemBanner::new, 446);

        registerVanilla(IRON_NUGGET, SimpleItem::new, 452);
        registerVanilla(TRIDENT, ItemTrident::new, 455);

        registerVanilla(BEETROOT, ItemBeetroot::new, 457);
        registerVanilla(BEETROOT_SEEDS, PlaceableItem.factory(BlockIds.BEETROOT), 458);
        registerVanilla(BEETROOT_SOUP, ItemBeetrootSoup::new, 459);
        registerVanilla(SALMON, ItemSalmon::new, 460);
        registerVanilla(CLOWNFISH, ItemClownfish::new, 461);
        registerVanilla(PUFFERFISH, ItemPufferfish::new, 462);
        registerVanilla(COOKED_SALMON, ItemSalmonCooked::new, 463);
        registerVanilla(DRIED_KELP, ItemDriedKelp::new, 464);

        registerVanilla(APPLE_ENCHANTED, ItemAppleGoldEnchanted::new, 466);

        registerVanilla(TURTLE_HELMET, ItemTurtleShell::new, 469);
        registerVanilla(SPRUCE_SIGN, SignItem.factory(BlockIds.SPRUCE_STANDING_SIGN), 472);
        registerVanilla(BIRCH_SIGN, SignItem.factory(BlockIds.BIRCH_STANDING_SIGN),473);
        registerVanilla(JUNGLE_SIGN, SignItem.factory(BlockIds.JUNGLE_STANDING_SIGN),474);
        registerVanilla(ACACIA_SIGN, SignItem.factory(BlockIds.ACACIA_STANDING_SIGN),475);
        registerVanilla(DARK_OAK_SIGN, SignItem.factory(BlockIds.DARK_OAK_STANDING_SIGN), 476);
        registerVanilla(SWEET_BERRIES, ItemSweetBerries::new, 477);

        registerVanilla(RECORD_CAT, RecordItem.factory("record.cat"), 500);
        registerVanilla(RECORD_13, RecordItem.factory("record.13"), 501);
        registerVanilla(RECORD_BLOCKS, RecordItem.factory("record.blocks"), 502);
        registerVanilla(RECORD_CHIRP, RecordItem.factory("record.chirp"), 503);
        registerVanilla(RECORD_FAR, RecordItem.factory("record.far"), 504);
        registerVanilla(RECORD_MALL, RecordItem.factory("record.mall"), 505);
        registerVanilla(RECORD_MELLOHI, RecordItem.factory("record.mellohi"), 506);
        registerVanilla(RECORD_STAL, RecordItem.factory("record.stal"), 507);
        registerVanilla(RECORD_STRAD, RecordItem.factory("record.strad"), 508);
        registerVanilla(RECORD_WARD, RecordItem.factory("record.ward"), 509);
        registerVanilla(RECORD_11, RecordItem.factory("record.11"), 510);
        registerVanilla(RECORD_WAIT, RecordItem.factory("record.wait"), 511);

        registerVanilla(SHIELD, ItemShield::new, 513);

        registerVanilla(CAMPFIRE, PlaceableItem.factory(BlockIds.CAMPFIRE), 720);

        registerVanilla(HONEYCOMB, SimpleItem::new, 736);
        registerVanilla(HONEY_BOTTLE, ItemHoneyBottle::new, 737);
    }

    @Getter
    private static class ItemData {
        private String name;
        private int id;
    }
}
