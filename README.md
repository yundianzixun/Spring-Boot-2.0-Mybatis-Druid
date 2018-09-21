# 简介

该项目主要利用Spring boot2.0 + Mybatis +Druid 实现监控数据库访问性能。
*   Druid是一个非常优秀的数据库连接池。在功能、性能、扩展性方面，都超过其他数据库连接池，包括DBCP、C3P0、BoneCP、Proxool、JBoss DataSource。
*   Druid已经在阿里巴巴部署了超过600个应用，经过一年多生产环境大规模部署的严苛考验。
*   Druid是一个JDBC组件，它包括三个部分：
    基于Filter－Chain模式的插件体系。
    DruidDataSource 高效可管理的数据库连接池。
    SQLParser



*   源码地址
    *   GitHub：[https://github.com/yundianzixun/Spring-Boot-2.0-Mybatis-Druid](https://github.com/yundianzixun/Spring-Boot-2.0-Mybatis-Druid)
*   联盟公众号：IT实战联盟
*   我们社区：[https://100boot.cn](https://100boot.cn)

**小工具一枚，欢迎使用和Star支持，如使用过程中碰到问题，可以提出Issue，我会尽力完善该Starter**

# 版本基础

*   Spring Boot：2.0.4
*   Mybatis：3.4.5
*   Druid：1.1.10

### 操作步骤
#### 第一步：下载Spring boot2.0 + Mybatis + PageHelper项目
*   GitHub地址：https://github.com/yundianzixun/Spring-boot2.0-Mybatis-PageHelper
*   参考文档：https://www.jianshu.com/p/920199133db0

#### 第二步：添加maven依赖
```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-configuration-processor</artifactId>
			<optional>true</optional>
		</dependency>
```
#### 第三步：application.properties 增加Druid监控的配置
```
# 开启哪些拦截器 stat：性能监控，wall： 防火墙控制
druid.filters=stat,wall
```
使用这个filter的配置，开启对监控的支持。属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有：
*   监控统计用的filter:stat
*   日志用的filter:log4j
*   防御sql注入的filter:wall

#### 第四步：DatasourceConfig配置信息
```
package com.itunion.springbootstarterdruid.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@MapperScan(basePackages = "com.itunion.springbootstarterdruid.mapper")
@ConfigurationProperties(prefix = "druid")

public class DatasourceConfig {
    private static Logger log = LoggerFactory.getLogger(DatasourceConfig.class);
    @Value("${druid.driver}")
    private String driverClassName;
    @Value("${druid.url}")
    private String url;
    @Value("${druid.username}")
    private String username;
    @Value("${druid.password}")
    private String password;
    @Value("${druid.init-size}")
    private int initSize;
    @Value("${druid.min-idel}")
    private int minIdel;
    @Value("${druid.max-active}")
    private int maxActive;
    @Value("${druid.login.timeout.seconds}")
    private int loginTimeoutSeconds;
    @Value("${druid.query.timeout.seconds}")
    private int queryTimeoutSeconds;
    @Value("${druid.filters}")
    private String filters;

    @Bean
    public DruidDataSource dataSource() throws SQLException {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setInitialSize(initSize);
        ds.setMinIdle(minIdel);
        ds.setMaxActive(maxActive);
        ds.setLoginTimeout(loginTimeoutSeconds);
        ds.setQueryTimeout(queryTimeoutSeconds);
        ds.setFilters(filters);
        return ds;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        final SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource());
        sqlSessionFactory.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
        sqlSessionFactory.setFailFast(true);
        return sqlSessionFactory.getObject();
    }

    public DataSourceTransactionManager dataSourceTransactionManager() throws SQLException {
        log.debug("> transactionManager");
        return new DataSourceTransactionManager(dataSource());
    }
    @PostConstruct
    public void postConstruct() {
        log.info("jdbc settings={}", this);
    }
}
```
## 备注
  * 增加@ConfigurationProperties(prefix = "druid")
  * 增加@Value("${druid.filters}")  private String filters

#### 第五步：创建DruidStatFilter 拦截器
```
package com.itunion.springbootstarterdruid.config;
import com.alibaba.druid.support.http.WebStatFilter;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

@WebFilter(filterName = "druidStatFilter", urlPatterns = "/*", asyncSupported = true,
        initParams = {
                @WebInitParam(name = "exclusions", value = "/static/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*")//忽略资源
        }
)
public class DruidStatFilter extends WebStatFilter{
}
```

#### 第六步：增加DruidStatViewServlet 访问入口
```
package com.itunion.springbootstarterdruid.config;
import com.alibaba.druid.support.http.StatViewServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
@WebServlet(urlPatterns = "/druid/*",
        initParams = {
                @WebInitParam(name = "allow", value=""),
                @WebInitParam(name="deny", value=""),// IP黑名单 (存在共同时，deny优先于allow)
                @WebInitParam(name="loginUsername", value="admin"),// 用户名
                @WebInitParam(name="loginPassword", value="123456"),// 密码
                @WebInitParam(name="resetEnable", value="false")// 禁用HTML页面上的“Reset All”功能
        })
public class DruidStatViewServlet extends StatViewServlet {

        private static final long serialVersionUID = 1L;

}

```

#### 第七步：SpringBootApplication配置
```
package com.itunion.springbootstarterdruid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@ServletComponentScan
public class SpringBootStarterDruidApplication  extends SpringBootServletInitializer {
	public static void main(String[] args) {
	SpringApplication.run(SpringBootStarterDruidApplication.class, args);
	}
}
```

#### 第八步：启动运行
```
http://127.0.0.1:8081/Demo/druid/login.html
```
## 备注
* 端口号已自己配置为准

如下图所示：
![微服务架构实战篇（四）：Spring boot2.0 + Mybatis +Druid监控数据库访问性能.jpg](https://upload-images.jianshu.io/upload_images/8122772-9c25f34c670b8716.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
输入用户名和密码：admin/123456

![微服务架构实战篇（四）：Spring boot2.0 + Mybatis +Druid监控数据库访问性能.jpg](https://upload-images.jianshu.io/upload_images/8122772-963a6d99c43d8f6f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


## 贡献者

*   [IT实战联盟-Line](https://www.jianshu.com/u/283f93ada597)
*   [IT实战联盟-咖啡](https://www.jianshu.com/u/29d607600e98)


#### 更多精彩内容可以关注“IT实战联盟”公众号哦~~~

![image](http://upload-images.jianshu.io/upload_images/8122772-b78dee4c5818c874?imageMogr2/auto-orient/strip%7CimageView2/2/w/500)
