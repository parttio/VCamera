package org.vaadin.pontus;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.server.StreamResource;

@Tag("video")
public class VideoComponent extends Component {
    
    public VideoComponent() {
        getElement().setAttribute("autoplay", "true");
        getElement().setAttribute("loop", "true");
        getElement().setAttribute("controls", "true");
    }

    public void setSrc(StreamResource resource) {
        getElement().setAttribute("src", resource);
    }
    
    public void setSrc(String resource) {
        getElement().setAttribute("src", resource);
    }
    
}
