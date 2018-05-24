package com.rosetta.face.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

/**
 *
 */
//证明是一个配置文件(使用@Component也可以,因为点入后会发现@Configuration还是使用了@Component)
@Component
//证明是一个切面
@Aspect
public class HttpAspect {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpAspect.class);

    //环绕aop
    //execution表达式 此表达式表示扫描controller下所有类的所有方法都执行此aop
//    @Around("execution (* com.rosetta.face.controller..*.*(..))")
//    public Object testAop(ProceedingJoinPoint pro) throws Throwable {
//        //获取response
//        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//        //核心设置
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        //执行调用的方法
//        Object proceed = pro.proceed();
//        return proceed;
//    }

//    @Pointcut("execution(public * com.rosetta.face.controller..*(..))")
//    public void log() {
//    }

    /*@Before("log()")
    public void doBefore(JoinPoint joinPoint) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            //url
            LOGGER.info("url={}", request.getRequestURL());

            //method
            LOGGER.info("method={}", request.getMethod());

            //ip
            LOGGER.info("ip={}", request.getRemoteAddr());

            //类方法
            LOGGER.info("class_method={}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());

            //参数
            LOGGER.info("args={}", joinPoint.getArgs());

            LOGGER.info("-----------Before Before Before Before---------");
        }catch (Exception e){
            LOGGER.error("do Before error");
        }
    }*/

//    @After("log()")
//    public void doAfter() {
//
//    }

//    @AfterReturning(returning = "object", pointcut = "log()")
//    public void doAfterReturning(Object object) {
//        LOGGER.info("response={}", object);
//    }
}
