package com.ues.core;

import com.ues.database.ResourceManager;
import com.ues.http.HttpRequest;
import com.ues.http.HttpResponse;
import com.ues.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PostRequestHandlerTest {

    private PostRequestHandler postRequestHandler;

    @BeforeEach
    void setUp() {
        postRequestHandler = new PostRequestHandler();
    }

    @Test
    void handle_createDataSuccess() {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getPath()).thenReturn("/table");
        when(request.getBody()).thenReturn("key=value");

        try (MockedStatic<ResourceManager> mockedStatic = mockStatic(ResourceManager.class)) {
            mockedStatic.when(() -> ResourceManager.createData(anyString(), anyMap()))
                    .thenReturn(Mono.just(true));

            Mono<Void> result = postRequestHandler.handle(request, response);

            StepVerifier.create(result)
                    .verifyComplete();

            assertEquals(HttpStatus.CREATED.getCode(), response.getStatusCode());
            assertEquals(HttpStatus.CREATED.getReasonPhrase(), response.getReasonPhrase());
        }
    }

    @Test
    void handle_createDataFailure() {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getPath()).thenReturn("/table");
        when(request.getBody()).thenReturn("key=value");

        try (MockedStatic<ResourceManager> mockedStatic = mockStatic(ResourceManager.class)) {
            mockedStatic.when(() -> ResourceManager.createData(anyString(), anyMap()))
                    .thenReturn(Mono.just(false));

            Mono<Void> result = postRequestHandler.handle(request, response);

            StepVerifier.create(result)
                    .verifyComplete();

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), response.getStatusCode());
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), response.getReasonPhrase());
            assertEquals("<h1>500 Internal Server Error</h1><p>Failed to create data</p>", new String(response.getBody()));
        }
    }
}