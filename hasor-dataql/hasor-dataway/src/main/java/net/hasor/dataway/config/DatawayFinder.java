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
package net.hasor.dataway.config;
import net.hasor.core.AppContext;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.binder.AppContextFinder;
import net.hasor.dataql.domain.ListModel;
import net.hasor.dataway.daos.GetScriptByPathQuery;
import net.hasor.utils.ResourcesUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Dataway 启动入口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
@Singleton
public class DatawayFinder extends AppContextFinder {
    private final DataQL dataQL;

    @Inject
    public DatawayFinder(AppContext appContext) {
        super(appContext);
        this.dataQL = appContext.getInstance(DataQL.class);
    }

    /** 负责处理 <code>import @"/net/hasor/demo.ql" as demo;</code>方式中 ‘/net/hasor/demo.ql’ 资源的加载 */
    public InputStream findResource(final String resourceName) throws IOException {
        if (resourceName.toLowerCase().startsWith("classpath:")) {
            String newResourceName = resourceName.substring("classpath:".length());
            return ResourcesUtils.getResourceAsStream(newResourceName);
        } else {
            QueryResult queryResult = new GetScriptByPathQuery(this.dataQL).execute(new HashMap<String, String>() {{
                put("apiPath", resourceName);
            }});
            //pub_script
            ListModel listModel = (ListModel) queryResult.getData();
            if (listModel == null || listModel.size() == 0) {
                throw new NullPointerException("import compiler failed -> '" + resourceName + "' not found.");
            }
            String scriptBody = listModel.getObject(0).getValue("pub_script").asString();
            return new ByteArrayInputStream(scriptBody.getBytes());
        }
    }
}