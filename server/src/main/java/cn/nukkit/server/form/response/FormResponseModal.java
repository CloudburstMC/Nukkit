package cn.nukkit.server.form.response;

public class FormResponseModal extends FormResponse {

    private int clickedButtonId;
    private String clickedButtonText;

    public FormResponseModal(int clickedButtonId, String clickedButtonText) {
        this.clickedButtonId = clickedButtonId;
        this.clickedButtonText = clickedButtonText;
    }

    public int getClickedButtonId() {
        return clickedButtonId;
    }

    public String getClickedButtonText() {
        return clickedButtonText;
    }

}
