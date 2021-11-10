package org.vaadin.pontus;

import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.server.StreamReceiver;
import com.vaadin.flow.server.StreamVariable;
import com.vaadin.flow.shared.Registration;

import elemental.json.JsonFactory;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import elemental.json.impl.JreJsonFactory;

@Tag("vcamera-element")
@JsModule("./vcamera-element.js")
public class VCamera extends Component {

    public VCamera(Map<String,Object> previewOptions, Map<String,Object> recordingOptions, DataReceiver receiver) {
        setReceiver(receiver);
        setOptions(previewOptions, recordingOptions);
    }

    public VCamera() {

    }

    public void setOptions(Map<String,Object> previewOptions, Map<String,Object> recordingOptions) {
        JsonFactory factory = new JreJsonFactory();
        getElement().setPropertyJson("previewOptions", toJson(previewOptions, factory));
        getElement().setPropertyJson("recordingOptions", toJson(recordingOptions, factory));
    }

    private JsonValue toJson(Map<String,Object> map, JsonFactory factory) {
        JsonObject obj = factory.createObject();
        for(Entry<String,Object> entry: map.entrySet()) {
            if(entry.getValue() instanceof Boolean) {
                obj.put(entry.getKey(), factory.create((Boolean)entry.getValue()));
            } else if(entry.getValue() instanceof Double) {
                obj.put(entry.getKey(), factory.create((Double)entry.getValue()));
            } else if(entry.getValue() instanceof Integer) {
                obj.put(entry.getKey(), factory.create((Integer)entry.getValue()));
            } else if(entry.getValue() instanceof String) {
                obj.put(entry.getKey(), factory.create((String)entry.getValue()));
            } else if(entry.getValue() instanceof Map) {
                obj.put(entry.getKey(), toJson((Map<String,Object>)entry.getValue(), factory));
            } else {
                throw new IllegalArgumentException("Unsopported argument in options");
            }
        }
        return obj;
    }

    public void setReceiver(DataReceiver receiver) {
        getElement().setAttribute("target", new StreamReceiver(
                getElement().getNode(), "camera", new CameraStreamVariable(receiver)));
    }

    private void fireFinishedEvent(String mime) {
        fireEvent(new FinishedEvent(this, true, mime));
    }

    public void startRecording() {
        getElement().callFunction("startRecording");
    }

    public void stopRecording() {
        getElement().callFunction("stopRecording");
    }

    public void stopCamera() {
        getElement().callFunction("stopCamera");
    }

    public void takePicture() {
        getElement().callFunction("takePicture");
    }

    public void showPreview() {
        getElement().callFunction("showPreview");
    }

    public void showPicture() {
        getElement().callFunction("showPicture");
    }

    public void showRecording() {
        getElement().callFunction("showRecording");
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
