package org.vaadin.addon.annyang.demo.components;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;

/**
 * Created by marco on 02/06/16.
 */
@JavaScript({"https://platform.twitter.com/widgets.js", "twitter_timeline_connector.js"})
public class TwitterTimeline extends AbstractJavaScriptComponent implements ComponentHelper<TwitterTimeline> {

    /*
                <a class="twitter-timeline"  href="https://twitter.com/vaadin" data-widget-id="738372609797173249">Tweet di @vaadin</a>
            <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
     */

    public enum Type {
        Profile, Likes, Collections, Lists, Urls, Widget
    }

    public TwitterTimeline(Type type) {
        getState().sourceType = type.toString().toLowerCase();
    }

    public TwitterTimeline widget(String widgetId) {
        getState().widgetId = widgetId;
        return this;
    }
    public TwitterTimeline screenName(String screenName) {
        getState().screenName = screenName;
        return this;
    }



    @Override
    protected TwitterState getState() {
        return (TwitterState)super.getState();
    }

    @Override
    protected TwitterState getState(boolean markAsDirty) {
        return (TwitterState)super.getState(markAsDirty);
    }
}
