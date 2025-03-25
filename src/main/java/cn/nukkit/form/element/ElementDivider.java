package cn.nukkit.form.element;

import lombok.Getter;
import lombok.Setter;

public class ElementDivider extends Element implements SimpleElement {

    @SuppressWarnings("unused")
    private final String type = "divider";
    @Getter
    @Setter
    private String text = "";

    /**
     * Element divider (1.21.70+)
     */
    public ElementDivider() {
        this("");
    }

    public ElementDivider(String text) {
        this.text = text;
    }
}
