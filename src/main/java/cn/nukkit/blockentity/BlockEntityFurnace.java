package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.inventory.FurnaceBurnEvent;
import cn.nukkit.event.inventory.FurnaceSmeltEvent;
import cn.nukkit.inventory.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.ContainerSetDataPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX
 */
public class BlockEntityFurnace extends BlockEntitySpawnable implements InventoryHolder, BlockEntityContainer, BlockEntityNameable {

    protected FurnaceInventory inventory;

    protected int burnTime;
    protected int burnDuration;
    protected int cookTime;
    protected int maxTime;
    protected int crackledTime;
    protected double experience;

    public static final Map<Integer, Double> FURNACE_XP = new HashMap<>();

    static {
        FURNACE_XP.put(Item.BAKED_POTATO, 0.35d);
        FURNACE_XP.put(Item.DRIED_KELP, 0.1d);
        FURNACE_XP.put(Item.STEAK, 0.35d);
        FURNACE_XP.put(Item.COOKED_PORKCHOP, 0.35d);
        FURNACE_XP.put(Item.COOKED_MUTTON, 0.35d);
        FURNACE_XP.put(Item.COOKED_CHICKEN, 0.35d);
        FURNACE_XP.put(Item.COOKED_RABBIT, 0.35d);
        FURNACE_XP.put(Item.COOKED_FISH, 0.35d);
        FURNACE_XP.put(Item.COOKED_SALMON, 0.35d);

        FURNACE_XP.put(Item.REDSTONE_DUST, 0.3d);
        FURNACE_XP.put(Item.COAL, 0.1d);
        FURNACE_XP.put(Item.EMERALD, 1d);
        FURNACE_XP.put(Item.DYE, 0.2d); // Lapis & Cactus
        FURNACE_XP.put(Item.DIAMOND, 1d);
        FURNACE_XP.put(Item.NETHER_QUARTZ, 0.2d);
        FURNACE_XP.put(Item.IRON_INGOT, 0.7d);
        FURNACE_XP.put(Item.COPPER_INGOT, 0.7d);
        FURNACE_XP.put(Item.GOLD_INGOT, 1d);
        FURNACE_XP.put(Item.NETHERITE_SCRAP, 1d);
        FURNACE_XP.put(Item.IRON_NUGGET, 0.1d);
        FURNACE_XP.put(Item.GOLD_NUGGET, 0.1d);

        FURNACE_XP.put(Item.STONE, 0.1d);
        FURNACE_XP.put(Item.TERRACOTTA, 0.35d);
        FURNACE_XP.put(Item.GLASS, 0.1d);
        FURNACE_XP.put(Item.SPONGE, 0.15d);
        FURNACE_XP.put(Item.POPPED_CHORUS_FRUIT, 0.1d);
        FURNACE_XP.put(Item.BRICK, 0.3d);
        FURNACE_XP.put(Item.NETHER_BRICK, 0.1d);

        FURNACE_XP.put(255 - Item.SMOOTH_BASALT, 0.1d);
    }

    public BlockEntityFurnace(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (this instanceof BlockEntityBlastFurnace) {
            this.inventory = new BlastFurnaceInventory((BlockEntityBlastFurnace) this);
        } else if (this instanceof BlockEntitySmoker) {
            this.inventory = new SmokerInventory((BlockEntitySmoker) this);
        } else {
            this.inventory = new FurnaceInventory(this);
        }

        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        }

        ListTag<CompoundTag> list = (ListTag<CompoundTag>) this.namedTag.getList("Items");
        for (CompoundTag compound : list.getAll()) {
            Item item = NBTIO.getItemHelper(compound);
            if (item.getId() != 0 && item.getCount() > 0) {
                this.inventory.slots.put(compound.getByte("Slot"), item);
            }
        }

        if (!this.namedTag.contains("BurnTime") || this.namedTag.getShort("BurnTime") < 0) {
            burnTime = 0;
        } else {
            burnTime = this.namedTag.getShort("BurnTime");
        }

        if (!this.namedTag.contains("CookTime") || this.namedTag.getShort("CookTime") < 0 || (this.namedTag.getShort("BurnTime") == 0 && this.namedTag.getShort("CookTime") > 0)) {
            cookTime = 0;
        } else {
            cookTime = this.namedTag.getShort("CookTime");
        }

        if (!this.namedTag.contains("BurnDuration") || this.namedTag.getShort("BurnDuration") < 0) {
            burnDuration = 0;
        } else {
            burnDuration = this.namedTag.getShort("BurnDuration");
        }

        if (!this.namedTag.contains("MaxTime")) {
            maxTime = burnTime;
            burnDuration = 0;
        } else {
            maxTime = this.namedTag.getShort("MaxTime");
        }

        if (this.namedTag.contains("BurnTicks")) {
            burnDuration = this.namedTag.getShort("BurnTicks");
            this.namedTag.remove("BurnTicks");
        }

        if (this.namedTag.contains("Experience") && this.namedTag.getDouble("Experience") > 0) {
            this.experience = this.namedTag.getDouble("Experience");
        } else {
            this.experience = 0;
        }

        if (burnTime > 0) {
            this.scheduleUpdate();
        }

        super.initBlockEntity();
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Furnace";
    }

    @Override
    public boolean hasName() {
        return this.namedTag.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag.putString("CustomName", name);
    }

    @Override
    public void close() {
        if (!closed) {
            for (Player player : new ArrayList<>(this.inventory.getViewers())) {
                player.removeWindow(this.inventory);
            }

            super.close();
        }
    }

    @Override
    public void onBreak() {
        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }
        inventory.clearAll();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        for (int index = 0; index < this.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }

        this.namedTag.putShort("CookTime", cookTime);
        this.namedTag.putShort("BurnTime", burnTime);
        this.namedTag.putShort("BurnDuration", burnDuration);
        this.namedTag.putShort("MaxTime", maxTime);
        this.namedTag.putDouble("Experience", experience);
    }

    @Override
    public boolean isBlockEntityValid() {
        int blockID = level.getBlockIdAt(chunk, (int) x, (int) y, (int) z);
        return blockID == Block.FURNACE || blockID == Block.BURNING_FURNACE;
    }

    @Override
    public int getSize() {
        return 3;
    }

    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = this.namedTag.getList("Items", CompoundTag.class);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getByte("Slot") == index) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Item getItem(int index) {
        int i = this.getSlotIndex(index);
        if (i < 0) {
            return new ItemBlock(Block.get(BlockID.AIR), 0, 0);
        } else {
            CompoundTag data = (CompoundTag) this.namedTag.getList("Items").get(i);
            return NBTIO.getItemHelper(data);
        }
    }

    @Override
    public void setItem(int index, Item item) {
        int i = this.getSlotIndex(index);

        CompoundTag d = NBTIO.putItemHelper(item, index);

        if (item.getId() == Item.AIR || item.getCount() <= 0) {
            if (i >= 0) {
                this.namedTag.getList("Items").getAll().remove(i);
            }
        } else if (i < 0) {
            (this.namedTag.getList("Items", CompoundTag.class)).add(d);
        } else {
            (this.namedTag.getList("Items", CompoundTag.class)).add(i, d);
        }
    }

    @Override
    public FurnaceInventory getInventory() {
        return inventory;
    }

    protected void checkFuel(Item fuel) {
        FurnaceBurnEvent ev = new FurnaceBurnEvent(this, fuel, fuel.getFuelTime() == null ? 0 : fuel.getFuelTime());

        this.server.getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return;
        }

        maxTime = ev.getBurnTime();
        burnTime = ev.getBurnTime();
        burnDuration = 0;

        Block block = this.level.getBlock(this.chunk, (int) x, (int) y, (int) z, true);
        if (block.getId() == Item.FURNACE) {
            this.getLevel().setBlock(this, Block.get(BlockID.BURNING_FURNACE, block.getDamage()), true);
        } else if (block.getId() == Item.SMOKER) {
            this.getLevel().setBlock(this, Block.get(BlockID.LIT_SMOKER, block.getDamage()), true);
        } else if (block.getId() == Item.BLAST_FURNACE) {
            this.getLevel().setBlock(this, Block.get(BlockID.LIT_BLAST_FURNACE, block.getDamage()), true);
        }

        if (burnTime > 0 && ev.isBurning()) {
            fuel.setCount(fuel.getCount() - 1);
            if (fuel.getCount() == 0) {
                if (fuel.getId() == Item.BUCKET && fuel.getDamage() == 10) {
                    fuel.setDamage(0);
                    fuel.setCount(1);
                } else {
                    fuel = new ItemBlock(Block.get(BlockID.AIR), 0, 0);
                }
            }
            this.inventory.setFuel(fuel);
        }
    }

    @Override
    public boolean onUpdate() {
        if (this.closed) {
            return false;
        }

        boolean ret = false;
        Item raw = this.inventory.getSmelting();
        Item product = this.inventory.getResult();
        FurnaceRecipe smelt = this.server.getCraftingManager().matchFurnaceRecipe(raw);
        boolean canSmelt = (smelt != null && raw.getCount() > 0 && ((smelt.getResult().equals(product) && product.getCount() < product.getMaxStackSize()) || product.getId() == Item.AIR));

        Item fuel;
        if (burnTime <= 0 && canSmelt && (fuel = this.inventory.getItemFast(1)).getFuelTime() != null && fuel.getCount() > 0) {
            this.checkFuel(fuel.clone());
        }

        if (burnTime > 0) {
            burnTime--;
            burnDuration = (int) Math.ceil((float) burnTime / maxTime * 200);

            if (this.crackledTime-- <= 0) {
                this.crackledTime = ThreadLocalRandom.current().nextInt(30, 110);
                this.getLevel().addLevelSoundEvent(this.add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_BLOCK_FURNACE_LIT);
            }

            if (smelt != null && canSmelt) {
                cookTime++;
                if (cookTime >= 200) {
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

                    cookTime -= 200;
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
            if (block.getId() == Item.BURNING_FURNACE) {
                this.getLevel().setBlock(this, Block.get(BlockID.FURNACE, block.getDamage()), true);
            }
            burnTime = 0;
            cookTime = 0;
            burnDuration = 0;
            crackledTime = 0;
        }

        sendPacket();

        return ret;
    }

    protected void sendPacket() {
        for (Player player : this.inventory.getViewers()) {
            int windowId = player.getWindowId(this.inventory);
            if (windowId > 0) {
                ContainerSetDataPacket pk = new ContainerSetDataPacket();
                pk.windowId = windowId;
                pk.property = ContainerSetDataPacket.PROPERTY_FURNACE_TICK_COUNT;
                pk.value = cookTime;
                player.dataPacket(pk);

                pk = new ContainerSetDataPacket();
                pk.windowId = windowId;
                pk.property = ContainerSetDataPacket.PROPERTY_FURNACE_LIT_TIME;
                pk.value = burnDuration;
                player.dataPacket(pk);
            }
        }
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = new CompoundTag()
                .putString("id", BlockEntity.FURNACE)
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

    public int getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    public int getBurnDuration() {
        return burnDuration;
    }

    public void setBurnDuration(int burnDuration) {
        this.burnDuration = burnDuration;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public double getExperience() {
        return this.experience;
    }

    public void setExperience(double experience) {
        this.experience = experience;
    }

    public void releaseExperience() {
        int experience = NukkitMath.floorDouble(this.experience);
        if (experience >= 1) {
            this.experience = 0;
            this.level.dropExpOrb(this, experience);
        }
    }
}
