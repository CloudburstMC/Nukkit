package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.UpdateBlockPacket;

import javax.annotation.Nullable;

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

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    protected ItemBucket(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
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
                return BlockID.WATER;
            case 10:
            case 11:
                return BlockID.LAVA;
            default:
                return BlockID.AIR;
        }
    }
    
    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public boolean isEmpty() {
        return getId() == BUCKET && getDamage() == 0;
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public boolean isWater() {
        return getTargetBlock().getId() == BlockID.WATER;
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public boolean isLava() {
        return getTargetBlock().getId() == BlockID.LAVA;
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    @Nullable
    public String getFishEntityId() {
        if (getId() != BUCKET) {
            return null;
        }
        switch (this.getDamage()) {
            case 2: return "Cod";
            case 3: return "Salmon";
            case 4: return "TropicalFish";
            case 5: return "Pufferfish";
            default: return null;
        }
    }

    @Override
    public int getMaxStackSize() {
        return this.meta == 0 && getId() == BUCKET ? 16 : 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public Block getTargetBlock() {
        return getId() == BUCKET? Block.get(getDamageByTarget(this.meta)) : Block.get(BlockID.AIR);
    }


    @PowerNukkitDifference(info = "You can't use bucket in adventure mode.", since = "1.4.0.0-PN")
    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
            return false;
        }

        Block targetBlock = getTargetBlock();

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
                        if (player.getInventory().canAddItem(ev.getItem())) {
                            player.getInventory().addItem(ev.getItem());
                        } else {
                            player.dropItem(ev.getItem());
                        }
                    }

                    if (target instanceof BlockLava) {
                        level.addSound(block, Sound.BUCKET_FILL_LAVA);
                    } else {
                        level.addSound(block, Sound.BUCKET_FILL_WATER);
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

            if (!canBeUsedOnDimension(player.getLevel().getDimension())) {
                ev.setCancelled(true);
            }

            player.getServer().getPluginManager().callEvent(ev);

            if (!ev.isCancelled()) {
                player.getLevel().setBlock(placementBlock, placementBlock.layer, targetBlock, true, true);
                if (player.isSurvival()) {
                    Item clone = this.clone();
                    clone.setCount(this.getCount() - 1);
                    player.getInventory().setItemInHand(clone);
                    if (player.getInventory().canAddItem(ev.getItem())) {
                        player.getInventory().addItem(ev.getItem());
                    } else {
                        player.dropItem(ev.getItem());
                    }
                }

                afterUse(level, block);

                return true;
            } else {
                player.getLevel().sendBlocks(new Player[] {player}, new Block[] {block.getLevelBlockAtLayer(1)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 1);
                player.getInventory().sendContents(player);
            }
        }

        return false;
    }
    
    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    protected boolean canBeUsedOnDimension(int dimension) {
        if (getId() != BUCKET) {
            return true;
        }
        
        return dimension != Level.DIMENSION_NETHER || (getDamage() == 10 || getDamage() == 1);
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    protected void afterUse(Level level, Block block) {
        if (getId() != BUCKET) {
            return;
        }
        
        if (this.getDamage() == 10) {
            level.addSound(block, Sound.BUCKET_EMPTY_LAVA);
        } else {
            level.addSound(block, Sound.BUCKET_EMPTY_WATER);
        }

        switch (this.getDamage()) {
            case 2:
                Entity e2 = Entity.createEntity("Cod", block);
                if (e2 != null) e2.spawnToAll();
                break;
            case 3:
                Entity e3 = Entity.createEntity("Salmon", block);
                if (e3 != null) e3.spawnToAll();
                break;
            case 4:
                Entity e4 = Entity.createEntity("TropicalFish", block);
                if (e4 != null) e4.spawnToAll();
                break;
            case 5:
                Entity e5 = Entity.createEntity("Pufferfish", block);
                if (e5 != null) e5.spawnToAll();
                break;
        }
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return getId() == BUCKET && this.getDamage() == 1; // Milk
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
