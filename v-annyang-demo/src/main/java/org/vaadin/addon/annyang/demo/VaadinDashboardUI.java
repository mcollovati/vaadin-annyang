package org.vaadin.addon.annyang.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import org.vaadin.addon.annyang.Annyang;
import org.vaadin.addon.annyang.demo.components.TwitterTimeline;
import org.vaadin.addon.annyang.demo.components.WidgetList;
import org.vaadin.addon.annyang.events.AnnyangEvent;
import org.vaadin.addon.annyang.events.AnnyangEvents;
import org.vaadin.addon.annyang.shared.AnnyangStatus;
import org.vaadin.alump.masonry.MasonryDnDWrapper;
import org.vaadin.alump.masonry.MasonryLayout;
import org.vaadin.sliderpanel.SliderPanel;
import org.vaadin.sliderpanel.SliderPanelBuilder;
import org.vaadin.sliderpanel.SliderPanelStyles;
import org.vaadin.sliderpanel.client.SliderMode;
import org.vaadin.sliderpanel.client.SliderTabPosition;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.servlet.annotation.WebServlet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by marco on 06/06/16.
 */
@Theme("demo")
@Title("Vaadin Voice Dashboard")
public class VaadinDashboardUI extends UI {

    private final MLabel status = new MLabel().withFullWidth().withStyleName("annyang-status");

    private final Map<String, Component> widgets = new LinkedHashMap<>();
    MasonryLayout contentContainer;

    @Override
    protected void init(VaadinRequest request) {

        Annyang annyang = Annyang.of(this).withAutorestart(true).withDebug(true);
        annyang.withSpeechKitt()
            .addSampleCommands("widgets", "show <widget>", "hide <widget>", "toggle <widget>");

        initWidgets(annyang);
        initVoiceCommands(annyang);

        SliderPanel rightPanel = new SliderPanelBuilder(widgetList(annyang), "Widgets")
            .autoCollapseSlider(true)
            .flowInContent(true)
            .mode(SliderMode.RIGHT).tabPosition(SliderTabPosition.MIDDLE)
            .style(SliderPanelStyles.COLOR_BLUE)
            .build();

        contentContainer = content();
        MVerticalLayout contentLayout = new MVerticalLayout(status, contentContainer)
            .withFullHeight().withFullWidth()
            .withMargin(false).withSpacing(false)
            .withExpand(contentContainer, 1);


        annyang.addStatusChangeListener(onAnnyangStatusChanged(rightPanel));
        annyang.addCommand("widgets", params -> rightPanel.toogle())
            .withShortcut(ShortcutAction.KeyCode.W, ShortcutAction.ModifierKey.ALT);
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

    private void initWidgets(Annyang annyang) {
        widgets.put("timeline", new TwitterTimeline(TwitterTimeline.Type.Profile).
            screenName("vaadin")
            //timeline("738372609797173249", 300, 300)
            .withHeight("300px").withWidth("300px"));
        //widgets.put("timeline", new TwitterTimeline().timeline("738372609797173249", 300, 300));
        //widgets.put("issues", new TwitterTimeline().timeline("738372609797173249", 300, 300));

    }

    private void initVoiceCommands(Annyang annyang) {
        annyang.addCommand("show :widget",
            params -> Optional.ofNullable(widgets.get(params[0]))
                .filter(c -> !c.isAttached())
                .ifPresent(contentContainer::addComponent));
        annyang.addCommand("hide :widget",
            params -> Optional.ofNullable(widgets.get(params[0]))
                .filter(Component::isAttached)
                .ifPresent(contentContainer::removeComponent));
        annyang.addCommand("toggle :widget",
            params -> Optional.ofNullable(widgets.get(params[0]))
                .ifPresent(c -> {
                    if (contentContainer.getComponentIndex(c) < 0) {
                        contentContainer.addComponent(c);
                    } else {
                        contentContainer.removeComponent(c);
                    }
                }));
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

    private MasonryLayout content() {
        MasonryLayout layout = new MasonryLayout();
        //layout.setReorderable(true);
        //layout.setComponentDragStartMode(DragAndDropWrapper.DragStartMode.WRAPPER);
        //layout.addStyleNameToLayout(MasonryLayout.MASONRY_PAPER_SHADOW_STYLENAME);
        // TODO save order and visibility in cookie
        // layout.addMasonryReorderListener(event -> {});
        layout.setAutomaticLayoutWhenImagesLoaded(true);
        layout.setSizeFull();
        widgets.values().stream().forEach(c -> layout.addComponent(c, MasonryLayout.MASONRY_PAPER_SHADOW_STYLENAME));
        layout.addComponent(new Label("ciao"));
        return layout;
        /*
        return new MVerticalLayout(new Label("Content"))
            .add(widgets.values().stream()
                .map(c -> new MCssLayout(c).withStyleName("widget-container"))
                .collect(Collectors.toList()))
            .withFullHeight().withFullWidth();
            */
    }

    private Component widgetList(Annyang annyang) {
        return new WidgetList().withWidth("400px").withFullHeight()
            .category("Trac", FontAwesome.BUG)
            .command("Timeline", createWidgetCommand(annyang, "timeline"))
            .command("Issues", createWidgetCommand(annyang, "issues"))/*,
            category("Review", FontAwesome.CHECK),
            command("Open changes"), command("Merged changes"),
            category("Twitter", FontAwesome.TWITTER),
            command("@vaadin"), command("@vaadindirectory"),
            category("Forum", FontAwesome.COMMENTS_O),
            command("recent"),
            category("Voice commands", FontAwesome.MICROPHONE),
            command("Help"), command("history")*/

            ;
    }

    private Runnable createWidgetCommand(Annyang annyang, String widgetName) {
        return () -> annyang.trigger("toggle " + widgetName.toLowerCase());
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
