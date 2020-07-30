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
/**
 *
 * @version : 2014年11月14日
 * @author 赵永春 (zyc@hasor.net)
 */
public class RsfException extends RuntimeException {
    private static final long  serialVersionUID = -2959224725202940531L;
    private              short status           = ProtocolStatus.Unknown;

    public RsfException(String string, Throwable e) {
        super(string, e);
    }

    public RsfException(short status, String string) {
        super("(" + status + ") - " + string);
        this.status = status;
    }

    public RsfException(short status, Throwable e) {
        this(status, e.getMessage(), e);
    }

    public RsfException(short status, String string, Throwable e) {
        super("(" + status + ") - " + string, e);
        this.status = status;
    }

    public short getStatus() {
        return this.status;
    }
}