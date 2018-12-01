package yb.ecp.fast.infra;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author f00lish
 * @version 2018/2/6
 * Created By IntelliJ IDEA.
 * Qun:530350843
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Configuration
@ImportAutoConfiguration(CrosFilterConfig.class)
public @interface EnabelCorsFilter {
}
