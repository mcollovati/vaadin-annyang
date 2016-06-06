package org.vaadin.addon.annyang.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.addon.annyang.Annyang;
import org.vaadin.addon.annyang.events.AnnyangEvent;
import org.vaadin.addon.annyang.events.AnnyangEvents;
import org.vaadin.addon.annyang.shared.AnnyangStatus;
import org.vaadin.sliderpanel.SliderPanel;
import org.vaadin.sliderpanel.SliderPanelBuilder;
import org.vaadin.sliderpanel.SliderPanelStyles;
import org.vaadin.sliderpanel.client.SliderMode;
import org.vaadin.sliderpanel.client.SliderTabPosition;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.servlet.annotation.WebServlet;

/**
 * Created by marco on 06/06/16.
 */
@Theme("demo")
@Title("Vaadin Voice Dashboard")
public class VaadinDashboardUI extends UI {

    private final MLabel status = new MLabel().withFullWidth().withStyleName("annyang-status");

    @Override
    protected void init(VaadinRequest request) {

        SliderPanel rightPanel = new SliderPanelBuilder(widgetList(), "Widgets")
            .autoCollapseSlider(true)
            .flowInContent(true)
            .mode(SliderMode.RIGHT).tabPosition(SliderTabPosition.MIDDLE)
            .style(SliderPanelStyles.COLOR_BLUE)
            .build();

        Component content = content();
        MVerticalLayout contentLayout = new MVerticalLayout(status, content)
            .withFullHeight().withFullWidth()
            .withMargin(false).withSpacing(false)
            .withExpand(content, 1);

        Annyang annyang = Annyang.of(this).withAutorestart(true)
            .withDebug(true)
            .addStatusChangeListener(onAnnyangStatusChanged(rightPanel));
        annyang.withSpeechKitt()
            .addSampleCommands("widgets", "show <widget name>", "hide <widget name>");
        annyang.addCommand("widgets", params -> rightPanel.toogle());
        annyang.addCallback((AnnyangEvents.PermissionBlockedListener) this::permissionError);
        annyang.addCallback((AnnyangEvents.PermissionDeniedListener) this::permissionError);
        annyang.start();

        MHorizontalLayout mainLayout = new MHorizontalLayout(contentLayout, rightPanel)
            .withStyleName("dashboard")
            .withSpacing(false).withMargin(false)
            .withFullHeight().withFullWidth()
            .expand(contentLayout);


        setContent(mainLayout);
    }

    private void permissionError(AnnyangEvent event) {
        status.withStyleName("warning")
            .withContent("Speech recognition is blocked or denied");
    }

    @Override
    public void attach() {
        super.attach();
        Annyang.current().ifPresent(Annyang::start);
    }

    @Override
    public void detach() {
        Annyang.current().ifPresent(Annyang::stop);
        super.detach();
    }

    private Component content() {
        return new MVerticalLayout(new Label("Content"))
            .withFullHeight().withFullWidth();
    }

    private Component widgetList() {
        return new WidgetList().withWidth("400px").withFullHeight();
    }

    private AnnyangEvents.StatusChangeListener onAnnyangStatusChanged(SliderPanel sliderPanel) {
        return event -> {
            if (event.getNewStatus() == AnnyangStatus.UNSUPPORTED) {
                sliderPanel.removeStyleName(SliderPanelStyles.COLOR_BLUE);
                sliderPanel.addStyleName(SliderPanelStyles.COLOR_RED);
                sliderPanel.expand();
                status.withStyleName("error")
                    .withContent("It looks like your browser doesn't support speech recognition.");
            }
        };
    }

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = VaadinDashboardUI.class, widgetset = "org.vaadin.addon.annyang.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

}
