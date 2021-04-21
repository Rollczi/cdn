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

package net.dzikoysk.cdn;

import net.dzikoysk.cdn.entity.CustomComposer;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.entity.SectionLink;
import net.dzikoysk.cdn.entity.SectionValue;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.serialization.Serializer;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.commons.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public final class CdnSerializer {

    private final CdnSettings settings;

    CdnSerializer(CdnSettings settings) {
        this.settings = settings;
    }

    public Configuration serialize(Object entity) {
        Configuration root = new Configuration();

        try {
            serialize(root, entity);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot access serialize member", e);
        }

        return root;
    }

    private Section serialize(Section root, Object entity) throws Exception {
        Class<?> scheme = entity.getClass();

        for (Field field : scheme.getDeclaredFields()) {
            if (CdnUtils.isIgnored(field)) {
                continue;
            }

            List<String> description = Arrays.stream(field.getAnnotationsByType(Description.class))
                    .flatMap(annotation -> Arrays.stream(annotation.value()))
                    .collect(Collectors.toList());

            if (field.isAnnotationPresent(SectionLink.class)) {
                Section section = new Section(description, CdnConstants.OBJECT_SEPARATOR, field.getName());
                root.append(section);
                serialize(section, field.get(entity));
                continue;
            }

            Object propertyValue = field.get(entity);

            if (propertyValue != null) {
                Serializer<Object> serializer = getSerializer(settings, field.getType(), field);
                root.append(serializer.serialize(settings, description, field.getName(), field.getGenericType(), propertyValue));
            }
        }

        return root;
    }

    public static Serializer<Object> getSerializer(CdnSettings settings, Class<?> type, @Nullable Field field) throws Exception {
        Serializer<Object> serializer = null;

        if (field != null && field.isAnnotationPresent(CustomComposer.class)) {
            CustomComposer customComposer = field.getAnnotation(CustomComposer.class);
            serializer = ObjectUtils.cast(customComposer.value().getConstructor().newInstance());
        }
        else {
            for (Entry<? extends Class<?>, ? extends Serializer<Object>> serializerEntry : settings.getSerializers().entrySet()) {
                if (type.isAssignableFrom(serializerEntry.getKey())) {
                    serializer = serializerEntry.getValue();
                    break;
                }
            }
        }

        if (type.isAnnotationPresent(SectionValue.class)) {
            CdnSerializer sectionSerializer = new CdnSerializer(settings);

            return (s, description, key, genericType, entity) -> {
                Section section = new Section(description, CdnConstants.OBJECT_SEPARATOR, key);
                return sectionSerializer.serialize(section, entity);
            };
        }

        if (serializer == null) {
            throw new UnsupportedOperationException("Cannot serialize '" + type  + "' - missing serializer");
        }

        return serializer;
    }

}
