package com.nukkitx.server.inventory.transaction.action.function;


import com.nukkitx.protocol.bedrock.data.InventoryAction;
import com.nukkitx.server.inventory.transaction.InventoryTransactionResult;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NormalInventoryActionFunction implements InventoryActionFunction {
    public static final NormalInventoryActionFunction INSTANCE = new NormalInventoryActionFunction();

    @Override
    public InventoryTransactionResult verify(InventoryAction action, NukkitPlayerSession player, boolean ignoreChecks) {
        return null;
    }

    @Override
    public InventoryTransactionResult execute(InventoryAction action, NukkitPlayerSession player) {
        return null;
    }
}
