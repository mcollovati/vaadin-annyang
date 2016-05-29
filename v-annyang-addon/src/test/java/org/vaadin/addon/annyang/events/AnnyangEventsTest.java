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

import com.vaadin.ui.UI;
import elemental.json.Json;
import elemental.json.JsonArray;
import org.junit.Test;
import org.vaadin.addon.annyang.Annyang;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created by marco on 29/05/16.
 */
@SuppressWarnings("unchecked")
public class AnnyangEventsTest {

    @Test
    public void shouldReturnCallbackTypeForWellKnownInterfaces() throws Exception {
        assertCallbackType((AnnyangEvents.UnsupportedListener) args -> {
        }, AnnyangCallbackType.UNSUPPORTED);
        assertCallbackType((AnnyangEvents.StartListener) args -> {
        }, AnnyangCallbackType.START);
        assertCallbackType((AnnyangEvents.EndListener) args -> {
        }, AnnyangCallbackType.END);
        assertCallbackType((AnnyangEvents.ErrorListener) args -> {
        }, AnnyangCallbackType.ERROR);
        assertCallbackType((AnnyangEvents.NetworkErrorListener) args -> {
        }, AnnyangCallbackType.ERROR_NETWORK);
        assertCallbackType((AnnyangEvents.PermissionBlockedListener) args -> {
        }, AnnyangCallbackType.ERROR_PERMISSION_BLOCKED);
        assertCallbackType((AnnyangEvents.PermissionDeniedListener) args -> {
        }, AnnyangCallbackType.ERROR_PERMISSION_DENIED);
        assertCallbackType((AnnyangEvents.ResultListener) args -> {
        }, AnnyangCallbackType.RESULT);
        assertCallbackType((AnnyangEvents.ResultMatchedListener) args -> {
        }, AnnyangCallbackType.RESULT_MATCH);
        assertCallbackType((AnnyangEvents.ResultNotMatchedListener) args -> {
        }, AnnyangCallbackType.RESULT_NO_MATCH);

        AnnyangEvents.UnsupportedListener listener = new AnnyangEvents.UnsupportedListener() {
            @Override
            public void onEvent(AnnyangEvents.UnsupportedEvent event) {
            }
        };
        assertCallbackType(listener, AnnyangCallbackType.UNSUPPORTED);

    }

    @Test
    public void shouldReturnEmptyOptionalForNotAnnotatedInterface() {
        assertThat(AnnyangEvents.callabackFromListener(args -> {
        })).isEmpty();
        assertThat(AnnyangEvents.callabackFromListener(new TestListener())).isEmpty();
        assertThat(AnnyangEvents.callabackFromListener((MyInterface) args -> {
        })).isEmpty();
    }

    @Test
    public void shouldCreateEventsForKnownCallbacks() throws Exception {
        assertNoParamsEventCreation(AnnyangCallbackType.UNSUPPORTED, AnnyangEvents.UnsupportedEvent.class);
        assertNoParamsEventCreation(AnnyangCallbackType.START, AnnyangEvents.StartEvent.class);
        assertNoParamsEventCreation(AnnyangCallbackType.END, AnnyangEvents.EndEvent.class);
        assertNoParamsEventCreation(AnnyangCallbackType.ERROR, AnnyangEvents.ErrorEvent.class);
        assertNoParamsEventCreation(AnnyangCallbackType.ERROR_NETWORK, AnnyangEvents.NetworkErrorEvent.class);
        assertNoParamsEventCreation(AnnyangCallbackType.ERROR_PERMISSION_BLOCKED, AnnyangEvents.PermissionBlockedEvent.class);
        assertNoParamsEventCreation(AnnyangCallbackType.ERROR_PERMISSION_DENIED, AnnyangEvents.PermissionDeniedEvent.class);

        List<String> expectedPhrases = Arrays.asList("a", "b", "c");
        JsonArray phrases = Json.createArray();
        IntStream.range(0, expectedPhrases.size()).forEach(i -> phrases.set(i, expectedPhrases.get(i)));

        JsonArray array = Json.createArray();
        array.set(0, phrases);
        AnnyangEvents.ResultEvent resultEvent = assertEventCreation(AnnyangCallbackType.RESULT, array, AnnyangEvents.ResultEvent.class);
        assertThat(resultEvent.getPhrases()).containsExactlyElementsOf(expectedPhrases);

        AnnyangEvents.ResultNotMatchedEvent resultNotMatchedEvent = assertEventCreation(AnnyangCallbackType.RESULT_NO_MATCH,
            array, AnnyangEvents.ResultNotMatchedEvent.class);
        assertThat(resultNotMatchedEvent.getPhrases()).containsExactlyElementsOf(expectedPhrases);

        array.set(0, expectedPhrases.get(0));
        array.set(1, "myCommand");
        array.set(2, phrases);
        AnnyangEvents.ResultMatchedEvent resultMatched = assertEventCreation(AnnyangCallbackType.RESULT_MATCH,
            array, AnnyangEvents.ResultMatchedEvent.class);
        assertThat(resultMatched.getPhrase()).isEqualTo(expectedPhrases.get(0));
        assertThat(resultMatched.getCommandName()).isEqualTo("myCommand");
        assertThat(resultMatched.getPhrases()).containsExactlyElementsOf(expectedPhrases);

    }


    private <T extends AnnyangEvent> T assertNoParamsEventCreation(AnnyangCallbackType callbackType, Class<T> expectedEvent) {
        return assertEventCreation(callbackType, Json.createArray(), expectedEvent);
    }


    private <T extends AnnyangEvent> T assertEventCreation(AnnyangCallbackType callbackType, JsonArray args, Class<T> expectedEvent) {
        AnnyangEvent event = createEvent(args, callbackType);
        assertThat(event).isNotNull()
            .isExactlyInstanceOf(expectedEvent);
        assertThat(event.getComponent()).isNotNull();
        assertThat(event.getSource()).isNotNull();
        assertThat(event.getSource()).isSameAs(event.getComponent());
        return (T) event;
    }

    private AnnyangEvent createEvent(JsonArray array, AnnyangCallbackType callbackType) {
        Annyang annyang = mock(Annyang.class);
        when(annyang.getUI()).thenReturn(mock(UI.class));
        Annyang.Callback callback = mock(Annyang.Callback.class);
        when(callback.getCallbackType()).thenReturn(callbackType);
        return AnnyangEvents.eventMapper(callback, array).apply(annyang);
    }

    @Test
    public void shouldTransformJsonStringArrayToStringArray() throws Exception {
        JsonArray array = Json.createArray();
        array.set(0, "a");
        array.set(1, "b");
        array.set(2, "c");
        assertThat(AnnyangEvents.toStringArray(array))
            .containsExactly("a", "b", "c");
    }

    @Test
    public void shouldTransformMixedJsonArrayToStringArray() throws Exception {
        JsonArray array = Json.createArray();
        array.set(0, "a");
        array.set(1, 12.33);
        array.set(2, true);
        assertThat(AnnyangEvents.toStringArray(array))
            .containsExactly("a", "12.33", "true");
    }

    @Test
    public void shouldTransormingEmptyJsonArray() {
        JsonArray array = Json.createArray();
        assertThat(AnnyangEvents.toStringArray(array)).isEmpty();
    }

    private <T extends AnnyangListener> void assertCallbackType(T listener, AnnyangCallbackType expected) {
        assertThat(AnnyangEvents.callabackFromListener(listener)).isPresent().contains(expected);
    }

    static class TestListener implements AnnyangListener<AnnyangEvent> {

        @Override
        public void onEvent(AnnyangEvent event) {
        }
    }

    interface MyInterface extends AnnyangListener<AnnyangEvent> {
    }

}