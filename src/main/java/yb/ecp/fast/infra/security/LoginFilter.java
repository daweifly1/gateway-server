package yb.ecp.fast.infra.security;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import java.io.InputStream;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yb.ecp.fast.infra.infra.ActionResult;
import yb.ecp.fast.infra.util.StringUtil;

@Service
public class LoginFilter extends ZuulFilter {

   private Logger i;
   @Value("${fast.auth.login.url}")
   String[] ALLATORIxDEMO;


   protected String postUserLogin(RequestContext a1) throws Exception {
      ObjectMapper var2;
      ObjectMapper var10000 = var2 = new ObjectMapper();
      var10000.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      InputStream var3 = a1.getResponseDataStream();
      ActionResult var4 = (ActionResult)var10000.readValue(var3, ActionResult.class);
      var3.close();
      String var6 = "";
      RequestContext var5;
      if(var4.getCode() != 0) {
         var5 = a1;
         this.i.error(var4.getMessage());
      } else {
         var6 = (String)var4.getValue();
         var5 = a1;
         var4.setValue((Object)null);
      }

      var5.setResponseBody(var2.writeValueAsString(var4));
      return var6;
   }

   public Object run() {
      RequestContext var1;
      HttpSession var2 = (var1 = RequestContext.getCurrentContext()).getRequest().getSession();

      try {
         String var4;
         if(!StringUtil.isNullOrSpace(var4 = this.postUserLogin(var1))) {
            var2.setAttribute("uid", var4);
         }
      } catch (Exception var3) {
         this.i.error("failed to process things", var3);
      }

      return null;
   }

   public boolean shouldFilter() {
      String var1 = RequestContext.getCurrentContext().getRequest().getRequestURI();
      String[] var2 = this.ALLATORIxDEMO;
      int var3 = this.ALLATORIxDEMO.length;

      int var4;
      for(int var10000 = var4 = 0; var10000 < var3; var10000 = var4) {
         String var5 = var2[var4];
         if(var1.matches(var5)) {
            return true;
         }

         ++var4;
      }

      return false;
   }

   public String filterType() {
      return "post";
   }

   public LoginFilter() {
      this.i = LoggerFactory.getLogger(this.getClass());
   }

   public int filterOrder() {
      return 900;
   }
}
