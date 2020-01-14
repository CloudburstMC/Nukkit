package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockLava;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.*;
import static cn.nukkit.item.ItemIds.BUCKET;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBucket extends Item {

    public ItemBucket(Identifier id) {
        super(id);
    }

    protected static String getName(int meta) {
        switch (meta) {
            case 1:
                return "Milk";
            case 2:
                return "Bucket of Cod";
            case 3:
                return "Bucket of Salmon";
            case 4:
                return "Bucket of Tropical Fish";
            case 5:
                return "Bucket of Pufferfish";
            case 8:
                return "Water Bucket";
            case 10:
                return "Lava Bucket";
            default:
                return "Bucket";
        }
    }

    public Identifier getBlockIdFromDamage(int target) {
        switch (target) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
            case 9:
                return FLOWING_WATER;
            case 10:
            case 11:
                return FLOWING_LAVA;
            default:
                return AIR;
        }
    }

    public int getDamageFromIdentifier(Identifier id) {
        if (id == FLOWING_WATER) {
            return 8;
        } else if (id == FLOWING_LAVA) {
            return 10;
        }
        throw new IllegalArgumentException(id + " cannot be in bucket");
    }

    @Override
    public int getMaxStackSize() {
        return this.getDamage() == 0 ? 16 : 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, Vector3f clickPos) {
        Block targetBlock = Block.get(getBlockIdFromDamage(this.getDamage()));

        if (targetBlock instanceof BlockAir) {
            if (target instanceof BlockLiquid && target.getDamage() == 0) {
                Item result = Item.get(BUCKET, this.getDamageFromIdentifier(target.getId()), 1);
                PlayerBucketFillEvent ev;
                player.getServer().getPluginManager().callEvent(ev = new PlayerBucketFillEvent(player, block, face, this, result));
                if (!ev.isCancelled()) {
                    player.getLevel().setBlock(target, Block.get(AIR), true, true);

                    // When water is removed ensure any adjacent still water is
                    // replaced with water that can flow.
                    for (BlockFace side : Plane.HORIZONTAL) {
                        Block b = target.getSide(side);
                        if (b.getId() == WATER) {
                            level.setBlock(b, Block.get(FLOWING_WATER));
                        }
                    }

                    if (player.isSurvival()) {
                        Item clone = this.clone();
                        clone.setCount(this.getCount() - 1);
                        player.getInventory().setItemInHand(clone);
                        player.getInventory().addItem(ev.getItem());
                    }

                    if (target instanceof BlockLava) {
                        level.addLevelSoundEvent(block.asVector3f(), LevelSoundEventPacket.SOUND_BUCKET_FILL_LAVA);
                    } else {
                        level.addLevelSoundEvent(block.asVector3f(), LevelSoundEventPacket.SOUND_BUCKET_FILL_WATER);
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
            ev.setCancelled(!block.canBeFlooded());

            if (player.getLevel().getName().equals("nether") && this.getDamage() != 10) {
                ev.setCancelled(true);
            }

            if (!ev.isCancelled()) {
                player.getLevel().setBlock(block, targetBlock, true, true);
                if (player.isSurvival()) {
                    Item clone = this.clone();
                    clone.setCount(this.getCount() - 1);
                    player.getInventory().setItemInHand(clone);
                    player.getInventory().addItem(ev.getItem());
                }

                if (this.getDamage() == 10) {
                    level.addLevelSoundEvent(block.asVector3f(), LevelSoundEventPacket.SOUND_BUCKET_EMPTY_LAVA);
                } else {
                    level.addLevelSoundEvent(block.asVector3f(), LevelSoundEventPacket.SOUND_BUCKET_EMPTY_WATER);
                }

                return true;
            } else {
                player.getLevel().sendBlocks(new Player[]{player}, new Block[]{Block.get(AIR, 0, block)}, UpdateBlockPacket.FLAG_ALL_PRIORITY);
                player.getInventory().sendContents(player);
            }
        }

        return false;
    }

    @Override
    public boolean onClickAir(Player player, Vector3f directionVector) {
        return this.getDamage() == 1; // Milk
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(player, this);

        player.getServer().getPluginManager().callEvent(consumeEvent);
        if (consumeEvent.isCancelled()) {
            player.getInventory().sendContents(player);
            return false;
        }

        if (player.isSurvival()) {
            this.decrementCount();
            player.getInventory().setItemInHand(this);
            player.getInventory().addItem(Item.get(BUCKET));
        }

        player.removeAllEffects();
        return true;
    }
}
