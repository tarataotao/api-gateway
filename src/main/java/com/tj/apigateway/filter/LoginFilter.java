package com.tj.apigateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录过滤器
 */

@Component
@Slf4j
public class LoginFilter extends ZuulFilter{


    /**
     * 过滤器类型：前置过滤器
     * @return
     */
    @Override
    public String filterType() {
        //过滤器类型
        //FilterConstants.POST_TYPE;//
        return FilterConstants.PRE_TYPE;
    }

    /**
     * order 的值越小，越先执行
     * @return
     */
    @Override
    public int filterOrder() {
        return 4;
    }

    /**
     * 过滤器是否要生效 true:生效 false:不生效
     * @return
     */
    @Override
    public boolean shouldFilter() {
        RequestContext requestContext=RequestContext.getCurrentContext();
        HttpServletRequest request=requestContext.getRequest();
        log.info("uri:"+request.getRequestURI());
        log.info("url:"+request.getRequestURL());
        //ACL 放在统一的授权里面去，做到动态授权，放在缓存中也行
        if("/apigateway/order/api/v1/order/saveByOpenReign".equalsIgnoreCase(request.getRequestURI())){
            return true; //需要拦截，并进入run方法
        }
        return false;
    }

    /**
     * 过滤器业务逻辑
      * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
//        log.info("==============zuul网关拦截器进行拦截啦=====================");
        RequestContext requestContext=RequestContext.getCurrentContext();
        HttpServletRequest request=requestContext.getRequest();

        String token=request.getHeader("token");
        if(StringUtils.isBlank(token)){
            token=request.getParameter("token");
        }
        //登录校验逻辑，根据公司情况自定义 推荐JWT
        if(StringUtils.isBlank(token)){
                requestContext.setSendZuulResponse(false); //停止往下走
                requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
}
