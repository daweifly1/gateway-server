package yb.ecp.fast.infra.security;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LogoutFilter extends ZuulFilter {

   @Value("${fast.auth.logout.url}")
   String[] i;
   private Logger ALLATORIxDEMO;


   public String filterType() {
      return "post";
   }

   public LogoutFilter() {
      this.ALLATORIxDEMO = LoggerFactory.getLogger(this.getClass());
   }

   public int filterOrder() {
      return 901;
   }

   public Object run() {
      RequestContext.getCurrentContext().getRequest().getSession().invalidate();
      return null;
   }

   public boolean shouldFilter() {
      boolean var1 = false;
      String var6 = RequestContext.getCurrentContext().getRequest().getRequestURI();
      String[] var2 = this.i;
      int var3 = this.i.length;

      int var4;
      for(int var10000 = var4 = 0; var10000 < var3; var10000 = var4) {
         String var5 = var2[var4];
         if(var6.matches(var5)) {
            return true;
         }

         ++var4;
      }

      return false;
   }
}
