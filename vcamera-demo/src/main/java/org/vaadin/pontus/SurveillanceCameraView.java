package org.vaadin.pontus;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.router.Route;

@Route(value = "surveillance", layout = VCameraDemo.class)
//@Tag("vcamera-surveillance-element")
//@JsModule("./camera-surveillance-element.js")
public class SurveillanceCameraView extends AbstractCameraView
        implements KeyGenerator {

    @Id("key")
    private Div keyLabel;

    public SurveillanceCameraView() {
        getCamera().addFinishedListener(e -> {
            FileService.notify(keyLabel.getText(), latest);
        });
        keyLabel.setText(generateRandomKey());
    }

}
