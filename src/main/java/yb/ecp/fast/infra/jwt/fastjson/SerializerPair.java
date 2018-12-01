package yb.ecp.fast.infra.jwt.fastjson;

import com.alibaba.fastjson.serializer.ObjectSerializer;

/**
 * @author hzwangyuantao
 * @since 2015/10/10
 */
public class SerializerPair {
    private Class<?> clazz;
    private ObjectSerializer objectSerializer;

    public SerializerPair(Class<?> clazz, ObjectSerializer objectSerializer) {
        this.clazz = clazz;
        this.objectSerializer = objectSerializer;
    }

    public SerializerPair() {
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public ObjectSerializer getObjectSerializer() {
        return objectSerializer;
    }

    public void setObjectSerializer(ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }
}
