package uz.department.uai.shared.http;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;

// Bu klassni hozircha hech qayerga component sifatida qo'shmaymiz
public class RestTemplateResponseErrorHandler extends DefaultResponseErrorHandler {

    private final String serviceName;

    public RestTemplateResponseErrorHandler(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public void handleError(@NonNull ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is4xxClientError()) {
            throw HttpClientErrorException.create(
                    response.getStatusCode(),
                    response.getStatusText(),
                    response.getHeaders(),
                    getResponseBody(response),
                    getCharset(response)
            );
        } else if (response.getStatusCode().is5xxServerError()) {
            throw HttpServerErrorException.create(
                    response.getStatusCode(),
                    response.getStatusText(),
                    response.getHeaders(),
                    getResponseBody(response),
                    getCharset(response)
            );
        }
    }
}