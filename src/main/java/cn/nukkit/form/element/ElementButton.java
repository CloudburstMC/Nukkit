package cn.nukkit.form.element;

public class ElementButton implements SimpleElement {

    @SuppressWarnings("unused")
    private final String type = "button";
    private String text = "";
    private ElementButtonImageData image;

    public ElementButton(String text) {
        this.text = text;
    }

    public ElementButton(String text, ElementButtonImageData image) {
        this.text = text;
        if (!image.getData().isEmpty() && !image.getType().isEmpty()) this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ElementButtonImageData getImage() {
        return image;
    }

    public void addImage(ElementButtonImageData image) {
        if (!image.getData().isEmpty() && !image.getType().isEmpty()) this.image = image;
    }
}
