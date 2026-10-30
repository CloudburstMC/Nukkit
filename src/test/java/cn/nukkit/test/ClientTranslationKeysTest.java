package cn.nukkit.test;

import cn.nukkit.lang.ClientTranslationKeys;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientTranslationKeysTest {

    @Test
    void whitelistKeysUseTheAllowlistKeysOfTheClient() {
        assertEquals("commands.allowlist.enabled", ClientTranslationKeys.get("commands.whitelist.enabled"));
        assertEquals("commands.allowlist.add.success", ClientTranslationKeys.get("commands.whitelist.add.success"));
        assertEquals("commands.allowlist.list", ClientTranslationKeys.get("commands.whitelist.list"));
    }

    @Test
    void negativeLevelsUseTheKeyOfTheClient() {
        assertEquals("commands.xp.success.negative.levels", ClientTranslationKeys.get("commands.xp.success.levels.minus"));
    }

    @Test
    void otherKeysStayAsTheyAre() {
        assertEquals("commands.give.success", ClientTranslationKeys.get("commands.give.success"));
        assertEquals("nukkit.command.version", ClientTranslationKeys.get("nukkit.command.version"));
    }
}
