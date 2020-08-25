package com.luxf.cloud.eureka.feign.client.service;

import com.luxf.cloud.api.service.HelloApiController;
import com.luxf.cloud.eureka.feign.client.service.fallback.FeignApiServiceFallbackFactory;
import com.netflix.client.ClientRequest;
import com.netflix.client.config.IClientConfig;
import com.netflix.hystrix.HystrixCommand;
import feign.Client;
import feign.InvocationHandlerFactory;
import feign.Request;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.ribbon.FeignLoadBalancer;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;

/**
 * {@link FeignClient}注解内容不可少、对于可以访问HELLO-SERVICE服务的接口具体内容已由{@link HelloApiController}定义完成
 * <p>
 * TODO: 只能使用{@link FeignClient#fallbackFactory()}处理Hystrix降价、 FallbackFactory 的优点是可以获取到异常信息。
 * 如果{@link FeignClient}的接口继承了对应的服务的API接口, 只能使用'fallbackFactory',不能使用'fallback',使用'fallback'会报错：存在映射路径相同的问题！
 * <p>
 * 对于{@link FeignClient}的接口的Bean对象,与{@link feign.hystrix.HystrixInvocationHandler}拦截器绑定,
 * 当在调用{@link FeignClient}接口的方法时,都会被{@link feign.hystrix.HystrixInvocationHandler#invoke(Object, Method, Object[])}所拦截,
 * 由{@link HystrixCommand#run()}进行具体方法的invoke()操作！
 *
 * TODO：该run()方法中,会使用调用{@link feign.SynchronousMethodHandler#invoke(Object[])}
 * protected Object run() throws Exception {
 *      try {
 *          // HystrixInvocationHandler.this.dispatch.get(method)
 *          // 获取到的对象是{@link InvocationHandlerFactory.MethodHandler}的实现类{@link feign.SynchronousMethodHandler}.
 *          return HystrixInvocationHandler.this.dispatch.get(method).invoke(args);
 *      } catch (Exception e) {
 *          throw e;
 *      }
 * }
 *
 * // {@link feign.SynchronousMethodHandler#invoke(Object[])}详解
 * public Object invoke(Object[] argv) throws Throwable {
 *     // TODO：创建RequestTemplate对象、
 *     RequestTemplate template = buildTemplateFromArgs.create(argv);
 *     Options options = findOptions(argv);
 *     ...
 *     while (true) {
 *       try {
 *         // TODO：利用RequestTemplate对象发起请求、在executeAndDecode()方法中,主要是 client.execute(request, options); 发起Http请求、
 *         // {@link Client#execute(Request, Request.Options)}、
 *         // TODO：该接口的实现{@link LoadBalancerFeignClient#execute(Request, Request.Options)},在具体实现中,
 *                  最终由{@link FeignLoadBalancer#executeWithLoadBalancer(ClientRequest, IClientConfig)}发起负载均衡的HTTP请求。
 *         return executeAndDecode(template, options);
 *       } catch (RetryableException e) {
 *         ...
 *       }
 *     }
 *   }
 *
 *   TODO：{@link LoadBalancerFeignClient#execute(Request, Request.Options)} 发起负载均衡的HTTP请求的具体实现、
 *   public Response execute(Request request, Request.Options options) throws IOException {
 * 		try {
 * 			URI asUri = URI.create(request.url());
 * 			String clientName = asUri.getHost();
 * 			URI uriWithoutHost = cleanUrl(request.url(), clientName);
 * 		    // TODO：构造Ribbon的Request对象
 * 			FeignLoadBalancer.RibbonRequest ribbonRequest = new FeignLoadBalancer.RibbonRequest(this.delegate, request, uriWithoutHost);
 *
 *          // 初始化{@link IClientConfig}、TODO：调用{@link NamedContextFactory#getInstance(String, Class)},最终触发主动加载{@link FeignClientsConfiguration}
 * 			IClientConfig requestConfig = getClientConfig(options, clientName);
 *
 * 		    // lbClient(clientName)、TODO：该方法返回{@link FeignLoadBalancer}对象、执行负载均衡的HTTP请求.
 * 			return lbClient(clientName).executeWithLoadBalancer(ribbonRequest, requestConfig).toResponse();
 *      } catch (ClientException e) {
 * 			throw new RuntimeException(e);
 *      }
 *	}
 * <p>
 *
 * TODO：Ribbon和Feign负载均衡的区别.
 * Ribbon是利用{@link LoadBalancerInterceptor}拦截器,绑定到{@link RestTemplate}上,拦截{@link RestTemplate}的方法,进行负载均衡。
 *
 * @author 小66
 * @date 2020-08-23 12:59
 **/
//@FeignClient(value = "HELLO-SERVICE", fallback = FeignApiServiceFallback.class) // 由于FeignApiService继承了HelloApiController接口、使用fallback会报错
@FeignClient(value = "HELLO-SERVICE", fallbackFactory = FeignApiServiceFallbackFactory.class)
public interface FeignApiService extends HelloApiController {
}
