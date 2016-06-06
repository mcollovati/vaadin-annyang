package org.vaadin.addon.annyang.demo;

import com.vaadin.ui.Component;

import java.util.Arrays;

/**
 * Created by marco on 06/06/16.
 */
@SuppressWarnings("unchecked")
public interface ComponentHelper<T extends Component & ComponentHelper<T>> extends Component {

    default T withWidth(String width) {
        setWidth(width);
        return (T)this;
    }
    default T withFullWidth() {
        return withWidth("100%");
    }
    default T withHeight(String height) {
        setHeight(height);
        return (T)this;
    }
    default T withFullHeight() {
        return withHeight("100%");
    }
    default T withFullSize() {
        return withFullWidth().withFullHeight();
    }

    default T withStyleName(String... styleName) {
        Arrays.stream(styleName).forEach(this::addStyleName);
        return (T)this;
    }
}
