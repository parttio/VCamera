package org.vaadin.vcamera;

import java.io.OutputStream;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.server.StreamReceiver;
import com.vaadin.flow.server.StreamVariable;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.vcamera.mediadevices.Device;
import org.vaadin.vcamera.mediadevices.DevicesListedEvent;

/**
 * A special video element that streams content from browser camera.
 * <p>During streaming, users can record still or video clips of the stream, that browser send to the server. The data can be accessed using the DataReceiver interface, see {@link #setReceiver(DataReceiver)}.</p>
 */
@Tag("video")
public class VCamera extends Component {

    private boolean cameraOn;
    private boolean recording;
    private boolean flashOn;

    private static final Logger LOG = LoggerFactory.getLogger(VCamera.class);

    public VCamera() {
        getElement().setProperty("volume", 0);
    }

    public void setReceiver(DataReceiver receiver) {
        getElement().setAttribute("target", new StreamReceiver(
                getElement().getNode(), "camera", new CameraStreamVariable(receiver)));
    }

    private void fireFinishedEvent(String mime) {
        fireEvent(new FinishedEvent(this, true, mime));
    }

    public void startRecording() {
        if(!cameraOn) {
            throw new IllegalStateException("Camera is not on");
        }
        recording = true;
        getElement().executeJs("""
                let target = this.getAttribute("target");;
                this.recorder = new MediaRecorder(this.stream);
                this.recorder.ondataavailable = e => {
                    let formData = new FormData();
                    formData.append("data", e.data);
                    fetch(target, {
                        method: "post",
                        body: formData
                    }).then(response => console.log(response));
                }
                this.recorder.start();
                """);
    }

    public void stopRecording() {
        if(!recording) {
            throw new IllegalStateException("Not recording");
        }
        getElement().executeJs("this.recorder.stop()");
        recording = false;
    }

    public void closeCamera() {
        cameraOn = false;
        getElement().executeJs("""
                if(this.stream!=null) {
                    this.stream.getTracks().forEach( t=> {
                        t.stop();
                    });
                    this.stream = null;
                }
                """);
    }

    public void takePicture() {
        if(!cameraOn) {
            throw new IllegalStateException("Camera is not on");
        }
        getElement().executeJs("""
                let canvas = document.createElement("canvas");
                let context = canvas.getContext('2d');
                let target = this.getAttribute("target");;
                canvas.height = this.videoHeight;
                canvas.width = this.videoWidth;
                context.drawImage(this, 0, 0, this.videoWidth, this.videoHeight);
                canvas.toBlob(b => {
                    let formData = new FormData();
                    formData.append("data",b);
                    fetch(target, {
                        method: "post",
                        body: formData
                    }).then(response => console.log(response));
                },'image/jpeg',0.95);
                """);
    }

    public void openCamera() {
        openCamera("{audio:true,video:true}");
    }

    public void openCamera(String optionsJson) {
        cameraOn = true;
        getElement().executeJs("""
                if(this.stream == null) {
                    if(navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
                        navigator.mediaDevices.getUserMedia(%s).then(stream => {
                            this.stream = stream;
                            this.srcObject = this.stream;
                            this.play();
                        });
                    }
                }
                """.formatted(optionsJson));
    }

    public boolean isFlashOn() {
        return flashOn;
    }

    public void toggleFlashlight() {
        flashOn = !flashOn;
        getElement().executeJs("""
                if(this.stream != null) {
                    const track = this.stream.getVideoTracks()[0];
            
                    //Create image capture object and get camera capabilities
                    const imageCapture = new ImageCapture(track)
                    const photoCapabilities = imageCapture.getPhotoCapabilities().then(() => {
                      track.applyConstraints({
                        advanced: [{torch: %s}]
                      });
                    });
                }
            """.formatted(flashOn));
    }

    /**
     * Opens a specific videoinput device.
     * @param device device to open
     * @throws IllegalArgumentException if device is not of kind "videoinput"
     */
    public void openCamera(Device device) {
        String sourceId = device.getDeviceId();
        if(device.getKind() != Device.DeviceKind.videoinput) {
            throw new IllegalArgumentException("Device "+device+" is not a video device");
        }
        openCamera("""
                 {  video: {
                      optional: [{sourceId: "%s"}]
                 }}
                 """.formatted(sourceId));
    }

    /**
     * Reads available devices on the users browser
     * @param listener is called with a list of devices after Javascript execution completes
     */
    public void listDevices(ComponentEventListener<DevicesListedEvent> listener) {
        PendingJavaScriptResult pendingJavaScriptResult = getElement().executeJs("""
                 if (!navigator.mediaDevices || !navigator.mediaDevices.enumerateDevices) {
                   console.log("enumerateDevices() not supported.");
                   return "";
                 }
                
                 // List cameras and microphones
                 return new Promise(function(myResolve, myReject){
                    navigator.mediaDevices.enumerateDevices()
                        .then(function(devices) {
                            devicesString = JSON.stringify(devices);
                            myResolve(devicesString); // when successful
                            myReject("");  // when error
                     } )
                   });
                """);

        pendingJavaScriptResult.then(String.class, (s) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<Device> devices = objectMapper.readValue(s, new TypeReference<>() {
                });
                DevicesListedEvent event = new DevicesListedEvent(this, false, devices);
                listener.onComponentEvent(event);
            } catch (JsonProcessingException e) {
                LOG.error("Could not read devices");
                listener.onComponentEvent(new DevicesListedEvent(this, false, List.of()));
            }
        });
    }

    /**
     * Tries to open the "normal", back-facing camera on a mobile phone. While it is possible to set a constraint facingMode = environment,
     * it is up to the device, which camera to use. E.g. there could be a fishEye camera which is then used, which is rarely wanted.
     * <a href="https://www.reddit.com/r/javascript/comments/8eg8w5/comment/dxvqycs/">A reddit post</a> mentions, that the most reliable way
     * was to just use the last enumerated device as returned by {@link #listDevices(ComponentEventListener)}
     */
    public void openNormalEnvironmentCamera() {
        listDevices(e -> {
            List<Device> videoDevices = e.getDevices().stream()
                    .filter(device -> device.getKind() == Device.DeviceKind.videoinput)
                    .toList();
            openCamera(videoDevices.get(videoDevices.size() - 1));
        });
    }


    public boolean isCameraOpen() {
        return cameraOn;
    }

    public Registration addFinishedListener(ComponentEventListener<FinishedEvent> listener) {
        return addListener(FinishedEvent.class, listener);
    }


    private class CameraStreamVariable implements StreamVariable {

        String mime;
        DataReceiver receiver;

        public CameraStreamVariable(DataReceiver receiver) {
            this.receiver = receiver;
        }


        @Override
        public OutputStream getOutputStream() {
            return receiver.getOutputStream(mime);
        }

        @Override
        public boolean isInterrupted() {
            return false;
        }

        @Override
        public boolean listenProgress() {
            return false;
        }

        @Override
        public void onProgress(StreamingProgressEvent arg0) {

        }

        @Override
        public void streamingFailed(StreamingErrorEvent arg0) {

        }

        @Override
        public void streamingFinished(StreamingEndEvent arg0) {
            fireFinishedEvent(mime);

        }

        @Override
        public void streamingStarted(StreamingStartEvent arg0) {
            mime = arg0.getMimeType();
        }

    }

}
