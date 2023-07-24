package org.vaadin.vcamera;

import com.vaadin.flow.component.html.Div;

import java.io.File;


public abstract class AbstractCameraView extends Div
        implements HasDataReceiver {

    private VCamera camera = new VCamera();

    File latest;

    public AbstractCameraView() {
        camera.setReceiver(this);
    }

    public VCamera getCamera() {
        return camera;
    }

    @Override
    public File getLatest() {
        return latest;
    }

    @Override
    public void setLatest(File file) {
        latest = file;
    }

}
