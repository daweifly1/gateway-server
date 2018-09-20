package yb.ecp.fast.infra;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

   public void configurePathMatch(PathMatchConfigurer a1) {
      AntPathMatcher var2 = new AntPathMatcher();
      var2.setCaseSensitive(false);
      a1.setPathMatcher(var2);
   }
}
