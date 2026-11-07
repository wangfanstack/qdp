package cn.sailstack.qdp.framework.ratelimiter.core.keyresolver.impl;

import cn.sailstack.qdp.framework.common.util.string.StrUtils;
import cn.sailstack.qdp.framework.ratelimiter.core.annotation.RateLimiter;
import cn.sailstack.qdp.framework.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import org.aspectj.lang.JoinPoint;

/**
 * Server 节点级别的限流 Key 解析器，使用方法名 + 方法参数 + IP，组装成一个 Key
 *
 * 为了避免 Key 过长，使用 MD5 进行“压缩”
 *
 * @author 芋道源码
 */
public class ServerNodeRateLimiterKeyResolver implements RateLimiterKeyResolver {

    @Override
    public String resolver(JoinPoint joinPoint, RateLimiter rateLimiter) {
        String methodName = joinPoint.getSignature().toString();
        String argsStr = StrUtils.joinMethodArgs(joinPoint);
        String address;
        try {
            address = java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (java.net.UnknownHostException e) {
            address = "127.0.0.1";
        }
        String serverNode = String.format("%s@%d", address, java.lang.management.ManagementFactory.getRuntimeMXBean().getPid());
        return cn.sailstack.qdp.framework.common.util.string.DigestUtils.md5Hex(methodName + argsStr + serverNode);
    }

}