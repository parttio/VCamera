# VCamera

Vaadin 10 (upgraded to Vaadin 14) component for taking pictures and recording short videos. The component is based on HTML5 standards. It uses `navigator.mediaDevices.getUserMedia` to get a `MediaStream` object containing a video and/or audio stream. A `MediaRecorder` object is used to record video. The `video` HTML5-element is used to show videos. Images are created from the media stream using a canvas. 

Recorded videos and pictures are saved directly to the server. A receiver class implementing the `DataReceiver` interface can be used on the server side to provide a `OutputStream` that saves the pictures and videos in a desired way. 

`navigator.mediaDevices.getUserMedia` is supported in most modern browsers. `MediaRecorder` is supported only in Chrome and Firefox. See [caniuse.com](https://caniuse.com) for more details and more up to date information.  The device also obviously needs a camera in order for the component to work.

## Development instructions

Starting the test/demo server:
```
mvn jetty:run
```

This deploys demo at http://localhost:8080


