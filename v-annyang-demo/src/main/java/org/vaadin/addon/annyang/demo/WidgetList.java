package org.vaadin.addon.annyang.demo;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.fields.MTextArea;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * Created by marco on 06/06/16.
 */
public class WidgetList extends CustomComponent implements ComponentHelper<WidgetList> {

    public WidgetList() {
        setStyleName(ValoTheme.MENU_ROOT);
        setCompositionRoot(new MVerticalLayout(
            new MLabel("Widgets").withStyleName(ValoTheme.MENU_TITLE),

            category("Trac", FontAwesome.BUG),
            command("Timeline"), command("Issues"),
            category("Review", FontAwesome.CHECK),
            command("Open changes"), command("Merged changes"),
            category("Twitter", FontAwesome.TWITTER),
            command("@vaadin"), command("@vaadindirectory"),
            category("Forum", FontAwesome.COMMENTS_O),
            command("recent"),
            category("Voice commands", FontAwesome.MICROPHONE),
            command("Help"), command("history")
        ).withStyleName(ValoTheme.MENU_PART)
            .withSpacing(false).withMargin(false)
            .withFullWidth().withFullHeight()
            .expand(new CssLayout())
        );
    }

    private MLabel category(String caption, FontAwesome icon) {
        return new MLabel(String.format("%s %s", icon.getHtml(), caption))
            .withContentMode(ContentMode.HTML).withStyleName(ValoTheme.MENU_SUBTITLE);
    }

    private MLabel command(String command) {
        return new MLabel(command).withStyleName(ValoTheme.MENU_ITEM);
    }

}
