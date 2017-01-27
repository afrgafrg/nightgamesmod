package nightgames.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Provides a Type for maps that lets Gson.fromJson() get around type erasure.
 */
public class ParameterizedMapType<K, V> implements ParameterizedType {
    private Class<K> keyType;
    private Class<V> valueType;
    private Class<? extends Map> mapType;

    public ParameterizedMapType(Class<K> keyClazz, Class<V> valueClazz) {
        this(keyClazz, valueClazz, Map.class);
    }

    public ParameterizedMapType(Class<K> keyClazz, Class<V> valueClazz, Class<? extends Map> mapClazz) {
        keyType = keyClazz;
        valueType = valueClazz;
        mapType = Map.class;
    }

    @Override public Type[] getActualTypeArguments() {
        return new Type[] {keyType, valueType};
    }

    @Override public Type getRawType() {
        return mapType;
    }

    @Override public Type getOwnerType() {
        return null;
    }
}
