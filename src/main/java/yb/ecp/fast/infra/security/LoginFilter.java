package yb.ecp.fast.infra.security;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.xgit.bj.auth.service.VO.sys.SysUserLoginInfoVO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yb.ecp.fast.infra.infra.ActionResult;
import yb.ecp.fast.infra.jwt.TokenAuthenticationHandler;
import yb.ecp.fast.infra.jwt.fastjson.FastJsonUtil;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginFilter extends ZuulFilter {

    private Logger mylog = LoggerFactory.getLogger(getClass());

    @Value("${fast.auth.login.url}")
    String[] loginUrls;

    @Autowired
    TokenAuthenticationHandler tokenAuthenticationHandler;

    public String filterType() {
        return "post";
    }

    public int filterOrder() {
        return 900;
    }


    protected SysUserLoginInfoVO postUserLogin(RequestContext requestContext) throws Exception {
        InputStream inputStream = requestContext.getResponseDataStream();
        try {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        ActionResult<SysUserLoginInfoVO> actionResult = (ActionResult<SysUserLoginInfoVO>) objectMapper.readValue(inputStream, ActionResult.class);
            String json = FastJsonUtil.convertStream2Json(inputStream);
            requestContext.setResponseBody(json);
            SysUserLoginInfoVO user = null;
            if (StringUtils.isNotBlank(json)) {
//                ActionResult<SysUserLoginInfoVO> obj = (ActionResult<SysUserLoginInfoVO>) JSON.parseObject(js, new TypeReference<Result<User>>(){});
                ActionResult<SysUserLoginInfoVO> actionResult = JSON.parseObject(json, new TypeReference<ActionResult<SysUserLoginInfoVO>>() {
                });
                if (actionResult.getCode() != 0) {
                    this.mylog.error(actionResult.getMessage());
                } else {
                    user = actionResult.getValue();
                }
            }
            return user;
        } finally {
            inputStream.close();
        }
    }

    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
//        HttpSession httpSession = ctx.getRequest().getSession();
        try {
            SysUserLoginInfoVO sysUserLoginInfoVO = postUserLogin(ctx);
            if (null != sysUserLoginInfoVO) {
//                httpSession.setAttribute("uid", userId);
//                TokenAuthenticationHandler tokenAuthenticationHandler = new TokenAuthenticationHandler();
                Map<String, String> user = new HashMap<>();
                user.put("uid", sysUserLoginInfoVO.getUserId());
                user.put("loginName", sysUserLoginInfoVO.getLoginName());
                String token = tokenAuthenticationHandler.generateToken(FastJsonUtil.toJSONString(user));
                tokenAuthenticationHandler.doRefreshToken(ctx.getResponse(), token, true);
            }
        } catch (Exception exc) {
            this.mylog.error("failed to process things", exc);
        }
        return null;
    }

    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String requestUri = ctx.getRequest().getRequestURI();
        for (String url : this.loginUrls) {
            if (requestUri.matches(url)) {
                return true;
            }
        }
        return false;
    }


}
