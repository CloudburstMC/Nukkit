package cn.nukkit.form.element;

import lombok.Getter;
import lombok.Setter;

public class ElementHeader extends Element implements SimpleElement {

    @SuppressWarnings("unused")
    private final String type = "header";
    @Getter
    @Setter
    private String text = "";

    /**
     * Text header (1.21.70+)
     */
    public ElementHeader(String text) {
        this.text = text;
    }
}
