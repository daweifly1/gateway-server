# gateway-server
相关规范
1.业务相关非第三方包 统一前缀com.xgit.bj.
2.编码规范尽量参考《阿里巴巴编码手册》
3.业务模块结构划分,参考,例如工程名business
pom工程名称business-server-pom
server工程名称business-server
/common 通用业务包，具体业务可以在按功能细分,该功能考虑独立出来做api
/common/util/fastjson json解析的工具类
/common/localcache  
/common/thread
/dao
/dao/entity 实体信息
/dao/mapper mapper接口
/service 业务实现，事物层可以被facade、web层调用
/facade 业务层组合实现，包含组合几个service业务或者网络接口调用，对相关业务降级实现
/web 接口层  返回对象参考com.bkrwin.ufast.infra.infra.ActionResult（包名要换掉）

api工程名称business-api
/service/VO 对外模型对象，不单独命名DTO
/feigin
/feigin/fallback

ps:同名称结尾DO和VO对象互转推荐使用 BeanUtil#do2bo（同一底层提供），其他请set(get)实现，避免混乱

4.工程划分已经中间件的使用
eureka 注册中心
auth-server 用户权限中心，用户权限网关拦截  可以考虑对相应的业务使用redis
gateway-server 网关 除eureka、auth-server 不依赖其他业务，可以多实例部署
gen    snowflake生成id服务（根据业务选择使用，因为强依赖建议前台工程不使用）
。。。
业务工程

5.












