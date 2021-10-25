/*
 * Copyright (c) 2021 dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dzikoysk.cdn.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface MutableReference<V> extends Reference<V> {

    MutableReference<V> update(V value);

    @Contract(value = "_ -> new", pure = true)
    static <T> MutableReference<@NotNull T> mutableReference(@NotNull T value) {
        return new MutableReferenceImpl<>(value);
    }

}
