package s.android.ffmpeg.model;

/**
 * Created by CodingMates on 2015/1/30.
 */
public class KVObject {
    private String key;
    private Object value;

    public KVObject(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "KVObject{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KVObject kvObject = (KVObject) o;

        if (key != null ? !key.equals(kvObject.key) : kvObject.key != null) return false;
        if (value != null ? !value.equals(kvObject.value) : kvObject.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
