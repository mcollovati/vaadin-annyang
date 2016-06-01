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
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.server.KeyMapper;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.UI;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.vaadin.addon.annyang.events.AnnyangCallbackType;
import org.vaadin.addon.annyang.events.AnnyangEvents;
import org.vaadin.addon.annyang.events.AnnyangListener;
import org.vaadin.addon.annyang.shared.AnnyangState;
import org.vaadin.addon.annyang.shared.AnnyangStatus;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@JavaScript({
    "annyang.min.js",
    "annyang-connector.js"
})
public class Annyang extends AbstractJavaScriptExtension {

    private final KeyMapper<String> commandsMapper = new KeyMapper<String>() {
        @Override
        public String key(String o) {
            return "command_" + super.key(o);
        }
    };
    private final KeyMapper<AnnyangCallbackType> callbacksMapper = new KeyMapper<AnnyangCallbackType>() {
        @Override
        public String key(AnnyangCallbackType o) {
            return String.format("_on_%s_%s", o.getCallbackName(), super.key(o));
        }
    };
    private Locale locale = Locale.US;

    private Annyang(UI ui) {
        super(ui);
        addFunction("fireStatusChanged", arguments -> {
            AnnyangEvents.StatusChangedEvent event = new AnnyangEvents.StatusChangedEvent(Annyang.this.getUI(),
                Annyang.this.status(), AnnyangStatus.valueOf(arguments.getString(0).toUpperCase()));
            getState(false).status = event.getNewStatus();
            if (event.getNewStatus() != event.getOldStatus()) {
                Annyang.this.fireEvent(event);
            }
        });
    }

    public static Optional<Annyang> current() {
        return Optional.ofNullable(UI.getCurrent())
            .map(Annyang::of);
    }

    public static Annyang start(UI ui) {
        Annyang annyang = Annyang.of(ui);
        return annyang;
    }

    public static Annyang of(UI ui) {
        Objects.requireNonNull(ui);
        return ExtensionUtil.from(ui, Annyang.class)
            .orElseGet(() -> new Annyang(ui));
    }

    public Annyang withLocale(Locale locale) {
        this.locale = locale;
        getState().lang = locale.toLanguageTag();
        callFunction("setLanguage");
        return this;
    }

    public Locale getLocale() {
        return locale;
    }

    @SuppressWarnings("unchecked")
    public <T extends AnnyangListener> Callback addCallback(T listener) {
        Callback callback = AnnyangEvents.callabackFromListener(Objects.requireNonNull(listener))
            .map(cbt -> new Callback(cbt, callbacksMapper.key(cbt)))
            .orElseThrow(() -> new IllegalArgumentException("Invalid listener implementation: " + listener));

        addFunction(callback.callbackName, arguments -> {
            listener.onEvent(AnnyangEvents.eventMapper(callback, arguments).apply(Annyang.this));
        });
        callFunction("addCallback", callback.callbackType.getCallbackName(), callback.callbackName);
        // Force state change to early fire unsupported events
        if (callback.callbackType == AnnyangCallbackType.UNSUPPORTED) {
            markAsDirty();
        }
        return callback;
    }

    public void removeCallback(Callback callback) {
        callFunction("removeCallback", callback.callbackType.getCallbackName(), callback.callbackName);
    }

    public Annyang addStatusChangeListener(AnnyangEvents.StatusChangeListener listener) {
        addListener(AnnyangEvents.StatusChangedEvent.class, listener, AnnyangEvents.StatusChangeListener.eventMethod);
        return this;
    }

    public void removeStatusChangeListener(AnnyangEvents.ErrorListener listener) {
        removeListener(AnnyangEvents.StatusChangedEvent.class, listener);
    }

    public SpeechKITT withSpeechKitt() {
        return SpeechKITT.of(this);
    }

    public AnnyangStatus status() {
        return getState(false).status;
    }

    public void start() {
        callFunction("start");
    }

    public void pause() {
        callFunction("pause");
    }

    public void stop() {
        callFunction("abort");
    }

    public void resume() {
        callFunction("resume");
    }

    public Annyang withDebug(boolean enableDebug) {
        callFunction("debug", enableDebug);
        getState().debug = enableDebug;
        return this;
    }

    public boolean isDebug() {
        return getState(false).debug;
    }

    public boolean isListening() {
        return getState(false).listening;
    }

    public Annyang withAutorestart(boolean autorestart) {
        getState().autoRestart = autorestart;
        return this;
    }

    public Command addCommand(String phrase, CommandCallback callback) {
        Command command = new Command(commandsMapper.key(phrase), phrase);
        addFunction(command.name, (JavaScriptFunction) arguments -> callback.run(AnnyangEvents.toStringArray(arguments)));
        callFunction("addCommand", command.phrase, command.name);
        return command;
    }

    private void trigger(String sentence) {
        callFunction("trigger", sentence);
    }

    private void addCommandShortcutListener(CommandShortcutListener listener) {
        Optional.ofNullable(listener).ifPresent(getUI()::addAction);
    }

    private void removeCommandShortcutListener(CommandShortcutListener listener) {
        Optional.ofNullable(listener).ifPresent(getUI()::removeAction);
    }

    public void removeCommand(Command command) {
        callFunction("removeCommand", command.phrase, command.name);
        removeCommandShortcutListener(command.shortcutListener);
    }

    @Override
    protected AnnyangState getState() {
        return (AnnyangState) super.getState();
    }

    @Override
    protected AnnyangState getState(boolean markAsDirty) {
        return (AnnyangState) super.getState(markAsDirty);
    }

    @FunctionalInterface
    public interface CommandCallback extends Serializable {
        void run(String... params);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static class Callback {
        @Getter
        private final AnnyangCallbackType callbackType;
        private final String callbackName;
    }

    public static class Command {
        private final String name;
        private final String phrase;
        private CommandShortcutListener shortcutListener;

        private Command(String name, String phrase) {
            this.phrase = phrase;
            this.name = name;
        }

        public String getPhrase() {
            return phrase;
        }

        public Command withShortcut(String shorthandCaption) {
            return withShortcutListener(new CommandShortcutListener(this, shorthandCaption));
        }

        public Command withShortcut(int keyCode, int... modifiers) {
            return withShortcutListener(new CommandShortcutListener(this, keyCode, modifiers));
        }

        private Command withShortcutListener(CommandShortcutListener listener) {
            CommandShortcutListener oldListener = this.shortcutListener;
            shortcutListener = listener;
            Annyang.current().ifPresent(a -> {
                a.removeCommandShortcutListener(oldListener);
                a.addCommandShortcutListener(shortcutListener);
            });
            return this;
        }

        protected void trigger() {
            Annyang.current().ifPresent(a -> a.trigger(phrase));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Command command1 = (Command) o;
            return Objects.equals(name, command1.name) &&
                Objects.equals(phrase, command1.phrase);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, phrase);
        }

    }

    private static class CommandShortcutListener extends ShortcutListener {

        private final Command command;

        private CommandShortcutListener(Command command, String shorthandCaption) {
            super(shorthandCaption);
            this.command = command;
        }

        private CommandShortcutListener(Command command, int keyCode, int... modifierKeys) {
            super("", keyCode, modifierKeys);
            this.command = command;
        }

        @Override
        public void handleAction(Object sender, Object target) {
            command.trigger();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CommandShortcutListener that = (CommandShortcutListener) o;
            return Objects.equals(command, that.command);
        }

        @Override
        public int hashCode() {
            return Objects.hash(command);
        }
    }
}
