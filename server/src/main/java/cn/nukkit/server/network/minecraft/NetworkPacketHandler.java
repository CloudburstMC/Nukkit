/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.network.minecraft;

import cn.nukkit.server.network.minecraft.packet.*;

public interface NetworkPacketHandler {

    default void handle(AdventureSettingsPacket packet) {
    }

    default void handle(AnimatePacket packet) {
    }

    default void handle(BlockEntityDataPacket packet) {
    }

    default void handle(BlockPickRequestPacket packet) {
    }

    default void handle(BookEditPacket packet) {
    }

    default void handle(ClientToServerHandshakePacket packet) {
    }

    default void handle(CommandBlockUpdatePacket packet) {
    }

    default void handle(CommandRequestPacket packet) {
    }

    default void handle(ContainerClosePacket packet) {
    }

    default void handle(CraftingEventPacket packet) {
    }

    default void handle(EntityEventPacket packet) {
    }

    default void handle(EntityFallPacket packet) {
    }

    default void handle(EntityPickRequestPacket packet) {
    }

    default void handle(EventPacket packet) {
    }

    default void handle(InteractPacket packet) {
    }

    default void handle(InventoryContentPacket packet) {
    }

    default void handle(InventorySlotPacket packet) {
    }

    default void handle(InventoryTransactionPacket packet) {
    }

    default void handle(ItemFrameDropItemPacket packet) {
    }

    default void handle(LabTablePacket packet) {
    }

    default void handle(LevelSoundEventPacket packet) {
    }

    default void handle(LoginPacket packet) {
    }

    default void handle(MapInfoRequestPacket packet) {
    }

    default void handle(MobArmorEquipmentPacket packet) {
    }

    default void handle(MobEquipmentPacket packet) {
    }

    default void handle(ModalFormResponsePacket packet) {
    }

    default void handle(MoveEntityPacket packet) {
    }

    default void handle(MovePlayerPacket packet) {
    }

    default void handle(PhotoTransferPacket packet) {
    }

    default void handle(PlayerActionPacket packet) {
    }

    default void handle(PlayerHotbarPacket packet) {
    }

    default void handle(PlayerInputPacket packet) {
    }

    default void handle(PlayerSkinPacket packet) {
    }

    default void handle(PurchaseReceiptPacket packet) {
    }

    default void handle(RequestChunkRadiusPacket packet) {
    }

    default void handle(ResourcePackChunkRequestPacket packet) {
    }

    default void handle(ResourcePackClientResponsePacket packet) {
    }

    default void handle(RiderJumpPacket packet) {
    }

    default void handle(ServerSettingsRequestPacket packet) {
    }

    default void handle(SetDefaultGameTypePacket packet) {
    }

    default void handle(SetPlayerGameTypePacket packet) {
    }

    default void handle(SubClientLoginPacket packet) {
    }

    default void handle(TextPacket packet) {
    }
}
