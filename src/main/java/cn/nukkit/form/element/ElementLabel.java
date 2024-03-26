package cn.nukkit.form.element;

public class ElementLabel extends Element {

    @SuppressWarnings("unused")
    private final String type = "label";
    private String text = "";

    public ElementLabel(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
