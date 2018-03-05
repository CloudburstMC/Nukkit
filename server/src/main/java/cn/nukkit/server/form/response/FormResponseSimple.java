package cn.nukkit.server.form.response;

import cn.nukkit.server.form.element.ElementButton;

public class FormResponseSimple extends FormResponse {

    private int clickedButtonId;
    private ElementButton clickedButton;

    public FormResponseSimple(int clickedButtonId, ElementButton clickedButton) {
        this.clickedButtonId = clickedButtonId;
        this.clickedButton = clickedButton;
    }

    public int getClickedButtonId() {
        return clickedButtonId;
    }

    public ElementButton getClickedButton() {
        return clickedButton;
    }

}
