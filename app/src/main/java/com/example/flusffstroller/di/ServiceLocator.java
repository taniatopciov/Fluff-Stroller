package com.example.flusffstroller.di;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {
    private static ServiceLocator instance = null;
    private final Map<Class, Object> injectionMap = new HashMap<>();

    private ServiceLocator() {
    }

    public static ServiceLocator getInstance() {
        if (instance == null) {
            synchronized (ServiceLocator.class) {
                instance = new ServiceLocator();
            }
        }
        return instance;
    }

    public <T> void register(Class<T> injectClass, T concreteClass) {
        injectionMap.put(injectClass, concreteClass);
    }

    public void dispose() {
        instance = null;
    }

    public void inject(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Injectable.class)) {
                field.setAccessible(true);

                try {
                    field.set(object, getConcreteClass(field.getType()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Object getConcreteClass(Class c) {
        if (injectionMap.containsKey(c)) {
            return injectionMap.get(c);
        }
        return null;
    }
}
