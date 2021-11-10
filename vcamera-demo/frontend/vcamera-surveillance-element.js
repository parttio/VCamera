import { PolymerElement, html } from '@polymer/polymer/polymer-element.js';
//import {html} from "@polymer/polymer/lib/utils/html-tag";

import '@vaadin/vaadin-button/src/vaadin-button.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/vcamera-element/vcamera-element.js';

//<dom-module id="vcamera-surveillance-element">
//	<template>
static get template {
    return html'
		<vaadin-vertical-layout>
			<div>
			<div><h1>A simple monitor application</h1>
			<p>This view contains a simple toy baby or pet monitor. 
				The camera is in this case sound activated. 
				The sensitivity can be adjusted with the slider. This view is connected to the "Viewer"-view. Use the "Sharing key" shown below in the viewer on another device. Every time a sound is detected, a short video is recorded and pushed to viewers with the same key. </p>
			</div>
			<vcamera-element id="camera"></vcamera-element>
			<vaadin-horizontal-layout theme="spacing">
				<vaadin-button on-click="_activate">Activate</vaadin-button>
				<vaadin-button on-click="_deactivate">Deactivate</vaadin-button>
				<input value="{{threshold::input}}" type="range" name="Sensitivity" min="0" max="4"></input>
			</vaadin-horizontal-layout>
			<vaadin-horizontal-layout theme="spacing"><div>Sharing key: </div><div id="key"></div></vaadin-horizontal-layout>
		</vaadin-vertical-layout>';
}
//	</template>
//	<script>
		class  VCameraSurveillanceElement extends PolymerElement {
			static get is() {return "vcamera-surveillance-element";}
    		static get properties() {
    			return {
    				threshold: {
    					type:Number,
    					value:2
    				}	
    			};
    		
    		}
    		
    		_activate() {
    			let audioCtx = new window.AudioContext();
    			let analyser = audioCtx.createAnalyser();
    			analyser.fftSize = 2048;
    			let dataArray = new  Float32Array(analyser.fftSize);
    			navigator.getUserMedia (
 					{
    					audio: true
  					},
  					stream => {
    					let source = audioCtx.createMediaStreamSource(stream);
    					source.connect(analyser);
    					this.analysing = true;
    					window.setTimeout(t => {
    						this._analyse(analyser, dataArray);
    					}, 1000);

  					},
  					err => {
    					console.log('The following error occured: ' + err);
 					});
    		}
    		
    		_deactivate() {
    			this.analysing = false;
    		}
    		
    		_analyse(analyser, dataArray) {
    			if(!this.analysing) {
    				return;
    			}
    			analyser.getFloatTimeDomainData(dataArray);
    			let threshold = (1e-3)/Math.pow(10,this.threshold);
    			let p = 0;
    			for(let i=0;i<dataArray.length;i++) {
    				p+=(dataArray[i]*dataArray[i]);
    			}
    			console.log("The average power^2 of the signal was: " + p/dataArray.length + " threshold is " + threshold);
    			if(p/dataArray.length>=threshold) {
    				this.$.camera.startRecording();
    				this.analysing = false;
    				window.setTimeout(t=> {
    					this.$.camera.stopRecording();
    					this.analysing = true;
    					this._analyse(analyser, dataArray);	
    				}, 5000)
    			}
    			window.setTimeout(t => {
    				if(this.analysing) {
    					this._analyse(analyser, dataArray);	
    				}	
    			}, 1000);
    		}
		
		}
		customElements.define(VCameraSurveillanceElement.is, VCameraSurveillanceElement);
//	</script>
//</dom-module>
