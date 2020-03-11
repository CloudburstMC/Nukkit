package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.Sign;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.TextFormat;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import java.util.Arrays;
import java.util.Objects;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SignBlockEntity extends BaseBlockEntity implements Sign {

    private static final String[] LEGACY_TEXT_TAGS = {"Text1", "Text2", "Text3", "Text4"};

    private String[] text = new String[4];
    private String creator;

    public SignBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    private static void sanitizeText(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            // Don't allow excessive characters per line.
            if (lines[i] != null) {
                lines[i] = lines[i].substring(0, Math.min(255, lines[i].length()));
            }
        }
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        String[] text;
        if (!tag.contains("Text")) {
            text = new String[4];
            for (int i = 0; i < 4; i++) {
                text[i] = tag.getString(LEGACY_TEXT_TAGS[i], "");
            }
        } else {
            text = Arrays.copyOf(tag.getString("Text").split("\n", 4), 4);
        }

        this.text = text;

        tag.listenForString("Creator", this::setCreator);
    }

    @Override
    public void saveClientData(CompoundTagBuilder tag) {
        super.saveClientData(tag);

        tag.stringTag("Text", String.join("\n", this.text));
        tag.stringTag("Creator", this.creator);
    }

    @Override
    public boolean isValid() {
        Identifier blockId = getBlock().getId();
        return blockId == BlockIds.STANDING_SIGN || blockId == BlockIds.WALL_SIGN ||
                blockId == BlockIds.SPRUCE_STANDING_SIGN || blockId == BlockIds.SPRUCE_WALL_SIGN ||
                blockId == BlockIds.BIRCH_STANDING_SIGN || blockId == BlockIds.BIRCH_WALL_SIGN ||
                blockId == BlockIds.JUNGLE_STANDING_SIGN || blockId == BlockIds.JUNGLE_WALL_SIGN ||
                blockId == BlockIds.ACACIA_STANDING_SIGN || blockId == BlockIds.ACACIA_WALL_SIGN ||
                blockId == BlockIds.DARK_OAK_STANDING_SIGN || blockId == BlockIds.DARK_OAK_WALL_SIGN;
    }

    public void setText(String... lines) {
        for (int i = 0; i < 4; i++) {
            if (i < lines.length)
                text[i] = lines[i];
            else
                text[i] = "";
        }

        this.spawnToAll();
        this.setDirty();
    }

    public String[] getText() {
        return Arrays.copyOf(text, text.length);
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public boolean updateCompoundTag(CompoundTag tag, Player player) {
        String[] splitText = tag.getString("Text").split("\n", 4);
        String[] text = new String[4];

        for (int i = 0; i < 4; i++) {
            if (i < splitText.length)
                text[i] = splitText[i];
            else
                text[i] = "";
        }

        sanitizeText(text);

        SignChangeEvent event = new SignChangeEvent(this.getBlock(), player, text);

        if (!tag.contains("Creator") || !Objects.equals(player.getXuid(), tag.getString("Creator"))) {
            event.setCancelled();
        }

        if (player.getRemoveFormat()) {
            for (int i = 0; i < 4; i++) {
                text[i] = TextFormat.clean(text[i]);
            }
        }

        this.server.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.setText(event.getLines());
            return true;
        }

        return false;
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}
