package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.message.ChatMessage;
import cn.nukkit.api.message.Message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class PlayerJoinEvent implements PlayerEvent {
    private final Player player;
    private Message message;

    public PlayerJoinEvent(Player player, String message) {
        this(player, new ChatMessage(message));
    }

    public PlayerJoinEvent(Player player, Message message) {
        this.player = player;
        this.message = message;
    }

    public boolean isSilentlyJoining() {
        return message == null;
    }

    public void silentlyJoin() {
        message = null;
    }

    @Nonnull
    public Optional<Message> getJoinMessage() {
        return Optional.ofNullable(message);
    }

    public void setJoinMessage(@Nullable Message message) {
        this.message = message;
    }

    public void setJoinMessage(@Nullable String message) {
        if (message == null) {
            this.message = null;
        } else {
            this.setJoinMessage(new ChatMessage(message));
        }
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
