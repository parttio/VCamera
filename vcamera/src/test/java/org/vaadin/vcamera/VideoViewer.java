package org.vaadin.vcamera;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.vaadin.vcamera.FileService.Registration;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

@Route(value = "Viewer", layout = VCameraDemo.class)
public class VideoViewer extends Composite<VerticalLayout> {

    TextField key = new TextField();

    Div selectedKey = new Div();

    VideoComponent video = new VideoComponent();

    Registration registration;

    public VideoViewer() {

        selectedKey.setText("Selected key: None");
        Div desc = new Div();
        H1 h1 = new H1();
        h1.setText("A viewer application connected to the Surveillance view");
        Paragraph p = new Paragraph();
        p.setText("When a video is recorded in the \"Surveillance\" view, this "
                + "view updated with the video if they both have the same sharing key");
        desc.add(h1, p);
        getContent().add(desc, key, selectedKey, video);
        key.addValueChangeListener(e -> {
            selectedKey.setText("Selected key: " + e.getValue());
            if (registration != null) {
                registration.unregister();
            }
            UI ui = UI.getCurrent();
            registration = FileService.register(e.getValue(), file -> {
                ui.access(() -> {
                    StreamResource resource = new StreamResource("video",
                            () -> {
                                try {
                                    return new FileInputStream(file);
                                } catch (FileNotFoundException e1) {
                                    e1.printStackTrace();
                                }
                                return null;
                            });
                    video.setSrc(resource);
                });
            });
        });
    }

}
