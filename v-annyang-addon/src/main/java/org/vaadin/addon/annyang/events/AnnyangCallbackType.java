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
package org.vaadin.addon.annyang.events;

/**
 * Created by marco on 26/05/16.
 */
public enum AnnyangCallbackType {
    UNSUPPORTED,
    START, END, RESULT,
    RESULT_MATCH("resultMatch"), RESULT_NO_MATCH("resultNoMatch"),
    ERROR, ERROR_NETWORK("errorNetwork"),
    ERROR_PERMISSION_BLOCKED("errorPermissionBlocked"), ERROR_PERMISSION_DENIED("errorPermissionDenied"),;

    private final String callbackName;

    AnnyangCallbackType() {
        this.callbackName = name().toLowerCase();
    }

    AnnyangCallbackType(String callbackName) {
        this.callbackName = callbackName;
    }

    public String getCallbackName() {
        return callbackName;
    }
}
