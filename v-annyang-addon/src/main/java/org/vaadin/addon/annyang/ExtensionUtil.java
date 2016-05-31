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

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.AbstractComponent;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by marco on 31/05/16.
 */
class ExtensionUtil {

    static <T extends AbstractExtension> Optional<T> from(AbstractComponent component, Class<T> extensionType) {
        return Objects.requireNonNull(component).getExtensions().stream()
            .filter(extensionType::isInstance)
            .findFirst()
            .map(extensionType::cast);
    }
}
