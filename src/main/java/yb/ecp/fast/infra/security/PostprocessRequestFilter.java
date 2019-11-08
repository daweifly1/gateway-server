package yb.ecp.fast.infra.security;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import yb.ecp.fast.infra.infra.http.CookieUtil;

import javax.servlet.http.HttpSession;

@Service
public class PostprocessRequestFilter extends ZuulFilter {

    private Logger mylog = LoggerFactory.getLogger(getClass());

    public String filterType() {
        return "post";
    }

    public int filterOrder() {
        return 20;
    }

    public boolean shouldFilter() {
        return true;
    }

    public Object run() {

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpSession httpSession = ctx.getRequest().getSession();
        CookieUtil.doSetCookie(ctx.getResponse(), CookieUtil.OSS_SESSION, httpSession.getId(), 0, false);
        return null;
    }

}
