# Spring boot 业务核心组件工具库
## 1.0.3版本 

作用：
1. 集成mybatis-plus 、redis 、aop 组件库 。

   ```xml
   <!--详细用法，请参见：https://github.com/tntfyf/cnaworld-mybatis-plus-->
   <dependency>
             <groupId>cn.cnaworld.framework</groupId>
             <artifactId>mybatis-plus</artifactId>
             <version>{latest}</version>
   </dependency>
   
   <!--详细用法，请参见：https://github.com/tntfyf/cnaworld-redis-->
   <dependency>
            <groupId>cn.cnaworld.framework</groupId>
            <artifactId>redis</artifactId>
            <version>{latest}</version>
   </dependency>
   
   <!--详细用法，请参见：https://github.com/tntfyf/cnaworld-aop-->
   <dependency>
             <groupId>cn.cnaworld.framework</groupId>
             <artifactId>aop</artifactId>
             <version>{latest}</version>
   </dependency>
   ```

2. 提供常用工具组件库，所有工具方法均可直接静态调用，无需注入。

   ```
   CnaRedisUtil ： 分布式缓存、分布式锁
   
   CnaMd5Util ： MD5加密
   
   CnaAesUtil ： 对称加密，提供16进制及BASE64编码格式
   
   CnaHttpClientUtil：提供restful 请求支持及日志打印 ， 提供忽略ssl认证请求方法
   
   CnaCommonUrlUtil ：提供统一地址配置管理，可在yaml进行地址管理，缓存采用redisson LocalCachedMap , 一台客户端修改后，会通过redis订阅发布机制同步到其他客户端，实现去中心化轻量级地址配置中心。
   
   CnaSysConfigUtil ：自定义配置工具，配置到yaml的配置可直接通过静态方法获取。提供获取系统IP，ApplicationName，ProfilesActive等通过工具方法。
   
   CnaCodeUtil ：提供单机的UUID、ID、GUID、雪花ID的生成，其中GUID可生成N位随机字符串，可配置含数字 + 大小写,可以用于任意验证码密码等随机字符生成。
   
   BusinessException：自定义业务异常
   
   HttpCodeConstant：HTTP code码定义
   
   ResponseResult：统一响应包装类
   ```

3. 客户端配置

   pom.xml 引入依赖

   ```xml
   <dependency>
       <groupId>cn.cnaworld.framework</groupId>
       <artifactId>core</artifactId>
       <version>1.0.3</version>
   </dependency>
   ```

   application.yml 配置

   ```yaml
   cnaworld:
     common-url: #通用地址
       host-name:  #地址名称集合
         baidu:  #自定义地址名称
           host: "https://www.baidu.com" #自定义地址
           path-name:  #路径名称集合
             query: "/s?wd=enum" #自定义路径query
             view: "/v?wd=enum" #自定义路径view
     system-config: #通用配置
       config-name: #配置名称集合
         sever-code: "test-core"  #自定义配置
   ```

4. 项目启动时进行注册

   ```lua
   2023-03-10 04:04:27.035  INFO 14004 --- [           main] c.c.f.i.u.resources.CnaCommonUrlUtil     : CnaCommonUrlUtil  initialized ！
   2023-03-10 04:04:27.042  INFO 14004 --- [           main] c.c.f.i.u.resources.CnaSysConfigUtil     : CnaSysConfigUtil  initialized ！
   ```

5. 调用方式

   ```java
   //获取自定义地址 baidu
   String baidu = CnaCommonUrlUtil.getCommonUrl("baidu");
   //获取自定义地址 baidu + query
   String baiduQuery = CnaCommonUrlUtil.getCommonUrl("baidu","query");
   //获取自定义配置 sever-code
   String severCode = CnaSysConfigUtil.getCnaConfigByName("sever-code");  
   
   ```
