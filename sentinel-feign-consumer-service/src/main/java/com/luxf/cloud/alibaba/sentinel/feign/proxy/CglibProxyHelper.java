package com.luxf.cloud.alibaba.sentinel.feign.proxy;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.luxf.cloud.alibaba.sentinel.feign.service.FeignClientService;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.reflect.Method;

/**
 * CGLIB的方法拦截器、
 * 实现{@link org.springframework.cglib.proxy.MethodInterceptor}接口的intercept()方法、
 *
 * @author 小66
 * @Description
 * @create 2020-02-21 19:23
 **/
public class CglibProxyHelper implements MethodInterceptor {
    // 被代理的实体类对象
    // T bean;

    /**
     * @param o           代理类
     * @param method      代理类的方法、-->包含生成代理类的时候实现接口的方法、
     * @param objects     方法的参数、
     * @param methodProxy
     * @return 返回方法的调用结果
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        //此处是实现enhancer.setInterfaces(interfaces);中的接口中的方法、
        if ("eatFood".equals(method.getName())) {
            //因为eatFood()不是被代理类(Person)中的方法,所有直接调用methodProxy.invokeSuper(o, objects);会抛出异常、
            //此处单独实现Eat接口中的eatFood()方法、
            return objects[0];
        }
        //此处不可使用methodProxy.invoke(o, objects)和method.invoke(o, objects);
        //否则会出现死循环、-->StackOverflowError
        // 如果该拦截器保留了被代理的实体类对象(具有实体类对象属性bean,创建拦截器时,为属性赋值),
        // 则可以使用 method.invoke(bean,objects);
        return methodProxy.invokeSuper(o, objects);
    }

    /**
     * @param clazz      需要被代理的类的Class对象、(被拦截的类的Class对象)
     * @param interfaces 需要实现的接口的Class数组对象
     * @return 指定class的代理类
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> clazz, Class[] interfaces) {
        Enhancer enhancer = new Enhancer();
        //设置需要动态代理的类、 enhancer生成的代理类是这个clazz的子类
        enhancer.setSuperclass(clazz);
        /*方法调用的结果返回固定值、 enhancer.setCallback((FixedValue) () -> "FixedValue");*/
        //设置拦截器、 此处如果对被代理对象的实体类引用,则拦截器方法return可以使用该实体类调用、
        enhancer.setCallback(new CglibProxyHelper());
        //设置需要实现的接口、-->需要在MethodInterceptor的intercept()方法中单独实现这个接口的方法,否则无法调用接口的方法、
        if (interfaces != null) {
            enhancer.setInterfaces(interfaces);
        }

	    /*//此处如果需要使用setCallbackFilter()来对不同的方法使用不同的回调、则需要设置多个callback、
        enhancer.setCallbacks(new Callback[]{new CglibProxyHelper(), new OtherInterceptor()});
        enhancer.setCallbackFilter(method -> {
            //此处的返回值、即callbacks数组的index、
            if ("work".equals(method.getName())) {
                return 0;
            } else {
                return 1;
            }
        });*/
        return (T) enhancer.create();
    }

    public static void main(String[] args) throws Throwable {
        // 通过CGLIB代理,测试获取FeignClient接口中方法上的注解。
        FeignClientService proxy = CglibProxyHelper.getProxy(FeignClientService.class, null);
        System.out.println("proxy = " + proxy);
        Method method = proxy.getClass().getMethod("testE");
        GetMapping annotation = method.getAnnotation(GetMapping.class);
        // null -> 代理类无法获取被代理对象中的的注解.
        System.out.println("annotation = " + annotation);
    }
}