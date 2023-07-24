package org.vaadin.vcamera;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Route(value = "", layout = VCameraDemo.class)
public class VCameraDemoView extends AbstractCameraView {

    Button takePicture = new Button("Take picture");

    Button preview = new Button("Preview");

    Button startRecording = new Button("Start recording");

    Button stopRecording = new Button("Stop recording");

    Button stopCamera = new Button("Stop camera");

    Div imageContainer = new Div();

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
            getCamera().openCamera();
        });

        startRecording.addClickListener(e -> {
            getCamera().startRecording();
        });

        stopRecording.addClickListener(e -> {
            getCamera().stopRecording();
        });

        stopCamera.addClickListener(e -> {
            getCamera().closeCamera();
        });

        getCamera().openCamera();
        getCamera().addFinishedListener(e -> {
            System.out.println("Received image or video to the server side");
            String mime = e.getMime();
            if (mime.contains("image")) {
                setImage();
            } else if (mime.contains("video")) {
                setVideo(e.getMime());
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

    private void setVideo(String mime) {
        clearImageAndVideo();
        File file = getLatest();
        if (file != null) {
            VideoComponent videoComponent = new VideoComponent();
            InputStreamFactory f = () -> {
                try {
                    VaadinRequest vr = VaadinRequest.getCurrent();
                    VaadinResponse vresp = VaadinResponse.getCurrent();
                    String range = vr.getHeader("Range");
                    if(range != null) {
                        System.out.println("Range: " + range);
                        // Safari uses range requests for video, like bytes=0-1
                        String[] split = range.substring("bytes=".length()).split("-");
                        if(split.length == 2) {
                            int start = Integer.parseInt(split[0]);
                            int end = Integer.parseInt(split[1]);
                            int length = end - start + 1;
                            long fileLength = file.length();
                            FileInputStream fileInputStream = new FileInputStream(file);
                            fileInputStream.skip(start);
                            byte[] bytes = new byte[length];
                            fileInputStream.read(bytes, 0, length);
                            vresp.setStatus(206);
                            vresp.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
                            vresp.setHeader("Content-Length", length + "");
                            return new ByteArrayInputStream(bytes);
                        }
                    }
                    return new FileInputStream(file);
                } catch (FileNotFoundException ex) {
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return null;
            };
            StreamResource streamResource = new StreamResource("video", f);
            streamResource.setContentType(mime);
            videoComponent.setSrc(streamResource);
            imageContainer.add(videoComponent);
        }
    }

}

