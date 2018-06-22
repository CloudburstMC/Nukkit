package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCauldron;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.item.*;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;

import java.util.Map;

/**
 * author: CreeperFace
 * Nukkit Project
 */
public class BlockCauldron extends BlockSolidMeta {

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
        return this.getDamage() == 0x06;
    }

    public boolean isEmpty() {
        return this.getDamage() == 0x00;
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
                    bucket.setDamage(8);//water bucket

                    PlayerBucketFillEvent ev = new PlayerBucketFillEvent(player, this, null, item, bucket);
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        if (player.isSurvival()) {
                            player.getInventory().setItemInHand(ev.getItem());
                        }
                        this.setDamage(0);//empty
                        this.level.setBlock(this, this, true);
                        cauldron.clearCustomColor();
                        this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.CAULDRON_TAKEWATER);
                    }
                } else if (item.getDamage() == 8) {//water bucket

                    if (isFull() && !cauldron.isCustomColor() && !cauldron.hasPotion()) {
                        break;
                    }

                    ItemBucket bucket = (ItemBucket) item.clone();
                    bucket.setDamage(0);//empty bucket

                    PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, this, null, item, bucket);
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        if (player.isSurvival()) {
                            player.getInventory().setItemInHand(ev.getItem());
                        }
                        if (cauldron.hasPotion()) {//if has potion
                            this.setDamage(0);//empty
                            cauldron.setPotionId(0xffff);//reset potion
                            cauldron.setSplashPotion(false);
                            cauldron.clearCustomColor();
                            this.level.setBlock(this, this, true);
                            this.level.addSound(this.add(0.5, 0, 0.5), Sound.CAULDRON_EXPLODE);
                        } else {
                            this.setDamage(6);//fill
                            cauldron.clearCustomColor();
                            this.level.setBlock(this, this, true);
                            this.level.addSound(this.add(0.5, 1, 0.5), Sound.BUCKET_FILL_WATER);
                        }
                        //this.update();
                    }
                }
                break;
            case Item.DYE: //TODO
                break;
            case Item.LEATHER_CAP:
            case Item.LEATHER_TUNIC:
            case Item.LEATHER_PANTS:
            case Item.LEATHER_BOOTS:
                break;
            case Item.POTION:
                if (isFull()) {
                    break;
                }
                this.setDamage(this.getDamage() + 1);
                if (this.getDamage() > 0x06)
                    this.setDamage(0x06);

                if (item.getCount() == 1) {
                    player.getInventory().setItemInHand(new ItemBlock(new BlockAir()));
                } else if (item.getCount() > 1) {
                    item.setCount(item.getCount() - 1);
                    player.getInventory().setItemInHand(item);

                    Item bottle = new ItemGlassBottle();
                    if (player.getInventory().canAddItem(bottle)) {
                        player.getInventory().addItem(bottle);
                    } else {
                        player.getLevel().dropItem(player.add(0, 1.3, 0), bottle, player.getDirectionVector().multiply(0.4));
                    }
                }

                this.level.addSound(this.add(0.5, 0.5, 0.5), Sound.CAULDRON_FILLPOTION);
                break;
            case Item.GLASS_BOTTLE:
                if (isEmpty()) {
                    break;
                }

                this.setDamage(this.getDamage() - 1);
                if (this.getDamage() < 0x00)
                    this.setDamage(0x00);

                if (item.getCount() == 1) {
                    player.getInventory().setItemInHand(new ItemPotion());
                } else if (item.getCount() > 1) {
                    item.setCount(item.getCount() - 1);
                    player.getInventory().setItemInHand(item);

                    Item potion = new ItemPotion();
                    if (player.getInventory().canAddItem(potion)) {
                        player.getInventory().addItem(potion);
                    } else {
                        player.getLevel().dropItem(player.add(0, 1.3, 0), potion, player.getDirectionVector().multiply(0.4));
                    }
                }

                this.level.addSound(this.add(0.5, 0.5, 0.5), Sound.CAULDRON_TAKEPOTION);
                break;
            default:
                return true;
        }

        this.level.updateComparatorOutputLevel(this);
        return true;
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

        new BlockEntityCauldron(this.level.getChunk((int) this.x >> 4, (int) this.z >> 4), nbt);
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{new ItemCauldron()};
        }

        return new Item[0];
    }

    @Override
    public Item toItem() {
        return new ItemCauldron();
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride() {
        return this.getDamage();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
