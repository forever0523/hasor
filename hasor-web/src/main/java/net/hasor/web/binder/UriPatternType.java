/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.web.binder;
import java.util.regex.Pattern;

/**
 * An enumeration of the available URI-pattern matching styles
 * @since 3.0
 */
public enum UriPatternType {
    SERVLET, REGEX;

    public static UriPatternMatcher get(final UriPatternType type, final String pattern) {
        if (type == SERVLET) {
            return new ServletStyleUriPatternMatcher(pattern);
        }
        if (type == REGEX) {
            return new RegexUriPatternMatcher(pattern);
        }
        return null;
    }

    /**
     * Matches URIs using the pattern grammar of the Servlet API and web.xml.
     * @author dhanji@gmail.com (Dhanji R. Prasanna)
     */
    private static class ServletStyleUriPatternMatcher implements UriPatternMatcher {
        private final String pattern;
        private final Kind   patternKind;

        private static enum Kind {
            PREFIX, SUFFIX, LITERAL, WITHROOT,
        }

        public ServletStyleUriPatternMatcher(final String pattern) {
            if (pattern.startsWith("*")) {
                this.pattern = pattern.substring(1);
                this.patternKind = Kind.PREFIX;
            } else if (pattern.endsWith("*")) {
                this.pattern = pattern.substring(0, pattern.length() - 1);
                this.patternKind = Kind.SUFFIX;
            } else if (pattern.startsWith("/")) {
                this.pattern = pattern;
                this.patternKind = Kind.WITHROOT;
            } else {
                this.pattern = pattern;
                this.patternKind = Kind.LITERAL;
            }
        }

        @Override
        public boolean matches(final String uri) {
            if (null == uri) {
                return false;
            }
            if (this.patternKind == Kind.PREFIX) {
                return uri.endsWith(this.pattern);
            } else if (this.patternKind == Kind.SUFFIX) {
                return uri.startsWith(this.pattern);
            } else if (this.patternKind == Kind.WITHROOT) {
                return this.pattern.equals(uri);
            }
            //else treat as a literal
            return this.pattern.equals(uri.substring(1));
        }

        @Override
        public UriPatternType getPatternType() {
            return UriPatternType.SERVLET;
        }

        @Override
        public String getPattern() {
            return pattern;
        }
    }

    /**
     * Matches URIs using a regular expression.
     * @author dhanji@gmail.com (Dhanji R. Prasanna)
     */
    private static class RegexUriPatternMatcher implements UriPatternMatcher {
        private final Pattern pattern;

        public RegexUriPatternMatcher(final String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        @Override
        public boolean matches(final String uri) {
            return null != uri && this.pattern.matcher(uri).matches();
        }

        @Override
        public UriPatternType getPatternType() {
            return UriPatternType.REGEX;
        }

        @Override
        public String getPattern() {
            return pattern.pattern();
        }
    }
}