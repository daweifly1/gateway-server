package yb.ecp.fast.infra;

import org.apache.catalina.Context;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.DefaultCookieSerializer;
import yb.ecp.fast.infra.infra.eureka.EurekaDeregister;
import yb.ecp.fast.infra.util.StringUtil;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@EnableFeignClients
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 180000)
@EnableScheduling
public class GatewayServerApplication {
    public static void main(String[] a) {
        SpringApplication.run(GatewayServerApplication.class, a);
    }




    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer(@Value("${fast.webfront.error-page.notfound: }") final String a) {

        EmbeddedServletContainerCustomizer c = new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                if (!StringUtil.isNullOrSpace(String.valueOf(a))) {
                    ErrorPage localErrorPage = new ErrorPage(HttpStatus.NOT_FOUND, a);
                    Set<ErrorPage> s = new HashSet<ErrorPage>();
                    s.add(localErrorPage);
                    ((TomcatEmbeddedServletContainerFactory) container).addContextCustomizers(
                            new TomcatContextCustomizer() {
                                @Override
                                public void customize(Context context) {
                                    context.setUseHttpOnly(false);
                                }
                            });
                    container.setErrorPages(s);
                }
            }
        };
        return c;
    }

//    @Bean
//    public MemoryMonitor memoryMonitor() {
//        return new MemoryMonitor() {
//            @Scheduled(fixedRate = 300000L)
//            public void scheduleMonitor() {
//            }
//        };
//    }

    @Bean(initMethod = "showDeregisterInfo", destroyMethod = "deregister")
    public EurekaDeregister eurekaDeregister(EurekaRegistration a, EurekaServiceRegistry a2, EurekaClientConfigBean a3) {
        return new EurekaDeregister(a, a2, a3);
    }

    @Bean
    public DefaultCookieSerializer defaultCookieSerializer(@Value("${server.session.cookie.name:BJ_SESSION}") String a) {
        DefaultCookieSerializer tmp7_4 = new DefaultCookieSerializer();
        tmp7_4.setCookieName(a);
        return tmp7_4;
    }

}
