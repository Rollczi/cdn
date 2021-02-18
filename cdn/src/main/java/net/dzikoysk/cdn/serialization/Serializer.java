package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.model.ConfigurationElement;

import java.lang.reflect.Type;
import java.util.List;

@FunctionalInterface
public interface Serializer<T> {

    ConfigurationElement<?> serialize(CdnSettings settings, List<String> description, String key, Type genericType, T entity) throws Exception;

}
