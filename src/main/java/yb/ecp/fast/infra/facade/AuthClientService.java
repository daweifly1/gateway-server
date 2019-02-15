package yb.ecp.fast.infra.facade;

import com.bkrwin.ufast.infra.infra.ActionResult;
import com.bkrwin.ufast.user.feign.AuthClient;
import com.devi.cache.interceptor.GuavaLocalCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class AuthClientService {
    @Autowired
    private AuthClient authClient;


    @GuavaLocalCache(expireTime = 300, refreshTime = 150, group = "gw", preFix = "getAuthCodes_", keyExt = "#userId")
    public List<String> getAuthCodes(String userId) {
        if (StringUtils.isBlank(userId)) {
            return Collections.emptyList();
        }
        ActionResult<List<String>> ar = authClient.getAuthCodes(userId, userId);
        if (null == ar) {
            return Collections.emptyList();
        }
        return ar.getValue();
    }

//    @GuavaLocalCache(expireTime = 60, refreshTime = 40, group = "gw", preFix = "checkAuthCodeExist_", keyExt = "#userId+#url")
    public boolean checkAuthCodeExist(String userId, String url) {
        ActionResult<Boolean> r = authClient.checkAuthCodes(userId, url);
        if (null == r || null == r.getValue()) {
            log.info("无权限url:{}", url);
            return false;
        }
        return r.getValue();
    }
}
