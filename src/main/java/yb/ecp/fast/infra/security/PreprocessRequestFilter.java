package yb.ecp.fast.infra.security;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class PreprocessRequestFilter extends ZuulFilter {

    private Logger mylog = LoggerFactory.getLogger(getClass());

    public String filterType() {
        return "pre";
    }

    public int filterOrder() {
        return 20;
    }

    public boolean shouldFilter() {
        return true;
    }

    public Object run() {
//      RequestContext ctx = RequestContext.getCurrentContext();
//      String requestUri = ctx.getRequest().getRequestURI();
//      HttpSession httpSession = ctx.getRequest().getSession();
//      ctx.addZuulRequestHeader("x-access-client", "gateway");
//      if(!StringUtil.isNullOrSpace(requestUri = (String)httpSession.getAttribute("uid"))) {
//         ctx.addZuulRequestHeader("x-user-id", requestUri);
//         return null;
//      } else {
//         ctx.addZuulRequestHeader("x-user-id", " ");
//         return null;
//      }

        System.out.println(getClass());
        RequestContext ctx = RequestContext.getCurrentContext();
//        String requestUri = ctx.getRequest().getRequestURI();
        HttpSession httpSession = ctx.getRequest().getSession();
        ctx.addZuulRequestHeader("x-access-client", "true");
        String userId = (String) httpSession.getAttribute("uid");
        if (StringUtils.isNotBlank(userId)) {
            ctx.addZuulRequestHeader("x-user-id", userId);
            return null;
        }
        ctx.addZuulRequestHeader("x-user-id", " ");

        return null;
    }

}
