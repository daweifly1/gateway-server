package yb.ecp.fast.infra.security;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yb.ecp.fast.infra.infra.ActionResult;
import yb.ecp.fast.infra.infra.log.LogHelper;
import yb.ecp.fast.infra.util.StringUtil;

@Service
public class ThirdAuthFilter extends ZuulFilter {

   @Value("${fast.auth.thirdAuth.url:/third}")
   private String[] thirdAuthUrl;


   public int filterOrder() {
      return 999;
   }

   public boolean shouldFilter() {
      String var1 = RequestContext.getCurrentContext().getRequest().getRequestURI();
      String[] var2 = this.thirdAuthUrl;
      int var3 = this.thirdAuthUrl.length;

      int var4;
      for(int var10000 = var4 = 0; var10000 < var3; var10000 = var4) {
         String var5 = var2[var4];
         if(var1.contains(var5)) {
            return true;
         }

         ++var4;
      }

      return false;
   }

   public Object run() {
      RequestContext var1;
      HttpSession var2 = (var1 = RequestContext.getCurrentContext()).getRequest().getSession();
      ObjectMapper var3;
      (var3 = new ObjectMapper()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      InputStream var4 = var1.getResponseDataStream();

      try {
         Map var8;
         String var5 = (String)(var8 = (Map)((ActionResult)var3.readValue(var4, ActionResult.class)).getValue()).get("userId");
         Map var6 = (Map)var8.get("wxMpUser");
         String var9 = (String)var8.get("redirectUrl");
         if(!StringUtil.isNullOrEmpty(var5)) {
            var2.setAttribute("uid", var5);
         }

         if(var6 != null) {
            var2.setAttribute("wxMpUser", var6);
         }

         var1.setResponseDataStream((InputStream)null);
         var1.setResponseBody((String)null);
         var1.getResponse().sendRedirect(var9);
      } catch (IOException var7) {
         LogHelper.fatal(var7.getMessage(), var7);
      }

      return null;
   }

   public String filterType() {
      return "post";
   }
}
