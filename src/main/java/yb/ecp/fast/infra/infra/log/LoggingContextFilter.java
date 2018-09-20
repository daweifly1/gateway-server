package yb.ecp.fast.infra.infra.log;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.Debug;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.util.HTTPRequestUtils;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.stereotype.Service;

@Service
public class LoggingContextFilter extends ZuulFilter {

   public static final String USER_IP = "USER_IP";
   @Autowired
   private SimpleRouteLocator l;
   private Logger i;
   public static final String DURATION = "DURATION";
   @Autowired
   private ZuulProperties ALLATORIxDEMO;


   public Object run() {
      RequestContext var10000 = RequestContext.getCurrentContext();
      HttpServletRequest var1 = var10000.getRequest();
      var10000.set("DURATION", Long.valueOf(System.currentTimeMillis()));
      Debug.addRequestDebug((new StringBuilder()).insert(0, "REQUEST:: > ").append(var1.getScheme()).append(" ").append(HTTPRequestUtils.getInstance().getClientIP(var1)).append(":").append(var1.getRemotePort()).toString());
      Debug.addRequestDebug((new StringBuilder()).insert(0, "REQUEST:: > ").append(var1.getMethod()).append(" ").append(var1.getRequestURI()).append(" ").append(var1.getProtocol()).toString());
      Route var2;
      if((var2 = this.l.getMatchingRoute(var1.getRequestURI())) != null) {
         Debug.addRequestDebug((new StringBuilder()).insert(0, "REQUEST:: > matched route:").append(var2.toString()).toString());
      }

      return null;
   }

   public boolean shouldFilter() {
      return true;
   }

   public int filterOrder() {
      return 0;
   }

   public String filterType() {
      return "pre";
   }

   public LoggingContextFilter() {
      this.i = LoggerFactory.getLogger(this.getClass());
   }
}
