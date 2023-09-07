package domain;

import java.net.http.HttpResponse;

public interface HttpConnector {
    HttpResponse<String> post(String url, String body, String jwt);
}
