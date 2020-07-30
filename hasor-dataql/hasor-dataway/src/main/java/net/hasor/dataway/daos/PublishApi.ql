hint FRAGMENT_SQL_COLUMN_CASE = "lower";

var copyDataMap = {
    "default" : @@sql(apiId,newScript)<%
        insert into interface_release (
            pub_api_id, pub_method, pub_path,   pub_status, pub_type,   pub_script, pub_schema, pub_sample, pub_option, pub_script_ori,pub_release_time
        ) select
            api_id,     api_method,  api_path,   0,         api_type,   #{newScript},api_schema, api_sample,api_option, api_script, now()
        from interface_info where api_id = #{apiId}
    %>,
    "oracle" : @@sql(apiId,newScript)<%
        insert into interface_release (
            pub_api_id, pub_method, pub_path,   pub_status, pub_type,   pub_script, pub_schema, pub_sample, pub_option, pub_script_ori,pub_release_time
        ) select
            api_id,     api_method,  api_path,   0,         api_type,   #{newScript},api_schema, api_sample,api_option, api_script, sysdate
        from interface_info where api_id = #{apiId}
    %>,
    "sqlserver2012" : @@sql(apiId,newScript)<%
        insert into interface_release (
            pub_api_id, pub_method, pub_path,   pub_status, pub_type,   pub_script, pub_schema, pub_sample, pub_option, pub_script_ori,pub_release_time
        ) select
            api_id,     api_method,  api_path,   0,         api_type,   #{newScript},api_schema, api_sample,api_option, api_script, getdate()
        from interface_info where api_id = #{apiId}
    %>,
    "postgresql" : @@sql(apiId,newScript)<%
        insert into interface_release (
            pub_api_id, pub_method, pub_path,   pub_status, pub_type,   pub_script, pub_schema, pub_sample, pub_option, pub_script_ori,pub_release_time
        ) select
            api_id,     api_method,  api_path,   0,         api_type,   #{newScript},api_schema, api_sample,api_option, api_script, now()
        from interface_info where api_id = cast(#{apiId} as integer)
    %>
};

var copyDataExec = (copyDataMap[dbMapping] == null) ? copyDataMap["default"] : copyDataMap[dbMapping];
var res = copyDataExec(${apiId},${newScript});
if (res == 0) {
    throw 500, "copy Data to release failed.";
}

var updatePublishMap = {
    "default" : @@sql(apiId)<%
        update interface_info set api_status = 1, api_gmt_time = now() where api_id = #{apiId}
    %>,
    "oracle" : @@sql(apiId)<%
        update interface_info set api_status = 1, api_gmt_time = sysdate where api_id = #{apiId}
    %>,
    "sqlserver2012" : @@sql(apiId)<%
        update interface_info set api_status = 1, api_gmt_time = getdate() where api_id = #{apiId}
    %>,
    "postgresql" : @@sql(apiId)<%
        update interface_info set api_status = 1, api_gmt_time = now() where api_id = cast(#{apiId} as integer)
    %>
};

var updatePublishExec = (updatePublishMap[dbMapping] == null) ? updatePublishMap["default"] : updatePublishMap[dbMapping];
var res = updatePublishExec(${apiId});
if (res == 0) {
    throw 500, "update publish failed.";
}

return true;