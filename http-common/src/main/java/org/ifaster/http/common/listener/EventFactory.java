package org.ifaster.http.common.listener;

import okhttp3.Call;
import okhttp3.EventListener;

/**
 * @author yangnan
 */
public class EventFactory implements EventListener.Factory {
    private String clientName;
    public EventFactory() {
    }

    public EventFactory(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public EventListener create(Call call) {
        return new MonitorEventListener(call.request().url().toString(), clientName);
    }
}
