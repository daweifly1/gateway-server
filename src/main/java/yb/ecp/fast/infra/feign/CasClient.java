package yb.ecp.fast.infra.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import yb.ecp.fast.infra.infra.ActionResult;

@FeignClient("${cas.feign.name:ljy-nju-charging-service}")
public interface CasClient {

   @RequestMapping(
      value = {"/cas/ticket"},
      method = {RequestMethod.GET}
   )
   ActionResult ticket(@RequestHeader("x-cas-ticket") String var1);
}
