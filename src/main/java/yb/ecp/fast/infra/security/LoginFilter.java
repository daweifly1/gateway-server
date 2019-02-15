package yb.ecp.fast.infra.security;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yb.ecp.fast.infra.infra.ActionResult;
import yb.ecp.fast.infra.jwt.TokenAuthenticationHandler;
import yb.ecp.fast.infra.jwt.fastjson.FastJsonUtil;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginFilter extends ZuulFilter {

    private Logger mylog = LoggerFactory.getLogger(getClass());

    @Value("${fast.auth.login.url}")
    String[] loginUrls;

    @Autowired
    TokenAuthenticationHandler tokenAuthenticationHandler;

    public String filterType() {
        return "post";
    }

    public int filterOrder() {
        return 900;
    }


    protected String postUserLogin(RequestContext requestContext) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        InputStream inputStream = requestContext.getResponseDataStream();
        ActionResult<String> actionResult = (ActionResult) objectMapper.readValue(inputStream, ActionResult.class);
        inputStream.close();
        String userId = "";
        if (actionResult.getCode() != 0) {
            this.mylog.error(actionResult.getMessage());
        } else {
            userId = (String) actionResult.getValue();
//            actionResult.setValue(null);
        }
        requestContext.setResponseBody(objectMapper.writeValueAsString(actionResult));
        return userId;
    }

    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
//        HttpSession httpSession = ctx.getRequest().getSession();
        try {
            String userId = postUserLogin(ctx);
            if (StringUtils.isNotBlank(userId)) {
//                httpSession.setAttribute("uid", userId);
//                TokenAuthenticationHandler tokenAuthenticationHandler = new TokenAuthenticationHandler();
                Map<String, String> user = new HashMap<>();
                user.put("uid", userId);
                String token = tokenAuthenticationHandler.generateToken(FastJsonUtil.toJSONString(user));
                tokenAuthenticationHandler.doRefreshToken(ctx.getResponse(), token, true);
            }
        } catch (Exception exc) {
            this.mylog.error("failed to process things", exc);
        }
        return null;
    }

    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String requestUri = ctx.getRequest().getRequestURI();
        for (String url : this.loginUrls) {
            if (requestUri.matches(url)) {
                return true;
            }
        }
        return false;
    }


}
