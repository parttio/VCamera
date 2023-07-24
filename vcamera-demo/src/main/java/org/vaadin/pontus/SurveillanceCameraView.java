package org.vaadin.pontus;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@Route(value = "surveillance", layout = VCameraDemo.class)
public class SurveillanceCameraView extends AbstractCameraView
        implements KeyGenerator {

    private Div keyLabel = new Div();

    public SurveillanceCameraView() {
        add(keyLabel);
        getCamera().addFinishedListener(e -> {
            FileService.notify(keyLabel.getText(), latest);
        });
        keyLabel.setText(generateRandomKey());
    }

}
