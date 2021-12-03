import { PolymerElement, html } from '@polymer/polymer/polymer-element.js';

class VCameraElement extends PolymerElement {

    static get template() {
        return html`<div id=content></div>
        <canvas id=canvas hidden></canvas>
        `;
    }
    // noinspection JSUnusedGlobalSymbols
    static get is() { return 'vcamera-element'; }
    static get properties() {
        return {
            previewOptions: {
                type:Object,
                value: null
            },
            recordingOptions: {
                type:Object,
                value: null
            },
            imageData: {
                type: Object,
                value:null
            },
            recordingData: {
                type: Object,
                value: null
            },
            target: {
                type:String,
                value:""
            }
        }
    }

    _clearContents() {
        let el = this.$.content;
        while (el.hasChildNodes()) {
            el.removeChild(el.lastChild);
        }
    }

    _createVideo() {
        this._clearContents();
        let vid = document.createElement("video");
        vid.id="video";
        let el = this.$.content;
        el.appendChild(vid);
        return vid;
    }

    _createImage() {
        this._clearContents();
        let img = document.createElement("img");
        img.id="img";
        let el = this.$.content;
        el.appendChild(img);
        return img;
    }

    takePicture() {
        let vid = this.shadowRoot.getElementById("video");
        if (vid == null) {
            return;
        }
        let canvas =  this.$.canvas;
        let context = canvas.getContext('2d');
        canvas.height = vid.videoHeight;
        canvas.width = vid.videoWidth;
        context.drawImage(vid, 0, 0, vid.videoWidth, vid.videoHeight);
        let blob = this.$.canvas.toBlob(b => {
            this.imageData = b;
            this.showPicture();
            this.saveToServer(b);
        },'image/jpeg',0.95)

    }

    stopCamera() {
        this._stopStream(this.stream);
    }

    _stopStream(stream) {
        if(stream!=null) {
            stream.getTracks().forEach( t=> {
                t.stop();
            });
        }
    }

    saveToServer(data) {
        let formData = new FormData();
        formData.append("data",data);
        fetch(this.target, {
            method: "post",
            body: formData
        }).then(response => console.log(response));
    }

    startRecording() {
        if(navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
            navigator.mediaDevices.getUserMedia(this.recordingOptions).then(stream => {
                this.recorder = new MediaRecorder(stream);
                this.recorder.ondataavailable = e => {
                    this.recordingData = e.data;
                    let video = this._createVideo();
                    let url = window.URL.createObjectURL(this.recordingData);
                    video.src = url;
                    video.setAttribute("controls","controls");
                    this.saveToServer(e.data);
                    this._stopStream(stream);
                }
                this.recorder.start();
            });
        }
    }

    stopRecording() {
        if(this.recorder!=null) {
            this.recorder.stop();
        }
    }

    showPreview() {
        let video = this._createVideo();
        if(this.stream!=null) {
            this._stopStream(this.stream);
        }
        if(navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
            navigator.mediaDevices.getUserMedia(this.previewOptions).then(stream => {
                this.stream = stream;
                video.srcObject = this.stream;
                video.play();
            });
        }
    }

    showPicture() {
        let img = this._createImage();
        img.src = URL.createObjectURL(this.imageData);
    }

    showRecording() {
        if(this.recordingData!=null) {
            let video = this._createVideo();
            video.srcObject = this.recordingData;
        }
    }

}

customElements.define(VCameraElement.is, VCameraElement);
