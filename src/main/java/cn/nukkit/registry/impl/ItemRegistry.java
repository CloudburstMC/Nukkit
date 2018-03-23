package cn.nukkit.registry.impl;

import cn.nukkit.item.*;
import cn.nukkit.registry.AbstractRegistry;
import cn.nukkit.registry.RegistryType;
import cn.nukkit.registry.function.IntIntegerObjectFunction;

public final class ItemRegistry extends AbstractRegistry<Item, IntIntegerObjectFunction<Item>> implements ItemID {
    public ItemRegistry() {
        super(RegistryType.ITEM);
    }

    @Override
    protected void init() {
        //tools+weapons
        register("wooden_axe", ItemAxeWood::new, ItemAxeWood.class);
        register("wooden_hoe", ItemHoeWood::new, ItemHoeWood.class);
        register("wooden_pickaxe", ItemPickaxeWood::new, ItemPickaxeWood.class);
        register("wooden_shovel", ItemShovelWood::new, ItemShovelWood.class);
        register("wooden_sword", ItemSwordWood::new, ItemSwordWood.class);
        register("stone_axe", ItemAxeStone::new, ItemAxeStone.class);
        register("stone_hoe", ItemHoeStone::new, ItemHoeStone.class);
        register("stone_pickaxe", ItemPickaxeStone::new, ItemPickaxeStone.class);
        register("stone_shovel", ItemShovelStone::new, ItemShovelStone.class);
        register("stone_sword", ItemSwordStone::new, ItemSwordStone.class);
        register("iron_axe", ItemAxeIron::new, ItemAxeIron.class);
        register("iron_hoe", ItemHoeIron::new, ItemHoeIron.class);
        register("iron_pickaxe", ItemPickaxeIron::new, ItemPickaxeIron.class);
        register("iron_shovel", ItemShovelIron::new, ItemShovelIron.class);
        register("iron_sword", ItemSwordIron::new, ItemSwordIron.class);
        register("gold_axe", ItemAxeGold::new, ItemAxeGold.class);
        register("gold_hoe", ItemHoeGold::new, ItemHoeGold.class);
        register("gold_pickaxe", ItemPickaxeGold::new, ItemPickaxeGold.class);
        register("gold_shovel", ItemShovelGold::new, ItemShovelGold.class);
        register("gold_sword", ItemSwordGold::new, ItemSwordGold.class);
        register("diamond_axe", ItemAxeDiamond::new, ItemAxeDiamond.class);
        register("diamond_hoe", ItemHoeDiamond::new, ItemHoeDiamond.class);
        register("diamond_pickaxe", ItemPickaxeDiamond::new, ItemPickaxeDiamond.class);
        register("diamond_shovel", ItemShovelDiamond::new, ItemShovelDiamond.class);
        register("diamond_sword", ItemSwordDiamond::new, ItemSwordDiamond.class);
        //armor
        register("leather_helmet", ItemHelmetLeather::new, ItemHelmetLeather.class);
        register("leather_chestplate", ItemChestplateLeather::new, ItemChestplateLeather.class);
        register("leather_leggings", ItemLeggingsLeather::new, ItemLeggingsLeather.class);
        register("leather_boots", ItemBootsLeather::new, ItemBootsLeather.class);
        register("chain_helmet", ItemHelmetChain::new, ItemHelmetChain.class);
        register("chain_chestplate", ItemChestplateChain::new, ItemChestplateChain.class);
        register("chain_leggings", ItemLeggingsChain::new, ItemLeggingsChain.class);
        register("chain_boots", ItemBootsChain::new, ItemBootsChain.class);
        register("iron_helmet", ItemHelmetIron::new, ItemHelmetIron.class);
        register("iron_chestplate", ItemChestplateIron::new, ItemChestplateIron.class);
        register("iron_leggings", ItemLeggingsIron::new, ItemLeggingsIron.class);
        register("iron_boots", ItemBootsIron::new, ItemBootsIron.class);
        register("gold_helmet", ItemHelmetGold::new, ItemHelmetGold.class);
        register("gold_chestplate", ItemChestplateGold::new, ItemChestplateGold.class);
        register("gold_leggings", ItemLeggingsGold::new, ItemLeggingsGold.class);
        register("gold_boots", ItemBootsGold::new, ItemBootsGold.class);
        register("diamond_helmet", ItemHelmetDiamond::new, ItemHelmetDiamond.class);
        register("diamond_chestplate", ItemChestplateDiamond::new, ItemChestplateDiamond.class);
        register("diamond_leggings", ItemLeggingsDiamond::new, ItemLeggingsDiamond.class);
        register("diamond_boots", ItemBootsDiamond::new, ItemBootsDiamond.class);
        //other things
        register("flint_and_steel", ItemFlintSteel::new, ItemFlintSteel.class);
        register("apple", ItemApple::new, ItemApple.class);
        register("bow", ItemBow::new, ItemBow.class);
        register("arrow", ItemArrow::new, ItemArrow.class);
        register("coal", ItemCoal::new, ItemCoal.class);
        register("diamond", ItemDiamond::new, ItemDiamond.class);
        register("iron_ingot", ItemIngotIron::new, ItemIngotIron.class);
        register("gold_ingot", ItemIngotGold::new, ItemIngotGold.class);
        register("stick", ItemStick::new, ItemStick.class);
        register("bowl", ItemBowl::new, ItemBowl.class);
        register("mushroom_stew", ItemMushroomStew::new, ItemMushroomStew.class);
        register("string", ItemString::new, ItemString.class);
        register("feather", ItemFeather::new, ItemFeather.class);
        register("gunpowder", ItemGunpowder::new, ItemGunpowder.class);
        register("seeds", ItemSeedsWheat::new, ItemSeedsWheat.class);
        register("wheat", ItemWheat::new, ItemWheat.class);
        register("bread", ItemBread::new, ItemBread.class);
        register("flint", ItemFlint::new, ItemFlint.class);
        register("raw_porkchop", ItemPorkchopRaw::new, ItemPorkchopRaw.class);
        register("good_food", ItemPorkchopRaw::new, ItemPorkchopRaw.class);
        register("cooked_porkchop", ItemPorkchopCooked::new, ItemPorkchopCooked.class);
        register("painting", ItemPainting::new, ItemPainting.class);
        register("golden_apple", ItemAppleGold::new, ItemAppleGold.class);
        register("sign", ItemSign::new, ItemSign.class);
        register("wooden_door", ItemDoorWood::new, ItemDoorWood.class);
        register("bucket", ItemBucket::new, ItemBucket.class);
        register("minecart", ItemMinecart::new, ItemMinecart.class);
        register("saddle", ItemSaddle::new, ItemSaddle.class);
        register("iron_door", ItemDoorIron::new, ItemDoorIron.class);
        register("redstone", ItemRedstone::new, ItemRedstone.class);
        register("snowball", ItemSnowball::new, ItemSnowball.class);
        register("boat", ItemBoat::new, ItemBoat.class);
        register("leather", ItemLeather::new, ItemLeather.class);
        register("brick", ItemBrick::new, ItemBrick.class);
        register("clay", ItemClay::new, ItemClay.class);
        register("sugarcane", ItemSugarcane::new, ItemSugarcane.class);
        register("paper", ItemPaper::new, ItemPaper.class);
        register("book", ItemBook::new, ItemBook.class);
        register("slimeball", ItemSlimeball::new, ItemSlimeball.class);
        register("minecart_with_chest", ItemMinecartChest::new, ItemMinecartChest.class);
        register("egg", ItemEgg::new, ItemEgg.class);
        register("compass", ItemCompass::new, ItemCompass.class);
        register("fishing_rod", ItemFishingRod::new, ItemFishingRod.class);
        register("clock", ItemClock::new, ItemClock.class);
        register("glowstone_dust", ItemGlowstoneDust::new, ItemGlowstoneDust.class);
        register("raw_fish", ItemFish::new, ItemFish.class);
        register("cooked_fish", ItemFishCooked::new, ItemFishCooked.class);
        register("dye", ItemDye::new, ItemDye.class);
        register("bone", ItemBone::new, ItemBone.class);
        register("sugar", ItemSugar::new, ItemSugar.class);
        register("cake", ItemCake::new, ItemCake.class);
        register("bed", ItemBed::new, ItemBed.class);
        register("redstone_repeater", ItemRedstoneRepeater::new, ItemRedstoneRepeater.class);
        register("cookie", ItemCookie::new, ItemCookie.class);
        register("map", ItemMap::new, ItemMap.class);
        register("shears", ItemShears::new, ItemShears.class);
        register("melon", ItemMelon::new, ItemMelon.class);
        register("pumpkin_seeds", ItemSeedsPumpkin::new, ItemSeedsPumpkin.class);
        register("melon_seeds", ItemSeedsMelon::new, ItemSeedsMelon.class);
        register("raw_beef", ItemBeefRaw::new, ItemBeefRaw.class);
        register("steak", ItemSteak::new, ItemSteak.class);
        register("raw_chicken", ItemChickenRaw::new, ItemChickenRaw.class);
        register("cooked_chicken", ItemChickenCooked::new, ItemChickenCooked.class);
        register("rotten_flesh", ItemRottenFlesh::new, ItemRottenFlesh.class);
        register("ender_pearl", ItemEnderPearl::new, ItemEnderPearl.class);
        register("blaze_rod", ItemBlazeRod::new, ItemBlazeRod.class);
        //TODO: list[GHAST_TEAR] = ItemGhastTear.class; //370
        register("gold_nugget", ItemNuggetGold::new, ItemNuggetGold.class);
        register("nether_wart", ItemNetherWart::new, ItemNetherWart.class);
        register("potion", ItemPotion::new, ItemPotion.class);
        register("glass_bottle", ItemGlassBottle::new, ItemGlassBottle.class);
        register("spider_eye", ItemSpiderEye::new, ItemSpiderEye.class);
        //TODO: list[FERMENTED_SPIDER_EYE] = ItemSpiderEyeFermented.class; //376
        //TODO: list[BLAZE_POWDER] = ItemBlazePowder.class; //377
        //TODO: list[MAGMA_CREAM] = ItemMagmaCream.class; //378
        register("brewing_stand", ItemBrewingStand::new, ItemBrewingStand.class);
        register("cauldron", ItemCauldron::new, ItemCauldron.class);
        register("eye_of_ender", ItemEnderEye::new, ItemEnderEye.class);
        //TODO: list[GLISTERING_MELON] = ItemMelonGlistering.class; //382
        register("spawn_egg", ItemSpawnEgg::new, ItemSpawnEgg.class);
        register("xp_bottle", ItemExpBottle::new, ItemExpBottle.class);
        //TODO: list[FIRE_CHARGE] = ItemFireCharge.class; //385
        //TODO: list[BOOK_AND_QUILL] = ItemBookAndQuill.class; //386
        register("written_book", ItemBookWritten::new, ItemBookWritten.class);
        register("emerald", ItemEmerald::new, ItemEmerald.class);
        register("item_frame", ItemItemFrame::new, ItemItemFrame.class);
        register("flower_pot", ItemFlowerPot::new, ItemFlowerPot.class);
        register("carrot", ItemCarrot::new, ItemCarrot.class);
        register("baked_potato", ItemPotatoBaked::new, ItemPotatoBaked.class);
        register("poisonous_potato", ItemPotatoPoisonous::new, ItemPotatoPoisonous.class);
        //TODO: list[EMPTY_MAP] = ItemEmptyMap.class; //395
        //TODO: list[GOLDEN_CARROT] = ItemCarrotGolden.class; //396
        register("skull", ItemSkull::new, ItemSkull.class);
        register("carrot_on_a_stick", ItemCarrotOnAStick::new, ItemCarrotOnAStick.class);
        register("nether_star", ItemNetherStar::new, ItemNetherStar.class);
        register("pumpkin_pie", ItemPumpkinPie::new, ItemPumpkinPie.class);
        register("fireworks", ItemFirework::new, ItemFirework.class);
        register("enchanted_book", ItemBookEnchanted::new, ItemBookEnchanted.class);
        register("redstone_comparator", ItemRedstoneComparator::new, ItemRedstoneComparator.class);
        register("nether_brick", ItemNetherBrick::new, ItemNetherBrick.class);
        register("quartz", ItemQuartz::new, ItemQuartz.class);
        register("minecart_with_tnt", ItemMinecartTNT::new, ItemMinecartTNT.class);
        register("minecart_with_hopper", ItemMinecartHopper::new, ItemMinecartHopper.class);
        register("prismarine_shard", ItemPrismarineShard::new, ItemPrismarineShard.class);
        register("hopper", ItemHopper::new, ItemHopper.class);
        register("raw_rabbit", ItemRabbitRaw::new, ItemRabbitRaw.class);
        register("cooked_rabbit", ItemRabbitCooked::new, ItemRabbitCooked.class);
        register("rabbit_stew", ItemRabbitStew::new, ItemRabbitStew.class);
        register("rabbit_foot", ItemRabbitFoot::new, ItemRabbitFoot.class);
        //TODO: list[RABBIT_HIDE] = ItemRabbitHide.class; //415
        register("leather_horse_armor", ItemHorseArmorLeather::new, ItemHorseArmorLeather.class);
        register("iron_horse_armor", ItemHorseArmorIron::new, ItemHorseArmorIron.class);
        register("gold_horse_armor", ItemHorseArmorGold::new, ItemHorseArmorGold.class);
        register("diamond_horse_armor", ItemHorseArmorDiamond::new, ItemHorseArmorDiamond.class);
        //TODO: list[LEAD] = ItemLead.class; //420
        //TODO: list[NAME_TAG] = ItemNameTag.class; //421
        register("prismarine_crystals", ItemPrismarineCrystals::new, ItemPrismarineCrystals.class);
        register("raw_mutton", ItemMuttonRaw::new, ItemMuttonRaw.class);
        register("cooked_mutton", ItemMuttonCooked::new, ItemMuttonCooked.class);
        register("end_crystal", ItemEndCrystal::new, ItemEndCrystal.class);
        register("spruce_door", ItemDoorSpruce::new, ItemDoorSpruce.class);
        register("birch_door", ItemDoorBirch::new, ItemDoorBirch.class);
        register("jungle_door", ItemDoorJungle::new, ItemDoorJungle.class);
        register("acacia_door", ItemDoorAcacia::new, ItemDoorAcacia.class);
        register("dark_oak_door", ItemDoorDarkOak::new, ItemDoorDarkOak.class);
        //TODO: list[CHORUS_FRUIT] = ItemChorusFruit.class; //432
        //TODO: list[POPPED_CHORUS_FRUIT] = ItemChorusFruitPopped.class; //433
        //TODO: list[DRAGON_BREATH] = ItemDragonBreath.class; //437
        register("splash_potion", ItemPotionSplash::new, ItemPotionSplash.class);
        //TODO: list[LINGERING_POTION] = ItemPotionLingering.class; //441
        register("elytra", ItemElytra::new, ItemElytra.class);
        register("beetroot", ItemBeetroot::new, ItemBeetroot.class);
        register("beetroot_seeds", ItemSeedsBeetroot::new, ItemSeedsBeetroot.class);
        register("beetroot_soup", ItemBeetrootSoup::new, ItemBeetrootSoup.class);
        register("raw_salmon", ItemSalmon::new, ItemSalmon.class);
        register("cooked_salmon", ItemSalmonCooked::new, ItemSalmonCooked.class);
        register("clownfish", ItemClownfish::new, ItemClownfish.class);
        register("pufferfish", ItemPufferfish::new, ItemPufferfish.class);
        register("enchanted_golden_apple", ItemAppleGoldEnchanted::new, ItemAppleGoldEnchanted.class);
        register("record_11", ItemRecord11::new, ItemRecord11.class);
        register("record_cat", ItemRecordCat::new, ItemRecordCat.class);
        register("record_13", ItemRecord13::new, ItemRecord13.class);
        register("record_blocks", ItemRecordBlocks::new, ItemRecordBlocks.class);
        register("record_chirp", ItemRecordChirp::new, ItemRecordChirp.class);
        register("record_far", ItemRecordFar::new, ItemRecordFar.class);
        register("record_ward", ItemRecordWard::new, ItemRecordWard.class);
        register("record_mall", ItemRecordMall::new, ItemRecordMall.class);
        register("record_mellohi", ItemRecordMellohi::new, ItemRecordMellohi.class);
        register("record_stal", ItemRecordStal::new, ItemRecordStal.class);
        register("record_strad", ItemRecordStrad::new, ItemRecordStrad.class);
        register("record_wait", ItemRecordWait::new, ItemRecordWait.class);
    }

    @Override
    protected Item accept(IntIntegerObjectFunction<Item> func, int i, Object... args) {
        if (args.length == 0) {
            return func.accept(i);
        }
        //porktodo: separate meta and count from `i`
        //porktodo: check if there's anything else that needs to be done here
        return null;
    }
}
