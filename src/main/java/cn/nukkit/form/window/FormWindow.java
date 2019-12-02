package cn.nukkit.form.window;

import cn.nukkit.form.response.FormResponse;
import com.google.gson.Gson;

public abstract class FormWindow {

    protected boolean closed = false;

    public String getJSONData() {
        return new Gson().toJson(this);
    }

    public abstract FormResponse getResponse();

    public abstract void setResponse(String data);

    public boolean wasClosed() {
        return closed;
    }

}
