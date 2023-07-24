package org.vaadin.pontus;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import org.vaadin.vcamera.DataReceiver;
import org.vaadin.vcamera.VCamera;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Route(value = "")
public class VCameraDemoView extends VerticalLayout {
    private VCamera camera;

    File latest;

    Button takePicture = new Button("Take picture");

    Button onoff = new Button("Close camera");

    Button startRecording = new Button("Start recording");

    Button stopRecording = new Button("Stop recording");

    Div imageContainer = new Div();

    Div videoContainer = new Div();

    public VCameraDemoView() {

        camera = new VCamera();
        camera.setReceiver(new DataReceiver() {
            @Override
            public OutputStream getOutputStream(String mimeType) {
                String suffix;
                if (mimeType.contains("jpeg")) {
                    suffix = ".jpeg";
                } else if (mimeType.contains("matroska")) {
                    suffix = ".mkv";
                } else {
                    suffix = ".file";
                }
                if (latest != null) {
                    latest.delete();
                }
                try {
                    latest = File.createTempFile("camera", suffix);
                    System.out.println("Streaming to temp file " + latest);
                    return new FileOutputStream(latest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        
        add(camera);
        add(new HorizontalLayout(takePicture, onoff, startRecording, stopRecording));
        add(imageContainer);
        add(videoContainer);

        takePicture.addClickListener(e -> {
            camera.takePicture();
        });

        onoff.addClickListener(e -> {
            if(camera.isCameraOpen()) {
                camera.closeCamera();
                onoff.setText("Open camera");
                takePicture.setEnabled(false);
                startRecording.setEnabled(false);
            } else {
                camera.openCamera();
                onoff.setText("Close camera");
                takePicture.setEnabled(true);
                startRecording.setEnabled(true);
            }
        });

        startRecording.addClickListener(e -> {
            camera.startRecording();
            stopRecording.setEnabled(true);
        });

        stopRecording.setEnabled(false);
        stopRecording.addClickListener(e -> {
            camera.stopRecording();
            stopRecording.setEnabled(false);
        });

        camera.openCamera();
        camera.addFinishedListener(e -> {
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
        File file = latest;
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
        File file = latest;
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

