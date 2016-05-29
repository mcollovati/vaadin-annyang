package org.vaadin.addon.annyang.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import org.vaadin.addon.annyang.Annyang;
import org.vaadin.addon.annyang.events.AnnyangEvent;
import org.vaadin.addon.annyang.events.AnnyangEvents;
import org.vaadin.addon.annyang.shared.AnnyangStatus;

import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

@Theme("demo")
@Title("MyComponent Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }

    private Layout buttons;
    private Label unsupportedLabel = new Label("It looks like your browser doesn't support speech recognition.");
    private BeanItemContainer<ResultItem> phrasesContainer = new BeanItemContainer<>(ResultItem.class);
    private Table phrases = new Table("", phrasesContainer);


    private Map<Locale, Consumer<Annyang>> commandsMap = new HashMap<>();
    private List<Annyang.Command> commands = new ArrayList<>();


    private void initCommandMap() {
        commandsMap.clear();
        commandsMap.put(Locale.ITALY, ann -> {
            ann.addCommand("prova", params -> Notification.show("Hai invocato un comando di prova"));
            ann.addCommand("saluta :nome :cognome", params -> Notification.show("Hai salutato " + params[0] + " " + params[1]));
        });
        commandsMap.put(Locale.US, ann -> {
            ann.addCommand("test", params -> Notification.show("You called test command"));
            ann.addCommand("say :nome :cognome", params -> Notification.show("You said hello to " + params[0] + " " + params[1]));
        });
    }

    @Override
    protected void init(VaadinRequest request) {

        initCommandMap();

        unsupportedLabel.setVisible(false);
        phrases.setVisibleColumns("phrase");
        phrases.setSizeFull();

        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();


        // Initialize our new UI component
        final Annyang annyang = Annyang.of(this);
        annyang.withDebug(true);
        //component.start();
        commandsMap.values().forEach(f -> f.accept(annyang) );
        //component.addCommand("test", uiRunner(() -> Notification.show("Test command")));

        layout.addComponents(unsupportedLabel);
        layout.addComponent(buttons = buttons(annyang));
        layout.addComponents(phrases);
        layout.setExpandRatio(phrases, 1);
        setContent(layout);
    }

    private HorizontalLayout buttons(Annyang annyang) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);

        Button start = new Button("Start", FontAwesome.PLAY);
        start.addClickListener(event -> annyang.start());

        Button pause = new Button("Pause", FontAwesome.PAUSE);
        pause.setEnabled(false);
        pause.addClickListener(event -> annyang.pause());

        Button resume = new Button("Resume", FontAwesome.REFRESH);
        resume.setEnabled(false);
        resume.addClickListener(event -> annyang.resume());

        Button stop = new Button("Stop", FontAwesome.STOP);
        stop.setEnabled(false);
        stop.addClickListener(event -> annyang.stop());

        List<Annyang.Callback> callbacks = Arrays.asList(
            annyang.addCallback((AnnyangEvents.UnsupportedListener) this::unsupported),
            annyang.addCallback((AnnyangEvents.StartListener) this::logEvent),
            annyang.addCallback((AnnyangEvents.EndListener) this::logEvent),
            annyang.addCallback((AnnyangEvents.ErrorListener) this::logEvent),
            annyang.addCallback((AnnyangEvents.NetworkErrorListener) this::logEvent),
            annyang.addCallback((AnnyangEvents.PermissionBlockedListener) this::logEvent),
            annyang.addCallback((AnnyangEvents.PermissionDeniedListener) this::logEvent),
            annyang.addCallback((AnnyangEvents.ResultNotMatchedListener) this::logResults),
            annyang.addCallback((AnnyangEvents.ResultMatchedListener) this::logMatchedResults)
        );

        Button removeCallbacks = new Button("Remove callbacks", FontAwesome.BALANCE_SCALE);
        removeCallbacks.addClickListener(event -> callbacks.forEach(annyang::removeCallback));

        ComboBox lang = new ComboBox("Choose language", new BeanItemContainer<Locale>(
            Locale.class, commandsMap.keySet()));
        lang.setNullSelectionAllowed(false);
        lang.select(Locale.US);
        lang.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        lang.setItemCaptionPropertyId("displayLanguage");
        lang.addValueChangeListener(event -> {
            annyang.withLocale((Locale) lang.getValue());
        });

        annyang.addStatusChangeListener(event -> {
            start.setEnabled(event.getNewStatus() == AnnyangStatus.STOPPED);
            pause.setEnabled(event.getNewStatus() == AnnyangStatus.STARTED);
            resume.setEnabled(event.getNewStatus() == AnnyangStatus.PAUSED);
            stop.setEnabled(event.getNewStatus() == AnnyangStatus.STARTED);
        });
        layout.addComponents(start, pause, resume, stop, removeCallbacks, lang);

        return layout;
    }


    private void unsupported(AnnyangEvents.UnsupportedEvent event) {
        logEvent(event);
        buttons.setEnabled(false);
        unsupportedLabel.setVisible(true);
        phrases.setEnabled(false);
    }

    private void logResults(AnnyangEvents.ResultEvent event) {
        logEvent(event);
        System.out.println("Event:: " + event);
        phrases.setCaption("No match");
        phrasesContainer.removeAllItems();
        phrasesContainer.addAll(ResultItem.of(event.getPhrases()));
    }

    private void logMatchedResults(AnnyangEvents.ResultMatchedEvent event) {
        logResults(event);
        phrases.setCaptionAsHtml(true);
        phrases.setCaption("<h1>Command " + event.getCommandName() + "<h1><h3>" + event.getPhrase() + "<h3>");

    }

    private void logEvent(AnnyangEvent event) {
        System.out.println("Got event " + event.getClass().getName());
    }
}
