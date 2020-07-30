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
package net.hasor.rsf.address.route.flowcontrol.unit;
import net.hasor.core.Settings;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.address.route.rule.AbstractRule;
import net.hasor.utils.MatchUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 单元流量控制规则，用来控制跨单元调用。<p>
 * <pre>例：
 * 配置实例：
 * &lt;flowControl enable="true|false" type="unit"&gt;
 *   &lt;threshold&gt;0.3&lt;/threshold&gt;
 *   &lt;exclusions&gt;172.23.*,172.19.*&lt;/exclusions&gt;
 * &lt;/flowControl&gt;
 * </pre>
 * 解释： 对某一服务，开启本单元优先调用策略
 * 但当本单元内的可用机器的数量占服务地址全部数量的比例小于0.3时，本单元优先调用策略失效，启用跨单元调用。
 * 该规则对以下网段的服务消费者不生效：172.23.*,172.19.*
 */
public class UnitFlowControl extends AbstractRule {
    private float        threshold;
    private List<String> exclusions;

    public void parseControl(Settings settings) {
        this.enable(settings.getBoolean("flowControl.enable"));
        this.threshold = settings.getFloat("flowControl.threshold");
        String exclusions = settings.getString("flowControl.exclusions");
        this.exclusions = Arrays.asList(exclusions.split(","));
    }

    public float getThreshold() {
        return this.threshold;
    }

    public List<String> getExclusions() {
        return this.exclusions;
    }

    /**
     * 是否启用本地机房优先规则
     * @param allAmount 所有可用地址数量
     * @param localAmount 本地机房地址数量
     */
    public boolean isLocalUnit(int allAmount, int localAmount) {
        if (localAmount == 0 || !this.enable()) {
            return false;
        }
        float value = (localAmount + 0.0F) / allAmount;
        if (value >= this.getThreshold()) {
            return true;
        }
        return false;
    }

    /** 筛选本机房地址 */
    public List<InterAddress> siftUnitAddress(String unitName, List<InterAddress> address) {
        if (address == null || address.isEmpty())
            return null;
        //
        List<InterAddress> local = new ArrayList<>();
        List<String> exclusions = getExclusions();
        for (InterAddress inter : address) {
            boolean appendMark = false;
            //A.如果位于规则排除名单中,则直接标记appendMark为 true
            if (exclusions != null && !exclusions.isEmpty()) {
                String hostIP = inter.getHost();
                for (String ipPattern : exclusions) {
                    if (MatchUtils.matchWild(ipPattern, hostIP)) {
                        appendMark = true;
                        break;
                    }
                }
            }
            //B.如果匹配规则,则直接标记appendMark为 true
            if (!appendMark) {
                appendMark = unitName.equalsIgnoreCase(inter.getFormUnit());
            }
            //
            if (appendMark) {
                local.add(inter);
            }
        }
        return local;
    }
}
