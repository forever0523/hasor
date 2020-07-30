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
package net.hasor.core.exts.aop;
import net.hasor.utils.ClassUtils;
import net.hasor.utils.MatchUtils;
import net.hasor.utils.MatchUtils.MatchTypeEnum;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @version : 2013-8-20
 * @author 赵永春 (zyc@hasor.net)
 */
public class Matchers {
    private Matchers() {
    }

    /** 匹配任意类型*/
    public static Predicate<Class<?>> anyClass() {
        return t -> true;
    }

    /** 匹配任意方法*/
    public static Predicate<Method> anyMethod() {
        return t -> true;
    }

    /** 在类型中匹配注解 */
    public static Predicate<Class<?>> annotatedWithClass(final Class<? extends Annotation> annotationType) {
        return new ClassAnnotationOf(annotationType);
    }

    /** 在方法中匹配注解 */
    public static Predicate<Method> annotatedWithMethod(final Class<? extends Annotation> annotationType) {
        return new MethodAnnotationOf(annotationType);
    }

    /** 返回一个匹配器，匹配给定类型的子类（或实现了的接口） */
    public static Predicate<Class<?>> subClassesOf(final Class<?> superclass) {
        return new SubClassesOf(superclass);
    }

    /**将表达式解析为<code>Matcher&lt;Method&gt;</code>。
     * 格式为：<code>&lt;包名&gt;.&lt;类名&gt;</code>，可以使用通配符。*/
    public static Predicate<Class<?>> expressionClass(final String matcherExpression) {
        return new ClassOf(MatchUtils.MatchTypeEnum.Wild, matcherExpression);
    }

    /**
     * 使用表达式配置Aop。
     * <p>例：<pre>格式：&lt;返回值&gt;&nbsp;&lt;类名&gt;.&lt;方法名&gt;(&lt;参数签名列表&gt;)
     *  * *.*()                  匹配：任意无参方法
     *  * *.*(*)                 匹配：任意方法
     *  * *.add*(*)              匹配：任意add开头的方法
     *  * *.add*(*,*)            匹配：任意add开头并且具有两个参数的方法。
     *  * net.test.hasor.*(*)    匹配：包“net.test.hasor”下的任意类，任意方法。
     *  * net.test.hasor.add*(*) 匹配：包“net.test.hasor”下的任意类，任意add开头的方法。
     *  java.lang.String *.*(*)  匹配：任意返回值为String类型的方法。
     * </pre>
     * @param matcherExpression 格式为“<code>&lt;返回值&gt;&nbsp;&lt;类名&gt;.&lt;方法名&gt;(&lt;参数签名列表&gt;)</code>”
     */
    public static Predicate<Method> expressionMethod(final String matcherExpression) {
        return new MethodOf(MatchTypeEnum.Wild, matcherExpression);
    }

    /** 任意一个在集合中出现的 */
    public static Predicate<Class<?>> anyClassInclude(Set<Class<?>> checkTypes) {
        return checkTypes::contains;
    }

    /** 任意一个在集合中不出现的 */
    public static Predicate<Class<?>> anyClassExcludes(Set<Class<?>> checkTypes) {
        return anyClassInclude(checkTypes).negate();
    }

    /**匹配方法*/
    private static class MethodOf implements Predicate<Method> {
        private final MatchTypeEnum matchTypeEnum;
        private final String        matcherExpression;

        public MethodOf(MatchTypeEnum matchTypeEnum, String matcherExpression) {
            this.matchTypeEnum = matchTypeEnum;
            this.matcherExpression = matcherExpression;
        }

        public boolean test(Method target) {
            String methodStr = ClassUtils.getDescNameWithOutModifiers(target);
            return MatchUtils.wildToRegex(matcherExpression, methodStr, this.matchTypeEnum);
        }

        public boolean equals(final Object other) {
            if (other instanceof MethodOf) {
                MatchTypeEnum otherMatchTypeEnum = ((MethodOf) other).matchTypeEnum;
                String otherMatcherExpression = ((MethodOf) other).matcherExpression;
                return this.matchTypeEnum.equals(otherMatchTypeEnum) && this.matcherExpression.equals(otherMatcherExpression);
            }
            return false;
        }

        public int hashCode() {
            return 37 * this.matcherExpression.hashCode() * this.matchTypeEnum.hashCode();
        }

        public String toString() {
            return "MethodOf(" + this.matchTypeEnum.name() + ")";
        }
    }

    /**匹配类名*/
    private static class ClassOf implements Predicate<Class<?>> {
        private final MatchTypeEnum matchTypeEnum;
        private final String        matcherExpression;

        public ClassOf(MatchTypeEnum matchTypeEnum, String matcherExpression) {
            this.matchTypeEnum = matchTypeEnum;
            this.matcherExpression = matcherExpression;
        }

        public boolean test(Class<?> target) {
            return MatchUtils.wildToRegex(matcherExpression, target.getName(), this.matchTypeEnum);
        }

        public boolean equals(final Object other) {
            if (other instanceof ClassOf) {
                MatchTypeEnum otherMatchTypeEnum = ((ClassOf) other).matchTypeEnum;
                String otherMatcherExpression = ((ClassOf) other).matcherExpression;
                return this.matchTypeEnum.equals(otherMatchTypeEnum) && this.matcherExpression.equals(otherMatcherExpression);
            }
            return false;
        }

        public int hashCode() {
            return 37 * this.matcherExpression.hashCode() * this.matchTypeEnum.hashCode();
        }

        public String toString() {
            return "ClassOf(" + this.matchTypeEnum.name() + ")";
        }
    }

    /**匹配子类*/
    private static class SubClassesOf implements Predicate<Class<?>> {
        private final Class<?> superclass;

        public SubClassesOf(final Class<?> superclass) {
            this.superclass = Objects.requireNonNull(superclass, "superclass");
        }

        public boolean test(final Class<?> subclass) {
            return this.superclass.isAssignableFrom(subclass);
        }

        public boolean equals(final Object other) {
            return other instanceof SubClassesOf && ((SubClassesOf) other).superclass.equals(this.superclass);
        }

        public int hashCode() {
            return 37 * this.superclass.hashCode();
        }

        public String toString() {
            return "SubClassesOf(" + this.superclass.getSimpleName() + ".class)";
        }
    }

    /**匹配类或类方法上标记的注解。*/
    private static class ClassAnnotationOf implements Predicate<Class<?>> {
        private Class<? extends Annotation> annotationType = null;

        public ClassAnnotationOf(final Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }

        public boolean test(final Class<?> matcherType) {
            Annotation[] annoByType1 = matcherType.getAnnotationsByType(this.annotationType);
            if (annoByType1 != null && annoByType1.length > 0) {
                return true;
            }
            //
            Annotation[] annoByType2 = Arrays.stream(matcherType.getMethods()).flatMap((Function<Method, Stream<?>>) method -> {
                return Arrays.stream(method.getAnnotationsByType(annotationType));
            }).toArray(Annotation[]::new);
            if (annoByType2.length > 0) {
                return true;
            }
            //
            Annotation[] annoByType3 = Arrays.stream(matcherType.getDeclaredMethods()).flatMap((Function<Method, Stream<?>>) method -> {
                return Arrays.stream(method.getAnnotationsByType(annotationType));
            }).toArray(Annotation[]::new);
            if (annoByType3.length > 0) {
                return true;
            }
            //
            return false;
        }

        public boolean equals(final Object other) {
            return other instanceof ClassAnnotationOf && ((ClassAnnotationOf) other).annotationType.equals(this.annotationType);
        }

        public int hashCode() {
            return 37 * this.annotationType.hashCode();
        }

        public String toString() {
            return "ClassAnnotationOf(" + this.annotationType.getSimpleName() + ".class)";
        }
    }

    /**匹配方法上的注解。*/
    private static class MethodAnnotationOf implements Predicate<Method> {
        private Class<? extends Annotation> annotationType = null;

        public MethodAnnotationOf(final Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }

        public boolean test(final Method matcherType) {
            if (matcherType.isAnnotationPresent(this.annotationType)) {
                return true;
            }
            if (matcherType.getDeclaringClass().isAnnotationPresent(this.annotationType)) {
                return true;
            }
            return false;
        }

        public boolean equals(final Object other) {
            return other instanceof MethodAnnotationOf && ((MethodAnnotationOf) other).annotationType.equals(this.annotationType);
        }

        public int hashCode() {
            return 37 * this.annotationType.hashCode();
        }

        public String toString() {
            return "MethodAnnotationOf(" + this.annotationType.getSimpleName() + ".class)";
        }
    }
}
