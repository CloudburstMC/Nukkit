package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityPotionEffectEvent;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.particle.ExplodeParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;

/**
 * @author MagicDroidX
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
            case 11:
                return "Powder Snow Bucket";
            case 12:
                return "Bucket of Axolotl";
            case 13:
                return "Bucket of Tadpoles";
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
            case 9: // still water
            case 12:
            case 13:
                return WATER;
            case 10:
            case 11: // still lava
                return LAVA;
            default:
                return AIR;
        }
    }

    public static int getBlockByDamage(int target) {
        switch (target) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
            case 12:
            case 13:
                return WATER;
            case 10:
                return LAVA;
            case 11:
                return POWDER_SNOW;
            default:
                return AIR;
        }
    }

    @Override
    public int getMaxStackSize() {
        return this.meta == 0 ? 16 : 1;
    }

    @Override
    public boolean canBeActivated() {
        return this.getDamage() != 1; // Not milk
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
            return false;
        }

        Block targetBlock;
        if (target instanceof BlockPowderSnow && this.getDamage() == 0) {
            PlayerBucketFillEvent ev = new PlayerBucketFillEvent(player, block, face, this, Item.get(BUCKET, 11, 1));
            player.getServer().getPluginManager().callEvent(ev);

            if (!ev.isCancelled()) {
                player.getLevel().setBlock(target, BlockLayer.NORMAL, Block.get(BlockID.AIR), true, true);

                if (!player.isCreative()) {
                    if (this.getCount() - 1 <= 0) {
                        player.getInventory().setItemInHand(ev.getItem());
                    } else {
                        player.getInventory().setItemInHand(this.decrement(1));
                        if (player.getInventory().canAddItem(ev.getItem())) {
                            player.getInventory().addItem(ev.getItem());
                        } else {
                            player.dropItem(ev.getItem());
                        }
                    }
                }

                level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_FILL_POWDER_SNOW);
                return true;
            } else {
                player.setNeedSendInventory(true);
            }
        } else if ((targetBlock = Block.get(getBlockByDamage(this.meta))) instanceof BlockAir) {
            if (!(target instanceof BlockLiquid) || target.getDamage() != 0) {
                target = target.getLevelBlock(BlockLayer.WATERLOGGED);
            }
            if (!(target instanceof BlockLiquid) || target.getDamage() != 0) {
                target = block;
            }
            if (!(target instanceof BlockLiquid) || target.getDamage() != 0) {
                target = block.getLevelBlock(BlockLayer.WATERLOGGED);
            }

            if (target instanceof BlockLiquid && target.getDamage() == 0) {
                Item result = Item.get(BUCKET, getDamageByTarget(target.getId()), 1);
                PlayerBucketFillEvent ev;
                player.getServer().getPluginManager().callEvent(ev = new PlayerBucketFillEvent(player, block, face, this, result));

                if (!ev.isCancelled()) {
                    player.getLevel().setBlock(target, target.getLayer(), Block.get(BlockID.AIR), true, true);

                    // When water is removed ensure any adjacent still water is
                    // replaced with water that can flow.
                    for (BlockFace side : Plane.HORIZONTAL) {
                        Block b = target.getSide(Block.LAYER_NORMAL, side);
                        if (b.getId() == STILL_WATER) {
                            level.setBlock(b, Block.get(BlockID.WATER));
                        }
                    }

                    if (!player.isCreative()) {
                        if (this.getCount() - 1 <= 0) {
                            player.getInventory().setItemInHand(ev.getItem());
                        } else {
                            player.getInventory().setItemInHand(this.decrement(1));
                            if (player.getInventory().canAddItem(ev.getItem())) {
                                player.getInventory().addItem(ev.getItem());
                            } else {
                                player.dropItem(ev.getItem());
                            }
                        }
                    }

                    if (target instanceof BlockLava) {
                        level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_FILL_LAVA);
                    } else {
                        level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_FILL_WATER);
                    }

                    return true;
                } else {
                    player.setNeedSendInventory(true);
                }
            }
        } else if (targetBlock instanceof BlockLiquid) {
            Item result = Item.get(BUCKET, 0, 1);
            boolean usesWaterlogging = ((BlockLiquid) targetBlock).usesWaterLogging();
            Block placementBlock;
            if (usesWaterlogging) {
                if (block.getId() == BlockID.BAMBOO) {
                    placementBlock = block;
                } else if (target.getWaterloggingType() != Block.WaterloggingType.NO_WATERLOGGING) {
                    placementBlock = target.getLevelBlock(BlockLayer.WATERLOGGED);
                } else if (block.getWaterloggingType() != Block.WaterloggingType.NO_WATERLOGGING) {
                    placementBlock = block.getLevelBlock(BlockLayer.WATERLOGGED);
                } else {
                    placementBlock = block;
                }
            } else {
                placementBlock = block;
            }
            PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, block, face, this, result, true);
            boolean canBeFlowedInto = placementBlock.canBeFlowedInto() || placementBlock.getId() == BlockID.BAMBOO;
            if (usesWaterlogging) {
                ev.setCancelled(placementBlock.getWaterloggingType() == Block.WaterloggingType.NO_WATERLOGGING && !canBeFlowedInto);
            } else {
                ev.setCancelled(!canBeFlowedInto);
            }
            if (!block.canBeFlowedInto() && !(block instanceof BlockTrapdoor)) {
                ev.setCancelled(true);
            }

            boolean nether = false;
            if (player.getLevel().getDimension() == Level.DIMENSION_NETHER && this.getDamage() != 10) {
                ev.setCancelled(true);
                nether = true;
            }

            player.getServer().getPluginManager().callEvent(ev);

            if (!ev.isCancelled()) {
                player.getLevel().setBlock(placementBlock, placementBlock.getLayer(), targetBlock, true, true);
                if (!player.isCreative()) {
                    if (this.getCount() - 1 <= 0) {
                        player.getInventory().setItemInHand(ev.getItem());
                    } else {
                        player.getInventory().setItemInHand(this.decrement(1));
                        if (player.getInventory().canAddItem(ev.getItem())) {
                            player.getInventory().addItem(ev.getItem());
                        } else {
                            player.dropItem(ev.getItem());
                        }
                    }
                }

                if (this.getDamage() == 10) {
                    level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_EMPTY_LAVA);
                } else {
                    level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_EMPTY_WATER);
                }

                if (ev.isMobSpawningAllowed()) {
                    switch (this.getDamage()) {
                        case 2:
                            Entity e2 = Entity.createEntity("Cod", block.add(0.5, 0, 0.5));
                            if (e2 != null) e2.spawnToAll();
                            break;
                        case 3:
                            Entity e3 = Entity.createEntity("Salmon", block.add(0.5, 0, 0.5));
                            if (e3 != null) e3.spawnToAll();
                            break;
                        case 4:
                            Entity e4 = Entity.createEntity("TropicalFish", block.add(0.5, 0, 0.5));
                            if (e4 != null) e4.spawnToAll();
                            break;
                        case 5:
                            Entity e5 = Entity.createEntity("Pufferfish", block.add(0.5, 0, 0.5));
                            if (e5 != null) e5.spawnToAll();
                            break;
                        case 12:
                            Entity e12 = Entity.createEntity("Axolotl", block.add(0.5, 0, 0.5));
                            if (e12 != null) e12.spawnToAll();
                            break;
                        case 13:
                            Entity e13 = Entity.createEntity("Tadpole", block.add(0.5, 0, 0.5));
                            if (e13 != null) e13.spawnToAll();
                            break;
                    }
                }

                return true;
            } else if (nether) {
                if (!player.isCreative()) {
                    this.setDamage(0); // Empty bucket
                    player.getInventory().setItemInHand(this);
                }
                player.getLevel().addLevelSoundEvent(target, LevelSoundEventPacket.SOUND_FIZZ);
                player.getLevel().addParticle(new ExplodeParticle(target.add(0.5, 1, 0.5)));
            } else {
                player.getLevel().sendBlocks(new Player[] {player}, new Block[] {block.getLevelBlock(Block.LAYER_WATERLOGGED)}, UpdateBlockPacket.FLAG_ALL_PRIORITY, BlockLayer.WATERLOGGED);
                player.setNeedSendInventory(true);
            }
        } else if (targetBlock instanceof BlockPowderSnow) {
            if (player.getLevel().getProvider() instanceof Anvil || !block.canBeReplaced()) {
                return false;
            }

            PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, block, face, this, Item.get(BUCKET, 0, 1), true);
            player.getServer().getPluginManager().callEvent(ev);

            if (!ev.isCancelled()) {
                player.getLevel().setBlock(block, BlockLayer.NORMAL, targetBlock, true, true);
                if (!player.isCreative()) {
                    if (this.getCount() - 1 <= 0) {
                        player.getInventory().setItemInHand(ev.getItem());
                    } else {
                        player.getInventory().setItemInHand(this.decrement(1));
                        if (player.getInventory().canAddItem(ev.getItem())) {
                            player.getInventory().addItem(ev.getItem());
                        } else {
                            player.dropItem(ev.getItem());
                        }
                    }
                }

                level.addLevelSoundEvent(block, LevelSoundEventPacket.SOUND_BUCKET_EMPTY_POWDER_SNOW);
                return true;
            } else {
                player.setNeedSendInventory(true);
            }
        }

        return false;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return this.getDamage() == 1; // Milk
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        if (player.isSpectator() || this.getDamage() != 1) {
            return false;
        }

        PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(player, this);

        player.getServer().getPluginManager().callEvent(consumeEvent);
        if (consumeEvent.isCancelled()) {
            player.setNeedSendInventory(true);
            return false;
        }

        if (!player.isCreative()) {
            player.getInventory().setItemInHand(Item.get(Item.BUCKET));
        }

        player.removeAllEffects(EntityPotionEffectEvent.Cause.MILK);
        return true;
    }
}
