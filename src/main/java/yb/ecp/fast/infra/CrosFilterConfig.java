package yb.ecp.fast.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author f00lish
 * @version 2018/7/18
 * Created By IntelliJ IDEA.
 * Qun:530350843
 */
@Configuration
public class CrosFilterConfig {


    /**
     * 跨域解决
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/auth/**", null);
        source.registerCorsConfiguration("/**", this.buildConfig());
        return new CorsFilter(source);
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        //为true则AllowedOrigin会可能不为
        corsConfiguration.setAllowCredentials(true);
        return corsConfiguration;
    }

}
