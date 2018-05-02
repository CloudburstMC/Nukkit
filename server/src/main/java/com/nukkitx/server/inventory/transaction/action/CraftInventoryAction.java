package com.nukkitx.server.inventory.transaction.action;

import com.nukkitx.server.network.minecraft.session.PlayerSession;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.nukkitx.nbt.util.VarInt.readSignedInt;
import static com.nukkitx.nbt.util.VarInt.writeSignedInt;

@EqualsAndHashCode(callSuper = true)
@Data
public class CraftInventoryAction extends InventoryAction {
    public static final int TYPE_CRAFTING_ADD_INGREDIENT = -2;
    public static final int TYPE_CRAFTING_REMOVE_INGREDIENT = -2;
    public static final int TYPE_CRAFTING_RESULT = -2;
    public static final int TYPE_CRAFTING_USE_INGREDIENT = -2;
    public static final int TYPE_CONTAINER_DROP_CONTENTS = -100;
    private static final Type type = Type.CRAFT;
    private int inventoryId;

    @Override
    public void write(ByteBuf buffer){
        writeSignedInt(buffer, inventoryId);
        super.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer){
        inventoryId = readSignedInt(buffer);
        super.read(buffer);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void execute(PlayerSession session) {

    }
}
