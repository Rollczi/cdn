package net.dzikoysk.cdn;

import net.dzikoysk.cdn.entity.CustomComposer;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.entity.SectionLink;
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

    private final CDN cdn;

    CdnSerializer(CDN cdn) {
        this.cdn = cdn;
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

    private void serialize(Section root, Object entity) throws Exception {
        Class<?> scheme = entity.getClass();

        for (Field field : scheme.getDeclaredFields()) {
            if (CdnUtils.isIgnored(field)) {
                continue;
            }

            List<String> description = Arrays.stream(field.getAnnotationsByType(Description.class))
                    .flatMap(annotation -> Arrays.stream(annotation.value()))
                    .collect(Collectors.toList());

            if (field.isAnnotationPresent(SectionLink.class)) {
                Section section = new Section(CdnConstants.OBJECT_SEPARATOR, field.getName(), description);
                root.append(section);
                serialize(section, field.get(entity));
                continue;
            }

            Serializer<Object> serializer = getSerializer(cdn.getSettings(), field.getType(), field);
            root.append(serializer.serialize(cdn.getSettings(), description, field.getName(), field.getGenericType(), field.get(entity)));
        }
    }


    public static Serializer<Object> getSerializer(CdnSettings settings, Class<?> type, @Nullable Field field) throws Exception {
        Serializer<Object> serializer = null;

        if (field != null && field.isAnnotationPresent(CustomComposer.class)) {
            CustomComposer customComposer = field.getAnnotation(CustomComposer.class);
            serializer = ObjectUtils.cast(customComposer.value().getConstructor().newInstance());
        }
        else {
            for (Entry<Class<?>, Serializer<Object>> serializerEntry : settings.getSerializers().entrySet()) {
                if (type.isAssignableFrom(serializerEntry.getKey())) {
                    serializer = serializerEntry.getValue();
                    break;
                }
            }
        }

        if (serializer == null) {
            throw new UnsupportedOperationException("Cannot serialize '" + type  + "' - missing serializer");
        }

        return serializer;
    }

}
