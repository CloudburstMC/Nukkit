package cn.nukkit.form.response;

public class FormResponseData {

    private final int elementID;
    private final String elementContent;

    public FormResponseData(int id, String content) {
        this.elementID = id;
        this.elementContent = content;
    }

    public int getElementID() {
        return elementID;
    }

    public String getElementContent() {
        return elementContent;
    }

}
