package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.inventory.transaction.*;
import cn.nukkit.server.inventory.transaction.action.*;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.concurrent.Callable;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;

@Data
public class InventoryTransactionPacket implements MinecraftPacket {
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
        writeUnsignedInt(buffer, transaction.getRecords().size());
        for (InventoryAction record : transaction.getRecords()) {
            writeUnsignedInt(buffer, record.getType().getId());
            record.write(buffer);
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
            transaction.getRecords().add(record);
        }
        transaction.read(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
