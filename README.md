# spring-boot-starter-retrofit
这是一个用于在spring-boot微服务中集成retrofit2的starter。

在微服务架构中，除了依赖基于内网RPC的内部服务外，通常还会依赖基于Http协议的外部服务。那些外部服务通常使用JSON作为序列化方案，并使用多样的认证策略、加密逻辑。

为了减少Http请求的样板代码和硬编码，可以借助Interface和Annotation实现声明式的**Api DSL**，通过动态代理劫持方法调用透明的进行Http请求。基于该思路的常用解决方案有[Feign](https://github.com/OpenFeign/feign)和[Retrofit2](https://github.com/square/retrofit)。
- Feign是Spring-Cloud生态中的Http请求组件，主要用户服务端RPC，和Spring IOC有很好的亲和性，但和Spring—Cloud系列组件耦合太深，依赖树太庞大。
- Retrofit2是一个简单轻量的Http请求门面,但主要用于安卓客户端编程，在服务端开发中往往需要写一些样板代码，没法借助Spring的IOC实现依赖注入。

该项目旨在为Retrofit2提供一个简单的胶水层，使其生成的Stub能透明的在Spring容器中注册，方便在业务代码中使用。

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
## 拓展思路
通过指定接口方法上的注解继承`BaseMethodAnnotationInterceptor`，可以实现更多的定制化。
具体可参考如下请求超时的拦截器实现：
```java
public class RequestTimeoutInterceptor extends BaseMethodAnnotationInterceptor<RequestTimeout> {
    public RequestTimeoutInterceptor() {
        super(RequestTimeout.class);
    }

    @Override
    protected Response doIntercept(@NotNull RequestTimeout annotation, @NotNull Chain chain, @NotNull Request request) throws IOException {
        int connectTimeout = annotation.connectTimeout();
        if (connectTimeout > 0) {
            chain = chain.withConnectTimeout(connectTimeout, TimeUnit.SECONDS);
        }
        int readTimeout = annotation.readTimeout();
        if (readTimeout > 0) {
            chain = chain.withReadTimeout(readTimeout, TimeUnit.SECONDS);
        }
        int writeTimeout = annotation.writeTimeout();
        if (writeTimeout > 0) {
            chain = chain.withWriteTimeout(writeTimeout, TimeUnit.SECONDS);
        }
        return chain.proceed(request);
    }
}
```