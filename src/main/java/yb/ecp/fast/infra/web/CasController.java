package yb.ecp.fast.infra.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yb.ecp.fast.infra.feign.CasClient;
import yb.ecp.fast.infra.infra.ActionResult;
import yb.ecp.fast.infra.util.StringUtil;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@RestController
@RequestMapping({"/cas"})
public class CasController {

    @Value("${cas.defaultUrl:http://219.219.116.11:8080}")
    private String defaultUrl;
    @Autowired
    private CasClient casClient;

    private Logger logger = LoggerFactory.getLogger(CasController.class);


    @RequestMapping(value = {"/login"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    public void login(@RequestParam("ticket") String ticket, @RequestParam("target") String targetUrl, HttpServletResponse httpServletResponse, HttpSession httpSession) {
        try {
            ActionResult actionResult = this.casClient.ticket(ticket);
            System.out.print("actionResult:" + actionResult);
            this.logger.info("actionResult:" + actionResult);
            if (actionResult.getCode() != 0) {
                httpServletResponse.sendRedirect(this.defaultUrl);
            }
            String userId = (String) actionResult.getValue();
            if (StringUtil.isNullOrSpace(userId)) {
                httpServletResponse.sendRedirect(this.defaultUrl);
            }
            httpSession.setAttribute("uid", userId);
            httpServletResponse.sendRedirect(targetUrl);
        } catch (Exception exc) {
            this.logger.error("failed to process things", exc);
        }
    }
}
