package org.vaadin.vcamera;

import com.vaadin.flow.component.ComponentEvent;

public class FinishedEvent extends ComponentEvent<VCamera>{

    private String mime;

    public FinishedEvent(VCamera source, boolean fromClient, String mime) {
        super(source, fromClient);
        this.mime =mime;
    }

    public String getMime() {
        return mime;
    }

}
