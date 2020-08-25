package com.luxf.cloud.eureka.feign.client.helper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author 小66
 * @date 2020-08-24 16:08
 **/
public class ReflectHelper {

    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null);
    }

    public static Field findField(Class<?> clazz, String name, Class<?> type) {
        Class<?> searchType = clazz;
        // while循环直到Object.class
        while (Object.class != searchType && searchType != null) {
            final Field field = Arrays.stream(searchType.getDeclaredFields())
                    .filter(f -> f.getName().equals(name) && (type == null || f.getType().equals(type)))
                    .findFirst().orElse(null);

            if (field != null) {
                return field;
            }
            //向上查找父类的字段、
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <V> V getFieldValue(Object target, Field field) {
        boolean accessible = field.isAccessible();
        setFieldAccessible(field, accessible);
        Object val;
        try {
            val = field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            setFieldInaccessible(field, accessible);
        }
        return (V) val;
    }

    public static void setFieldValue(Object target, Field field, Object val) {
        boolean accessible = field.isAccessible();
        setFieldAccessible(field, accessible);
        try {
            field.set(target, val);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            setFieldInaccessible(field, accessible);
        }
    }

    public static Object invokeMethod(Method method, Object target) {
        return invokeMethod(method, target, new Object[0]);
    }

    public static Object invokeMethod(Method method, Object target, Object... args) {
        final boolean accessible = method.isAccessible();
        try {
            setMethodAccessible(method, accessible);
            return method.invoke(target, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } finally {
            setMethodInaccessible(method, accessible);
        }
    }

    private static void setFieldAccessible(Field field, boolean accessible) {
        if (!accessible) {
            field.setAccessible(true);
        }
    }

    private static void setFieldInaccessible(Field field, boolean accessible) {
        if (!accessible) {
            field.setAccessible(false);
        }
    }

    private static void setMethodInaccessible(Method method, boolean accessible) {
        if (!accessible) {
            method.setAccessible(false);
        }
    }

    private static void setMethodAccessible(Method method, boolean accessible) {
        if (!accessible) {
            method.setAccessible(true);
        }
    }
}
