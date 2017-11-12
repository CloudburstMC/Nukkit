package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.PlayerProtocol;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockEntitySign extends BlockEntitySpawnable {

    public BlockEntitySign(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        if (!nbt.contains("Text")) {
            List<String> lines = new ArrayList<>();

            for (int i = 1; i <= 4; i++) {
                String key = "Text" + i;

                if (nbt.contains(key)) {
                    String line = nbt.getString(key);

                    lines.add(line);

                    nbt.remove(key);
                }
            }

            nbt.putString("Text", String.join("\n", lines));
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.remove("Creator");
    }

    @Override
    public boolean isBlockEntityValid() {
        int blockID = getBlock().getId();
        return blockID == Block.SIGN_POST || blockID == Block.WALL_SIGN;
    }

    public boolean setText(String... lines) {
        this.namedTag.putString("Text", String.join("\n", lines));
        this.spawnToAll();

        if (this.chunk != null) {
            this.chunk.setChanged();
            this.level.clearChunkCache(this.chunk.getX(), this.chunk.getZ());
        }

        return true;
    }

    public String[] getText() {
        return this.namedTag.getString("Text").split("\n");
    }

    @Override
    public boolean updateCompoundTag(CompoundTag nbt, Player player) {
        if (!nbt.getString("id").equals(BlockEntity.SIGN)) {
            return false;
        }
        String[] text = nbt.getString("Text").split("\n", 4);

        SignChangeEvent signChangeEvent = new SignChangeEvent(this.getBlock(), player, text);

        if (!this.namedTag.contains("Creator") || !Objects.equals(player.getUniqueId().toString(), this.namedTag.getString("Creator"))) {
            signChangeEvent.setCancelled();
        }

        if (player.getRemoveFormat()) {
            for (int i = 0; i < text.length; i++) {
                text[i] = TextFormat.clean(text[i]);
            }
        }

        this.server.getPluginManager().callEvent(signChangeEvent);

        if (!signChangeEvent.isCancelled()) {
            this.setText(signChangeEvent.getLines());
            return true;
        }

        return false;
    }

    @Override
    public CompoundTag getSpawnCompound(PlayerProtocol protocol) {
        if (protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113)) return new CompoundTag()
                .putString("id", BlockEntity.SIGN)
                .putString("Text1", getText(0))
                .putString("Text2", getText(1))
                .putString("Text3", getText(2))
                .putString("Text4", getText(3))
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);
        return new CompoundTag()
                .putString("id", BlockEntity.SIGN)
                .putString("Text", this.namedTag.getString("Text"))
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);
    }
    private String getText(int split){
        return this.namedTag.getString("Text").split("\n").length > split ?
                this.namedTag.getString("Text").split("\n")[split] : "";
    }
}
