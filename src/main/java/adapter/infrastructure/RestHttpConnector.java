package adapter.infrastructure;

import domain.HttpConnector;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class RestHttpConnector implements HttpConnector {
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public HttpResponse<String> post(String url, String body, String jwt) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(CONTENT_TYPE_HEADER, APPLICATION_JSON_CONTENT_TYPE)
                    .header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwt)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (IOException e) {
            //throw new RuntimeException(e);
        } catch (InterruptedException e) {
            //throw new RuntimeException(e);
        }
        return null;
    }

}
