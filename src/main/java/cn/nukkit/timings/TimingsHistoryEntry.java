package cn.nukkit.timings;

import com.google.gson.JsonArray;

/**
 * @author Tee7even
 */
class TimingsHistoryEntry {
    final TimingData data;
    final TimingData[] children;

    TimingsHistoryEntry(Timing timing) {
        this.data = timing.record.clone();
        this.children = new TimingData[timing.children.size()];

        int i = 0;
        for (TimingData child : timing.children.values()) {
            this.children[i++] = child.clone();
        }
    }

    JsonArray export() {
        JsonArray json = this.data.export();
        if (this.children.length > 0) json.add(JsonUtil.mapToArray(this.children, TimingData::export));
        return json;
    }
}
