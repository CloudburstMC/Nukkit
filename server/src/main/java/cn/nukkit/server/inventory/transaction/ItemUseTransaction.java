package cn.nukkit.server.inventory.transaction;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.entity.component.PlayerData;
import cn.nukkit.api.event.block.BlockBreakEvent;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.api.metadata.item.GenericDamageValue;
import cn.nukkit.api.util.GameMode;
import cn.nukkit.server.block.BlockUtil;
import cn.nukkit.server.block.NukkitBlockState;
import cn.nukkit.server.block.behavior.BlockBehavior;
import cn.nukkit.server.block.behavior.BlockBehaviors;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.network.minecraft.packet.LevelEventPacket;
import cn.nukkit.server.network.minecraft.packet.UpdateBlockPacket;
import cn.nukkit.server.network.minecraft.session.PlayerSession;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

import static cn.nukkit.server.nbt.util.VarInt.*;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
@Log4j2
@EqualsAndHashCode(callSuper = true)
public class ItemUseTransaction extends ComplexTransaction {
    private static final Type type = Type.ITEM_USE;
    private Action action;
    private Vector3i position;
    private int face;
    private Vector3f clickPosition;

    @Override
    public void execute(PlayerSession session) {
        ItemInstance serverItem = session.getInventory().getItemInHand().orElse(BlockUtil.AIR);
        if (!serverItem.equals(getItem())) {
            log.debug("{} interacted with block using {} but has {} in hand {}", session.getName(), getItem(), serverItem, getSlot());
            session.sendPlayerInventory();
            return;
        }
        switch (action) {
            case BREAK:
                breakBlock(session, serverItem);
                break;
        }
    }

    private void breakBlock(PlayerSession session, ItemInstance withItem) {
        PlayerData data = session.ensureAndGet(PlayerData.class);

        int chunkX = position.getX() >> 4;
        int chunkZ = position.getZ() >> 4;

        Optional<Chunk> chunk = session.getLevel().getChunkIfLoaded(chunkX, chunkZ);
        if (!chunk.isPresent()) {
            log.debug("{} tried to break block at unloaded chunk ({}, {})", session.getName(), chunkX, chunkZ);
            return;
        }

        int inChunkX = position.getX() & 0x0f;
        int inChunkZ = position.getZ() & 0x0f;

        Block block = chunk.get().getBlock(inChunkX, position.getY(), inChunkZ);
        BlockBehavior blockBehavior = BlockBehaviors.getBlockBehavior(block.getBlockState().getBlockType());

        BlockBreakEvent event = new BlockBreakEvent(session, block, NukkitBlockState.AIR, withItem,
                blockBehavior.getDrops(session, block, withItem), data.getGameMode() == GameMode.CREATIVE);
        session.getServer().getEventManager().fire(event);

        if (!event.isCancelled()) {
            if (data.getGameMode() == GameMode.SURVIVAL) {
                switch (blockBehavior.onBreak(session, block, withItem)) {
                    case REDUCE_DURABILITY:
                        withItem.getItemData().ifPresent(metadata -> {
                            if (metadata instanceof GenericDamageValue) {
                                int damage = ((GenericDamageValue) metadata).getDamage() + 1;
                                session.getInventory().setItem(session.getInventory().getHeldHotbarSlot(),
                                        withItem.toBuilder().itemData(new GenericDamageValue((short) damage)).build());
                            }
                        });
                    case BREAK_BLOCK:
                        event.getDrops().forEach(item -> session.getLevel().dropItem(item, block.getBlockPosition().toFloat().add(0.5f, 0.5f, 0.5f)));

                        chunk.get().setBlock(inChunkX, position.getY(), inChunkZ, event.getNewState());
                }
            } else if (data.getGameMode() == GameMode.CREATIVE) {
                chunk.get().setBlock(inChunkX, position.getY(), inChunkZ, event.getNewState());
            }

            int runtimeId = NukkitLevel.getPaletteManager().getOrCreateRuntimeId(block.getBlockState());
            session.getLevel().getPacketManager().queueEventForViewers(position.toFloat(), LevelEventPacket.Event.PARTICLE_DESTROY, runtimeId);
            session.getLevel().broadcastBlockUpdate(session, position);
        } else {
            // Need to send block update to player only.
            UpdateBlockPacket packet = new UpdateBlockPacket();
            packet.setBlockPosition(position);
            packet.setRuntimeId(NukkitLevel.getPaletteManager().getOrCreateRuntimeId(block.getBlockState()));
            packet.setDataLayer(UpdateBlockPacket.DataLayer.NORMAL);
            session.getMinecraftSession().addToSendQueue(packet);
        }
    }

    public void read(ByteBuf buffer){
        action = Action.values()[readUnsignedInt(buffer)];
        position = readVector3i(buffer);
        face = readSignedInt(buffer);
        super.read(buffer);
        clickPosition = readVector3f(buffer);
    }

    public void write(ByteBuf buffer){
        writeUnsignedInt(buffer, action.ordinal());
        writeVector3i(buffer, position);
        writeSignedInt(buffer, face);
        super.write(buffer);
        writeVector3f(buffer, clickPosition);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void handle(PlayerSession.PlayerNetworkPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "ItemUseTransaction" + super.toString() +
                ", action=" + action +
                ", position=" + position +
                ", face=" + face +
                ", clickPosition=" + clickPosition +
                ')';
    }

    public enum Action {
        PLACE,
        USE,
        BREAK
    }
}
