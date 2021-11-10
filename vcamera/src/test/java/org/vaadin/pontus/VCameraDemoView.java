package org.vaadin.pontus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;

@Route(value = "", layout = VCameraDemo.class)
@Tag("vcamera-demo-element")
@HtmlImport("bower_components/vcamera-demo-element/vcamera-demo-element.js")
public class VCameraDemoView extends AbstractCameraView {

    @Id("snap")
    Button takePicture;

    @Id("preview")
    Button preview;

    @Id("start")
    Button startRecording;

    @Id("stop")
    Button stopRecording;

    @Id("stopcamera")
    Button stopCamera;

    @Id("image")
    Div imageContainer;

    @Id("video")
    Div videoContainer;

    public VCameraDemoView() {

        takePicture.addClickListener(e -> {
            getCamera().takePicture();
        });

        preview.addClickListener(e -> {
            getCamera().showPreview();
        });

        startRecording.addClickListener(e -> {
            getCamera().startRecording();
        });

        stopRecording.addClickListener(e -> {
            getCamera().stopRecording();
        });

        stopCamera.addClickListener(e -> {
            getCamera().stopCamera();
        });

        getCamera().showPreview();
        getCamera().addFinishedListener(e -> {

            String mime = e.getMime();
            if (mime.contains("image")) {
                setImage();
            } else if (mime.contains("video")) {
                setVideo();
            }
        });

    }

    private void clearImageAndVideo() {
        imageContainer.removeAll();
        videoContainer.removeAll();
    }

    private void setImage() {
        clearImageAndVideo();
        File file = getLatest();
        if (file != null) {
            InputStreamFactory f = () -> {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException e) {
                }
                return null;
            };
            Image image = new Image(new StreamResource("image", f),
                    "The captured image");
            imageContainer.add(image);
        }
    }

    private void setVideo() {
        clearImageAndVideo();
        File file = getLatest();
        if (file != null) {
            Element e = new Element("video");
            InputStreamFactory f = () -> {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException ex) {
                }
                return null;
            };
            e.setAttribute("src", new StreamResource("video", f));
            e.setAttribute("autoplay", true);
            imageContainer.getElement().appendChild(e);
        }
    }

}
