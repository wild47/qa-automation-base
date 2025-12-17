package reflection;

import java.lang.reflect.Field;

/**
 * Utility class for common reflection operations in tests.
 **/

public class ReflectionTestUtils {

    /**
     * Get a field value from an object, regardless of access modifier.
     * Useful for verifying internal state in tests.
     *
     * @param target    the object to read from
     * @param fieldName the field name
     * @return the field value
     * @throws ReflectionException if field access fails
     */
    public static Object getFieldValue(Object target, String fieldName) {
        try {
            Field field = findField(target.getClass(), fieldName);
            boolean wasAccessible = field.canAccess(target);
            field.setAccessible(true);
            try {
                return field.get(target);
            } finally {
                field.setAccessible(wasAccessible);
            }
        } catch (Exception e) {
            throw new ReflectionException(
                    String.format("Failed to get field '%s' from %s", fieldName, target.getClass().getName()),
                    e
            );
        }
    }

    /**
     * Set a field value on an object, regardless of access modifier.
     * Useful for preparing test fixtures with specific state.
     *
     * @param target    the object to modify
     * @param fieldName the field name
     * @param value     the value to set
     * @throws ReflectionException if field access fails
     */
    public static void setFieldValue(Object target, String fieldName, Object value) {
        try {
            Field field = findField(target.getClass(), fieldName);
            boolean wasAccessible = field.canAccess(target);
            field.setAccessible(true);
            try {
                field.set(target, value);
            } finally {
                field.setAccessible(wasAccessible);
            }
        } catch (Exception e) {
            throw new ReflectionException(
                    String.format("Failed to set field '%s' on %s", fieldName, target.getClass().getName()),
                    e
            );
        }
    }

    private static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return findField(clazz.getSuperclass(), fieldName);
            }
            throw e;
        }
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new AssertionError("Field not found: " + fieldName, e);
        }
    }

    /**
     * Custom exception for reflection operations.
     */
    public static class ReflectionException extends RuntimeException {
        public ReflectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
