package cn.nukkit.blockentity.impl;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBrewingStand;
import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.BrewingStand;
import cn.nukkit.event.inventory.BrewEvent;
import cn.nukkit.event.inventory.StartBrewEvent;
import cn.nukkit.inventory.BrewingInventory;
import cn.nukkit.inventory.BrewingRecipe;
import cn.nukkit.inventory.ContainerRecipe;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemUtils;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.data.SoundEvent;
import com.nukkitx.protocol.bedrock.packet.ContainerSetDataPacket;

import java.util.*;

import static cn.nukkit.block.BlockIds.REDSTONE_WIRE;
import static cn.nukkit.item.ItemIds.*;

public class BrewingStandBlockEntity extends BaseBlockEntity implements BrewingStand {

    public static final short MAX_COOK_TIME = 400;
    public static final List<Identifier> ingredients = new ArrayList<>(Arrays.asList(
            NETHER_WART, GOLD_NUGGET, GHAST_TEAR, GLOWSTONE_DUST, REDSTONE_WIRE, GUNPOWDER, MAGMA_CREAM, BLAZE_POWDER,
            GOLDEN_CARROT, SPIDER_EYE, FERMENTED_SPIDER_EYE, SPECKLED_MELON, SUGAR, FISH, RABBIT_FOOT, PUFFERFISH,
            TURTLE_SHELL_PIECE, PHANTOM_MEMBRANE, DRAGON_BREATH
    ));
    protected final BrewingInventory inventory = new BrewingInventory(this);
    public short cookTime = MAX_COOK_TIME;
    public short fuelTotal;
    public short fuelAmount;

    public BrewingStandBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForList("Items", CompoundTag.class, tags -> {
            for (CompoundTag itemTag : tags) {
                Item item = ItemUtils.deserializeItem(itemTag);
                this.inventory.setItem(itemTag.getByte("Slot"), item);
            }
        });

        tag.listenForShort("CookTime", this::setCookTime);
        tag.listenForShort("FuelAmount", this::setFuelAmount);
        tag.listenForShort("FuelTotal", this::setFuelTotal);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        List<CompoundTag> items = new ArrayList<>();
        for (Map.Entry<Integer, Item> entry : this.inventory.getContents().entrySet()) {
            items.add(ItemUtils.serializeItem(entry.getValue(), entry.getKey()));
        }
        tag.listTag("Items", CompoundTag.class, items);
        tag.shortTag("CookTime", this.cookTime);
    }

    @Override
    protected void saveClientData(CompoundTagBuilder tag) {
        super.saveClientData(tag);

        tag.shortTag("FuelAmount", this.fuelAmount);
        tag.shortTag("FuelTotal", this.fuelTotal);
    }

    @Override
    public void close() {
        if (!closed) {
            for (Player player : new HashSet<>(getInventory().getViewers())) {
                player.removeWindow(getInventory());
            }
            super.close();
        }
    }

    @Override
    public void onBreak() {
        for (Item content : inventory.getContents().values()) {
            this.getLevel().dropItem(this.getPosition(), content);
        }
    }

    @Override
    public boolean isValid() {
        return getBlock().getId() == BlockIds.BREWING_STAND;
    }

    @Override
    public BrewingInventory getInventory() {
        return inventory;
    }

    protected boolean checkIngredient(Item ingredient) {
        return ingredients.contains(ingredient.getId());
    }

    public short getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        if (cookTime < MAX_COOK_TIME) {
            this.scheduleUpdate();
        }
        this.cookTime = (short) cookTime;
    }

    public short getFuelTotal() {
        return fuelTotal;
    }

    public void setFuelTotal(short fuelTotal) {
        this.fuelTotal = fuelTotal;
    }

    @Override
    public boolean onUpdate() {
        if (closed) {
            return false;
        }

        boolean ret = false;

        Item ingredient = this.inventory.getIngredient();
        boolean canBrew = false;

        Item fuel = this.getInventory().getFuel();
        if (this.fuelAmount <= 0 && fuel.getId() == BLAZE_POWDER && fuel.getCount() > 0) {
            fuel.decrementCount();
            this.fuelAmount = 20;
            this.fuelTotal = 20;

            this.inventory.setFuel(fuel);
            this.sendFuel();
        }

        if (this.fuelAmount > 0) {
            for (int i = 1; i <= 3; i++) {
                if (this.inventory.getItem(i).getId() == POTION) {
                    canBrew = true;
                }
            }

            if (this.cookTime <= MAX_COOK_TIME && canBrew && ingredient.getCount() > 0) {
                if (!this.checkIngredient(ingredient)) {
                    canBrew = false;
                }
            } else {
                canBrew = false;
            }
        }

        if (canBrew) {
            if (this.cookTime == MAX_COOK_TIME) {
                this.sendBrewTime();
                StartBrewEvent e = new StartBrewEvent(this);
                this.server.getPluginManager().callEvent(e);

                if (e.isCancelled()) {
                    return false;
                }
            }

            this.cookTime--;

            if (this.cookTime <= 0) { //20 seconds
                BrewEvent e = new BrewEvent(this);
                this.server.getPluginManager().callEvent(e);

                if (!e.isCancelled()) {
                    for (int i = 1; i <= 3; i++) {
                        Item potion = this.inventory.getItem(i);

                        ContainerRecipe containerRecipe = Server.getInstance().getCraftingManager().matchContainerRecipe(ingredient, potion);
                        if (containerRecipe != null) {
                            Item result = containerRecipe.getResult();
                            result.setMeta(potion.getMeta());
                            this.inventory.setItem(i, result);
                        } else {
                            BrewingRecipe recipe = Server.getInstance().getCraftingManager().matchBrewingRecipe(ingredient, potion);
                            if (recipe != null) {
                                this.inventory.setItem(i, recipe.getResult());
                            }
                        }
                    }
                    this.getLevel().addLevelSoundEvent(this.getPosition(), SoundEvent.POTION_BREWED);

                    ingredient.decrementCount();
                    this.inventory.setIngredient(ingredient);

                    this.fuelAmount--;
                    this.sendFuel();
                }

                this.cookTime = MAX_COOK_TIME;
            }

            ret = true;
        } else {
            this.cookTime = MAX_COOK_TIME;
        }

        //this.sendBrewTime();
        lastUpdate = System.currentTimeMillis();

        this.updateBlock();

        return ret;
    }

    public void sendFuel() {
        for (Player p : this.inventory.getViewers()) {
            int windowId = p.getWindowId(this.inventory);
            if (windowId > 0) {
                ContainerSetDataPacket fuelAmountPacket = new ContainerSetDataPacket();
                fuelAmountPacket.setWindowId((byte) windowId);

                fuelAmountPacket.setProperty(ContainerSetDataPacket.BREWING_STAND_FUEL_AMOUNT);
                fuelAmountPacket.setValue(this.fuelAmount);
                p.sendPacket(fuelAmountPacket);

                ContainerSetDataPacket totalFuelPacket = new ContainerSetDataPacket();

                totalFuelPacket.setProperty(ContainerSetDataPacket.BREWING_STAND_FUEL_TOTAL);
                totalFuelPacket.setValue(this.fuelTotal);
                p.sendPacket(totalFuelPacket);
            }
        }
    }

    protected void sendBrewTime() {
        for (Player p : this.inventory.getViewers()) {
            int windowId = p.getWindowId(this.inventory);
            if (windowId > 0) {
                ContainerSetDataPacket packet = new ContainerSetDataPacket();
                packet.setProperty(ContainerSetDataPacket.BREWING_STAND_BREW_TIME);
                packet.setWindowId((byte) windowId);
                packet.setValue(this.cookTime);

                p.sendPacket(packet);
            }
        }
    }

    public void updateBlock() {
        Block block = this.getBlock();

        if (!(block instanceof BlockBrewingStand)) {
            return;
        }

        int meta = 0;

        for (int i = 1; i <= 3; ++i) {
            Item potion = this.inventory.getItem(i);

            Identifier id = potion.getId();
            if ((id == POTION || id == SPLASH_POTION || id == LINGERING_POTION) && potion.getCount() > 0) {
                meta |= 1 << (i - 1);
            }
        }

        if (block.getMeta() != meta) {
            block.setMeta(meta);
            this.getLevel().setBlock(block.getPosition(), block, false, false);
        }
    }

    public short getFuelAmount() {
        return fuelAmount;
    }

    public void setFuelAmount(int fuel) {
        this.fuelAmount = (short) fuel;
    }

    @Override
    public int[] getHopperPushSlots(BlockFace direction, Item item) {
        Identifier id = item.getId();

        if (direction.getAxis().isHorizontal()) {
            if (id == BLAZE_POWDER) {
                return new int[]{BrewingInventory.SLOT_FUEL};
            }
        } else {
            if (id == NETHER_WART || id == REDSTONE || id == GLOWSTONE_DUST || id == FERMENTED_SPIDER_EYE || id == GUNPOWDER || id == DRAGON_BREATH) {
                return new int[]{BrewingInventory.SLOT_INGREDIENT};
            }
        }

        return null;
    }

    @Override
    public int[] getHopperPullSlots() {
        return new int[]{1, 2, 3};
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}
