package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.inventory.FurnaceSmeltEvent;
import cn.nukkit.inventory.FurnaceRecipe;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.concurrent.ThreadLocalRandom;

public class BlockEntitySmoker extends BlockEntityFurnace {

    public BlockEntitySmoker(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Smoker";
    }

    @Override
    public boolean isBlockEntityValid() {
        int blockID = level.getBlockIdAt(chunk, (int) x, (int) y, (int) z);
        return blockID == Block.SMOKER || blockID == Block.LIT_SMOKER;
    }

    private static final IntSet CAN_SMELT = new IntOpenHashSet(new int[]{
            Item.RAW_PORKCHOP, Item.RAW_BEEF, Item.RAW_RABBIT, Item.RAW_FISH, Item.RAW_CHICKEN, Item.RAW_MUTTON, Item.RAW_SALMON, Item.POTATO
    });

    @Override
    public boolean onUpdate() {
        if (this.closed) {
            return false;
        }

        Item raw = this.inventory.getSmelting();
        // TODO: smoker recipes
        if (!CAN_SMELT.contains(raw.getId())) {
            if (burnTime > 0) {
                burnTime--;
                burnDuration = (int) Math.ceil((float) burnTime / maxTime * 100);

                if (burnTime == 0) {
                    Block block = this.level.getBlock(this.chunk, (int) x, (int) y, (int) z, true);
                    if (block.getId() == BlockID.LIT_SMOKER) {
                        this.level.setBlock(this, Block.get(BlockID.SMOKER, block.getDamage()), true);
                    }
                    return false;
                }
            }

            cookTime = 0;
            sendPacket();
            return true;
        }

        boolean ret = false;
        Item product = this.inventory.getResult();
        FurnaceRecipe smelt = this.server.getCraftingManager().matchFurnaceRecipe(raw);
        boolean canSmelt = (smelt != null && raw.getCount() > 0 && ((smelt.getResult().equals(product) && product.getCount() < product.getMaxStackSize()) || product.getId() == Item.AIR));

        Item fuel;
        if (burnTime <= 0 && canSmelt && (fuel = this.inventory.getItemFast(1)).getFuelTime() != null && fuel.getCount() > 0) {
            this.checkFuel(fuel.clone());
        }

        if (burnTime > 0) {
            burnTime--;
            burnDuration = (int) Math.ceil((float) burnTime / maxTime * 100);

            if (this.crackledTime-- <= 0) {
                this.crackledTime = ThreadLocalRandom.current().nextInt(30, 110);
                this.getLevel().addLevelSoundEvent(this.add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_BLOCK_FURNACE_LIT);
            }

            if (smelt != null && canSmelt) {
                cookTime++;
                if (cookTime >= 100) {
                    product = Item.get(smelt.getResult().getId(), smelt.getResult().getDamage(), product.isNull() ? 1 : product.getCount() + 1);

                    FurnaceSmeltEvent ev = new FurnaceSmeltEvent(this, raw, product);
                    this.server.getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.inventory.setResult(ev.getResult());
                        this.experience += FURNACE_XP.getOrDefault(ev.getResult().getId(), 0d);
                        raw.setCount(raw.getCount() - 1);
                        if (raw.getCount() == 0) {
                            raw = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
                        }
                        this.inventory.setSmelting(raw);
                    }

                    cookTime -= 100;
                }
            } else if (burnTime <= 0) {
                burnTime = 0;
                cookTime = 0;
                burnDuration = 0;
            } else {
                cookTime = 0;
            }
            ret = true;
        } else {
            Block block = this.level.getBlock(this.chunk, (int) x, (int) y, (int) z, true);
            if (block.getId() == BlockID.LIT_SMOKER) {
                this.level.setBlock(this, Block.get(BlockID.SMOKER, block.getDamage()), true);
            }
            burnTime = 0;
            cookTime = 0;
            burnDuration = 0;
            crackledTime = 0;
        }

        sendPacket();

        return ret;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = new CompoundTag()
                .putString("id", BlockEntity.SMOKER)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putShort("BurnDuration", burnDuration)
                .putShort("BurnTime", burnTime)
                .putShort("CookTime", cookTime);

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c;
    }
}
