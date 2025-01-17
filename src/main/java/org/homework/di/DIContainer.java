package org.homework.di;

import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Контейнер для управления зависимостями и внедрения их в классы.
 */
public class DIContainer {
    private final Map<Class<?>, Object> createdServices = new HashMap<>();
    private final Map<Class<?>, Class<?>> registeredImplementations = new HashMap<>();

    public DIContainer() {
        autoRegister();
    }

    /**
     * Автоматически регистрирует все классы, помеченные аннотацией @Register.
     */
    private void autoRegister() {
        Reflections reflections = new Reflections("org.homework",
                new SubTypesScanner(false),
                new TypeAnnotationsScanner());

        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Register.class);

        for (Class<?> clazz : annotated) {
            if (clazz.isInterface()) {
                continue; // Пропускаем интерфейсы
            }

            if (!verifyNoArgConstructor(clazz)) {
                continue; // Пропускаем классы без конструктора по умолчанию
            }

            // Регистрируем класс и его интерфейсы
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> intf : interfaces) {
                if (!registeredImplementations.containsKey(intf) || registeredImplementations.get(intf).isInterface()) {
                    registeredImplementations.put(intf, clazz);
                }
            }

            if (interfaces.length == 0) {
                registeredImplementations.put(clazz, clazz);
            }
        }
    }

    /**
     * Разрешает зависимость для указанного класса.
     *
     * @param serviceClass Класс, для которого требуется разрешить зависимость.
     * @return Экземпляр класса.
     */
    public <T> T resolve(Class<T> serviceClass) {
        @SuppressWarnings("unchecked")
        T service = (T) createdServices.get(serviceClass);
        if (service == null) {
            service = createService(serviceClass);
        }
        return service;
    }

    /**
     * Создает экземпляр сервиса.
     *
     * @param serviceClass Класс сервиса.
     * @return Экземпляр сервиса.
     */
    private <T> T createService(Class<T> serviceClass) {
        if (serviceClass.isInterface()) {
            return createServiceFromInterface(serviceClass);
        } else {
            return createServiceFromClass(serviceClass);
        }
    }

    /**
     * Создает экземпляр сервиса для интерфейса.
     *
     * @param serviceClass Интерфейс сервиса.
     * @return Экземпляр реализации.
     */
    private <T> T createServiceFromInterface(Class<T> serviceClass) {
        Class<?> implementationClass = registeredImplementations.get(serviceClass);
        if (implementationClass == null) {
            throw new IllegalStateException("No implementation registered for interface: " + serviceClass.getName());
        }
        return createServiceFromClass(implementationClass.asSubclass(serviceClass));
    }

    /**
     * Создает экземпляр сервиса для конкретного класса.
     *
     * @param concreteClass Класс сервиса.
     * @return Экземпляр сервиса.
     */
    private <T> T createServiceFromClass(Class<T> concreteClass) {
        try {
            T instance = concreteClass.getDeclaredConstructor().newInstance();
            createdServices.put(concreteClass, instance); // Сохраняем перед инъекцией зависимостей
            injectDependencies(instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate service: " + concreteClass.getName(), e);
        }
    }

    /**
     * Внедряет зависимости в объект.
     *
     * @param object Объект для внедрения зависимостей.
     */
    private void injectDependencies(Object object) {
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Resolve.class)) {
                field.setAccessible(true);
                Class<?> dependencyType = field.getType();
                Object dependency = resolve(dependencyType);
                try {
                    field.set(object, dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(String.format("Could not set field '%s' on class '%s'.", field.getName(), clazz.getName()), e);
                }
            }
        }
    }

    /**
     * Проверяет наличие конструктора без аргументов.
     *
     * @param clazz Класс для проверки.
     * @return true, если конструктор существует, иначе false.
     */
    private boolean verifyNoArgConstructor(Class<?> clazz) {
        try {
            clazz.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}