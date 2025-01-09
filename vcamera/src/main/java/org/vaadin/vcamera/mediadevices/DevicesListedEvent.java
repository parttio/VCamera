package org.vaadin.vcamera.mediadevices;

import com.vaadin.flow.component.ComponentEvent;
import org.vaadin.vcamera.VCamera;

import java.util.List;

public class DevicesListedEvent extends ComponentEvent<VCamera> {

    private final List<Device> devices;

    public DevicesListedEvent(VCamera source, boolean fromClient, List<Device> devices) {
        super(source, fromClient);
        this.devices = devices;
    }

    public List<Device> getDevices() {
        return devices;
    }
}
