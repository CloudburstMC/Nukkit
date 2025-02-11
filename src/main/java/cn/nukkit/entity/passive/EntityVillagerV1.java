package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityWitch;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.TradeInventory;
import cn.nukkit.inventory.TradeInventoryRecipe;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.ArrayList;
import java.util.List;

public class EntityVillagerV1 extends EntityWalkingAnimal implements InventoryHolder {

    public static final int PROFESSION_FARMER = 0;
    public static final int PROFESSION_LIBRARIAN = 1;
    public static final int PROFESSION_PRIEST = 2;
    public static final int PROFESSION_BLACKSMITH = 3;
    public static final int PROFESSION_BUTCHER = 4;
    public static final int PROFESSION_GENERIC = 5;

    public static final int NETWORK_ID = 15;

    private TradeInventory inventory;
    private final List<TradeInventoryRecipe> recipes = new ArrayList<>();
    private int tradeTier = 0;
    private boolean willing = true;

    public EntityVillagerV1(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.975f;
        }
        return 1.9f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();

        this.inventory = new TradeInventory(this);

        try {
            CompoundTag offers = this.namedTag.getCompound("Offers");
            if (offers != null) {
                ListTag<CompoundTag> nbtRecipes = offers.getList("Recipes", CompoundTag.class);
                for (CompoundTag nbt : nbtRecipes.getAll()) {
                    recipes.add(TradeInventoryRecipe.toNBT(nbt));
                }
            }
        } catch (Exception ex) {
            server.getLogger().error("Failed to load trade recipes for a villager with entity id " + this.id, ex);
        }

        if (!this.namedTag.contains("Profession")) {
            this.setProfession(PROFESSION_GENERIC);
        }
    }

    public int getProfession() {
        return this.namedTag.getInt("Profession");
    }

    public void setProfession(int profession) {
        this.namedTag.putInt("Profession", profession);
    }

    @Override
    public int getKillExperience() {
        return 0;
    }

    @Override
    public void onStruckByLightning(Entity lightning) {
        Entity ent = Entity.createEntity("Witch", this);
        if (ent != null) {
            CreatureSpawnEvent cse = new CreatureSpawnEvent(EntityWitch.NETWORK_ID, this, ent.namedTag, CreatureSpawnEvent.SpawnReason.LIGHTNING);
            this.getServer().getPluginManager().callEvent(cse);

            if (cse.isCancelled()) {
                ent.close();
                return;
            }

            ent.yaw = this.yaw;
            ent.pitch = this.pitch;
            ent.setImmobile(this.isImmobile());
            if (this.hasCustomName()) {
                ent.setNameTag(this.getNameTag());
                ent.setNameTagVisible(this.isNameTagVisible());
                ent.setNameTagAlwaysVisible(this.isNameTagAlwaysVisible());
            }
            /*if (this.isBaby()) {
                ent.setBaby(); // TODO
            }*/

            this.close();
            ent.spawnToAll();
        } else {
            super.onStruckByLightning(lightning);
        }
    }

    public void setTradeTier(int tier) {
        this.tradeTier = tier;
    }

    public int getTradeTier() {
        return this.tradeTier;
    }

    public void setWilling(boolean value) {
        this.willing = value;
    }

    public boolean isWilling() {
        return this.willing;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (!recipes.isEmpty()) {
            player.addWindow(this.getInventory());
            return true;
        }
        return false;
    }

    public void addTradeRecipe(TradeInventoryRecipe recipe) {
        this.recipes.add(recipe);
    }

    public List<TradeInventoryRecipe> getRecipes() {
        return this.recipes;
    }

    public CompoundTag getOffers() {
        CompoundTag nbt = new CompoundTag();
        nbt.putList(recipesToNbt());
        nbt.putList(getDefaultTierExpRequirements());
        return nbt;
    }

    private ListTag<CompoundTag> recipesToNbt() {
        ListTag<CompoundTag> tag = new ListTag<>("Recipes");
        for (TradeInventoryRecipe recipe : this.recipes) {
            tag.add(recipe.toNBT());
        }
        return tag;
    }

    private ListTag<CompoundTag> getDefaultTierExpRequirements() {
        ListTag<CompoundTag> tag = new ListTag<>("TierExpRequirements");
        tag.add(new CompoundTag().putInt("0", 0));
        tag.add(new CompoundTag().putInt("1", 10));
        tag.add(new CompoundTag().putInt("2", 70));
        tag.add(new CompoundTag().putInt("3", 150));
        tag.add(new CompoundTag().putInt("4", 250));
        return tag;
    }

    @Override
    public TradeInventory getInventory() {
        return this.inventory;
    }
}
