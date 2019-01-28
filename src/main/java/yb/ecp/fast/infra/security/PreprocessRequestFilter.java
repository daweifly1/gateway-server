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
import yb.ecp.fast.infra.jwt.fastjson.FastJsonUtil;
import yb.ecp.fast.infra.jwt.http.TockenUtil;

import javax.servlet.http.HttpServletRequest;
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
        return -120;
    }

    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
//        String requestUri = ctx.getRequest().getRequestURI();
//        HttpSession httpSession = ctx.getRequest().getSession();
        HttpServletRequest request = ctx.getRequest();
        ctx.addZuulRequestHeader("x-access-client", "true");
        ctx.addZuulRequestHeader("x-user-id", " ");
        String userId = (String) TockenUtil.getSysUserDetailFromRequest(request);
        if (StringUtils.isNotBlank(userId)) {
            ctx.addZuulRequestHeader("x-user-id", userId);
            String url = request.getRequestURI();
            if (canPass(request, url, userId)) {
                return null;
            } else {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
                ctx.setResponseBody(FastJsonUtil.toJSONString(new ActionResult(ErrorCode.OAuthUnAuthorized.getCode(), ErrorCode.OAuthUnAuthorized.getDesc())));
            }
        }
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
        //若cookie存在从cookie中校验
//        String v = CookieUtil.getCookieValueByName(request, "userInfo");
//        if (StringUtils.isNotBlank(v)) {
//            String userVo = TockenUtil.getSubjectFromToken(v);
//            if (StringUtils.isNotBlank(userVo)) {
//                UserVO userVO = FastJsonUtil.parse(userVo, UserVO.class);
//                if (null != userVO && userId.equals(userVO.getUserId()) && !CollectionUtils.isEmpty(userVO.getAuthIds())) {
//                    for (String u : userVO.getAuthIds()) {
//                        if (u.equals(url)) {
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
        //调用接口判断?
        logger.info("无权限url:{}", url);
        return false;
    }

    public boolean shouldFilter() {
        return true;
    }

}
