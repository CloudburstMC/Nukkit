package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.inventory.transaction.*;
import cn.nukkit.server.inventory.transaction.record.*;
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
        for (TransactionRecord record: transaction.getRecords()) {
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
            TransactionRecord record = null;
            TransactionRecord.Type transactionType = TransactionRecord.Type.byId(readUnsignedInt(buffer));

            switch (transactionType) {
                case CONTAINER:
                    record = new ContainerTransactionRecord();
                    break;
                case GLOBAL:
                    record = new GlobalTransactionRecord();
                    break;
                case WORLD_INTERACTION:
                    record = new WorldInteractionTransactionRecord();
                    break;
                case CREATIVE:
                    record = new CreativeTransactionRecord();
                    break;
                case CRAFT:
                    record = new CraftTransactionRecord();
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
