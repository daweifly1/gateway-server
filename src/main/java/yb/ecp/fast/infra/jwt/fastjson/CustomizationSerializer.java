package yb.ecp.fast.infra.jwt.fastjson;


import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author hzwangyuantao
 * @since 2015/10/10
 */
public abstract class CustomizationSerializer<T> implements ObjectSerializer {

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
            throws IOException {
        SerializeWriter out = serializer.getWriter();
        if (object == null) {
            out.writeNull();
            return;
        }
        out.write('{');
        writeBody(serializer, (T) object);
        out.write('}');
    }

    /**
     * 用户实现此方法来自定义决定哪些字段需要序列化 在此方法中应该调用
     * {@linkplain this#writeKeyValue(JSONSerializer, String, Object)}
     * 和
     * {@linkplain this#writeLastKeyValue(JSONSerializer, String, Object)}
     * 方法
     *
     * @param serializer
     * @param object
     * @throws IOException
     */
    protected abstract void writeBody(JSONSerializer serializer, T object) throws IOException;

    /**
     * 序列化一个键值对
     *
     * @param serializer
     * @param key
     * @param value
     * @throws IOException
     */
    protected void writeKeyValue(JSONSerializer serializer, String key, Object value) throws IOException {
        writeKeyValue(serializer, key, value, false);
    }

    /**
     * 序列化最后一个键值对
     *
     * @param serializer
     * @param key
     * @param value
     * @throws IOException
     */
    protected void writeLastKeyValue(JSONSerializer serializer, String key, Object value) throws IOException {
        writeKeyValue(serializer, key, value, true);
    }

    protected void writeKeyValue(JSONSerializer serializer, String key, Object value, boolean isLast)
            throws IOException {
        if (null == value) {
            serializer.writeNull();
            return;
        }
        SerializeWriter out = serializer.getWriter();
        out.writeFieldName(key);
        ObjectSerializer objectSerializer = serializer.getObjectWriter(value.getClass());
        objectSerializer.write(serializer, value, null, null, 0);
        if (!isLast) {
            out.write(',');
        }
    }
}
