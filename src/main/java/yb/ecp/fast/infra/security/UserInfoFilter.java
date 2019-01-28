package yb.ecp.fast.infra.security;

import com.bkrwin.ufast.user.service.VO.UserVO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yb.ecp.fast.infra.infra.ActionResult;
import yb.ecp.fast.infra.jwt.fastjson.FastJsonUtil;
import yb.ecp.fast.infra.jwt.http.CookieUtil;
import yb.ecp.fast.infra.jwt.http.TockenUtil;

import java.io.IOException;
import java.io.InputStream;

import static yb.ecp.fast.infra.jwt.TokenAuthenticationHandler.DEFAULT_EXPIRATION;

@Service
public class UserInfoFilter extends ZuulFilter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${fast.auth.getLogin.url}")
    String[] loginInfoUrls;

    public String filterType() {
        return "post";
    }

    public int filterOrder() {
        return 902;
    }

    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        try {
            UserVO userVO = postGetLogin(ctx);
            if (null != userVO) {
                //将权限信息加密存储到cookie
                CookieUtil.setCookie(ctx.getResponse(), "userInfo", TockenUtil.generateToken(FastJsonUtil.toJSONString(userVO)), DEFAULT_EXPIRATION);
            }
        } catch (Exception exc) {
            logger.error("failed to process things", exc);
        }
        return null;
    }

    private UserVO postGetLogin(RequestContext requestContext) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        InputStream inputStream = requestContext.getResponseDataStream();

        ActionResult<UserVO> actionResult = (ActionResult<UserVO>) objectMapper.readValue(inputStream, ActionResult.class);
        inputStream.close();
        UserVO user = null;
        if (actionResult.getCode() != 0) {
            logger.error(actionResult.getMessage());
        } else {
            user = (UserVO) actionResult.getValue();
            actionResult.setValue(null);
        }
        String s = objectMapper.writeValueAsString(actionResult);
        requestContext.setResponseBody(s);
        return user;
    }

    public boolean shouldFilter() {
//        RequestContext ctx = RequestContext.getCurrentContext();
////        String requestUri = ctx.getRequest().getRequestURI();
////        for (String url : this.loginInfoUrls) {
////            if (requestUri.matches(url)) {
////                return true;
////            }
////        }
        return false;
    }


}
