package yb.ecp.fast.infra.security;

import com.bkrwin.ufast.infra.constants.ErrorCode;
import com.bkrwin.ufast.infra.infra.ActionResult;
import com.bkrwin.ufast.user.feign.AuthClient;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import yb.ecp.fast.infra.jwt.fastjson.FastJsonUtil;
import yb.ecp.fast.infra.jwt.http.TockenUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class PreprocessRequestFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${fast.auth.exclud.url}")
    String[] excludeUrls;

    @Autowired
    private AuthClient authClient;

    public String filterType() {
        return "pre";
    }

    public int filterOrder() {
        return 10;
    }

    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
//        String requestUri = ctx.getRequest().getRequestURI();
//        HttpSession httpSession = ctx.getRequest().getSession();
        HttpServletRequest request = ctx.getRequest();
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            String originHeader = request.getHeader("Origin");
            HttpServletResponse response = ctx.getResponse();
            response.setHeader("Access-Control-Allow-Origin", originHeader);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Connection", "keep-alive");
            response.setHeader("Vary", "Origin");
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.setHeader("Transfer-Encoding", "chunked");
            response.setHeader("X-Application-Context: gateway-server-cdw", "gateway-server-cdw:9003");

            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.NO_CONTENT.value());
        }
        ctx.addZuulRequestHeader("x-access-client", "true");
        String userId = (String) TockenUtil.getSysUserDetailFromRequest(request);
        if (StringUtils.isNotBlank(userId)) {
            ctx.addZuulRequestHeader("x-user-id", userId);
            String url = request.getRequestURI();
            if (canPass(request, url, userId)) {
                logger.info("{} 权限验证通过", url);
                return null;
            } else {
                logger.info("================={}======================== 权限验证不通过", url);
//                ctx.setSendZuulResponse(false);
//                ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
//                ctx.setResponseBody(FastJsonUtil.toJSONString(new ActionResult(ErrorCode.OAuthUnAuthorized.getCode(), ErrorCode.OAuthUnAuthorized.getDesc())));

                return null;
            }
        }
        ctx.addZuulRequestHeader("x-user-id", " ");
        return null;
    }

    private boolean canPass(HttpServletRequest request, String url, String userId) {
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        url = url.substring(url.indexOf("/"));
        for (String u : this.excludeUrls) {
            if (u.matches(url)) {
                return true;
            }
        }

        ActionResult<List<String>> ar = authClient.getAuthCodes(userId, userId);
        if (null == ar || CollectionUtils.isEmpty(ar.getValue())) {
            return false;
        }
        for (String u : ar.getValue()) {
            if (u.equals(url)) {
                return true;
            }
        }
        logger.info("无权限url:{}", url);
        return true;
    }

    public boolean shouldFilter() {
        return true;
    }

}
