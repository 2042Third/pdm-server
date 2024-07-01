package pw.pdm.pdmserver.util;

import jakarta.servlet.http.HttpServletRequest;

public class Common {
    static public String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
