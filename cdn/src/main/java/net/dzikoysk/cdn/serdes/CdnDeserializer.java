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

package net.dzikoysk.cdn.serdes;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.annotation.AnnotatedMember;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Section;
import panda.std.Option;
import panda.utilities.ObjectUtils;

public final class CdnDeserializer<T> {

    private final CdnSettings settings;

    public CdnDeserializer(CdnSettings settings) {
        this.settings = settings;
    }

    public T deserialize(Section source, Class<T> template) throws ReflectiveOperationException {
        return deserialize(source, template.getConstructor().newInstance());
    }

    public T deserialize(Section source, T instance) throws ReflectiveOperationException {
        deserializeToSection(source, instance);

        if (instance instanceof DeserializationHandler) {
            DeserializationHandler<T> handler = ObjectUtils.cast(instance);
            instance = handler.handle(ObjectUtils.cast(instance));
        }

        return instance;
    }

    private Object deserializeToSection(Section source, Object instance) throws ReflectiveOperationException {
        for (AnnotatedMember field : settings.getAnnotationResolver().getFields(instance)) {
            deserializeMember(source, field);
        }

        for (AnnotatedMember annotatedMember : settings.getAnnotationResolver().getProperties(instance)) {
            deserializeMember(source, annotatedMember);
        }

        return instance;
    }

    private void deserializeMember(Section source, AnnotatedMember member) throws ReflectiveOperationException {
        if (member.isIgnored()) {
            return;
        }

        Option<Element<?>> elementValue = source.get(member.getName());

        if (elementValue.isEmpty()) {
            return;
        }

        Element<?> element = elementValue.get();
        Object defaultValue = member.getValue();

        if (member.isAnnotationPresent(Contextual.class)) {
            deserializeToSection((Section) element, defaultValue);
            return;
        }

        Deserializer<Object> deserializer = CdnUtils.findComposer(settings, member.getType(), member.getAnnotatedType(), member);
        Object value = deserializer.deserialize(settings, element, member.getAnnotatedType(), defaultValue, false);

        if (value != Composer.MEMBER_ALREADY_PROCESSED) {
            member.setValue(value);
        }
    }

}
