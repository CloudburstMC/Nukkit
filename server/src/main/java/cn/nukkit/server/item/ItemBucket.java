package cn.nukkit.server.item;

import cn.nukkit.api.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.api.event.player.PlayerBucketFillEvent;
import cn.nukkit.server.Player;
import cn.nukkit.server.block.Block;
import cn.nukkit.server.block.BlockAir;
import cn.nukkit.server.block.BlockLiquid;
import cn.nukkit.server.block.BlockWater;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.math.BlockFace.Plane;

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
    public boolean onActivate(NukkitLevel level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        Block targetBlock = Block.get(this.meta);

        if (targetBlock instanceof BlockAir) {
            if (target instanceof BlockLiquid && target.getDamage() == 0) {
                Item result = Item.get(BUCKET, this.getDamageByTarget(target.getId()), 1);
                PlayerBucketFillEvent ev;
                player.getServer().getPluginManager().callEvent(ev = new PlayerBucketFillEvent(player, block, face, this, result));
                if (!ev.isCancelled()) {
                    player.getLevel().setBlock(target, new BlockAir(), true, true);

                    // When water is removed ensure any adjacent still water is
                    // replaced with water that can flow.
                    for (BlockFace side : Plane.HORIZONTAL) {
                        Block b = target.getSide(side);
                        if (b.getId() == STILL_WATER) {
                            level.setBlock(b, new BlockWater());
                        }
                    }

                    if (player.isSurvival()) {
                        Item clone = this.clone();
                        clone.setCount(this.getCount() - 1);
                        player.getInventory().setItemInHand(clone);
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
                    Item clone = this.clone();
                    clone.setCount(this.getCount() - 1);
                    player.getInventory().setItemInHand(clone);
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
