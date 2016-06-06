package org.vaadin.addon.annyang.demo;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;

/**
 * Created by marco on 02/06/16.
 */
@JavaScript({"https://platform.twitter.com/widgets.js", "twitter_connector.js"})
public class Twitter extends AbstractJavaScriptComponent {

    /*
                <a class="twitter-timeline"  href="https://twitter.com/vaadin" data-widget-id="738372609797173249">Tweet di @vaadin</a>
            <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>

     */

    public Twitter addTimeline(String id, int width, int height) {
        callFunction("createTimeline", id, width, height);
        return this;
    }
}
