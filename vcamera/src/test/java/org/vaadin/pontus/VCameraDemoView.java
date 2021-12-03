package org.vaadin.pontus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;

@Route(value = "", layout = VCameraDemo.class)
public class VCameraDemoView extends AbstractCameraView {

    @Id("snap")
    Button takePicture = new Button("Take picture");

    @Id("preview")
    Button preview = new Button("Preview");

    @Id("start")
    Button startRecording = new Button("Start recording");

    @Id("stop")
    Button stopRecording = new Button("Stop recording");

    @Id("stopcamera")
    Button stopCamera = new Button("Stop camera");

    @Id("image")
    Div imageContainer = new Div();

    @Id("video")
    Div videoContainer = new Div();

    public VCameraDemoView() {
        
        add(getCamera());
        add(new HorizontalLayout(takePicture, preview, startRecording, stopRecording, stopCamera));
        add(imageContainer);
        add(videoContainer);

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
            System.out.println("Received image or video to the server side");
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

