package cn.nukkit.api.item;

import cn.nukkit.api.block.BlockTypes;
import cn.nukkit.api.metadata.Dyed;
import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.api.metadata.item.Coal;
import cn.nukkit.api.metadata.item.GenericDamageValue;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Builder;

public class ItemTypes {
    private static final TIntObjectMap<ItemType> BY_ID = new TIntObjectHashMap<>(256);
    public static final ItemType IRON_SHOVEL = IntItem.builder().id(256).name("iron_shovel").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType IRON_PICKAXE = IntItem.builder().id(257).name("iron_pickaxe").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType IRON_AXE = IntItem.builder().id(258).name("iron_axe").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType FLINT_AND_STEEL = IntItem.builder().id(259).name("flint_and_steel").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType APPLE = IntItem.builder().id(260).name("apple").maxStackSize(64).build();
    public static final ItemType BOW = IntItem.builder().id(261).name("bow").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType ARROW = IntItem.builder().id(262).name("arrow").maxStackSize(64).build();
    public static final ItemType COAL = IntItem.builder().id(263).name("coal").maxStackSize(64).data(Coal.class).build();
    public static final ItemType DIAMOND = IntItem.builder().id(264).name("diamond").maxStackSize(64).build();
    public static final ItemType IRON_INGOT = IntItem.builder().id(265).name("iron_ingot").maxStackSize(64).build();
    public static final ItemType GOLD_INGOT = IntItem.builder().id(266).name("gold_ingot").maxStackSize(64).build();
    public static final ItemType IRON_SWORD = IntItem.builder().id(267).name("iron_sword").maxStackSize(1).build();
    public static final ItemType WOODEN_SWORD = IntItem.builder().id(268).name("wooden_sword").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType WOODEN_SHOVEL = IntItem.builder().id(269).name("wooden_shovel").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType WOODEN_PICKAXE = IntItem.builder().id(270).name("wooden_pickaxe").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType WOODEN_AXE = IntItem.builder().id(271).name("wooden_axe").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType STONE_SWORD = IntItem.builder().id(272).name("stone_sword").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType STONE_SHOVEL = IntItem.builder().id(273).name("stone_shovel").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType STONE_PICKAXE = IntItem.builder().id(274).name("stone_pickaxe").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType STONE_AXE = IntItem.builder().id(275).name("stone_axe").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType DIAMOND_SWORD = IntItem.builder().id(276).name("diamond_sword").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType DIAMOND_SHOVEL = IntItem.builder().id(277).name("diamond_shovel").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType DIAMOND_PICKAXE = IntItem.builder().id(278).name("diamond_pickaxe").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType DIAMOND_AXE = IntItem.builder().id(279).name("diamond_axe").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType STICK = IntItem.builder().id(280).name("stick").maxStackSize(64).build();
    public static final ItemType BOWL = IntItem.builder().id(281).name("bowl").maxStackSize(64).build();
    public static final ItemType MUSHROOM_STEW = IntItem.builder().id(282).name("mushroom_stew").maxStackSize(1).build();
    public static final ItemType GOLDEN_SWORD = IntItem.builder().id(283).name("golden_sword").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType GOLDEN_SHOVEL = IntItem.builder().id(284).name("golden_shovel").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType GOLDEN_PICKAXE = IntItem.builder().id(285).name("golden_pickaxe").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType GOLDEN_AXE = IntItem.builder().id(286).name("golden_axe").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType STRING = IntItem.builder().id(287).name("string").maxStackSize(64).build();
    public static final ItemType FEATHER = IntItem.builder().id(288).name("feather").maxStackSize(64).build();
    public static final ItemType GUNPOWDER = IntItem.builder().id(289).name("gunpowder").maxStackSize(64).build();
    public static final ItemType WOODEN_HOE = IntItem.builder().id(290).name("wooden_hoe").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType STONE_HOE = IntItem.builder().id(291).name("stone_hoe").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType IRON_HOE = IntItem.builder().id(292).name("iron_hoe").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType DIAMOND_HOE = IntItem.builder().id(293).name("diamond_hoe").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType GOLDEN_HOE = IntItem.builder().id(294).name("golden_hoe").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType SEEDS = IntItem.builder().id(295).name("wheat_seeds").maxStackSize(64).build();
    public static final ItemType WHEAT = IntItem.builder().id(296).name("wheat").maxStackSize(64).build();
    public static final ItemType BREAD = IntItem.builder().id(297).name("bread").maxStackSize(64).build();
    public static final ItemType LEATHER_CAP = IntItem.builder().id(298).name("leather_helmet").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType LEATHER_TUNIC = IntItem.builder().id(299).name("leather_chestplate").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType LEATHER_PANTS = IntItem.builder().id(300).name("leather_leggings").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType LEATHER_BOOTS = IntItem.builder().id(301).name("leather_boots").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType CHAIN_HELMET = IntItem.builder().id(302).name("chain_helmet").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType CHAIN_CHESTPLATE = IntItem.builder().id(303).name("chain_chestplate").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType CHAIN_LEGGINGS = IntItem.builder().id(304).name("chain_leggings").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType CHAIN_BOOTS = IntItem.builder().id(305).name("chain_boots").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType IRON_HELMET = IntItem.builder().id(306).name("iron_helmet").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType IRON_CHESTPLATE = IntItem.builder().id(307).name("iron_chestplate").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType IRON_LEGGINGS = IntItem.builder().id(308).name("iron_leggings").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType IRON_BOOTS = IntItem.builder().id(309).name("iron_boots").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType DIAMOND_HELMET = IntItem.builder().id(310).name("diamond_helmet").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType DIAMOND_CHESTPLATE = IntItem.builder().id(311).name("diamond_chestplate").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType DIAMOND_LEGGINGS = IntItem.builder().id(312).name("diamond_leggings").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType DIAMOND_BOOTS = IntItem.builder().id(313).name("diamond_boots").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType GOLDEN_HELMET = IntItem.builder().id(314).name("golden_helmet").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType GOLDEN_CHESTPLATE = IntItem.builder().id(315).name("golden_chestplate").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType GOLDEN_LEGGINGS = IntItem.builder().id(316).name("golden_leggings").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType GOLDEN_BOOTS = IntItem.builder().id(317).name("golden_boots").maxStackSize(1).data(GenericDamageValue.class).build();
    public static final ItemType FLINT = IntItem.builder().id(318).name("flint").maxStackSize(64).build();
    public static final ItemType RAW_PORKCHOP = IntItem.builder().id(319).name("porkchop").maxStackSize(64).build();
    public static final ItemType COOKED_PORKCHOP = IntItem.builder().id(320).name("cooked_porkchop").maxStackSize(64).build();
    public static final ItemType PAINTING = IntItem.builder().id(321).name("painting").maxStackSize(64).build();
    public static final ItemType GOLDEN_APPLE = IntItem.builder().id(322).name("golden_apple").maxStackSize(64).build();
    public static final ItemType SIGN = IntItem.builder().id(323).name("sign").maxStackSize(16).build();
    public static final ItemType WOODEN_DOOR = IntItem.builder().id(324).name("wooden_door").maxStackSize(64).build();
    public static final ItemType BUCKET = IntItem.builder().id(325).name("bucket").maxStackSize(16).build();
    public static final ItemType MINECART = IntItem.builder().id(328).name("minecart").maxStackSize(1).build();
    public static final ItemType SADDLE = IntItem.builder().id(329).name("saddle").maxStackSize(1).build();
    public static final ItemType IRON_DOOR = IntItem.builder().id(330).name("iron_door").maxStackSize(64).build();
    public static final ItemType REDSTONE = IntItem.builder().id(331).name("redstone").maxStackSize(64).build();
    public static final ItemType SNOWBALL = IntItem.builder().id(332).name("snowball").maxStackSize(16).build();
    public static final ItemType BOAT = IntItem.builder().id(333).name("boat").maxStackSize(1).build();
    public static final ItemType LEATHER = IntItem.builder().id(334).name("leather").maxStackSize(64).build();
    public static final ItemType BRICK = IntItem.builder().id(336).name("brick").maxStackSize(64).build();
    public static final ItemType CLAY = IntItem.builder().id(337).name("clay_ball").maxStackSize(64).build();
    public static final ItemType SUGAR_CANE = IntItem.builder().id(338).name("reeds").maxStackSize(64).build();
    public static final ItemType PAPER = IntItem.builder().id(339).name("paper").maxStackSize(64).build();
    public static final ItemType BOOK = IntItem.builder().id(340).name("book").maxStackSize(64).build();
    public static final ItemType SLIMEBALL = IntItem.builder().id(341).name("slimeball").maxStackSize(64).build();
    public static final ItemType MINECART_WITH_CHEST = IntItem.builder().id(342).name("chest_minecart").maxStackSize(1).build();
    public static final ItemType EGG = IntItem.builder().id(344).name("egg").maxStackSize(16).build();
    public static final ItemType COMPASS = IntItem.builder().id(345).name("compass").maxStackSize(64).build();
    public static final ItemType FISHING_ROD = IntItem.builder().id(346).name("fishing_rod").maxStackSize(1).build();
    public static final ItemType CLOCK = IntItem.builder().id(347).name("clock").maxStackSize(64).build();
    public static final ItemType GLOWSTONE_DUST = IntItem.builder().id(348).name("glowstone_dust").maxStackSize(64).build();
    public static final ItemType RAW_FISH = IntItem.builder().id(349).name("fish").maxStackSize(64).build();
    public static final ItemType COOKED_FISH = IntItem.builder().id(350).name("cooked_fish").maxStackSize(64).build();
    public static final ItemType DYE = IntItem.builder().id(351).name("dye").maxStackSize(64).data(Dyed.class).build();
    public static final ItemType BONE = IntItem.builder().id(352).name("bone").maxStackSize(64).build();
    public static final ItemType SUGAR = IntItem.builder().id(353).name("sugar").maxStackSize(64).build();
    public static final ItemType CAKE = IntItem.builder().id(354).name("cake").maxStackSize(1).build();
    public static final ItemType BED = IntItem.builder().id(355).name("bed").maxStackSize(1).data(Dyed.class).build();
    public static final ItemType REDSTONE_REPEATER = IntItem.builder().id(356).name("repeater").maxStackSize(64).build();
    public static final ItemType COOKIE = IntItem.builder().id(357).name("cookie").maxStackSize(64).build();
    public static final ItemType FILLED_MAP = IntItem.builder().id(358).name("map_filled").maxStackSize(64).build();
    public static final ItemType SHEARS = IntItem.builder().id(359).name("shears").maxStackSize(1).build();
    public static final ItemType MELON = IntItem.builder().id(360).name("melon").maxStackSize(64).build();
    public static final ItemType PUMPKIN_SEEDS = IntItem.builder().id(361).name("pumpkin_seeds").maxStackSize(64).build();
    public static final ItemType MELON_SEEDS = IntItem.builder().id(362).name("melon_seeds").maxStackSize(64).build();
    public static final ItemType RAW_BEEF = IntItem.builder().id(363).name("beef").maxStackSize(64).build();
    public static final ItemType STEAK = IntItem.builder().id(364).name("cooked_beef").maxStackSize(64).build();
    public static final ItemType RAW_CHICKEN = IntItem.builder().id(365).name("chicken").maxStackSize(64).build();
    public static final ItemType COOKED_CHICKEN = IntItem.builder().id(366).name("cooked_chicken").maxStackSize(64).build();
    public static final ItemType ROTTEN_FLESH = IntItem.builder().id(367).name("rotten_flesh").maxStackSize(64).build();
    public static final ItemType ENDER_PEARL = IntItem.builder().id(367).name("ender_pearl").maxStackSize(64).build();
    public static final ItemType BLAZE_ROD = IntItem.builder().id(369).name("blaze_rod").maxStackSize(64).build();
    public static final ItemType GHAST_TEAR = IntItem.builder().id(370).name("ghast_tear").maxStackSize(64).build();
    public static final ItemType GOLD_NUGGET = IntItem.builder().id(371).name("gold_nugget").maxStackSize(64).build();
    public static final ItemType NETHER_WART = IntItem.builder().id(372).name("nether_wart").maxStackSize(64).build();
    public static final ItemType POTION = IntItem.builder().id(373).name("potion").maxStackSize(1).build();
    public static final ItemType GLASS_BOTTLE = IntItem.builder().id(374).name("glass_bottle").maxStackSize(64).build();
    public static final ItemType SPIDER_EYE = IntItem.builder().id(375).name("spider_eye").maxStackSize(64).build();
    public static final ItemType FERMENTED_SPIDER_EYE = IntItem.builder().id(376).name("fermented_spider_eye").maxStackSize(64).build();
    public static final ItemType BLAZE_POWDER = IntItem.builder().id(377).name("blaze_powder").maxStackSize(64).build();
    public static final ItemType MAGMA_CREAM = IntItem.builder().id(378).name("magma_cream").maxStackSize(64).build();
    public static final ItemType BREWING_STAND = IntItem.builder().id(379).name("brewing_stand").maxStackSize(64).build();
    public static final ItemType CAULDRON = IntItem.builder().id(380).name("cauldron").maxStackSize(64).build();
    public static final ItemType EYE_OF_ENDER = IntItem.builder().id(381).name("ender_eye").maxStackSize(64).build();
    public static final ItemType GLISTERING_MELON = IntItem.builder().id(382).name("speckled_melon").maxStackSize(64).build();
    public static final ItemType SPAWN_EGG = IntItem.builder().id(383).name("spawn_egg").maxStackSize(64).build();
    public static final ItemType BOTTLE_O_ENCHANTING = IntItem.builder().id(384).name("experience_enchanting").maxStackSize(64).build();
    public static final ItemType FIRE_CHARGE = IntItem.builder().id(385).name("fireball").maxStackSize(64).build();
    public static final ItemType BOOK_AND_QUILL = IntItem.builder().id(386).name("writable_book").maxStackSize(1).build();
    public static final ItemType WRITTEN_BOOK = IntItem.builder().id(387).name("written_book").maxStackSize(16).build();
    public static final ItemType EMERALD = IntItem.builder().id(388).name("emerald").maxStackSize(64).build();
    public static final ItemType ITEM_FRAME = IntItem.builder().id(389).name("frame").maxStackSize(64).build();
    public static final ItemType FLOWER_POT = IntItem.builder().id(390).name("flower_pot").maxStackSize(64).build();
    public static final ItemType CARROT = IntItem.builder().id(391).name("carrot").maxStackSize(64).build();
    public static final ItemType POTATO = IntItem.builder().id(392).name("potato").maxStackSize(64).build();
    public static final ItemType BAKED_POTATO = IntItem.builder().id(393).name("baked_potato").maxStackSize(64).build();
    public static final ItemType POISONOUS_POTATO = IntItem.builder().id(394).name("poisonous_potato").maxStackSize(64).build();
    public static final ItemType MAP = IntItem.builder().id(395).name("emptymap").maxStackSize(64).build();
    public static final ItemType GOLDEN_CARROT = IntItem.builder().id(396).name("golden_carrot").maxStackSize(64).build();
    public static final ItemType MOB_HEAD = IntItem.builder().id(397).name("skull").maxStackSize(64).build();
    public static final ItemType CARROT_ON_A_STICK = IntItem.builder().id(398).name("carrotonastick").maxStackSize(1).build();
    public static final ItemType NETHER_STAR = IntItem.builder().id(399).name("netherstar").maxStackSize(64).build();
    public static final ItemType PUMPKIN_PIE = IntItem.builder().id(400).name("pumpkin_pie").maxStackSize(64).build();
    public static final ItemType FIREWORK_ROCKET = IntItem.builder().id(401).name("fireworks").maxStackSize(64).build();
    public static final ItemType FIREWORK_STAR = IntItem.builder().id(402).name("fireworkscharge").maxStackSize(64).build();
    public static final ItemType ENCHANTED_BOOK = IntItem.builder().id(403).name("enchanted_book").maxStackSize(1).build();
    public static final ItemType COMPARATOR = IntItem.builder().id(404).name("comparator").maxStackSize(64).build();
    public static final ItemType NETHER_BRICK = IntItem.builder().id(405).name("netherbrick").maxStackSize(64).build();
    public static final ItemType NETHER_QUARTZ = IntItem.builder().id(406).name("quartz").maxStackSize(64).build();
    public static final ItemType MINECART_WITH_TNT = IntItem.builder().id(407).name("tnt_minecart").maxStackSize(1).build();
    public static final ItemType MINECART_WITH_HOPPER = IntItem.builder().id(408).name("hopper_minecart").maxStackSize(1).build();
    public static final ItemType PRISMARINE_SHARD = IntItem.builder().id(409).name("prismarine_shard").maxStackSize(64).build();
    public static final ItemType HOPPER = IntItem.builder().id(410).name("hopper").maxStackSize(64).build();
    public static final ItemType RAW_RABBIT = IntItem.builder().id(411).name("rabbit").maxStackSize(64).build();
    public static final ItemType COOKED_RABBIT = IntItem.builder().id(412).name("cooked_rabbit").maxStackSize(64).build();
    public static final ItemType RABBIT_STEW = IntItem.builder().id(413).name("rabbit_stew").maxStackSize(64).build();
    public static final ItemType RABBITS_FOOT = IntItem.builder().id(414).name("rabbit_foot").maxStackSize(64).build();
    public static final ItemType RABBIT_HIDE = IntItem.builder().id(415).name("rabbit_hide").maxStackSize(64).build();
    public static final ItemType LEATHER_HORSE_ARMOR = IntItem.builder().id(416).name("horsearmorleather").maxStackSize(1).build();
    public static final ItemType IRON_HORSE_ARMOR = IntItem.builder().id(417).name("horsearmoriron").maxStackSize(1).build();
    public static final ItemType GOLDEN_HORSE_ARMOR = IntItem.builder().id(418).name("horsearmorgolden").maxStackSize(1).build();
    public static final ItemType DIAMOND_HORSE_ARMOR = IntItem.builder().id(419).name("horsearmordiamond").maxStackSize(1).build();
    public static final ItemType LEAD = IntItem.builder().id(420).name("lead").maxStackSize(64).build();
    public static final ItemType NAME_TAG = IntItem.builder().id(421).name("nametag").maxStackSize(64).build();
    public static final ItemType PRISMARINE_CRYSTALS = IntItem.builder().id(422).name("prismarine_crystals").maxStackSize(64).build();
    public static final ItemType MUTTON = IntItem.builder().id(423).name("muttonraw").maxStackSize(64).build();
    public static final ItemType COOKED_MUTTON = IntItem.builder().id(424).name("muttoncooked").maxStackSize(64).build();
    public static final ItemType ARMOR_STAND = IntItem.builder().id(425).name("armor_stand").maxStackSize(64).build();
    public static final ItemType END_CRYSTAL = IntItem.builder().id(426).name("end_crystal").maxStackSize(64).build();
    public static final ItemType SPRUCE_DOOR = IntItem.builder().id(427).name("spruce_door").maxStackSize(64).build();
    public static final ItemType BIRCH_DOOR = IntItem.builder().id(428).name("birch_door").maxStackSize(64).build();
    public static final ItemType JUNGLE_DOOR = IntItem.builder().id(429).name("jungle_door").maxStackSize(64).build();
    public static final ItemType ACACIA_DOOR = IntItem.builder().id(430).name("acacia_door").maxStackSize(64).build();
    public static final ItemType DARK_OAK_DOOR = IntItem.builder().id(431).name("dark_oak_door").maxStackSize(64).build();
    public static final ItemType CHORUS_FRUIT = IntItem.builder().id(432).name("chorus_fruit").maxStackSize(64).build();
    public static final ItemType POPPED_CHORUS_FRUIT = IntItem.builder().id(433).name("chorus_fruit_popped").maxStackSize(64).build();
    public static final ItemType DRAGONS_BREATH = IntItem.builder().id(437).name("dragon_breath").maxStackSize(64).build();
    public static final ItemType SPLASH_POTION = IntItem.builder().id(438).name("splash_potion").maxStackSize(1).build();
    public static final ItemType LINGERING_POTION = IntItem.builder().id(441).name("lingering_potion").maxStackSize(1).build();
    public static final ItemType MINECART_WITH_COMMAND_BLOCK = IntItem.builder().id(443).name("command_block_minecart").maxStackSize(1).build();
    public static final ItemType ELYTRA = IntItem.builder().id(444).name("elytra").maxStackSize(1).build();
    public static final ItemType SHULKER_SHELL = IntItem.builder().id(445).name("shulker_shell").maxStackSize(64).build();
    public static final ItemType BANNER = IntItem.builder().id(446).name("banner").maxStackSize(16).build();
    public static final ItemType TOTEM_OF_UNDYING = IntItem.builder().id(450).name("totem").maxStackSize(1).build();
    public static final ItemType CHALKBOARD = IntItem.builder().id(454).name("board").maxStackSize(16).build();
    public static final ItemType PORTFOLIO = IntItem.builder().id(456).name("portfolio").maxStackSize(64).build();
    public static final ItemType IRON_NUGGET = IntItem.builder().id(457).name("iron_nugget").maxStackSize(64).build();
    public static final ItemType BEETROOT = IntItem.builder().id(457).name("beetroot").maxStackSize(54).build();
    public static final ItemType BEETROOT_SEEDS = IntItem.builder().id(458).name("beetroot_seeds").maxStackSize(64).build();
    public static final ItemType BEETROOT_SOUP = IntItem.builder().id(459).name("beetroot_soup").maxStackSize(1).build();
    public static final ItemType RAW_SALMON = IntItem.builder().id(460).name("salmon").maxStackSize(64).build();
    public static final ItemType CLOWNFISH = IntItem.builder().id(461).name("clownfish").maxStackSize(64).build();
    public static final ItemType PUFFERFISH = IntItem.builder().id(462).name("pufferfish").maxStackSize(64).build();
    public static final ItemType COOKED_SALMON = IntItem.builder().id(463).name("cooked_salmon").maxStackSize(64).build();
    public static final ItemType ENCHANTED_GOLDEN_APPLE = IntItem.builder().id(466).name("appleenchanted").maxStackSize(64).build();
    public static final ItemType CAMERA = IntItem.builder().id(498).name("camera").maxStackSize(64).build();
    public static final ItemType DISC_13 = IntItem.builder().id(500).name("record_13").maxStackSize(1).build();
    public static final ItemType DISC_CAT = IntItem.builder().id(501).name("record_cat").maxStackSize(1).build();
    public static final ItemType DISC_BLOCKS = IntItem.builder().id(502).name("record_blocks").maxStackSize(1).build();
    public static final ItemType DISC_CHIRP = IntItem.builder().id(503).name("record_chirp").maxStackSize(1).build();
    public static final ItemType DISC_FAR = IntItem.builder().id(504).name("record_far").maxStackSize(1).build();
    public static final ItemType DISC_MALL = IntItem.builder().id(505).name("record_mall").maxStackSize(1).build();
    public static final ItemType DISC_MELLOHI = IntItem.builder().id(506).name("record_mellohi").maxStackSize(1).build();
    public static final ItemType DISC_STAL = IntItem.builder().id(507).name("record_stal").maxStackSize(1).build();
    public static final ItemType DISC_STRAD = IntItem.builder().id(508).name("record_strad").maxStackSize(1).build();
    public static final ItemType DISC_WARD = IntItem.builder().id(509).name("record_ward").maxStackSize(1).build();
    public static final ItemType DISC_11 = IntItem.builder().id(510).name("record_11").maxStackSize(1).build();
    public static final ItemType DISC_WAIT = IntItem.builder().id(511).name("record_wait").maxStackSize(1).build();

    @Builder
    private static class IntItem  implements ItemType {
        private final int id;
        private final String name;
        private final int maxStackSize;
        private final Class<? extends Metadata> data;

        public IntItem(int id, String name, int maxStackSize, Class<? extends Metadata> data) {
            this.id = id;
            this.name = name.toUpperCase();
            this.maxStackSize = maxStackSize;
            this.data = data;

            BY_ID.put(id, this);
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isBlock() {
            return false;
        }

        @Override
        public Class<? extends Metadata> getMetadataClass() {
            return data;
        }

        @Override
        public int getMaximumStackSize() {
            return maxStackSize;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    public static ItemType byId(int id) {
        return byId(id, false);
    }

    public static ItemType byId(int id, boolean itemsOnly) {
        ItemType type = BY_ID.get(id);
        if (type == null) {
            if (itemsOnly) {
                throw new IllegalArgumentException("ID " + id + " is not valid.");
            } else {
                return BlockTypes.byId(id);
            }
        }
        return type;
    }
}
