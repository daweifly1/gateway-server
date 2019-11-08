package yb.ecp.fast.infra.security;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yb.ecp.fast.infra.infra.http.CookieUtil;

@Service
public class LogoutFilter extends ZuulFilter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${fast.auth.logout.url}")
    String[] logoutUrls;

    public String filterType() {
        return "post";
    }

    public int filterOrder() {
        return 901;
    }

    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        CookieUtil.delCookie(ctx.getRequest(), ctx.getResponse(), CookieUtil.OSS_SESSION);
        ctx.getRequest().getSession().invalidate();
        return null;
    }

    public boolean shouldFilter() {
        boolean should = false;
        RequestContext ctx = RequestContext.getCurrentContext();
        String requestUri = ctx.getRequest().getRequestURI();
        for (String url : this.logoutUrls) {
            if (requestUri.matches(url)) {
                return true;
            }
        }
        return false;
    }
}
