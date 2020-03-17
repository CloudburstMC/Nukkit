package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Sheep;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.Location;
import cn.nukkit.player.Player;
import cn.nukkit.utils.DyeColor;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.BlockIds.WOOL;
import static cn.nukkit.item.ItemIds.DYE;
import static cn.nukkit.item.ItemIds.SHEARS;
import static com.nukkitx.protocol.bedrock.data.EntityData.COLOR;
import static com.nukkitx.protocol.bedrock.data.EntityFlag.SHEARED;

/**
 * Author: BeYkeRYkt Nukkit Project
 */
public class EntitySheep extends Animal implements Sheep {

    public static final int NETWORK_ID = 13;

    public EntitySheep(EntityType<Sheep> type, Location location) {
        super(type, location);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.65f;
        }
        return 1.3f;
    }

    @Override
    public String getName() {
        return "Sheep";
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(8);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForByte("Color", this::setColor);
        tag.listenForBoolean("Sheared", this::setSheared);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.byteTag("Color", (byte) this.getColor());
        tag.booleanTag("Sheared", this.isSheared());
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        if (item.getId() == DYE) {
            this.setColor(((ItemDye) item).getDyeColor().getWoolData());
            return true;
        }

        return item.getId() == SHEARS && shear();
    }

    public boolean shear() {
        if (isSheared()) {
            return false;
        }

        this.setSheared(true);
        this.data.setFlag(SHEARED, true);

        this.level.dropItem(this.getPosition(), Item.get(WOOL, getColor(), ThreadLocalRandom.current().nextInt(2) + 1));
        return true;
    }

    @Override
    public Item[] getDrops() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            return new Item[]{Item.get(WOOL, getColor(), 1)};
        }
        return new Item[0];
    }

    public boolean isSheared() {
        return this.data.getFlag(SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.data.setFlag(SHEARED, sheared);
    }

    public int getColor() {
        return this.data.getByte(COLOR);
    }

    public void setColor(int color) {
        this.data.setByte(COLOR, color);
    }

    private int randomColor() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double rand = random.nextDouble(1, 100);

        if (rand <= 0.164) {
            return DyeColor.PINK.getWoolData();
        }

        if (rand <= 15) {
            return random.nextBoolean() ? DyeColor.BLACK.getWoolData() : random.nextBoolean() ? DyeColor.GRAY.getWoolData() : DyeColor.LIGHT_GRAY.getWoolData();
        }

        return DyeColor.WHITE.getWoolData();
    }
}
