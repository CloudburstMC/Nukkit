package cn.nukkit.api.form.response;

import cn.nukkit.api.form.element.ElementButton;

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
