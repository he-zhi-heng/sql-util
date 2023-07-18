package com.he.sqlutils.utils;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hemoren
 */
public class ReflectUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ReflectUtil.class);

    private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is)[A-Z].*");
    private static final Pattern SETTER_PATTERN = Pattern.compile("set[A-Z].*");

    public static ClassLoader getClassLoader() {
        ClassLoader loader = getCustomClassLoader();
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        return loader;
    }

    /**
     * 加载类利用自定义类加载器
     * 
     * @param className
     * @return
     */
    public static Class<?> loadClass(String className) {
        Class<?> clazz = null;
        ClassLoader classLoader = getCustomClassLoader();

        // First exception in chain of classloaders will be used as cause when
        // no class is found in any of them
        Throwable throwable = null;

        if (classLoader != null) {
            try {
                LOG.trace("Trying to load class with custom classloader: {}", className);
                clazz = loadClass(classLoader, className);
            } catch (Throwable t) {
                throwable = t;
            }
        }
        if (clazz == null) {
            try {
                LOG.trace("Trying to load class with current thread context classloader: {}", className);
                clazz = loadClass(Thread.currentThread().getContextClassLoader(), className);
            } catch (Throwable t) {
                if (throwable == null) {
                    throwable = t;
                }
            }
            if (clazz == null) {
                try {
                    LOG.trace("Trying to load class with local classloader: {}", className);
                    clazz = loadClass(ReflectUtil.class.getClassLoader(), className);
                } catch (Throwable t) {
                    if (throwable == null) {
                        throwable = t;
                    }
                }
            }
        }

        if (clazz == null) {
            throw new RuntimeException(className, throwable);
        }
        return clazz;
    }

    /**
     * 获取资源文件流
     * 
     * @param name
     * @return
     */
    public static InputStream getResourceAsStream(String name) {
        InputStream resourceStream = null;
        ClassLoader classLoader = getCustomClassLoader();
        if (classLoader != null) {
            resourceStream = classLoader.getResourceAsStream(name);
        }

        if (resourceStream == null) {
            // Try the current Thread context classloader
            classLoader = Thread.currentThread().getContextClassLoader();
            resourceStream = classLoader.getResourceAsStream(name);
            if (resourceStream == null) {
                // Finally, try the classloader for this class
                classLoader = ReflectUtil.class.getClassLoader();
                resourceStream = classLoader.getResourceAsStream(name);
            }
        }
        return resourceStream;
    }

    /**
     * 获取资源文件URL
     * 
     * @param name
     * @return
     */
    public static URL getResource(String name) {
        URL url = null;
        ClassLoader classLoader = getCustomClassLoader();
        if (classLoader != null) {
            url = classLoader.getResource(name);
        }
        if (url == null) {
            // Try the current Thread context classloader
            classLoader = Thread.currentThread().getContextClassLoader();
            url = classLoader.getResource(name);
            if (url == null) {
                // Finally, try the classloader for this class
                classLoader = ReflectUtil.class.getClassLoader();
                url = classLoader.getResource(name);
            }
        }
        return url;
    }

    /**
     * 实例化类
     * 
     * @param className
     * @return
     */
    public static Object instantiate(String className) {
        try {
            Class<?> clazz = loadClass(className);
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("couldn't instantiate class " + className, e);
        }
    }

    /**
     * 函数调用
     * 
     * @param className
     * @param type
     * @return
     */
    public static Object invoke(Object target, String methodName, Object[] args) {
        try {
            Class<? extends Object> clazz = target.getClass();
            Method method = findMethod(clazz, methodName, args);
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException("couldn't invoke " + methodName + " on " + target, e);
        }
    }

    /**
     * Returns the field of the given object or null if it doesn't exist.
     * 返回给定对象的字段或如果不存在则为null
     */
    public static Field getField(String fieldName, Object object) {
        return getField(fieldName, object.getClass());
    }

    /**
     * 获取类的所有字段
     */
    public static Field[] getAllFields(Class<?> clazz) {
        return getAllFields(clazz, null);
    }

    private static Field[] getAllFields(Class<?> clazz, Object object) {
        return getAllFields(clazz, object, true);
    }

    private static Field[] getAllFields(Class<?> clazz, Object object, boolean b) {
        Field[] fields = clazz.getDeclaredFields();
        if (clazz.getSuperclass() != null) {
            Field[] superFields = getAllFields(clazz.getSuperclass(), object, false);
            if (superFields != null && superFields.length > 0) {
                Field[] allFields = new Field[fields.length + superFields.length];
                System.arraycopy(fields, 0, allFields, 0, fields.length);
                System.arraycopy(superFields, 0, allFields, fields.length, superFields.length);
                fields = allFields;
            }
        }

        if (b) {
            for (Field field : fields) {
                field.setAccessible(true);
            }
        }

        return fields;
    }

    /**
     * Returns the field of the given class or null if it doesn't exist.
     * 返回给定类的字段或如果不存在则为null
     */
    public static Field getField(String fieldName, Class<?> clazz) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (SecurityException e) {
            throw new RuntimeException(
                    "not allowed to access field " + field + " on class " + clazz.getCanonicalName());
        } catch (NoSuchFieldException e) {
            // for some reason getDeclaredFields doesn't search superclasses
            // 由于某种原因，getDeclaredFields() 不会搜索超类。
            // (which getFields() does ... but that gives only public fields)
            // (这会返回公共字段 ... 但是 getFields() 只返回公共字段)
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getField(fieldName, superClass);
            }
        }
        return field;
    }

    /**
     * 设置属性
     * 
     * @param field
     * @param object
     * @param value
     */
    public static void setField(Field field, Object object, Object value) {
        try {
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Could not set field " + field.toString(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not set field " + field.toString(), e);
        }
    }

    /**
     * Returns the setter-method for the given field name or null if no setter
     * exists.
     * 返回给定字段名称的setter方法或如果不存在setter则为null
     */
    public static Method getSetter(String fieldName, Class<?> clazz, Class<?> fieldType) {
        String setterName = "set" + Character.toTitleCase(fieldName.charAt(0))
                + fieldName.substring(1, fieldName.length());
        try {
            // Using getMethods(), getMethod(...) expects exact parameter type
            // 使用 getMethods() 和 getMethod(...) 方法时，期望参数类型必须完全匹配。
            // matching and ignores inheritance-tree.
            // 匹配并忽略继承树。
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(setterName)) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (paramTypes != null && paramTypes.length == 1 && paramTypes[0].isAssignableFrom(fieldType)) {
                        return method;
                    }
                }
            }
            return null;
        } catch (SecurityException e) {
            throw new RuntimeException(
                    "Not allowed to access method " + setterName + " on class " + clazz.getCanonicalName());
        }
    }

    /**
     * 查找指定方法
     * 
     * @param clazz
     * @param methodName
     * @param args
     * @return
     */
    private static Method findMethod(Class<? extends Object> clazz, String methodName, Object[] args) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && matches(method.getParameterTypes(), args)) {
                return method;
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            return findMethod(superClass, methodName, args);
        }
        return null;
    }

    /**
     * 实例化类
     * 
     * @param className
     * @param args
     * @return
     */
    public static Object instantiate(String className, Object[] args) {
        Class<?> clazz = loadClass(className);
        Constructor<?> constructor = findMatchingConstructor(clazz, args);
        if (constructor == null) {
            throw new RuntimeException(
                    "couldn't find constructor for " + className + " with args " + Arrays.asList(args));
        }
        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(
                    "couldn't find constructor for " + className + " with args " + Arrays.asList(args), e);
        }
    }

    /**
     * 查找匹配的构造函数
     * 
     * @param <T>
     * @param clazz
     * @param args
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> Constructor<T> findMatchingConstructor(Class<T> clazz, Object[] args) {
        for (Constructor constructor : clazz.getDeclaredConstructors()) {
            // cannot use <?> or <T> due to JDK 5/6 incompatibility
            // 在Java 5和Java 6之间存在兼容性问题，不能使用<?>或<T>这样的表达式
            if (matches(constructor.getParameterTypes(), args)) {
                return constructor;
            }
        }
        return null;
    }

    /**
     * 匹配参数
     * 
     * @param parameterTypes
     * @param args
     * @return
     */
    private static boolean matches(Class<?>[] parameterTypes, Object[] args) {
        if ((parameterTypes == null) || (parameterTypes.length == 0)) {
            return ((args == null) || (args.length == 0));
        }
        if ((args == null) || (parameterTypes.length != args.length)) {
            return false;
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            if ((args[i] != null) && (!parameterTypes[i].isAssignableFrom(args[i].getClass()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * getCustomClassLoader()方法是用于获取Spring
     * Boot应用的自定义类加载器，如果当前线程不为空，则获取当前线程的上下文类加载器，然后判断该类加载器是否为null，如果不为null，则返回该类加载器。如果当前线程为空，则返回null。
     * 
     * @return classLoader
     */
    private static ClassLoader getCustomClassLoader() {
        Thread currentThread = Thread.currentThread();
        if (currentThread != null) {
            final ClassLoader classLoader = currentThread.getContextClassLoader();
            if (classLoader != null) {
                return classLoader;
            }
        }
        return null;
    }

    /**
     * loadClass(ClassLoader classLoader, String
     * className)方法是用于加载指定类的方法，它接受一个类加载器和一个类名作为参数，并返回该类的Class对象。它首先获取当前线程的上下文类加载器，然后判断该类加载器是否为null或者与传入的类加载器是否相等，如果都不为null，则直接使用该类加载器加载类。如果都为null或者不相等，则使用传入的类加载器加载类。
     * 
     * @param classLoader
     * @param className
     * @return class
     * @throws ClassNotFoundException
     */
    private static Class<?> loadClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        boolean useClassForName = contextClassLoader == null || contextClassLoader.equals(classLoader);
        return useClassForName ? Class.forName(className, true, classLoader) : classLoader.loadClass(className);
    }

    /**
     * 标识
     */
    static String is = "is";
    static String get = "get";
    static String set = "set";

    public static boolean isGetter(Method method) {
        String name = method.getName();
        Class<?> type = method.getReturnType();
        Class<?>[] params = method.getParameterTypes();

        if (!GETTER_PATTERN.matcher(name).matches()) {
            return false;
        }
        // special for isXXX boolean
        if (name.startsWith(is)) {
            return params.length == 0 && type.getSimpleName().equalsIgnoreCase("boolean");
        }

        return params.length == 0 && !type.equals(Void.TYPE);
    }

    public static boolean isSetter(Method method, boolean allowBuilderPattern) {
        String name = method.getName();
        Class<?> type = method.getReturnType();
        Class<?>[] params = method.getParameterTypes();

        if (!SETTER_PATTERN.matcher(name).matches()) {
            return false;
        }

        return params.length == 1 && (type.equals(Void.TYPE)
                || (allowBuilderPattern && method.getDeclaringClass().isAssignableFrom(type)));
    }

    public static boolean isSetter(Method method) {
        return isSetter(method, false);
    }

    public static String getGetterShorthandName(Method method) {
        if (!isGetter(method)) {
            return method.getName();
        }

        String name = method.getName();
        if (name.startsWith(get)) {
            name = name.substring(3);
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        } else if (name.startsWith(is)) {
            name = name.substring(2);
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }

    public static String getSetterShorthandName(Method method) {
        if (!isSetter(method)) {
            return method.getName();
        }

        String name = method.getName();
        if (name.startsWith(set)) {
            name = name.substring(3);
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }
}
