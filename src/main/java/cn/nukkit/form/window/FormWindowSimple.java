package cn.nukkit.form.window;

import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.SimpleElement;
import cn.nukkit.form.response.FormResponseSimple;

import java.util.ArrayList;
import java.util.List;

public class FormWindowSimple extends FormWindow {

    @SuppressWarnings("unused")
    private final String type = "form"; // This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String title = "";
    private String content = "";
    @SuppressWarnings("FieldMayBeFinal")
    private transient List<ElementButton> buttons;
    @SuppressWarnings("FieldMayBeFinal")
    private List<SimpleElement> elements;

    private FormResponseSimple response = null;

    public FormWindowSimple(String title, String content) {
        this(title, content, new ArrayList<>());
    }

    public FormWindowSimple(String title, String content, List<ElementButton> buttons) {
        this(title, content, buttons, new ArrayList<>(buttons));
    }

    public FormWindowSimple(String title, String content, List<ElementButton> buttons, List<SimpleElement> elements) {
        this.title = title;
        this.content = content;
        this.buttons = buttons;
        this.elements = elements;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ElementButton> getButtons() {
        return buttons;
    }

    public void addButton(ElementButton button) {
        this.buttons.add(button);
        this.elements.add(button);
    }

    public List<SimpleElement> getElements() {
        return elements;
    }

    public void addElement(SimpleElement element) {
        if (element instanceof ElementButton) {
            this.buttons.add((ElementButton) element);
        }
        this.elements.add(element);
    }

    public FormResponseSimple getResponse() {
        return response;
    }

    public void setResponse(String data) {
        if (data.equals("null")) {
            this.closed = true;
            return;
        }
        int buttonID;
        try {
            buttonID = Integer.parseInt(data);
        } catch (Exception e) {
            return;
        }
        if (buttonID >= this.buttons.size()) {
            this.response = new FormResponseSimple(buttonID, null);
            return;
        }
        this.response = new FormResponseSimple(buttonID, buttons.get(buttonID));
    }
}
