package yb.ecp.fast.infra.security;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import yb.ecp.fast.infra.util.StringUtil;

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
//      RequestContext var1;
//      RequestContext var10000 = var1 = RequestContext.getCurrentContext();
//      String var2 = var10000.getRequest().getRequestURI();
//      HttpSession var3 = var10000.getRequest().getSession();
//      var10000.addZuulRequestHeader("x-access-client", "gateway");
//      if(!StringUtil.isNullOrSpace(var2 = (String)var3.getAttribute("uid"))) {
//         var1.addZuulRequestHeader("x-user-id", var2);
//         return null;
//      } else {
//         var1.addZuulRequestHeader("x-user-id", " ");
//         return null;
//      }

        System.out.println(getClass());
        RequestContext ctx = RequestContext.getCurrentContext();
        String requestUri = ctx.getRequest().getRequestURI();
        HttpSession httpSession = ctx.getRequest().getSession();
        ctx.addZuulRequestHeader("x-access-client", "true");
        String userId = (String) httpSession.getAttribute("uid");
        if (StringUtil.isNullOrSpace(userId) != true) {
            ctx.addZuulRequestHeader("x-user-id", userId);
            return null;
        }
        ctx.addZuulRequestHeader("x-user-id", " ");

        return null;
    }

}
