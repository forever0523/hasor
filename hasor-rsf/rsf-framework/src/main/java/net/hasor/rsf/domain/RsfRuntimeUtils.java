/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.rsf.domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @version : 2014年11月17日
 * @author 赵永春 (zyc@hasor.net)
 */
public class RsfRuntimeUtils {
    protected static Logger                                                 logger      = LoggerFactory.getLogger(RsfRuntimeUtils.class);
    private static   AtomicLong                                             requestID   = new AtomicLong(1);
    private static   ConcurrentMap<String, Class<?>>                        classCache  = new ConcurrentHashMap<>();
    private static   ConcurrentMap<Class<?>, ConcurrentMap<String, Method>> methodCache = new ConcurrentHashMap<>();

    /**生成一个新的RequestID*/
    public static long genRequestID() {
        return requestID.incrementAndGet();
    }

    /**使用指定的ClassLoader将一个asm类型转化为Class对象。*/
    public static Class<?> toJavaType(final String tType, final ClassLoader loader) throws ClassNotFoundException {
        char atChar = tType.charAt(0);
        if (/*   */'I' == atChar) {
            return int.class;
        } else if ('B' == atChar) {
            return byte.class;
        } else if ('C' == atChar) {
            return char.class;
        } else if ('D' == atChar) {
            return double.class;
        } else if ('F' == atChar) {
            return float.class;
        } else if ('J' == atChar) {
            return long.class;
        } else if ('S' == atChar) {
            return short.class;
        } else if ('Z' == atChar) {
            return boolean.class;
        } else if ('V' == atChar) {
            return void.class;
        } else if (atChar == '[') {
            int length = 0;
            while (true) {
                if (tType.charAt(length) != '[') {
                    break;
                }
                length++;
            }
            String arrayType = tType.substring(length);
            Class<?> returnType = toJavaType(arrayType, loader);
            for (int i = 0; i < length; i++) {
                Object obj = Array.newInstance(returnType, length);
                returnType = obj.getClass();
            }
            return returnType;
        } else {
            Class<?> cache = classCache.get(tType);
            if (cache == null) {
                cache = loader.loadClass(tType);
                classCache.put(tType, cache);
            }
            return cache;
        }
    }
    //

    /**将某一个类型转为asm形式的表述， int 转为 I，String转为 Ljava/lang/String。*/
    public static String toAsmType(final Class<?> classType) {
        if (classType == int.class) {
            return "I";
        } else if (classType == byte.class) {
            return "B";
        } else if (classType == char.class) {
            return "C";
        } else if (classType == double.class) {
            return "D";
        } else if (classType == float.class) {
            return "F";
        } else if (classType == long.class) {
            return "J";
        } else if (classType == short.class) {
            return "S";
        } else if (classType == boolean.class) {
            return "Z";
        } else if (classType == void.class) {
            return "V";
        } else if (classType.isArray()) {
            return "[" + toAsmType(classType.getComponentType());
        } else {
            return classType.getName();
        }
    }

    public static Method getServiceMethod(Class<?> serviceType, String methodName, Class<?>[] parameterTypes) {
        StringBuilder oriStr = new StringBuilder(methodName);
        if (parameterTypes != null) {
            for (Class<?> paramType : parameterTypes) {
                oriStr.append(paramType.getName());
            }
        }
        ConcurrentMap<String, Method> methodMap = methodCache.get(serviceType);
        if (methodMap == null) {
            ConcurrentMap<String, Method> newMethodMap = new ConcurrentHashMap<String, Method>();
            methodMap = methodCache.putIfAbsent(serviceType, newMethodMap);
            if (methodMap == null) {
                methodMap = newMethodMap;
            }
        }
        //
        String finalStr = oriStr.toString();
        Method method = methodMap.get(finalStr);
        if (method == null) {
            try {
                Method newMethod = serviceType.getMethod(methodName, parameterTypes);
                method = methodMap.putIfAbsent(finalStr, newMethod);
                if (method == null) {
                    method = newMethod;
                }
            } catch (Exception e) {
                logger.error("find method {} of type {} fail -> {}", methodName, serviceType, e);
                if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
                String errorMessage = "(" + e.getClass().getName() + ") - " + e.getMessage();
                throw new RsfException(errorMessage, e);
            }
        }
        return method;
    }

    public static Class<?> getType(String typeName, ClassLoader classLoader) {
        Class<?> type = classCache.get(typeName);
        if (type == null) {
            try {
                Class<?> newType = toJavaType(typeName, classLoader);
                type = classCache.putIfAbsent(typeName, newType);
                if (type == null) {
                    type = newType;
                }
            } catch (Throwable e) {
                logger.error("find of type {} fail -> {}", typeName, e);
                if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
                String errorMessage = "(" + e.getClass().getName() + ") - " + e.getMessage();
                throw new RsfException(errorMessage, e);
            }
        }
        return type;
    }
}