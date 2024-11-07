package cn.nukkit.inventory;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;

public class TradeInventoryRecipe {

    public static final int A_ITEM = 0;
    public static final int B_ITEM = 1;
    @Getter
    private final Item sellItem;
    @Getter
    private final Item buyItem;
    @Getter
    private final Item secondBuyItem;

    private int tier = -1;
    private int maxUses = 999;
    private int buyCountA = 0;
    private int buyCountB = 0;
    private final int uses = 0;
    private int demand = 0;
    private int rewardsExp = 0;
    private final int traderExp = 0;
    private float priceMultiplierA = 0F;
    private float priceMultiplierB = 0F;

    public TradeInventoryRecipe(Item sellItem, Item buyItem) {
        this(sellItem, buyItem, Item.get(0));
    }

    public TradeInventoryRecipe(Item sellItem, Item buyItem, Item secondBuyItem) {
        this.sellItem = sellItem;
        this.buyItem = buyItem;
        this.secondBuyItem = secondBuyItem;
    }

    public TradeInventoryRecipe setTier(int tier) {
        this.tier = tier;
        return this;
    }

    public TradeInventoryRecipe setMaxUses(int maxUses) {
        this.maxUses = maxUses;
        return this;
    }

    public TradeInventoryRecipe setBuyCount(int count, int type) {
        switch (type) {
            case 0:
                this.buyCountA = count;
                break;
            case 1:
                this.buyCountB = count;
                break;
        }
        this.buyCountA = count;
        return this;
    }

    public TradeInventoryRecipe setDemand(int demand) {
        this.demand = demand;
        return this;
    }

    public TradeInventoryRecipe setMultiplier(float multiplier, int type) {
        switch (type) {
            case 0:
                this.priceMultiplierA = multiplier;
                break;
            case 1:
                this.priceMultiplierB = multiplier;
                break;
        }
        return this;
    }

    public TradeInventoryRecipe setRewardExp(int reward) {
        this.rewardsExp = reward;
        return this;
    }

    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putCompound("buyA", NBTIO.putItemHelper(buyItem, -1));
        nbt.putCompound("buyB", NBTIO.putItemHelper(secondBuyItem,-1));
        nbt.putCompound("sell", NBTIO.putItemHelper(sellItem, -1));
        nbt.putInt("tier", tier);
        nbt.putInt("buyCountA", buyCountA);
        nbt.putInt("buyCountB", buyCountB);
        nbt.putInt("uses", uses);
        nbt.putInt("maxUses", maxUses);
        nbt.putInt("rewardExp", rewardsExp);
        nbt.putInt("demand", demand);
        nbt.putInt("traderExp", traderExp);
        nbt.putFloat("priceMultiplierA", priceMultiplierA);
        nbt.putFloat("priceMultiplierB", priceMultiplierB);
        return nbt;
    }

    public static TradeInventoryRecipe toNBT(CompoundTag nbt) {
        return new TradeInventoryRecipe(NBTIO.getItemHelper(nbt.getCompound("sell")), NBTIO.getItemHelper(nbt.getCompound("buyA")), NBTIO.getItemHelper(nbt.getCompound("buyB")))
                .setTier(nbt.getInt("tier"))
                .setBuyCount(nbt.getInt("buyCountA"), A_ITEM)
                .setBuyCount(nbt.getInt("buyCountB"), B_ITEM)
                .setMaxUses(nbt.getInt("maxUses"))
                .setMultiplier(nbt.getInt("priceMultiplierA"), A_ITEM)
                .setMultiplier(nbt.getInt("priceMultiplierB"), B_ITEM)
                .setDemand(nbt.getInt("demand"))
                .setRewardExp(nbt.getInt("rewardExp"));
    }
}
