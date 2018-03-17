package cn.nukkit.network;

import cn.nukkit.network.protocol.AddBehaviorTreePacket;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.AddItemEntityPacket;
import cn.nukkit.network.protocol.AddPaintingPacket;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.AdventureSettingsPacket;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.AvailableCommandsPacket;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.network.protocol.BlockPickRequestPacket;
import cn.nukkit.network.protocol.BookEditPacket;
import cn.nukkit.network.protocol.BossEventPacket;
import cn.nukkit.network.protocol.CameraPacket;
import cn.nukkit.network.protocol.ChangeDimensionPacket;
import cn.nukkit.network.protocol.ChunkRadiusUpdatedPacket;
import cn.nukkit.network.protocol.ClientToServerHandshakePacket;
import cn.nukkit.network.protocol.ClientboundMapItemDataPacket;
import cn.nukkit.network.protocol.CommandBlockUpdatePacket;
import cn.nukkit.network.protocol.CommandRequestPacket;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.ContainerSetDataPacket;
import cn.nukkit.network.protocol.CraftingDataPacket;
import cn.nukkit.network.protocol.CraftingEventPacket;
import cn.nukkit.network.protocol.DisconnectPacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.EntityFallPacket;
import cn.nukkit.network.protocol.EntityPickRequestPacket;
import cn.nukkit.network.protocol.ExplodePacket;
import cn.nukkit.network.protocol.FullChunkDataPacket;
import cn.nukkit.network.protocol.GUIDataPickItemPacket;
import cn.nukkit.network.protocol.GameRulesChangedPacket;
import cn.nukkit.network.protocol.HurtArmorPacket;
import cn.nukkit.network.protocol.InitiateWebSocketConnectionPacket;
import cn.nukkit.network.protocol.InteractPacket;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.network.protocol.ItemFrameDropItemPacket;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.network.protocol.MapInfoRequestPacket;
import cn.nukkit.network.protocol.MobArmorEquipmentPacket;
import cn.nukkit.network.protocol.MobEffectPacket;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.network.protocol.ModalFormRequestPacket;
import cn.nukkit.network.protocol.ModalFormResponsePacket;
import cn.nukkit.network.protocol.MoveEntityPacket;
import cn.nukkit.network.protocol.MovePlayerPacket;
import cn.nukkit.network.protocol.NPCRequestPacket;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.network.protocol.PlayStatusPacket;
import cn.nukkit.network.protocol.PlayerActionPacket;
import cn.nukkit.network.protocol.PlayerHotbarPacket;
import cn.nukkit.network.protocol.PlayerInputPacket;
import cn.nukkit.network.protocol.PlayerListPacket;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.RequestChunkRadiusPacket;
import cn.nukkit.network.protocol.ResourcePackChunkDataPacket;
import cn.nukkit.network.protocol.ResourcePackChunkRequestPacket;
import cn.nukkit.network.protocol.ResourcePackClientResponsePacket;
import cn.nukkit.network.protocol.ResourcePackDataInfoPacket;
import cn.nukkit.network.protocol.ResourcePackStackPacket;
import cn.nukkit.network.protocol.ResourcePacksInfoPacket;
import cn.nukkit.network.protocol.RespawnPacket;
import cn.nukkit.network.protocol.RiderJumpPacket;
import cn.nukkit.network.protocol.ServerSettingsRequestPacket;
import cn.nukkit.network.protocol.ServerSettingsResponsePacket;
import cn.nukkit.network.protocol.ServerToClientHandshakePacket;
import cn.nukkit.network.protocol.SetCommandsEnabledPacket;
import cn.nukkit.network.protocol.SetDifficultyPacket;
import cn.nukkit.network.protocol.SetEntityDataPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import cn.nukkit.network.protocol.SetEntityMotionPacket;
import cn.nukkit.network.protocol.SetHealthPacket;
import cn.nukkit.network.protocol.SetLastHurtByPacket;
import cn.nukkit.network.protocol.SetPlayerGameTypePacket;
import cn.nukkit.network.protocol.SetSpawnPositionPacket;
import cn.nukkit.network.protocol.SetTimePacket;
import cn.nukkit.network.protocol.SetTitlePacket;
import cn.nukkit.network.protocol.ShowCreditsPacket;
import cn.nukkit.network.protocol.ShowProfilePacket;
import cn.nukkit.network.protocol.SimpleEventPacket;
import cn.nukkit.network.protocol.SpawnExperienceOrbPacket;
import cn.nukkit.network.protocol.StartGamePacket;
import cn.nukkit.network.protocol.StopSoundPacket;
import cn.nukkit.network.protocol.StructureBlockUpdatePacket;
import cn.nukkit.network.protocol.SubClientLoginPacket;
import cn.nukkit.network.protocol.TakeItemEntityPacket;
import cn.nukkit.network.protocol.TelemetryEventPacket;
import cn.nukkit.network.protocol.TextPacket;
import cn.nukkit.network.protocol.TransferPacket;
import cn.nukkit.network.protocol.UpdateAttributesPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.network.protocol.UpdateEquipmentPacket;
import cn.nukkit.network.protocol.UpdateTradePacket;

/**
 * @author DaPorkchop_
 */
public interface NetHandler {
    default void handle(AddBehaviorTreePacket packet) {

    }

    default void handle(AddEntityPacket packet) {

    }

    default void handle(AddItemEntityPacket packet) {

    }

    default void handle(AddPaintingPacket packet) {

    }

    default void handle(AddPlayerPacket packet) {

    }

    default void handle(AdventureSettingsPacket packet) {

    }

    default void handle(AnimatePacket packet)   {

    }

    default void handle(AvailableCommandsPacket packet) {

    }

    default void handle(BatchPacket packet) {

    }

    default void handle(BlockEntityDataPacket packet) {

    }

    default void handle(BlockEventPacket packet) {

    }

    default void handle(BlockPickRequestPacket packet) {

    }

    default void handle(BookEditPacket packet) {

    }

    default void handle(BossEventPacket packet) {

    }

    default void handle(CameraPacket packet) {

    }

    default void handle(ChangeDimensionPacket packet) {

    }

    default void handle(ChunkRadiusUpdatedPacket packet) {

    }

    default void handle(ClientboundMapItemDataPacket packet) {

    }

    default void handle(ClientToServerHandshakePacket packet) {

    }

    default void handle(CommandBlockUpdatePacket packet) {

    }

    default void handle(CommandRequestPacket packet) {

    }

    default void handle(ContainerClosePacket packet) {

    }

    default void handle(ContainerOpenPacket packet) {

    }

    default void handle(ContainerSetDataPacket packet) {

    }

    default void handle(CraftingDataPacket packet) {

    }

    default void handle(CraftingEventPacket packet) {

    }

    default void handle(DisconnectPacket packet) {

    }

    default void handle(EntityEventPacket packet) {

    }

    default void handle(EntityFallPacket packet) {

    }

    default void handle(EntityPickRequestPacket packet) {

    }

    default void handle(ExplodePacket packet) {

    }

    default void handle(FullChunkDataPacket packet) {

    }

    default void handle(GameRulesChangedPacket packet) {

    }

    default void handle(GUIDataPickItemPacket packet) {

    }

    default void handle(HurtArmorPacket packet) {

    }

    default void handle(InitiateWebSocketConnectionPacket packet) {

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

    default void handle(LevelEventPacket packet) {

    }

    default void handle(LevelSoundEventPacket packet) {

    }

    default void handle(LoginPacket packet) {

    }

    default void handle(MapInfoRequestPacket packet) {

    }

    default void handle(MobArmorEquipmentPacket packet) {

    }

    default void handle(MobEffectPacket packet) {

    }

    default void handle(MobEquipmentPacket packet) {

    }

    default void handle(ModalFormRequestPacket packet) {

    }

    default void handle(ModalFormResponsePacket packet) {

    }

    default void handle(MoveEntityPacket packet) {

    }

    default void handle(MovePlayerPacket packet) {

    }

    default void handle(NPCRequestPacket packet) {

    }

    default void handle(PlayerActionPacket packet) {

    }

    default void handle(PlayerHotbarPacket packet) {

    }

    default void handle(PlayerInputPacket packet) {

    }

    default void handle(PlayerListPacket packet) {

    }

    default void handle(PlayerSkinPacket packet) {

    }

    default void handle(PlaySoundPacket packet) {

    }

    default void handle(PlayStatusPacket packet) {

    }

    default void handle(RemoveEntityPacket packet) {

    }

    default void handle(RequestChunkRadiusPacket packet) {

    }

    default void handle(ResourcePackChunkDataPacket packet) {

    }

    default void handle(ResourcePackChunkRequestPacket packet) {

    }

    default void handle(ResourcePackClientResponsePacket packet) {

    }

    default void handle(ResourcePackDataInfoPacket packet) {

    }

    default void handle(ResourcePacksInfoPacket packet) {

    }

    default void handle(ResourcePackStackPacket packet) {

    }

    default void handle(RespawnPacket packet) {

    }

    default void handle(RiderJumpPacket packet) {

    }

    default void handle(ServerSettingsRequestPacket packet) {

    }

    default void handle(ServerSettingsResponsePacket packet) {

    }

    default void handle(ServerToClientHandshakePacket packet) {

    }

    default void handle(SetCommandsEnabledPacket packet) {

    }

    default void handle(SetDifficultyPacket packet) {

    }

    default void handle(SetEntityDataPacket packet) {

    }

    default void handle(SetEntityLinkPacket packet) {

    }

    default void handle(SetEntityMotionPacket packet) {

    }

    default void handle(SetHealthPacket packet) {

    }

    default void handle(SetLastHurtByPacket packet) {

    }

    default void handle(SetPlayerGameTypePacket packet) {

    }

    default void handle(SetSpawnPositionPacket packet) {

    }

    default void handle(SetTimePacket packet) {

    }

    default void handle(SetTitlePacket packet) {

    }

    default void handle(ShowCreditsPacket packet) {

    }

    default void handle(ShowProfilePacket packet) {

    }

    default void handle(SimpleEventPacket packet) {

    }

    default void handle(SpawnExperienceOrbPacket packet) {

    }

    default void handle(StartGamePacket packet) {

    }

    default void handle(StopSoundPacket packet) {

    }

    default void handle(StructureBlockUpdatePacket packet) {

    }

    default void handle(SubClientLoginPacket packet) {

    }

    default void handle(TakeItemEntityPacket packet) {

    }

    default void handle(TelemetryEventPacket packet) {

    }

    default void handle(TextPacket packet) {

    }

    default void handle(TransferPacket packet) {

    }

    default void handle(UpdateAttributesPacket packet) {

    }

    default void handle(UpdateBlockPacket packet) {

    }

    default void handle(UpdateEquipmentPacket packet) {

    }

    default void handle(UpdateTradePacket packet) {

    }
}
