package yb.ecp.fast.infra.infra.log;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.Debug;
import com.netflix.zuul.context.RequestContext;
import java.util.Iterator;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import yb.ecp.fast.infra.infra.log.LogHelper;

@Service
public class LoggingApiFilter extends ZuulFilter {

   private Logger ALLATORIxDEMO;


   public Object run() {
      RequestContext var1 = RequestContext.getCurrentContext();
      Long var2 = Long.valueOf(System.currentTimeMillis() - ((Long)var1.get("DURATION")).longValue());
      HttpServletResponse var4 = var1.getResponse();
      Debug.addRequestDebug((new StringBuilder()).insert(0, "RESPONSE:: > ").append(var4.getStatus()).toString());
      Debug.addRequestDebug((new StringBuilder()).insert(0, "RESPONSE:: > duration:").append(var2).append("ms").toString());
      StringBuffer var5 = new StringBuffer("");
      Iterator var6;
      Iterator var10000 = var6 = Debug.getRequestDebug().iterator();

      while(var10000.hasNext()) {
         String var3 = (String)var6.next();
         var10000 = var6;
         var5.append(var3);
      }

      LogHelper.monitor(var5.toString());
      return null;
   }

   public int filterOrder() {
      return Integer.MAX_VALUE;
   }

   public String filterType() {
      return "post";
   }

   public boolean shouldFilter() {
      return true;
   }

   public LoggingApiFilter() {
      this.ALLATORIxDEMO = LoggerFactory.getLogger(this.getClass());
   }
}
