package cn.nukkit.collection;

import java.lang.reflect.Array;
import java.util.AbstractList;

/**
 * @author https://github.com/boy0001/
 */
public class PrimitiveList<T> extends AbstractList<T> {
    private final Class<?> primitive;
    private final Type type;
    private int length;
    private int totalLength;
    private Object arr;

    private enum Type {
        Byte,
        Boolean,
        Short,
        Character,
        Integer,
        Float,
        Long,
        Double
    }

    public PrimitiveList(Class<T> type) {
        try {
            Class<T> boxed;
            if (type.isPrimitive()) {
                this.primitive = type;
                boxed = (Class<T>) Array.get(Array.newInstance(primitive, 1), 0).getClass();
            } else {
                this.primitive = (Class<?>) type.getField("TYPE").get(null);
                boxed = type;
            }
            this.type = Type.valueOf(boxed.getSimpleName());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        length = 0;
        totalLength = 0;
        arr = Array.newInstance(primitive, 0);
    }

    public PrimitiveList(T[] arr) {
        try {
            Class<T> boxed = (Class<T>) arr.getClass().getComponentType();
            this.primitive = (Class<?>) boxed.getField("TYPE").get(null);
            this.type = Type.valueOf(boxed.getSimpleName());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        this.arr = Array.newInstance(primitive, arr.length);
        for (int i = 0; i < arr.length; i++) {
            T val = arr[i];
            if (val != null) setFast(i, val);
        }
        this.length = arr.length;
        this.totalLength = length;
    }

    public PrimitiveList(Object arr) {
        if (!arr.getClass().isArray()) {
            throw new IllegalArgumentException("Argument must be an array!");
        }
        this.primitive = arr.getClass().getComponentType();
        Class<T> boxed = (Class<T>) Array.get(Array.newInstance(primitive, 1), 0).getClass();
        this.type = Type.valueOf(boxed.getSimpleName());
        this.arr = arr;
        this.length = Array.getLength(arr);
        this.totalLength = length;
    }

    public Object getArray() {
        return arr;
    }

    @Override
    public T get(int index) {
        return (T) getFast(index);
    }

    public byte getByte(int index) {
        return type == Type.Double ? ((byte[]) arr)[index] : (byte) getFast(index);
    }

    public boolean getBoolean(int index) {
        return type == Type.Boolean ? ((boolean[]) arr)[index] : (boolean) getFast(index);
    }

    public short getShort(int index) {
        return type == Type.Short ? ((short[]) arr)[index] : (short) getFast(index);
    }

    public char getCharacter(int index) {
        return type == Type.Character ? ((char[]) arr)[index] : (char) getFast(index);
    }

    public int getInt(int index) {
        return type == Type.Integer ? ((int[]) arr)[index] : (int) getFast(index);
    }

    public float getFloat(int index) {
        return type == Type.Float ? ((float[]) arr)[index] : (float) getFast(index);
    }

    public long getLong(int index) {
        return type == Type.Long ? ((long[]) arr)[index] : (long) getFast(index);
    }

    public double getDouble(int index) {
        return type == Type.Double ? ((double[]) arr)[index] : (double) getFast(index);
    }

    private final Object getFast(int index) {
        switch (type) {
            case Byte:
                return ((byte[]) arr)[index];
            case Boolean:
                return ((boolean[]) arr)[index];
            case Short:
                return ((short[]) arr)[index];
            case Character:
                return ((char[]) arr)[index];
            case Integer:
                return ((int[]) arr)[index];
            case Float:
                return ((float[]) arr)[index];
            case Long:
                return ((long[]) arr)[index];
            case Double:
                return ((double[]) arr)[index];
        }
        return null;
    }

    @Override
    public T set(int index, T element) {
        T value = get(index);
        setFast(index, element);
        return value;
    }

    public void set(int index, char value) {
        switch (type) {
            default:
                setFast(index, value);
                return;
            case Character:
                ((char[]) arr)[index] = value;
                return;
        }
    }

    public void set(int index, byte value) {
        switch (type) {
            default:
                setFast(index, value);
                return;
            case Byte:
                ((byte[]) arr)[index] = value;
                return;
        }
    }

    public void set(int index, int value) {
        switch (type) {
            default:
                setFast(index, value);
                return;
            case Integer:
                ((int[]) arr)[index] = value;
                return;
            case Long:
                ((long[]) arr)[index] = (long) value;
                return;
            case Double:
                ((double[]) arr)[index] = (double) value;
                return;
        }
    }

    public void set(int index, long value) {
        switch (type) {
            default:
                setFast(index, value);
                return;
            case Integer:
                ((int[]) arr)[index] = (int) value;
                return;
            case Long:
                ((long[]) arr)[index] = value;
                return;
            case Double:
                ((double[]) arr)[index] = (double) value;
                return;
        }
    }

    public void set(int index, double value) {
        switch (type) {
            default:
                setFast(index, value);
                return;
            case Float:
                ((float[]) arr)[index] = (float) value;
                return;
            case Long:
                ((long[]) arr)[index] = (long) value;
                return;
            case Double:
                ((double[]) arr)[index] = value;
                return;
        }
    }

    public final void setFast(int index, Object element) {
        switch (type) {
            case Byte:
                ((byte[]) arr)[index] = (byte) element;
                return;
            case Boolean:
                ((boolean[]) arr)[index] = (boolean) element;
                return;
            case Short:
                ((short[]) arr)[index] = (short) element;
                return;
            case Character:
                ((char[]) arr)[index] = (char) element;
                return;
            case Integer:
                ((int[]) arr)[index] = (int) element;
                return;
            case Float:
                ((float[]) arr)[index] = (float) element;
                return;
            case Long:
                ((long[]) arr)[index] = (long) element;
                return;
            case Double:
                ((double[]) arr)[index] = (double) element;
                return;
        }
    }


    @Override
    public void add(int index, T element) {
        if (index == length) {
            if (totalLength == length) {
                Object tmp = arr;
                totalLength = (length << 1) + 16;
                arr = Array.newInstance(primitive, totalLength);
                System.arraycopy(tmp, 0, arr, 0, length);
            }
            setFast(length, element);
            length++;
        } else {
            if (totalLength == length) {
                Object tmp = arr;
                totalLength = (length << 1) + 16;
                arr = Array.newInstance(primitive, totalLength);
                System.arraycopy(tmp, 0, arr, 0, index);
            }
            System.arraycopy(arr, index, arr, index + 1, length - index);
            set(index, element);
            length++;
        }
    }

    private void ensureAddCapacity() {
        if (totalLength == length) {
            Object tmp = arr;
            totalLength = (length << 1) + 16;
            arr = Array.newInstance(primitive, totalLength);
            System.arraycopy(tmp, 0, arr, 0, length);
        }
    }

    @Override
    public boolean add(T element) {
        ensureAddCapacity();
        setFast(length++, element);
        return true;
    }

    public boolean add(int element) {
        ensureAddCapacity();
        set(length++, element);
        return true;
    }

    public boolean add(long element) {
        ensureAddCapacity();
        set(length++, element);
        return true;
    }

    public boolean add(double element) {
        ensureAddCapacity();
        set(length++, element);
        return true;
    }

    public boolean add(byte element) {
        ensureAddCapacity();
        set(length++, element);
        return true;
    }

    public boolean add(char element) {
        ensureAddCapacity();
        set(length++, element);
        return true;
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index > length) throw new IndexOutOfBoundsException(index + " not in [0, " + length + "]");
        T value = get(index);
        if (index != length) {
            System.arraycopy(arr, index + 1, arr, index, length - index - 1);
        }
        length--;
        return value;
    }

    @Override
    public int size() {
        return length;
    }

    @Override
    public void clear() {
        if (length != 0) {
            this.arr = Array.newInstance(primitive, 0);
        }
        length = 0;
    }
}
