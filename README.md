# spring-boot-starter-retrofit
这是一个用于在spring-boot微服务中集成retrofit2的starter。

在微服务架构中，除依赖基于内网RPC的内部服务外，通常还会依赖基于Http协议的外部服务。那些外部服务通常使用JSON作为序列化方案，并使用多样的认证策略、加密逻辑。

为了减少Http请求的样板代码和硬编码，可以借助Interface和Annotation实现声明式的**Api DSL**，通过动态代理劫持方法调用透明的进行Http请求。基于该思路的常用解决方案有[Feign](https://github.com/OpenFeign/feign)和[Retrofit2](https://github.com/square/retrofit)。
- Feign是Spring-Cloud生态中的Http请求组件，主要用户服务端RPC，和Spring IOC有很好的亲和性，但和Spring—Cloud系列组件耦合太深，依赖树太庞大。
- Retrofit2是一个简单轻量的Http请求门面,但主要用于安卓客户端编程，在服务端开发中往往需要写一些样板代码，没法借助Spring的IOC实现依赖注入。

该项目旨在为Retrofit2提供一个简单的胶水层，使其生成的Api Stub能透明的在Spring容器中注册，方便在业务代码中使用。

## 特性
- 继承Retrofit原生特性
- 方法级动态设置超时
- 基于Spring Event的请求事件广播
- 基于SPI引入既有HttpClient实例,方便老项目集成
- 基于Spring IOC的动态OkHttp拦截器链

## 要求
- Spring-Boot 2.x
> 该项目依赖Spring-Boot 2.0.8开发，主要依赖Spring稳定的核心Api，且没有传递Spring-Boot依赖，理论上兼容所有Spring版本。
- Java8

## 集成
该项目暂未发布到中央仓库，后续等公司内部生产环境上线后编译发布到中央仓库。
集成需要自行下载编译发布到私服，依赖坐标如下：
```xml
<plugin>
    <groupId>com.github.yungyu16.spring</groupId>
    <artifactId>spring-boot-starter-retrofit2</artifactId>
    <version>1.0.0</version>
</plugin>
```
>[查看release-tag](https://github.com/yungyu16/spring-boot-starter-retrofit2/releases) 
## 使用配置

1. 使用`@EnableRetrofitClient`指定包路径开启接口扫描。
```java
@SpringBootApplication
@EnableRetrofitClient(basePackageClasses = Test.class,basePackages = "xx.xx.xx")
public class Application {

}
```

2. 定义Stub接口
```java
@RetrofitClient(baseUrl = "${biz.tiqianyou.baseUrl}",
        responseConverterClazz = TqyResponseConverter.class,
        requestConverterClazz = TqyRequestConverter.class)
public interface TqyApiClient {
    @POST("/creditEnable")
    Call<TqyCreditEnableReply> checkIfCreditEnable(@Body TqyCreditEnableReq req);
}
```
使用`@RetrofitClient`指定请求网关地址`baseUrl`、响应转换器`ResponseConverter`、请求转换器`RequestConverter`    

3. 定义请求转换器
```java
@Component
public class RequestConverterImpl implements RequestConverter {
    @Override
    public RequestBody toRequestBody(@NotNull Object entity, Type type) {
        return RequestBody.create(JSON.toJSONBytes(entity));
    }
}
```

4. 定义响应转换器
```java
@Component
public class ResponseConverterImpl implements ResponseConverter {
    @SneakyThrows
    @Override
    public Object fromResponseBody(@NotNull ResponseBody body, Type type) {
        return JSON.parseObject(body.string(), type);
    }
}
```

5. 指定接口超时
```java
@POST("/creditEnable")
@RequestTimeout(readTimeout = 10)
Call<TqyCreditEnableReply> checkIfCreditEnable(@Body TqyCreditEnableReq req);
```
通过`@RequestTimeout`指定接口超时，单位秒。
## 拓展点
### SPI OkHttpClient提供者
如文档所述，OkHttpClient应为全局单例，为共享项目中既有的OkHttpClient实例，提供了基于SPI的提供者接口。    
默认使用`DefaultOkHttpClientLoader`构建新的OkHttpClient实例，可实现`OkHttpClientLoader`接入既有OkHttpClient实例；代码实例如下
```java
@AutoService(OkHttpClientLoader.class)
public class DefaultOkHttpClientLoader implements OkHttpClientLoader {
    private static volatile OkHttpClient HTTP_CLIENT;

    @Override
    public OkHttpClient getBaseHttpClient() {
        if (HTTP_CLIENT == null) {
            synchronized (DefaultOkHttpClientLoader.class) {
                if (HTTP_CLIENT == null) {
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(MiscConstants.log::info);
                    HTTP_CLIENT = new OkHttpClient.Builder()
                            .addInterceptor(interceptor)
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .readTimeout(5, TimeUnit.SECONDS)
                            .writeTimeout(5, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return HTTP_CLIENT;
    }
}
```
### 注解拦截器
已实现基于注解和OkHttp拦截器的拓展机制，拓展的抽象基类为`BaseMethodAnnotationInterceptor`。
![BaseMethodAnnotationInterceptor](doc/BaseMethodAnnotationInterceptor.png)
子类通过实现`BaseMethodAnnotationInterceptor.doIntercept`方法并结合自定义注解达到拦截请求实现自定义逻辑；具体思路可参考`RequestTimeoutInterceptor`。
```java
protected abstract Response doIntercept(@NotNull Method method, @NotNull T annotation, @NotNull Chain chain, @NotNull Request request) throws IOException;
```

### 动态拦截器链
实现`okhttp3.Interceptor`接口并添加`@RetrofitInterceptor`指定拦截器适用的Api存根接口即可动态配置OkHttp拦截器链。
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RetrofitInterceptor {
    /**
     * 包含的存根接口列表
     *
     * @return
     */
    Class<?>[] includeClasses() default Object.class;

    /**
     * 排除的存根接口列表
     *
     * @return
     */
    Class<?>[] excludeClasses() default Object.class;
}
```