package yb.ecp.fast.infra.security;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yb.ecp.fast.infra.jwt.JWTConsts;
import yb.ecp.fast.infra.jwt.http.TockenUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        ctx.getRequest().getSession().invalidate();
        TockenUtil.rmUserCookie(ctx.getRequest(), ctx.getResponse());
        ModifyParametersWrapper mParametersWrapper = new ModifyParametersWrapper(ctx.getRequest());
        mParametersWrapper.removeAttribute(JWTConsts.HEADER_STRING);

        return null;
    }

    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String requestUri = ctx.getRequest().getRequestURI();
        for (String url : this.logoutUrls) {
            if (requestUri.matches(url)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 继承HttpServletRequestWrapper，创建装饰类，以达到修改HttpServletRequest参数的目的
     */
    private class ModifyParametersWrapper extends HttpServletRequestWrapper {
        private final Map<String, String> customHeaders;

        ModifyParametersWrapper(HttpServletRequest request) {
            super(request);
            this.customHeaders = new HashMap<>();
        }

        void putHeader(String name, String value) {
            this.customHeaders.put(name, value);
        }

        public String getHeader(String name) {
            // check the custom headers first
            String headerValue = customHeaders.get(name);

            if (headerValue != null) {
                return headerValue;
            }
            // else return from into the original wrapped object
            return ((HttpServletRequest) getRequest()).getHeader(name);
        }

        public Enumeration<String> getHeaderNames() {
            // create a set of the custom header names
            Set<String> set = new HashSet<>(customHeaders.keySet());

            // now add the headers from the wrapped request object
            Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
            while (e.hasMoreElements()) {
                // add the names of the request headers into the list
                String n = e.nextElement();
                set.add(n);
            }

            // create an enumeration from the set and return
            return Collections.enumeration(set);
        }
    }

}
