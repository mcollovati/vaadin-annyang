/**
 * Copyright (C) 2016 Marco Collovati (mcollovati@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.addon.annyang.events;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;
import elemental.json.JsonArray;
import lombok.Getter;
import lombok.ToString;
import org.vaadin.addon.annyang.Annyang;
import org.vaadin.addon.annyang.shared.AnnyangStatus;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Created by marco on 26/05/16.
 */
public interface AnnyangEvents {


    static <L extends AnnyangListener> Optional<AnnyangCallbackType> callabackFromListener(L listener) {
        return Arrays.stream(listener.getClass().getAnnotatedInterfaces())
            .map(t -> EventsHelper.classFromType(t.getType()))
            .filter(t -> t.isAnnotationPresent(AnnyangCallbackHandler.class))
            .map(t -> t.getAnnotation(AnnyangCallbackHandler.class))
            .findFirst()
            .map(AnnyangCallbackHandler::value);
    }


    static Function<Annyang, AnnyangEvent> eventMapper(Annyang.Callback callback, JsonArray arguments) {
        return EventsHelper.createEvent(callback.getCallbackType(), arguments);
    }

    static String[] toStringArray(JsonArray array) {
        return EventsHelper.toStringArray(array);
    }


    class StatusChangedEvent extends Component.Event {
        @Getter
        private final AnnyangStatus oldStatus;
        @Getter
        private final AnnyangStatus newStatus;

        public StatusChangedEvent(Component source, AnnyangStatus oldStatus, AnnyangStatus newStatus) {
            super(source);
            this.oldStatus = oldStatus;
            this.newStatus = newStatus;
        }
    }

    interface StatusChangeListener extends ConnectorEventListener {
        Method eventMethod = ReflectTools.findMethod(
            StatusChangeListener.class, "onStatusChanged", StatusChangedEvent.class
        );

        void onStatusChanged(StatusChangedEvent event);
    }

    class UnsupportedEvent extends AnnyangEvent {
        UnsupportedEvent(Annyang source) {
            super(source);
        }
    }

    @AnnyangCallbackHandler(AnnyangCallbackType.UNSUPPORTED)
    interface UnsupportedListener extends AnnyangListener<UnsupportedEvent> {
    }

    class StartEvent extends AnnyangEvent {
        StartEvent(Annyang source) {
            super(source);
        }
    }

    @AnnyangCallbackHandler(AnnyangCallbackType.START)
    interface StartListener extends AnnyangListener<StartEvent> {
    }

    class EndEvent extends AnnyangEvent {
        EndEvent(Annyang source) {
            super(source);
        }
    }

    @AnnyangCallbackHandler(AnnyangCallbackType.END)
    interface EndListener extends AnnyangListener<EndEvent> {
    }

    class ErrorEvent extends AnnyangEvent {
        ErrorEvent(Annyang source) {
            super(source);
        }
    }

    @AnnyangCallbackHandler(AnnyangCallbackType.ERROR)
    interface ErrorListener extends AnnyangListener<ErrorEvent> {
    }

    class NetworkErrorEvent extends AnnyangEvent {
        NetworkErrorEvent(Annyang source) {
            super(source);
        }
    }

    @AnnyangCallbackHandler(AnnyangCallbackType.ERROR_NETWORK)
    interface NetworkErrorListener extends AnnyangListener<NetworkErrorEvent> {
    }

    class PermissionBlockedEvent extends AnnyangEvent {
        PermissionBlockedEvent(Annyang source) {
            super(source);
        }
    }

    @AnnyangCallbackHandler(AnnyangCallbackType.ERROR_PERMISSION_BLOCKED)
    interface PermissionBlockedListener extends AnnyangListener<PermissionBlockedEvent> {
    }

    class PermissionDeniedEvent extends AnnyangEvent {
        PermissionDeniedEvent(Annyang source) {
            super(source);
        }
    }

    @AnnyangCallbackHandler(AnnyangCallbackType.ERROR_PERMISSION_DENIED)
    interface PermissionDeniedListener extends AnnyangListener<PermissionDeniedEvent> {
    }

    @Getter
    @ToString
    class ResultEvent extends AnnyangEvent {
        private final String[] phrases;

        ResultEvent(Annyang annyang, String... phrases) {
            super(annyang);
            this.phrases = phrases;
        }
    }

    @AnnyangCallbackHandler(AnnyangCallbackType.RESULT)
    interface ResultListener extends AnnyangListener<ResultEvent> {
    }

    @Getter
    @ToString(callSuper = true)
    class ResultMatchedEvent extends ResultEvent {
        private final String commandName;
        private final String phrase;

        ResultMatchedEvent(Annyang annyang, String phrase, String commandName, String... phrases) {
            super(annyang, phrases);
            this.commandName = commandName;
            this.phrase = phrase;
        }
    }

    @AnnyangCallbackHandler(AnnyangCallbackType.RESULT_MATCH)
    interface ResultMatchedListener extends AnnyangListener<ResultMatchedEvent> {
    }


    @Getter
    @ToString(callSuper = true)
    class ResultNotMatchedEvent extends ResultEvent {
        public ResultNotMatchedEvent(Annyang annyang, String... phrases) {
            super(annyang, phrases);
        }
    }

    @AnnyangCallbackHandler(AnnyangCallbackType.RESULT_NO_MATCH)
    interface ResultNotMatchedListener extends AnnyangListener<ResultNotMatchedEvent> {
    }


}

class EventsHelper {

    interface EventGenerator extends Function<JsonArray, Function<Annyang, AnnyangEvent>> {
    }

    private static final EnumMap<AnnyangCallbackType, EventGenerator> mapper = new EnumMap<AnnyangCallbackType, EventGenerator>(AnnyangCallbackType.class) {{
        put(AnnyangCallbackType.UNSUPPORTED, args -> AnnyangEvents.UnsupportedEvent::new);
        put(AnnyangCallbackType.START, args -> AnnyangEvents.StartEvent::new);
        put(AnnyangCallbackType.END, args -> AnnyangEvents.EndEvent::new);
        put(AnnyangCallbackType.ERROR, args -> AnnyangEvents.ErrorEvent::new);
        put(AnnyangCallbackType.ERROR_NETWORK, args -> AnnyangEvents.NetworkErrorEvent::new);
        put(AnnyangCallbackType.ERROR_PERMISSION_BLOCKED, args -> AnnyangEvents.PermissionBlockedEvent::new);
        put(AnnyangCallbackType.ERROR_PERMISSION_DENIED, args -> AnnyangEvents.PermissionDeniedEvent::new);
        put(AnnyangCallbackType.RESULT, args -> annyang -> new AnnyangEvents.ResultEvent(annyang, toStringArray(args.getArray(0))));
        put(AnnyangCallbackType.RESULT_NO_MATCH, args -> annyang -> new AnnyangEvents.ResultNotMatchedEvent(annyang, toStringArray(args.getArray(0))));
        put(AnnyangCallbackType.RESULT_MATCH, args -> annyang -> new AnnyangEvents.ResultMatchedEvent(annyang,
            args.getString(0), args.getString(1), toStringArray(args.getArray(2))));
    }};

    private EventsHelper() {
        throw new AssertionError("Must not be instantiated");
    }

    static String[] toStringArray(JsonArray array) {
        return IntStream.range(0, array.length())
            .mapToObj(i -> array.get(i).asString()).toArray(String[]::new);
    }

    static Function<Annyang, AnnyangEvent> createEvent(AnnyangCallbackType callbackType, JsonArray arguments) {
        return mapper.get(callbackType).apply(arguments);
    }

    static Class<?> classFromType(Type type) {
        if (type instanceof ParameterizedType) {
            return classFromType(((ParameterizedType) type).getRawType());
        }
        return (Class<?>) type;
    }
}
