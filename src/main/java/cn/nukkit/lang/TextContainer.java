package cn.nukkit.lang;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
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
            log.throwing(Level.ERROR, e);
        }
        return null;
    }
}
