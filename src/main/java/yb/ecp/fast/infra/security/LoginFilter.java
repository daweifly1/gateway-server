package yb.ecp.fast.infra.security;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yb.ecp.fast.infra.infra.ActionResult;
import yb.ecp.fast.infra.util.StringUtil;

import javax.servlet.http.HttpSession;
import java.io.InputStream;

@Service
public class LoginFilter extends ZuulFilter {

    private Logger i;

    private Logger mylog = LoggerFactory.getLogger(getClass());

    @Value("${fast.auth.login.url}")
    String[] loginUrls;

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
            actionResult.setValue(null);
        }
        requestContext.setResponseBody(objectMapper.writeValueAsString(actionResult));
        return userId;
    }

    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpSession httpSession = ctx.getRequest().getSession();
        try {
            String userId = postUserLogin(ctx);
            if (!StringUtil.isNullOrSpace(userId)) {
                httpSession.setAttribute("uid", userId);
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
            int ind = requestUri.indexOf("/", 1);
            if (ind > 0) {
                String srequestUri = requestUri.substring(ind);
                if (requestUri.matches(url) || srequestUri.matches(url)) {
                    return true;
                }
            }
        }
        return false;
    }


    public LoginFilter() {
        this.i = LoggerFactory.getLogger(this.getClass());
    }
}
