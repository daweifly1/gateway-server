package yb.ecp.fast.infra.jwt.http;


import lombok.extern.slf4j.Slf4j;
import yb.ecp.fast.infra.jwt.JWTConsts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class TockenUtil {

    public static void rmUserCookie(HttpServletRequest req, HttpServletResponse response) {
        CookieUtil.delCookie(req, response, JWTConsts.HEADER_STRING);
        req.getHeader(JWTConsts.HEADER_STRING);
    }

//    public static String getSysUserId(String token) {
//        if (StringUtils.isBlank(token)) {
//            return null;
//        }
//        token = token.replace(JWTConsts.TOKEN_PREFIX.trim(), "");
//        TokenAuthenticationHandler tokenAuthenticationHandler = new TokenAuthenticationHandler();
//        String token2 = tokenAuthenticationHandler.getSubjectFromToken(token);
//        HashMap<String, String> map = FastJsonUtil.parse(token2, HashMap.class);
//        if (null != map) {
//            return map.get("uid");
//        }
//        return null;
//    }

//    public static String getSysUserDetailFromRequest(HttpServletRequest req) {
//        String token = req.getHeader(JWTConsts.HEADER_STRING);
//        if (StringUtils.isBlank(token)) {
//            token = (String) req.getSession().getAttribute(JWTConsts.HEADER_STRING);
//        }
//        if (StringUtils.isBlank(token)) {
//            token = (String) CookieUtil.getCookieValueByName(req, JWTConsts.HEADER_STRING);
//        }
//        if (StringUtils.isNotBlank(token)) {
//            return getSysUserId(token);
//        }
//        return null;
//    }


//    public static String generateToken(String toJSONString) {
//        TokenAuthenticationHandler tokenAuthenticationHandler = new TokenAuthenticationHandler();
//        String token2 = tokenAuthenticationHandler.generateToken(toJSONString);
//        return token2;
//    }

//    public static String getSubjectFromToken(String token) {
//        if (StringUtils.isBlank(token)) {
//            return null;
//        }
//        TokenAuthenticationHandler tokenAuthenticationHandler = new TokenAuthenticationHandler();
//        String token2 = tokenAuthenticationHandler.getSubjectFromToken(token);
//        HashMap<String, String> map = FastJsonUtil.parse(token2, HashMap.class);
//        if (null != map) {
//            return map.get(TokenAuthenticationHandler.CLAIM_KEY_SUBJECT);
//        }
//        return null;
//    }
}
