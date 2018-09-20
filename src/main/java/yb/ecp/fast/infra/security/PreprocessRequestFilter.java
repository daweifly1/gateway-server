package yb.ecp.fast.infra.security;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import yb.ecp.fast.infra.util.StringUtil;

@Service
public class PreprocessRequestFilter extends ZuulFilter {

   private Logger ALLATORIxDEMO;


   public boolean shouldFilter() {
      return true;
   }

   public PreprocessRequestFilter() {
      this.ALLATORIxDEMO = LoggerFactory.getLogger(this.getClass());
   }

   public String filterType() {
      return "pre";
   }

   public Object run() {
      RequestContext var1;
      RequestContext var10000 = var1 = RequestContext.getCurrentContext();
      String var2 = var10000.getRequest().getRequestURI();
      HttpSession var3 = var10000.getRequest().getSession();
      var10000.addZuulRequestHeader("x-access-client", "gateway");
      if(!StringUtil.isNullOrSpace(var2 = (String)var3.getAttribute("uid"))) {
         var1.addZuulRequestHeader("x-user-id", var2);
         return null;
      } else {
         var1.addZuulRequestHeader("x-user-id", " ");
         return null;
      }
   }

   public int filterOrder() {
      return 20;
   }
}
