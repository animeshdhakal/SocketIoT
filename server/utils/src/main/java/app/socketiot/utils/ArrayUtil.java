package app.socketiot.utils;

import java.lang.reflect.Array;

public class ArrayUtil {

    @SuppressWarnings("unchecked")
    public static <T> T[] remove(T[] array, int index, Class<T> type) {
        T[] newArray = (T[]) Array.newInstance(type, array.length - 1);
        System.arraycopy(array, 0, newArray, 0, index);
        System.arraycopy(array, index + 1, newArray, index, array.length - index - 1);
        return newArray;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] add(T[] array, T element, Class<T> type) {
        T[] newArray = (T[]) Array.newInstance(type, array.length + 1);
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = element;
        return newArray;
    }
}
