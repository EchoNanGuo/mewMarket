package top.nanguomm.mewMarket.aspect;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 记录系统操作日志AOP
 */
@Component
@Aspect
@Slf4j
public class SysLogAspect {

    /**
     * 切入点表达式
     */
    public static final String POINT_CUT = "execution (* top.nanguomm.mewMarket.controller.*.*(..))";

    @Around(value = POINT_CUT)
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        // 获取请求对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (requestAttributes != null) {
            request = requestAttributes.getRequest();
        }
        // 获取请求路径
        String path = null;
        if (request != null) {
            path = request.getRequestURI();
        }
        // 获取ip地址
        String remoteHost = null;
        if (request != null) {
            remoteHost = request.getRemoteHost();
        }
        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        // 获取请求方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.toString();
        // 获取目标方法上的ApiOperation注解
        ApiOperation apiOperation = method.getDeclaredAnnotation(ApiOperation.class);
        // 判断该注解对象是否为空
        String operation = "";
        if (ObjectUtil.isNotNull(apiOperation)) {
            // 获取apiOperation注解的描述
            operation = apiOperation.value();
        }

        String finalArgs = "";
        // 判断参数类型
        if (ObjectUtil.isNotNull(args) && args.length != 0 && args[0] instanceof MultipartFile) {
            // 说明当前参数为文件对象
            finalArgs = "file";
        } else {
            // 将参数对象转换为json格式的字符串
            finalArgs = JSONObject.toJSONString(apiOperation);
        }

        // 记录开始时间
        long startTime = System.currentTimeMillis();
            // 执行方法
        result = joinPoint.proceed(args);
        // 记录结束时间
        long endTime = System.currentTimeMillis();

        // 方法执行时长
        long execTime = endTime - startTime;


        log.info("调用时间:{},请求接口路径:{},请求IP地址:{},方法名称:{},执行时长:{},方法描述:{}",
                new Date(),
                path,
                remoteHost,
                methodName,
                execTime,
                operation);

        return result;
    }
}
