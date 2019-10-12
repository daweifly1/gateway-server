package yb.ecp.fast.infra.security;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yb.ecp.fast.infra.constants.ErrorCode;
import yb.ecp.fast.infra.infra.ActionResult;
import yb.ecp.fast.infra.infra.log.LogHelper;

import java.io.IOException;
import java.util.Map;

@Service
public class ThirdUserInfoFilter extends ZuulFilter {

    @Value("${fast.auth.thirdUser.url:/third}")
    private String[] thirdAuthUrl;


    public Object run() {
        RequestContext var1;
        Map var2;
        if ((var2 = (Map) (var1 = RequestContext.getCurrentContext()).getRequest().getSession().getAttribute("wxMpUser")) != null) {
            ObjectMapper var3;
            (var3 = new ObjectMapper()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ActionResult var5 = new ActionResult(ErrorCode.Success.getCode(), ErrorCode.Success.getDesc(), var2);

            try {
                var1.setResponseBody(var3.writeValueAsString(var5));
            } catch (IOException var4) {
                LogHelper.fatal(var4.getMessage(), var4);
            }
        }

        return null;
    }

    public String filterType() {
        return "pre";
    }

    public boolean shouldFilter() {
        String var1 = RequestContext.getCurrentContext().getRequest().getRequestURI();
        String[] var2 = this.thirdAuthUrl;
        int var3 = this.thirdAuthUrl.length;

        int var4;
        for (int var10000 = var4 = 0; var10000 < var3; var10000 = var4) {
            String var5 = var2[var4];
            if (var1.contains(var5)) {
                return true;
            }

            ++var4;
        }

        return false;
    }

    public int filterOrder() {
        return 5;
    }
}
