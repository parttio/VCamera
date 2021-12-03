package org.vaadin.vcamera;

import java.io.OutputStream;

@FunctionalInterface
public interface DataReceiver {

    public OutputStream getOutputStream(String mimeType);

}
