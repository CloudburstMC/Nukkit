package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityEgg;
import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockChorusFlower extends BlockTransparentMeta {

    // Version 7a3d8a5
    public BlockChorusFlower() {
        super(0);
    }

    public BlockChorusFlower(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHORUS_FLOWER;
    }

    @Override
    public String getName() {
        return "Chorus Flower";
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public double getResistance() {
        return 0.4;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    private boolean isPositionValid() {
        // Chorus flowers must be above end stone or chorus plant, or be above air and horizontally adjacent to exactly one chorus plant.
        // If these conditions are not met, the block breaks without dropping anything.
        Block down = down();
        if (down.getId() == CHORUS_PLANT || down.getId() == END_STONE) {
            return true;
        }
        if (down.getId() != AIR) {
            return false;
        }
        boolean foundPlant = false;
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Block side = getSide(face);
            if (side.getId() == CHORUS_PLANT) {
                if (foundPlant) {
                    return false;
                }
                foundPlant = true;
            }
        }

        return foundPlant;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isPositionValid()) {
                this.getLevel().scheduleUpdate(this, 1);
                return type;
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.getLevel().useBreakOn(this, null, null, true);
            return type;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            // Check limit
            Block up;
            if (this.y < level.getMaxBlockY() && (up = this.up()).getId() == AIR) {
                if (!isFullyAged()) {
                    boolean growUp = false; // Grow upward?
                    boolean ground = false; // Is on the ground directly?
                    Block down = this.down();
                    if (down.getId() == AIR || down.getId() == END_STONE) {
                        growUp = true;
                    } else if (down.getId() == CHORUS_PLANT) {
                        int height = 1;
                        for (int y = 2; y < 6; y++) {
                            Block downY = this.down(y);
                            if (downY.getId() == CHORUS_PLANT) {
                                height++;
                            } else {
                                if (downY.getId() == END_STONE) {
                                    ground = true;
                                }
                                break;
                            }
                        }

                        if (height < 2 || height <= ThreadLocalRandom.current().nextInt(ground ? 5 : 4)) {
                            growUp = true;
                        }
                    }

                    // Grow Upward
                    if (growUp && up.up().getId() == AIR && isHorizontalAir(up)) {
                        BlockChorusFlower block = (BlockChorusFlower) this.clone();
                        block.y = this.y + 1;
                        BlockGrowEvent ev = new BlockGrowEvent(this, block);
                        Server.getInstance().getPluginManager().callEvent(ev);

                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(this, Block.get(CHORUS_PLANT));
                            this.getLevel().setBlock(block, ev.getNewState());
                            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_CHORUSGROW);
                        } else {
                            return Level.BLOCK_UPDATE_RANDOM;
                        }
                        // Grow Horizontally
                    } else if (!isFullyAged()) {
                        ThreadLocalRandom random = ThreadLocalRandom.current();
                        for (int i = 0; i < random.nextInt(ground ? 5 : 4); i++) {
                            BlockFace face = BlockFace.Plane.HORIZONTAL.random();
                            Block check = this.getSide(face);
                            if (check.getId() == AIR && check.down().getId() == AIR && isHorizontalAirExcept(check, face.getOpposite())) {
                                BlockChorusFlower block = (BlockChorusFlower) this.clone();
                                block.x = check.x;
                                block.y = check.y;
                                block.z = check.z;
                                block.setAge(getAge() + 1);
                                BlockGrowEvent ev = new BlockGrowEvent(this, block);
                                Server.getInstance().getPluginManager().callEvent(ev);

                                if (!ev.isCancelled()) {
                                    this.getLevel().setBlock(this, Block.get(CHORUS_PLANT));
                                    this.getLevel().setBlock(block, ev.getNewState());
                                    this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_CHORUSGROW);
                                } else {
                                    return Level.BLOCK_UPDATE_RANDOM;
                                }
                            }
                        }
                        // Death
                    } else {
                        BlockChorusFlower block = (BlockChorusFlower) this.clone();
                        block.setAge(getMaxAge());
                        BlockGrowEvent ev = new BlockGrowEvent(this, block);
                        Server.getInstance().getPluginManager().callEvent(ev);

                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(block, ev.getNewState());
                            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_CHORUSDEATH);
                        } else {
                            return Level.BLOCK_UPDATE_RANDOM;
                        }
                    }
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!isPositionValid()) {
            return false;
        }
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{this.toItem()};
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        int e = entity.getNetworkId();
        if (e == EntityArrow.NETWORK_ID || e == EntityThrownTrident.NETWORK_ID || e == EntityFirework.NETWORK_ID || e == EntitySnowball.NETWORK_ID || e == EntityEgg.NETWORK_ID || e == 85 || e == 94 || e == 79 || e == 89) {
            entity.close();
            this.getLevel().useBreakOn(this);
        }
    }

    public int getMaxAge() {
        return 5;
    }

    public int getAge() {
        return getDamage();
    }

    public void setAge(int age) {
        this.setDamage(age);
    }

    public boolean isFullyAged() {
        return getAge() >= getMaxAge();
    }

    private boolean isHorizontalAir(Block block) {
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            if (block.getSide(face).getId() != AIR) {
                return false;
            }
        }
        return true;
    }

    private boolean isHorizontalAirExcept(Block block, BlockFace except) {
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            if (face != except) {
                if (block.getSide(face).getId() != AIR) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }
}
