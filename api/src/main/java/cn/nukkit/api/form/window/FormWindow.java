package cn.nukkit.api.form.window;

import cn.nukkit.api.form.response.FormResponse;

public abstract class FormWindow {

    protected boolean closed = false;

    public abstract String getJSONData();

    public abstract void setResponse(String data);

    public abstract FormResponse getResponse();

    public boolean wasClosed() {
        return closed;
    }

}
