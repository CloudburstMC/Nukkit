package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.block.*;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;

/**
 * @author MagicDroidX (Nukkit Project)
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

    public static int getDamageByTarget(int target) {
        switch (target) {
            case 2:
            case 3:
            case 4:
            case 5:
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

    @PowerNukkitDifference(info = "You can't use bucket in adventure mode.", since = "1.4.0.0-PN")
    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
            return false;
        }
        Block targetBlock = Block.get(getDamageByTarget(this.meta));

        if (targetBlock instanceof BlockAir) {
            if (!(target instanceof BlockLiquid) || target.getDamage() != 0) {
                target = target.getLevelBlockAtLayer(1);
            }
            if (!(target instanceof BlockLiquid) || target.getDamage() != 0) {
                target = block;
            }
            if (!(target instanceof BlockLiquid) || target.getDamage() != 0) {
                target = block.getLevelBlockAtLayer(1);
            }
            if (target instanceof BlockLiquid && target.getDamage() == 0) {
                Item result = Item.get(BUCKET, getDamageByTarget(target.getId()), 1);
                PlayerBucketFillEvent ev;
                player.getServer().getPluginManager().callEvent(ev = new PlayerBucketFillEvent(player, block, face, target, this, result));
                if (!ev.isCancelled()) {
                    player.getLevel().setBlock(target, target.layer, Block.get(BlockID.AIR), true, true);

                    // When water is removed ensure any adjacent still water is
                    // replaced with water that can flow.
                    for (BlockFace side : Plane.HORIZONTAL) {
                        Block b = target.getSideAtLayer(0, side);
                        if (b.getId() == STILL_WATER) {
                            level.setBlock(b, Block.get(BlockID.WATER));
                        }
                    }

                    if (player.isSurvival()) {
                        Item clone = this.clone();
                        clone.setCount(this.getCount() - 1);
                        player.getInventory().setItemInHand(clone);
                        player.getInventory().addItem(ev.getItem());
                    }

                    if (target instanceof BlockLava) {
                        level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_FILL_LAVA);
                    } else {
                        level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_FILL_WATER);
                    }

                    return true;
                } else {
                    player.getInventory().sendContents(player);
                }
            }
        } else if (targetBlock instanceof BlockLiquid) {
            Item result = Item.get(BUCKET, 0, 1);
            boolean usesWaterlogging = ((BlockLiquid) targetBlock).usesWaterLogging();
            Block placementBlock;
            if (usesWaterlogging) {
                if (block.getId() == BlockID.BAMBOO) {
                    placementBlock = block;
                } else if (target.getWaterloggingLevel() > 0) {
                    placementBlock = target.getLevelBlockAtLayer(1);
                } else if (block.getWaterloggingLevel() > 0) {
                    placementBlock = block.getLevelBlockAtLayer(1);
                } else {
                    placementBlock = block;
                }
            } else {
                placementBlock = block;
            }

            PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, placementBlock, face, target, this, result);
            player.getServer().getPluginManager().callEvent(ev);
            boolean canBeFlowedInto = placementBlock.canBeFlowedInto() || placementBlock.getId() == BlockID.BAMBOO;
            if (usesWaterlogging) {
                ev.setCancelled(placementBlock.getWaterloggingLevel() <= 0 && !canBeFlowedInto);
            } else {
                ev.setCancelled(!canBeFlowedInto);
            }

            if (player.getLevel().getDimension() == Level.DIMENSION_NETHER && this.getDamage() != 10) {
                ev.setCancelled(true);
            }

            player.getServer().getPluginManager().callEvent(ev);

            if (!ev.isCancelled()) {
                player.getLevel().setBlock(placementBlock, placementBlock.layer, targetBlock, true, true);
                if (player.isSurvival()) {
                    Item clone = this.clone();
                    clone.setCount(this.getCount() - 1);
                    player.getInventory().setItemInHand(clone);
                    player.getInventory().addItem(ev.getItem());
                }

                if (this.getDamage() == 10) {
                    level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_EMPTY_LAVA);
                } else {
                    level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_EMPTY_WATER);
                }

                return true;
            } else {
                player.getLevel().sendBlocks(new Player[] {player}, new Block[] {block.getLevelBlockAtLayer(1)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
                player.getInventory().sendContents(player);
            }
        }

        return false;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return this.getDamage() == 1; // Milk
    }

    @PowerNukkitDifference(info = "You can't use milk in spectator mode and milk is now 'drinked' in adventure mode", since = "1.4.0.0-PN")
    @Override
    public boolean onUse(Player player, int ticksUsed) {
        if (player.isSpectator()) {
            return false;
        }
        PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(player, this);

        player.getServer().getPluginManager().callEvent(consumeEvent);
        if (consumeEvent.isCancelled()) {
            player.getInventory().sendContents(player);
            return false;
        }

        if (!player.isCreative()) {
            this.count--;
            player.getInventory().setItemInHand(this);
            player.getInventory().addItem(new ItemBucket());
        }

        player.removeAllEffects();
        return true;
    }
}
