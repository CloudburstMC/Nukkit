package cn.nukkit.server.lang;

import cn.nukkit.server.NukkitServer;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class TextContainer implements Cloneable {
    protected String text;

    public TextContainer(String text) {
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return this.getText();
    }

    @Override
    public TextContainer clone() {
        try {
            return (TextContainer) super.clone();
        } catch (CloneNotSupportedException e) {
            NukkitServer.getInstance().getLogger().logException(e);
        }
        return null;
    }
}
