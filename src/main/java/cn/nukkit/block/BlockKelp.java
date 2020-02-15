package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.math.MathHelper.clamp;

public class BlockKelp extends FloodableBlock {

    public BlockKelp(Identifier id) {
        super(id);
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }

    @Override
    public boolean canWaterlogFlowing() {
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.KELP);
    }

    @Override
    public boolean canBeActivated()
    {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player)
    {
        if(item.getId() == ItemIds.DYE && item.getDamage() == 0x0f) { //Bone Meal
            int x = getX();
            int z = getZ();
            for(int y = getY() + 1; y < 255; y++)
            {
                Identifier blockAbove = getLevel().getBlock(x,y,z).id;
                if(blockAbove != BlockIds.KELP && (blockAbove == BlockIds.WATER || blockAbove == BlockIds.FLOWING_WATER))
                {
                    int waterData = getLevel().getBlock(x,y,z).meta;
                    if(waterData == 0 || waterData == 8)
                    {
                        BlockKelp highestKelp = (BlockKelp) getLevel().getBlock(x, y-1 , z);
                        if(highestKelp.grow())
                        {
                            this.level.addParticle(new BoneMealParticle(this));

                            if(player != null && !player.isCreative())
                            {
                                item.decrementCount(1);
                            }
                            return false;
                        }
                    }
                }
                else if (blockAbove == BlockIds.KELP) continue;
                return false;

            }

        }
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        Block down = down();
        Block layerOneBlock = getBlockAtLayer(1);
        int waterDamage;
        if ((down.getId() != BlockIds.KELP && !down.isSolid())
                || down.getId() == BlockIds.MAGMA
                || down.getId() == BlockIds.ICE
                || down.getId() == BlockIds.SOUL_SAND
                || (!(layerOneBlock instanceof BlockWater) || ((waterDamage = (block.getDamage())) != 0 && waterDamage != 8))
        ) {
            return false;
        }

        if (waterDamage == 8) {
            this.level.setBlock(this, new BlockWater(BlockIds.FLOWING_WATER), true, false);
        }
        if (down.getId() == BlockIds.KELP && down.getDamage() != 24) {
            down.setDamage(24);
            this.level.setBlock(this, this, true, true);
        }
        setDamage(ThreadLocalRandom.current().nextInt(25));
        this.level.setBlock(this, this, true, true);
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        Block down = down();
        if (down.getId() == BlockIds.KELP) {
            this.getLevel().setBlock(down, Block.get(BlockIds.KELP, ThreadLocalRandom.current().nextInt(25)), true, true);
        }
        super.onBreak(item);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block layerOneBlock = getBlockAtLayer(1);
            int waterDamage;
            if ((!(layerOneBlock instanceof BlockWater) || ((waterDamage = layerOneBlock.getDamage()) != 0 && waterDamage != 8))
            ) {
                this.getLevel().useBreakOn(this);
                return type;
            }


            Block down = down();
            if ((!down.isSolid() && down.getId() != BlockIds.KELP)
                    || down.getId() == BlockIds.MAGMA
                    || down.getId() == BlockIds.ICE
                    || down.getId() == BlockIds.SOUL_SAND) {
                this.getLevel().useBreakOn(this);
                return type;
            }

            if (waterDamage == 8) {
                this.getLevel().setBlock(layer(1), Block.get(BlockIds.FLOWING_WATER), true, false);
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(100) <= 14) {
                grow();
            }
            return type;
        }
        return super.onUpdate(type);
    }

    private boolean grow() {
        int age = clamp(getDamage(), 0, 25);
        if (age < 25) {
            Block up = up();
            if (up instanceof BlockWater && up.getDamage() == 0 || up.getDamage() == 8) {
                Block grown = Block.get(id, age + 1);
                BlockGrowEvent ev = new BlockGrowEvent(this, grown);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    this.setDamage(25);
                    this.getLevel().setBlock(this, this, true, true);
                    this.getLevel().setBlock(up.layer(1), Block.get(BlockIds.FLOWING_WATER), true, false);
                    this.getLevel().setBlock(up, ev.getNewState(), true, true);
                    return true;
                }
            }
        }
        return false;
    }
}
