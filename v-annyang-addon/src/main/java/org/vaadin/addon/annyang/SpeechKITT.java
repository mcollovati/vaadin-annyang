/**
 * Copyright (C) 2016 Marco Collovati (mcollovati@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.addon.annyang;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import org.vaadin.addon.annyang.shared.SpeechKITTState;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by marco on 30/05/16.
 */
@JavaScript({"speechkitt.min.js", "speechkitt_connector.js"})
public class SpeechKITT extends AbstractJavaScriptExtension {

    //private static final String SPEECHKITT_VERSION = "0.3.0";

    SpeechKITT(Annyang annyang) {
        super(annyang.getUI());
    }

    static SpeechKITT of(Annyang annyang) {
        return ExtensionUtil.from(annyang.getUI(), SpeechKITT.class)
            .orElse(new SpeechKITT(annyang)).withFlatTheme(FlatTheme.DEFAULT);
    }

    public SpeechKITT withStylesheet(Resource css) {
        setResource("css", css);
        return this;
    }

    public SpeechKITT withFlatTheme(FlatTheme theme) {
        //return withStylesheet(cdnTheme(theme, SPEECHKITT_VERSION));
        return withStylesheet(resourceTheme(theme));
    }

    public SpeechKITT hide() {
        callFunction("hide");
        getState(false).visible = false;
        return this;
    }

    public SpeechKITT show() {
        callFunction("show");
        getState(false).visible = true;
        return this;
    }

    public SpeechKITT toggleVisibility() {
        if (isVisible()) {
            hide();
        } else {
            show();
        }
        return this;
    }

    public SpeechKITT withInstructionsText(String instructionsText) {
        getState().instructionsText = instructionsText;
        return this;
    }

    public SpeechKITT withToggleLabelText(String toggleLabelText) {
        getState().toggleLabelText = toggleLabelText;
        return this;
    }

    public SpeechKITT withSampleCommands(String... commands) {
        getState().sampleCommands.clear();
        return addSampleCommands(commands);
    }

    public SpeechKITT addSampleCommands(String... commands) {
        getState().sampleCommands.addAll(Arrays.asList(commands));
        return this;
    }

    public SpeechKITT withRememberStatusInterval(int minutes) {
        if (minutes < 0) {
            getState().rememberStatusInterval = 0;
        } else {
            getState().rememberStatusInterval = minutes;
        }
        return this;
    }

    public String getInstructionsText() {
        return getState(false).instructionsText;
    }

    public String getToggleLabelText() {
        return getState(false).toggleLabelText;
    }

    public Set<String> getSampleCommands() {
        return new HashSet<>(getState(false).sampleCommands);
    }

    public int getRemeberStatusInterval() {
        return getState(false).rememberStatusInterval;
    }

    public boolean isVisible() {
        return getState(false).visible;
    }


    @Override
    protected SpeechKITTState getState() {
        return (SpeechKITTState) super.getState();
    }

    @Override
    protected SpeechKITTState getState(boolean markAsDirty) {
        return (SpeechKITTState) super.getState(markAsDirty);
    }

    /*
    private Resource cdnTheme(FlatTheme theme, String version) {
        return new ExternalResource(String.format("//cdnjs.cloudflare.com/ajax/libs/SpeechKITT/%s/themes/%s.css",
            version, theme.toCssName()));
    }
    */
    private Resource resourceTheme(FlatTheme theme) {
        return new ClassResource(SpeechKITT.class, String.format("speechkitt-themes/%s.css", theme.toCssName()));
    }

    public enum FlatTheme {
        DEFAULT("flat"), AMETHYST, CLOUDS, CONCRETE, EMERALD,
        MIDNIGTH_BLUE("flat-midnight-blue"), ORANGE, POMEGRANATE,
        PUMPKIN, TURQUOISE;

        private final String css;

        FlatTheme() {
            this.css = "flat-" + name().toLowerCase();
        }

        FlatTheme(String css) {
            this.css = css;
        }

        private String toCssName() {
            return css;
        }
    }

}
