package cn.nukkit.form.window;

import cn.nukkit.Nukkit;
import cn.nukkit.form.response.FormResponse;

import java.io.IOException;

public abstract class FormWindow {

    protected boolean closed = false;

    public String getJSONData() {
        try {
            return Nukkit.JSON_MAPPER.writeValueAsString(this);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write form window JSON", e);
        }
    }

    public abstract void setResponse(String data);

    public abstract FormResponse getResponse();

    public boolean wasClosed() {
        return closed;
    }

}
