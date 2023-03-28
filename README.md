# Spring boot 业务核心组件工具库
## 1.1.1版本 

作用：
1. 下沉资源层及日志层核心方法

2. 提供常用工具组件库，所有工具方法均可直接静态调用，无需注入。

   ```
   CnaLogUtil ： 可针对包和类切换打印等级日志
   
   CnaSysConfigUtil ：自定义配置工具，配置到yaml的配置可直接通过静态方法获取。提供获取系统IP，ApplicationName，ProfilesActive等通过工具方法。
   ```

3. 客户端配置

   pom.xml 引入依赖

   ```xml
   <dependency>
       <groupId>cn.cnaworld.framework</groupId>
       <artifactId>core</artifactId>
       <version>{latest}</version>
   </dependency>
   ```

   application.yml 配置

   ```yaml
   cnaworld:
     system-config: #通用配置
       config-name: #配置名称集合
         sever-code: "test-core"  #自定义配置
     log:
       log-properties:
         #设置当前包及子包的日志等级
         - path-name: cn.cnaworld.base.domain.student.controller
           log-level: error
         - path-name: cn.cnaworld.base.domain.student
           log-level: warn
         - path-name: cn.cnaworld.base.domain.student.controller.StudentController
           log-level: error
         - path-name: cn.cnaworld.framework.infrastructure.config
           log-level: warn
   ```

4. 项目启动时进行注册

   ```lua
   2023-03-10 04:04:27.042  INFO 14004 --- [           main] c.c.f.i.u.resources.CnaSysConfigUtil     : CnaSysConfigUtil  initialized ！
   ```

5. 调用方式

   ```java
CnaLogUtil.trace(log,"cnaworld aop register CnaworldAopBeanFactoryPostProcessor start");
CnaLogUtil.debug(log,"cnaworld aop register CnaworldAopBeanFactoryPostProcessor start");
CnaLogUtil.info(log,"cnaworld aop register CnaworldAopBeanFactoryPostProcessor start");
CnaLogUtil.warn(log,"cnaworld aop register CnaworldAopBeanFactoryPostProcessor start");
CnaLogUtil.error(log,"cnaworld aop register CnaworldAopBeanFactoryPostProcessor start");
   ```
