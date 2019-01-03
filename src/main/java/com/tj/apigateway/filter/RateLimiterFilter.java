package com.tj.apigateway.filter;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * 订单限    流 guava
 */
public class RateLimiterFilter  extends ZuulFilter{

    //取值使用压测来定
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(1000); //每秒创建多少个令牌
    
    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return -4;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext=RequestContext.getCurrentContext();
        HttpServletRequest request=requestContext.getRequest();
        //只对订单接口限流
        if("/apigateway/order/api/v1/order/saveByOpenReign".equalsIgnoreCase(request.getRequestURI())){
            return true; //需要拦截，并进入run方法
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        if(!RATE_LIMITER.tryAcquire()){
            //拿不到令牌的情况下，返回个错误码
            RequestContext requestContext=RequestContext.getCurrentContext();
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
        }

        return null;
    }
}
