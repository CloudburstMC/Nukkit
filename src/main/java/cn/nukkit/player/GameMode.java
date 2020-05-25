package cn.nukkit.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameMode {
    SURVIAL(false, true),
    CREATIVE(false, false),
    ADVENTURE(true, true),
    SPECTATOR(true, false);

    private final boolean visitor;
    private final boolean survival;

    public String getTranslation() {
        return "%gameMode." + name().toLowerCase();
    }
}
