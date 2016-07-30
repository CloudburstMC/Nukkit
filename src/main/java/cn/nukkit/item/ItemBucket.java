package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.level.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBucket extends Item {

    public ItemBucket() {
        this(0, 1);
    }

    public ItemBucket(Integer meta) {
        this(meta, 1);
    }

    public ItemBucket(Integer meta, int count) {
        super(BUCKET, meta, count, getName(meta));
    }

    protected static String getName(int meta) {
        switch (meta) {
            case 1:
                return "Milk";
            case 8:
                return "Water Bucket";
            case 10:
                return "Lava Bucket";
            default:
                return "Bucket";
        }
    }

    protected int getDamageByTarget(int target) {
        switch (target) {
            case 8:
            case 9:
                return 8;
            case 10:
            case 11:
                return 10;
            default:
                return 0;
        }
    }

    @Override
    public int getMaxStackSize() {
        return this.meta == 0 ? 16 : 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, int face, double fx, double fy, double fz) {
        Block targetBlock = Block.get(this.meta);

        if (targetBlock instanceof BlockAir) {
            if (target instanceof BlockLiquid && target.getDamage() == 0) {
                Item result = Item.get(BUCKET, this.getDamageByTarget(target.getId()), 1);
                PlayerBucketFillEvent ev;
                player.getServer().getPluginManager().callEvent(ev = new PlayerBucketFillEvent(player, block, face, this, result));
                if (!ev.isCancelled()) {
                    player.getLevel().setBlock(target, new BlockAir(), true, true);
                    if (player.isSurvival()) {
                        this.setCount(this.getCount() - 1);
                        player.getInventory().setItemInHand(this);
                        player.getInventory().addItem(ev.getItem());
                    }
                    return true;
                } else {
                    player.getInventory().sendContents(player);
                }
            }
        } else if (targetBlock instanceof BlockLiquid) {
            Item result = Item.get(BUCKET, 0, 1);
            PlayerBucketEmptyEvent ev;
            player.getServer().getPluginManager().callEvent(ev = new PlayerBucketEmptyEvent(player, block, face, this, result));
            if (!ev.isCancelled()) {
                player.getLevel().setBlock(block, targetBlock, true, true);
                if (player.isSurvival()) {
                    this.setCount(this.getCount() - 1);
                    player.getInventory().setItemInHand(this);
                    player.getInventory().addItem(ev.getItem());
                }
                return true;
            } else {
                player.getInventory().sendContents(player);
            }
        }

        return false;
    }
}
