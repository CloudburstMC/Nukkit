package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCauldron;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.item.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.particle.SmokeParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author CreeperFace
 * Nukkit Project
 */
public class BlockCauldron extends BlockTransparentMeta {

    /**
     * Used to cache biome check for freezing
     * 1 = can't freeze, 2 = can freeze
     */
    private byte freezing;

    public BlockCauldron() {
        super(0);
    }

    public BlockCauldron(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CAULDRON_BLOCK;
    }

    public String getName() {
        return "Cauldron Block";
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public boolean isFull() {
        return (this.getDamage() & 0x06) == 0x06;
    }

    public boolean isEmpty() {
        return this.getDamage() == 0x00;
    }

    public int getFillLevel() {
        return (getDamage() & 0x6) >> 1;
    }

    public void setFillLevel(int fillLevel) {
        fillLevel = MathHelper.clamp(fillLevel, 0, 3);
        setDamage(fillLevel << 1);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        BlockEntity be = this.level.getBlockEntity(this);

        if (!(be instanceof BlockEntityCauldron)) {
            return false;
        }

        BlockEntityCauldron cauldron = (BlockEntityCauldron) be;

        switch (item.getId()) {
            case Item.BUCKET:
                if (item.getDamage() == 0) {//empty bucket
                    if (!isFull() || cauldron.isCustomColor() || cauldron.hasPotion()) {
                        break;
                    }

                    ItemBucket bucket = (ItemBucket) item.clone();
                    bucket.setCount(1);
                    bucket.setDamage(8);//water bucket

                    PlayerBucketFillEvent ev = new PlayerBucketFillEvent(player, this, null, item, bucket);
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        replaceBucket(item, player, ev.getItem());
                        this.setFillLevel(0);//empty
                        this.level.setBlock(this, this, true);
                        cauldron.clearCustomColor();
                        this.getLevel().addSound(this, Sound.CAULDRON_TAKEWATER);
                    }
                } else if (item.getDamage() == 8 || item.getDamage() == 10) {//water and lava buckets
                    if (isFull() && !cauldron.isCustomColor() && !cauldron.hasPotion() && item.getDamage() == 8) {
                        break;
                    }

                    ItemBucket bucket = (ItemBucket) item.clone();
                    bucket.setCount(1);
                    bucket.setDamage(0);//empty bucket

                    PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, this, null, item, bucket);
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        if (player.isSurvival() || player.isAdventure()) {
                            replaceBucket(item, player, ev.getItem());
                        }
                        if (cauldron.hasPotion()) {//if has potion
                            clearWithFizz(cauldron);
                        } else if (item.getDamage() == 8) { //water bucket
                            this.setFillLevel(3);//fill
                            cauldron.clearCustomColor();
                            this.level.setBlock(this, this, true);
                            this.getLevel().addSound(this, Sound.CAULDRON_FILLWATER);
                        } else { // lava bucket
                            if (isEmpty()) {
                                BlockCauldronLava cauldronLava = new BlockCauldronLava(0xE);
                                cauldronLava.setFillLevel(3);
                                this.level.setBlock(this, cauldronLava, true, true);
                                cauldron.clearCustomColor();
                                this.getLevel().addSound(this.add(0.5, 0.5, 0.5), Sound.BUCKET_EMPTY_LAVA);
                            } else {
                                clearWithFizz(cauldron);
                            }
                        }
                        //this.update();
                    }
                }
                break;
            case Item.DYE:
                if (isEmpty() || cauldron.hasPotion()) {
                    break;
                }

                if (player.isSurvival() || player.isAdventure()) {
                    item.count--;
                }

                BlockColor color = new ItemDye(item.getDamage()).getDyeColor().getColor();
                if (!cauldron.isCustomColor()) {
                    cauldron.setCustomColor(color);
                } else {
                    BlockColor current = cauldron.getCustomColor();
                    BlockColor mixed = new BlockColor(
                            current.getRed() + ((color.getRed() - current.getRed()) >> 1),
                            current.getGreen() + ((color.getGreen() - current.getGreen()) >> 1),
                            current.getBlue() + ((color.getBlue() - current.getBlue()) >> 1)
                    );
                    cauldron.setCustomColor(mixed);
                }
                this.level.addSound(this, Sound.CAULDRON_ADDDYE);
                break;
            case Item.LEATHER_CAP:
            case Item.LEATHER_TUNIC:
            case Item.LEATHER_PANTS:
            case Item.LEATHER_BOOTS:
            case Item.LEATHER_HORSE_ARMOR:
                if (isEmpty() || cauldron.hasPotion()) {
                    break;
                }

                CompoundTag compoundTag = item.getNamedTag();
                if (compoundTag == null) compoundTag = new CompoundTag();
                if (cauldron.isCustomColor()) {
                    compoundTag.putInt("customColor", cauldron.getCustomColor().getRGB());
                } else {
                    compoundTag.remove("customColor");
                }
                item.setCompoundTag(compoundTag);
                player.getInventory().setItemInHand(item);

                setFillLevel(getFillLevel() - 1);
                this.level.setBlock(this, this, true, true);
                this.level.addSound(this, Sound.CAULDRON_DYEARMOR);
                break;
            case Item.POTION:
            case Item.SPLASH_POTION:
            case Item.LINGERING_POTION:
                if (!isEmpty() && (cauldron.hasPotion() ? cauldron.getPotionId() != item.getDamage() : item.getDamage() != 0)) {
                    clearWithFizz(cauldron);
                    consumePotion(item, player);
                    break;
                }
                if (isFull()) {
                    break;
                }
                if (item.getDamage() != 0 && isEmpty()) {
                    cauldron.setPotionId(item.getDamage());
                }
                cauldron.setPotionType(
                        item.getId() == Item.POTION ? BlockEntityCauldron.POTION_TYPE_NORMAL :
                                item.getId() == Item.SPLASH_POTION ? BlockEntityCauldron.POTION_TYPE_SPLASH :
                                        BlockEntityCauldron.POTION_TYPE_LINGERING
                );
                cauldron.spawnToAll();

                setFillLevel(getFillLevel() + 1);
                this.level.setBlock(this, this, true);
                consumePotion(item, player);
                this.getLevel().addSound(this, Sound.CAULDRON_FILLPOTION);
                break;
            case Item.GLASS_BOTTLE:
                if (isEmpty()) {
                    break;
                }

                int meta = cauldron.hasPotion() ? cauldron.getPotionId() : 0;
                Item potion;
                if (meta == 0) {
                    potion = Item.get(Item.POTION);
                } else {
                    switch (cauldron.getPotionType()) {
                        case BlockEntityCauldron.POTION_TYPE_SPLASH:
                            potion = Item.get(Item.SPLASH_POTION, meta);
                            break;
                        case BlockEntityCauldron.POTION_TYPE_LINGERING:
                            potion = Item.get(Item.LINGERING_POTION, meta);
                            break;
                        case BlockEntityCauldron.POTION_TYPE_NORMAL:
                        default:
                            potion = Item.get(Item.POTION, meta);
                            break;
                    }
                }

                setFillLevel(getFillLevel() - 1);
                if (isEmpty()) {
                    cauldron.setPotionId(0xffff);//reset potion
                    cauldron.clearCustomColor();
                }
                this.level.setBlock(this, this, true);

                boolean consumeBottle = player.isSurvival() || player.isAdventure();
                if (consumeBottle && item.getCount() == 1) {
                    player.getInventory().setItemInHand(potion);
                } else if (item.getCount() > 1) {
                    if (consumeBottle) {
                        item.count--;
                    }

                    if (player.getInventory().canAddItem(potion)) {
                        player.getInventory().addItem(potion);
                    } else {
                        player.getLevel().dropItem(player.add(0, 1.3, 0), potion, player.getDirectionVector().multiply(0.4));
                    }
                }

                this.getLevel().addSound(this, Sound.CAULDRON_TAKEPOTION);
                break;
            case Item.ARROW:
                if (item.getDamage() > 1 || !cauldron.hasPotion()) {
                    break;
                }

                if (!player.isCreative() && item.getCount() == 1) {
                    item.setDamage(potion2arrow(cauldron.getPotionId()));
                    player.getInventory().setItemInHand(item);
                } else if (item.getCount() > 1) {
                    Item newItem = item.clone();
                    newItem.setCount(1);
                    newItem.setDamage(potion2arrow(cauldron.getPotionId()));

                    if (!player.isCreative()) {
                        item.count--;
                    }

                    if (player.getInventory().canAddItem(newItem)) {
                        player.getInventory().addItem(newItem);
                    } else {
                        player.getLevel().dropItem(player.add(0, 1.3, 0), newItem, player.getDirectionVector().multiply(0.4));
                    }
                }

                setFillLevel(getFillLevel() - 1);
                if (isEmpty()) {
                    cauldron.setPotionId(0xffff);
                    cauldron.clearCustomColor();
                }
                this.level.setBlock(this, this, true);
                this.level.addLevelEvent(this.add(0.5, 0.375 + this.getDamage() * 0.125, 0.5), LevelEventPacket.EVENT_CAULDRON_DYE_ARMOR);
            case BlockID.SHULKER_BOX:
                if (isEmpty() || cauldron.isCustomColor() || cauldron.hasPotion()) {
                    break;
                }

                player.getInventory().setItemInHand(Item.get(Item.UNDYED_SHULKER_BOX).setCompoundTag(item.getCompoundTag()));
                setFillLevel(getFillLevel() - 1);
                this.level.setBlock(this, this, true);
                this.getLevel().addSound(this, Sound.CAULDRON_TAKEPOTION);
                break;
            default:
                return true;
        }

        this.level.updateComparatorOutputLevel(this);
        return true;
    }

    private static int potion2arrow(int potion) {
        int id = potion & 0xffff;
        if (id < 5 || id > 43) return 1; // if it fails don't create game crashing arrows
        return id < 43 ? id + 1 : id;
    }

    protected void replaceBucket(Item oldBucket, Player player, Item newBucket) {
        if (player.isSurvival() || player.isAdventure()) {
            if (oldBucket.getCount() == 1) {
                player.getInventory().setItemInHand(newBucket);
            } else {
                oldBucket.setCount(oldBucket.getCount() - 1);
                if (player.getInventory().canAddItem(newBucket)) {
                    player.getInventory().addItem(newBucket);
                } else {
                    player.getLevel().dropItem(player.add(0, 1.3, 0), newBucket, player.getDirectionVector().multiply(0.4));
                }
            }
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        CompoundTag nbt = new CompoundTag("")
                .putString("id", BlockEntity.CAULDRON)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putShort("PotionId", 0xffff)
                .putByte("SplashPotion", 0);

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntity.createBlockEntity(BlockEntity.CAULDRON, this.getChunk(), nbt);

        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{Item.get(Item.CAULDRON)};
        }

        return new Item[0];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM && level.isRaining() && !this.isFull()) {
            if (freezing < 1) {
                freezing = Biome.getBiome(level.getBiomeId((int) this.x, (int) this.z)).isFreezing() ? (byte) 2 : (byte) 1;
            }
            if (freezing == 1 && ThreadLocalRandom.current().nextInt(20) == 0 && level.canBlockSeeSky(this)) {
                this.setFillLevel(this.getFillLevel() + 1);
                this.getLevel().setBlock(this, this, true, true);
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return super.onUpdate(type);
    }

    @Override
    public Item toItem() {
        return Item.get(Item.CAULDRON);
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride() {
        return getFillLevel();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    // Source: PN/#666
    private void consumePotion(Item item, Player player) {
        if (player.isSurvival() || player.isAdventure()) {
            if (item.getCount() == 1) {
                player.getInventory().setItemInHand(new ItemBlock(Block.get(AIR)));
            } else if (item.getCount() > 1) {
                item.count--;
                Item bottle = Item.get(Item.GLASS_BOTTLE);
                if (player.getInventory().canAddItem(bottle)) {
                    player.getInventory().addItem(bottle);
                } else {
                    player.getLevel().dropItem(player.add(0, 1.3, 0), bottle, player.getDirectionVector().multiply(0.4));
                }
            }
        }
    }

    // Source: PN/#666
    public void clearWithFizz(BlockEntityCauldron cauldron) {
        this.setFillLevel(0);
        cauldron.setPotionId(0xffff);
        cauldron.setSplashPotion(false);
        cauldron.clearCustomColor();
        this.level.setBlock(this, Block.get(CAULDRON_BLOCK), true);
        this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_FIZZ);
        this.getLevel().addParticle(new SmokeParticle(add(ThreadLocalRandom.current().nextDouble(), 1.2, ThreadLocalRandom.current().nextDouble())), null, 8);
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    @Override
    public boolean canBePushed() {
        return false; // prevent item loss issue with pistons until a working implementation
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }
}
