package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Air;
import cn.nukkit.block.Block;
import cn.nukkit.block.Liquid;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.level.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Bucket extends Item {

    public Bucket() {
        this(0, 1);
    }

    public Bucket(Integer meta) {
        this(meta, 1);
    }

    public Bucket(Integer meta, int count) {
        super(BUCKET, meta, count, "Bucket");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, int face, double fx, double fy, double fz) {
        Block targetBlock = Block.get(this.meta);

        if (targetBlock instanceof Air) {
            if (target instanceof Liquid && target.getDamage() == 0) {
                Item result = this.clone();
                result.setDamage(target.getId());
                PlayerBucketFillEvent ev;
                player.getServer().getPluginManager().callEvent(ev = new PlayerBucketFillEvent(player, block, face, this, result));
                if (!ev.isCancelled()) {
                    player.getLevel().setBlock(target, new Air(), true, true);
                    if (player.isSurvival()) {
                        player.getInventory().setItemInHand(ev.getItem());
                    }
                    return true;
                } else {
                    player.getInventory().sendContents(player);
                }
            }
        } else if (targetBlock instanceof Liquid) {
            Item result = this.clone();
            result.setDamage(0);
            PlayerBucketFillEvent ev;
            player.getServer().getPluginManager().callEvent(ev = new PlayerBucketFillEvent(player, block, face, this, result));
            if (!ev.isCancelled()) {
                player.getLevel().setBlock(block, targetBlock, true, true);
                if (player.isSurvival()) {
                    player.getInventory().setItemInHand(ev.getItem());
                }
                return true;
            } else {
                player.getInventory().sendContents(player);
            }
        }

        return false;
    }
}
