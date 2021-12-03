package org.vaadin.vcamera;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;


public class VCameraDemo extends Composite<HorizontalLayout>
        implements RouterLayout {

    private final Div contentArea;

    public VCameraDemo() {

        VerticalLayout menu = new VerticalLayout();
        RouterLink vcamera = new RouterLink("Camera", VCameraDemoView.class);
        RouterLink surveillance = new RouterLink("Surveillance",
                SurveillanceCameraView.class);
        RouterLink viewer = new RouterLink("Viewer", VideoViewer.class);
        menu.add(vcamera, surveillance, viewer);
        menu.setWidth("100px");
        contentArea = new Div();
        getContent().add(menu, contentArea);
        getContent().setFlexGrow(1, contentArea);
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        contentArea.removeAll();
        contentArea.getElement().appendChild(content.getElement());
    }
}
