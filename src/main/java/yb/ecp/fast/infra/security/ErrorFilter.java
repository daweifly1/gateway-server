package yb.ecp.fast.infra.security;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ErrorFilter extends ZuulFilter {

    private Logger ALLATORIxDEMO;


    public Object run() {
        RequestContext var10000 = RequestContext.getCurrentContext();
        Throwable var1 = var10000.getThrowable();
        this.ALLATORIxDEMO.error("this is a Filter Error : {}", var1.getCause().getMessage());
        var10000.set("error.status_code", Integer.valueOf(500));
        var10000.set("error.exception", var1.getCause());
        return null;
    }

    public ErrorFilter() {
        this.ALLATORIxDEMO = LoggerFactory.getLogger(this.getClass());
    }

    public boolean shouldFilter() {
        return true;
    }

    public String filterType() {
        return "error";
    }

    public int filterOrder() {
        return 0;
    }
}
