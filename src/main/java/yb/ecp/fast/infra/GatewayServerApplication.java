package yb.ecp.fast.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.DefaultCookieSerializer;
import yb.ecp.fast.infra.infra.eureka.EurekaDeregister;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@EnableFeignClients
@EnableRedisHttpSession
@EnableScheduling
public class GatewayServerApplication {
    public static void main(String[] a) {
        SpringApplication.run(GatewayServerApplication.class, a);
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer(@Value("${fast.webfront.error-page.notfound: }") String a) {
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
                configurableEmbeddedServletContainer.setSessionTimeout(30, TimeUnit.MINUTES);
            }
        };
    }

    @Bean(initMethod = "showDeregisterInfo", destroyMethod = "deregister")
    public EurekaDeregister eurekaDeregister(EurekaRegistration registration, EurekaServiceRegistry serviceRegistry, EurekaClientConfigBean eurekaClientConfigBean) {
        return new EurekaDeregister(registration, serviceRegistry, eurekaClientConfigBean);
    }

    @Bean
    public DefaultCookieSerializer defaultCookieSerializer(@Value("${server.session.cookie.name:SESSION}") String a) {
        DefaultCookieSerializer cg = new DefaultCookieSerializer();
        cg.setCookieName(a);
        return cg;
    }
}
