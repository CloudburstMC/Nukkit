package cn.nukkit.event;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class TextContainer {
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

    public String __toString() {
        return this.getText();
    }
}
