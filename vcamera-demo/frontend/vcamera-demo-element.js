import { PolymerElement, html } from '@polymer/polymer/polymer-element.js';
//import {html} from "@polymer/polymer/lib/utils/html-tag";

import '@vaadin/vaadin-button/src/vaadin-button.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-vertical-layout.js';
import '@vaadin/vaadin-ordered-layout/src/vaadin-horizontal-layout.js';
import '@vaadin/vcamera-element/vcamera-element.js';

//<dom-module id="vcamera-demo-element">
//	<template>
static get template() {
    return html'
		<vaadin-vertical-layout>
			<div>
				<h1>VCamera component demo</h1> 
				<p>This demo shows a small demo of VCamera Vaadin Flow-component. The buttons below the camera component 
				are used to control the components. If a picture is taken or a video recorded, the image or video saved to the server is shown below the buttons. </p>
			</div>
			<vcamera-element id="camera"></vcamera-element>
			<vaadin-horizontal-layout theme="spacing">
				<vaadin-button id="snap">Take picture</vaadin-button>
				<vaadin-button id="preview">Preview</vaadin-button>
				<vaadin-button id="start">Start recording</vaadin-button>
				<vaadin-button id="stop">Stop recording</vaadin-button>
				<vaadin-button id="stopcamera">Stop camera</vaadin-button>
			</vaadin-horizontal-layout>
			<div id="image"></div>
			<div id="video"></div>
		</vaadin-vertical-layout>';
}
//	</template>
//	<script>
		class VCameraDemoElement extends PolymerElement {
			static get is() {return "vcamera-demo-element";}
		}
		customElements.define(VCameraDemoElement.is, VCameraDemoElement);
//	</script>
//</dom-module>
