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

package net.dzikoysk.cdn.serialization;

/**
 * The composer represents implementation of both: serializer and deserializer interfaces.
 *
 * @param <T> type of value to serialize/deserialize
 */
public interface Composer<T> extends Serializer<T>, Deserializer<T> {

    Object MEMBER_ALREADY_PROCESSED = new Object(); // kinda dirty, but I don't want to modify this API yet

}
