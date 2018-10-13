package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.inventory.transaction.*;
import com.nukkitx.server.inventory.transaction.action.*;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.concurrent.Callable;

import static com.nukkitx.server.network.util.VarInts.readUnsignedInt;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public class InventoryTransactionPacket implements BedrockPacket {
    @SuppressWarnings("unchecked")
    private static final Callable<InventoryTransaction>[] TRANSACTIONS = new Callable[]{
            NormalTransaction::new,
            InventoryMismatchTransaction::new,
            ItemUseTransaction::new,
            ItemUseOnEntityTransaction::new,
            ItemReleaseTransaction::new
    };

    private InventoryTransaction transaction;

    @Override
    public void encode(ByteBuf buffer) {
        writeUnsignedInt(buffer, transaction.getType().ordinal());
        writeUnsignedInt(buffer, transaction.getActions().size());
        for (InventoryAction action: transaction.getActions()) {
            writeUnsignedInt(buffer, action.getType().getId());
            action.write(buffer);
        }
        transaction.write(buffer);
    }

    @Override
    public void decode(ByteBuf buffer) {

        try {
            transaction = TRANSACTIONS[readUnsignedInt(buffer)].call();
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize transaction type");
        }

        int count = readUnsignedInt(buffer);
        for(int i = 0; i < count; i++) {
            InventoryAction record = null;
            InventoryAction.Type transactionType = InventoryAction.Type.byId(readUnsignedInt(buffer));

            switch (transactionType) {
                case CONTAINER:
                    record = new ContainerInventoryAction();
                    break;
                case GLOBAL:
                    record = new GlobalInventoryAction();
                    break;
                case WORLD_INTERACTION:
                    record = new WorldInteractionInventoryAction();
                    break;
                case CREATIVE:
                    record = new CreativeInventoryAction();
                    break;
                case CRAFT:
                    record = new CraftInventoryAction();
                    break;
                default:
                    break;
            }
            record.read(buffer);
            transaction.getActions().add(record);
        }
        transaction.read(buffer);
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        handler.handle(this);
    }
}
