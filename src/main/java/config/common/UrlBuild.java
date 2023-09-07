package config.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

public class UrlBuild {

    public static String from(String path) {
        return build(null, null, 0, path);
    }

    public static String from(int port, String serverName, String path) {
        return build(null, serverName, port, path);
    }

    private static String build(int serverPort, String path) {
        return build(null, null, serverPort, path);
    }

    public static String fromCustom(String scheme, String serverName, int port, String path) {
        return build(scheme, serverName, port, path);
    }

    public static String fromCustom(String serverName, int port, String path) {
        return build(null, serverName, port, path);
    }

    public static String fromCustom(String serverName, int port) {
        return build(null, serverName, port, null);
    }

    public static String fromCustom(String serverName) {
        return build(null, serverName, 0, null);
    }

    private static String build(String scheme, String serverName, int port, String path) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String _scheme = scheme != null ? scheme : request.getScheme();
        String _serverName = serverName != null ? serverName : request.getServerName();
        String _path = path != null ? path : request.getServletPath();
        UriComponentsBuilder uri = UriComponentsBuilder.newInstance()
                .scheme(_scheme)
                .host(_serverName)
                .path(_path);
        if (port > 0 && port != 80) {
            uri.port(port);
        }
        if (port == 0) {
            uri.port(request.getServerPort());
        }
        return uri.toUriString();
    }
}
