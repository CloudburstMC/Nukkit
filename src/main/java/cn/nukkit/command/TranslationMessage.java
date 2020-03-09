package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.locale.TranslationContainer;
import com.mojang.brigadier.Message;

public class TranslationMessage implements Message {
    private final String string;
    private final String[] params;

    public TranslationMessage(String string, String params) {
        this(string, new String[]{params});
    }

    public TranslationMessage(String string, String... params) {
        this.string = string;
        this.params = params;
    }

    @Override
    public String getString() {
        return Server.getInstance().getLanguage().translate(string, params);
    }

    @Override
    public String toString() {
        return getString();
    }

}
