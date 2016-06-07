package org.vaadin.addon.annyang.demo.components;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * Created by marco on 06/06/16.
 */
public class WidgetList extends CustomComponent implements ComponentHelper<WidgetList> {

    private final MVerticalLayout menuItems;

    public WidgetList() {
        setStyleName(ValoTheme.MENU_ROOT);
        setCompositionRoot(menuItems = new MVerticalLayout(
                new MLabel("Widgets").withStyleName(ValoTheme.MENU_TITLE)
            /*category("Trac", FontAwesome.BUG),
            command("Timeline"), command("Issues"),
            category("Review", FontAwesome.CHECK),
            command("Open changes"), command("Merged changes"),
            category("Twitter", FontAwesome.TWITTER),
            command("@vaadin"), command("@vaadindirectory"),
            category("Forum", FontAwesome.COMMENTS_O),
            command("recent"),
            category("Voice commands", FontAwesome.MICROPHONE),
            command("Help"), command("history")*/
            ).withStyleName(ValoTheme.MENU_PART)
                .withSpacing(false).withMargin(false)
                .withFullWidth().withFullHeight()
                .expand(new CssLayout())
        );
    }

    public WidgetList category(String caption, FontAwesome icon) {
        menuItems.addComponent(makeCategory(caption, icon), menuItems.getComponentCount() - 1);
        return this;
    }
    public WidgetList command(String caption, Runnable action) {
        menuItems.addComponent(makeCommand(caption).addClickListener(action::run), menuItems.getComponentCount() - 1);
        return this;
    }


    private MLabel makeCategory(String caption, FontAwesome icon) {
        return new MLabel(String.format("%s %s", icon.getHtml(), caption))
            .withContentMode(ContentMode.HTML).withStyleName(ValoTheme.MENU_SUBTITLE);
    }

    private MButton makeCommand(String caption) {
        MButton mButton = new MButton(caption);
        mButton.setPrimaryStyleName(ValoTheme.MENU_ITEM);
        return mButton;
    }

}
