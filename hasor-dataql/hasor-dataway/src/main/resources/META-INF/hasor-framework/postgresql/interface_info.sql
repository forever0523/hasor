create table interface_info (
    api_id          serial constraint pk_interface_info primary key,
    api_method      varchar(12)  NOT NULL,
    api_path        varchar(512) NOT NULL,
    api_status      smallint     NOT NULL,
    api_comment     varchar(255)     NULL,
    api_type        varchar(24)  NOT NULL,
    api_script      text         NOT NULL,
    api_schema      text             NULL,
    api_sample      text             NULL,
    api_option      text             NULL,
    api_create_time timestamp    DEFAULT(now()),
    api_gmt_time    timestamp    DEFAULT(now())
);
create unique index idx_interface_info on interface_info (api_method, api_path);

comment on table interface_info is 'Dataway 中的API';
comment on column interface_info.api_id is 'ID';
comment on column interface_info.api_method is 'HttpMethod：GET、PUT、POST';
comment on column interface_info.api_path is '拦截路径';
comment on column interface_info.api_status is '状态：0草稿，1发布，2有变更，3禁用';
comment on column interface_info.api_comment is '注释';
comment on column interface_info.api_type is '脚本类型：SQL、DataQL';
comment on column interface_info.api_script is '查询脚本：xxxxxxx';
comment on column interface_info.api_schema is '接口的请求/响应数据结构';
comment on column interface_info.api_sample is '请求/响应/请求头样本数据';
comment on column interface_info.api_option is '扩展配置信息';
comment on column interface_info.api_create_time is '创建时间';
comment on column interface_info.api_gmt_time is '修改时间';