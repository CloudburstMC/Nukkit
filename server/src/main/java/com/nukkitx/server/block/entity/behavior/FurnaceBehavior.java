package com.nukkitx.server.block.entity.behavior;

import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.block.entity.NukkitFurnaceBlockEntity;
import com.nukkitx.server.inventory.NukkitFurnaceInventory;
import com.nukkitx.server.network.bedrock.packet.ContainerSetDataPacket;
import lombok.AllArgsConstructor;

import java.util.Optional;

/**
 * @author CreeperFace
 */

@AllArgsConstructor
public class FurnaceBehavior implements BlockEntityBehaivor {

    private final NukkitFurnaceBlockEntity furnace;

    @Override
    public boolean tick() {
        NukkitFurnaceInventory inventory = furnace.getInventory();

        if (furnace.getBurnTime() > 0) {
            furnace.burnTime--;

            Optional<ItemInstance> ingredient = inventory.getIngredient();

            if (ingredient.isPresent()) {
                ItemInstance input = ingredient.get();

                if (!furnace.isFuel(input)) {
                    furnace.setCookTime(0);
                } else {

                    if (furnace.cookTime++ >= 200) {
                        ItemInstance result = furnace.getResult(input);
                        inventory.countDown();
                    }
                }
            } else {

            }
        } else {

        }

        return false;
    }

    private void sendProgress(ContainerSetDataPacket.Property type, int value) {
        //TODO: send to observers
    }
}
