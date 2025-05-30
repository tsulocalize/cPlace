
package com.cPlace.chzzk.config;

import com.cPlace.chzzk.exception.ChzzkException;
import com.cPlace.chzzk.exception.ChzzkExceptionCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import java.io.IOException;
import java.net.URI;

@Component
@Log4j2
public class ChzzkRestClient {

    private static final RestClient client = RestClient.create();
    private static final ResponseErrorHandler errorHandler = new ChzzkErrorHandler();

    public String get(String url) {
        return client.get()
                .uri(url)
                .retrieve()
                .onStatus(errorHandler)
                .body(String.class);
    }

    public String getWithToken(String url, String tokenName, String tokenValue) {
        return client.get()
                .uri(url)
                .header(tokenName, "Bearer " + tokenValue)
                .retrieve()
                .onStatus(errorHandler)
                .body(String.class);
    }

    public String post(String url, Object body) {
        return client.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .onStatus(errorHandler)
                .body(String.class);
    }

    private static class ChzzkErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode().isError();
        }

        @Override
        public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
            String supplementaryMessage = "url: " + url +
                    "\nmethod: " + method +
                    "\nresponse: " + new String(response.getBody().readAllBytes());

            throw new ChzzkException(ChzzkExceptionCode.CHZZK_SERVER_EXCEPTION, supplementaryMessage);
        }
    }
}