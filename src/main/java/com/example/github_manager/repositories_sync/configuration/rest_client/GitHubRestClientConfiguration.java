package com.example.github_manager.repositories_sync.configuration.rest_client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Configuration
public class GitHubRestClientConfiguration {

    @Bean
    RestClient githubRestClient(
            RestClient.Builder builder,
            GithubRestClientProperties properties
    ) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient); // setup connect timeout , read timeout

        requestFactory.setReadTimeout(properties.readTimeout());

        return builder
                .baseUrl(properties.baseUrl())
                .requestInitializer(request -> {
                    String token = properties.accessToken();
                    request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                })
                .requestFactory(requestFactory)
                .defaultHeader(
                        HttpHeaders.ACCEPT,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

}
