package cn.nukkit.form.window;

import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponseSimple;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class FormWindowSimple extends FormWindow {

    private final String type = "form"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String title = "";
    private String content = "";
    private List<ElementButton> buttons;

    private FormResponseSimple response = null;

    public FormWindowSimple(String title, String content) {
        this(title, content, new ArrayList<>());
    }

    public FormWindowSimple(String title, String content, List<ElementButton> buttons) {
        this.title = title;
        this.content = content;
        this.buttons = buttons;
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
    }

    public String getJSONData() {
        return new Gson().toJson(this);
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
