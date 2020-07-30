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
package net.hasor.registry.common;
/**
 * center工作模式
 * @version : 2017年2月16日
 * @author 赵永春 (zyc@hasor.net)
 */
public enum CenterMode {
    None("none", "禁用（默认）"),//
    Client("client", "注册中心-客户端"),//
    Server("server", "注册中心-服务器(单机)"),//
    Cluster("cluster", "注册中心-服务器(集群)"),//
    Leader("leader", "注册中心-服务器(集群Leader)"),//
    ;
    //
    private String workType;
    private String desc;

    CenterMode(String workType, String desc) {
        this.workType = workType;
        this.desc = desc;
    }

    public String getWorkType() {
        return workType;
    }

    public String getDesc() {
        return desc;
    }
}