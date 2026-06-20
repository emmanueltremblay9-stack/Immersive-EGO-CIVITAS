package com.oblixorprime.immersiveego.civitas.resident.upstream;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;

final class ReflectiveHostAccess {
    private ReflectiveHostAccess() {
    }

    static boolean hasTypeName(Object target, Set<String> typeNames) {
        return target != null && hasTypeName(target.getClass(), typeNames, new HashSet<>());
    }

    static Optional<Object> invokeNoArg(Object target, String methodName) {
        if (target == null) {
            return Optional.empty();
        }

        return findNoArg(target.getClass(), methodName)
                .flatMap(method -> invokeMethod(target, method));
    }

    static Optional<Object> invoke(Object target, String methodName, Object... arguments) {
        if (target == null) {
            return Optional.empty();
        }

        return findCompatible(target.getClass(), methodName, arguments)
                .flatMap(method -> invokeMethod(target, method, arguments));
    }

    static Optional<UUID> invokeUuid(Object target, String methodName) {
        return invokeNoArg(target, methodName)
                .filter(UUID.class::isInstance)
                .map(UUID.class::cast);
    }

    static OptionalInt invokeInt(Object target, String methodName) {
        Optional<Object> value = invokeNoArg(target, methodName);
        if (value.isPresent() && value.get() instanceof Number number) {
            return OptionalInt.of(number.intValue());
        }
        return OptionalInt.empty();
    }

    private static boolean hasTypeName(Class<?> type, Set<String> typeNames, Set<Class<?>> visited) {
        if (type == null || !visited.add(type)) {
            return false;
        }
        if (typeNames.contains(type.getName())) {
            return true;
        }
        for (Class<?> interfaceType : type.getInterfaces()) {
            if (hasTypeName(interfaceType, typeNames, visited)) {
                return true;
            }
        }
        return hasTypeName(type.getSuperclass(), typeNames, visited);
    }

    private static Optional<Method> findNoArg(Class<?> type, String methodName) {
        try {
            Method method = type.getMethod(methodName);
            makeAccessible(method);
            return Optional.of(method);
        } catch (NoSuchMethodException ignored) {
            return findDeclaredNoArg(type, methodName);
        }
    }

    private static Optional<Method> findDeclaredNoArg(Class<?> type, String methodName) {
        Class<?> current = type;
        while (current != null) {
            for (Method method : current.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.getParameterCount() == 0) {
                    makeAccessible(method);
                    return Optional.of(method);
                }
            }
            current = current.getSuperclass();
        }
        return Optional.empty();
    }

    private static Optional<Method> findCompatible(Class<?> type, String methodName, Object[] arguments) {
        for (Method method : type.getMethods()) {
            if (isCompatible(method, methodName, arguments)) {
                makeAccessible(method);
                return Optional.of(method);
            }
        }

        Class<?> current = type;
        while (current != null) {
            for (Method method : current.getDeclaredMethods()) {
                if (isCompatible(method, methodName, arguments)) {
                    makeAccessible(method);
                    return Optional.of(method);
                }
            }
            current = current.getSuperclass();
        }
        return Optional.empty();
    }

    private static boolean isCompatible(Method method, String methodName, Object[] arguments) {
        if (!method.getName().equals(methodName) || method.getParameterCount() != arguments.length) {
            return false;
        }

        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int index = 0; index < parameterTypes.length; index++) {
            Object argument = arguments[index];
            if (argument == null) {
                if (parameterTypes[index].isPrimitive()) {
                    return false;
                }
            } else if (!wrapPrimitive(parameterTypes[index]).isInstance(argument)) {
                return false;
            }
        }
        return true;
    }

    private static Class<?> wrapPrimitive(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        if (type == boolean.class) {
            return Boolean.class;
        }
        if (type == byte.class) {
            return Byte.class;
        }
        if (type == char.class) {
            return Character.class;
        }
        if (type == short.class) {
            return Short.class;
        }
        if (type == int.class) {
            return Integer.class;
        }
        if (type == long.class) {
            return Long.class;
        }
        if (type == float.class) {
            return Float.class;
        }
        if (type == double.class) {
            return Double.class;
        }
        return Void.class;
    }

    private static void makeAccessible(Method method) {
        try {
            method.setAccessible(true);
        } catch (RuntimeException ignored) {
            // Public upstream API methods remain invokable without relaxed access.
        }
    }

    private static Optional<Object> invokeMethod(Object target, Method method, Object... arguments) {
        try {
            return Optional.ofNullable(method.invoke(target, arguments));
        } catch (IllegalAccessException | InvocationTargetException | RuntimeException ignored) {
            return Optional.empty();
        }
    }
}
