package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;
import cn.nukkit.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

public class BlockKelp extends BlockFlowable {

    public BlockKelp() {
        this(0);
    }

    public BlockKelp(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Kelp";
    }

    @Override
    public int getId() {
        return BLOCK_KELP;
    }

    public double getHardness() {
        return 0;
    }

    public double getResistance() {
        return 0;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.FLOW_INTO_BLOCK;
    }

    @Override
    public boolean canBeFlowedInto() {
        return level == null || !(level.getProvider() instanceof Anvil);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (level != null && level.getProvider() instanceof Anvil) {
            Block down;
            if (!((block instanceof BlockWater && (block.getDamage() == 0 || block.getDamage() >= 8)) || block instanceof BlockBubbleColumn) || ((down = this.down()).getId() != this.getId() && down.isTransparent())) {
                return false;
            }
            // 15 is the highest supported meta value
            // Meta 0 does not grow to not overgrow previously generated kelp
            this.setDamage(Utils.rand(1, 15));
            return this.getLevel().setBlock(this, this, true, true);
        }


        Block down = this.down();
        Block layer1Block = block.getLevelBlock(BlockLayer.WATERLOGGED);
        int waterDamage;
        if ((down.getId() == BLOCK_KELP || down.isSolid()) && down.getId() != MAGMA && down.getId() != ICE && down.getId() != SOUL_SAND &&
                (layer1Block instanceof BlockWater && ((waterDamage = (block.getDamage())) == 0 || waterDamage == 8))
        ) {
            if (waterDamage == 8) {
                this.getLevel().setBlock(this, BlockLayer.WATERLOGGED, Block.get(WATER), true, false);
            }

            if (down.getId() == BLOCK_KELP && down.getDamage() != 24) {
                down.setDamage(24);
                this.getLevel().setBlock(down, down, true, true);
            }

            // Placing it by hand gives it a random age value between 0 and 24
            // Meta 0 does not grow to not overgrow previously generated kelp
            this.setDamage(Utils.rand(1, 24));
            this.getLevel().setBlock(this, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (level != null && level.getProvider() instanceof Anvil) {
            if (type == Level.BLOCK_UPDATE_NORMAL) {
                Block down = this.down();
                if (down.getId() != this.getId() && down.isTransparent()) {
                    this.getLevel().useBreakOn(this);
                }
            } else if (type == Level.BLOCK_UPDATE_RANDOM) {
                // Ignore meta value 0 to not overgrow previously generated kelp
                if (this.getDamage() > 0 && ThreadLocalRandom.current().nextInt(100) < 14) { // 14% chance to grow
                    if (this.getDamage() < 15) {
                        Block up = up();
                        if (up instanceof BlockWater && up.getDamage() == 0) {
                            Block grown = Block.get(BLOCK_KELP, this.getDamage() + 1);
                            BlockGrowEvent ev = new BlockGrowEvent(this, grown);
                            Server.getInstance().getPluginManager().callEvent(ev);
                            if (!ev.isCancelled()) {
                                this.level.setBlock((int) this.x, (int) this.y, (int) this.z, BlockLayer.NORMAL, this, false, false, false); // No need to send this to client
                                this.getLevel().setBlock(up, ev.getNewState(), true, true);
                            }
                        }
                    }
                }
            }
            return type;
        }


        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block blockLayer1 = this.getLevelBlock(BlockLayer.WATERLOGGED);
            int waterDamage = 0;
            if (!(blockLayer1 instanceof BlockIceFrosted) &&
                    (!(blockLayer1 instanceof BlockWater) || ((waterDamage = blockLayer1.getDamage()) != 0 && waterDamage != 8))) {
                this.getLevel().useBreakOn(this);
                return type;
            }

            Block down = this.down();
            if ((!down.isSolid() && down.getId() != BLOCK_KELP) || down.getId() == MAGMA || down.getId() == ICE || down.getId() == SOUL_SAND) {
                this.getLevel().useBreakOn(this);
                return type;
            }

            if (waterDamage == 8) {
                this.getLevel().setBlock(this, BlockLayer.WATERLOGGED, Block.get(WATER), true, false);
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            // Ignore meta value 0 to not overgrow previously generated
            if (this.getDamage() > 0 && ThreadLocalRandom.current().nextInt(100) < 14) { // 14% chance to grow
                this.grow();
            }
            return type;
        }
        return super.onUpdate(type);
    }

    public boolean grow() {
        int age = MathHelper.clamp(this.getDamage(), 0, 25);
        if (age < 25) {
            Block up = this.up();
            if (up instanceof BlockWater && (up.getDamage() == 0 || up.getDamage() == 8)) {
                Block grown = Block.get(BLOCK_KELP, age + 1);
                BlockGrowEvent ev = new BlockGrowEvent(this, grown);
                this.level.getServer().getPluginManager().callEvent(ev);

                if (!ev.isCancelled()) {
                    this.setDamage(25);
                    this.level.setBlock((int) this.x, (int) this.y, (int) this.z, BlockLayer.NORMAL, this, false, false, false); // No need to send this to client

                    this.getLevel().setBlock(up, BlockLayer.WATERLOGGED, Block.get(WATER), true, false);
                    this.getLevel().setBlock(up, BlockLayer.NORMAL, ev.getNewState(), true, true);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        if (level != null && level.getProvider() instanceof Anvil) {
            return super.onBreak(item);
        }


        Block down = this.down();
        if (down.getId() == BLOCK_KELP) {
            this.getLevel().setBlock(down, Block.get(BLOCK_KELP, ThreadLocalRandom.current().nextInt(25)), true, true);
        }
        this.getLevel().setBlock(this, Block.get(AIR), true, true);
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (level != null && level.getProvider() instanceof Anvil) {
            if (item.getId() == ItemID.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
                if (this.grow()) {
                    if (player != null && !player.isCreative()) {
                        item.count--;
                    }
                    level.addParticle(new BoneMealParticle(this));
                }
                return true;
            }
            return super.onActivate(item, player);
        }


        if (item.getId() != Item.DYE || item.getDamage() != ItemDye.BONE_MEAL) {
            return false;
        }
        int x = (int) this.x;
        int z = (int) this.z;

        for (int y = (int) this.y + 1; y < this.getLevel().getMaxBlockY(); y++) {
            int blockIdAbove = this.getLevel().getBlockIdAt(x, y, z);
            if (blockIdAbove == BLOCK_KELP) continue;
            if (!Block.isWater(blockIdAbove)) {
                return false;
            }

            int waterData = this.getLevel().getBlockDataAt(x, y, z);
            if (waterData == 0 || waterData == 8) {
                BlockKelp highestKelp = (BlockKelp) this.getLevel().getBlock(x, y - 1, z);
                if (highestKelp.grow()) {
                    this.level.addParticle(new BoneMealParticle(this));

                    if (player != null && (player.gamemode & 0x01) == 0) {
                        item.count--;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Item toItem() {
        return Item.get(Item.KELP, 0, 1);
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }
}
