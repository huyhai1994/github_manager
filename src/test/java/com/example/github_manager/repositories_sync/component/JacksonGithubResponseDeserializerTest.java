package com.example.github_manager.repositories_sync.component;

import com.example.github_manager.repositories_sync.dto.GithubPageResponse;
import com.example.github_manager.repositories_sync.dto.GithubRawResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.exc.MismatchedInputException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JacksonGithubResponseDeserializerTest {

    private final JacksonGithubResponseDeserializer jacksonGithubResponseDeserializer = new JacksonGithubResponseDeserializer();


    @Test
    void deserialize_whenResponseBodyMapSuccess_thenReturnGithubResponse() throws IOException {
        String rawResponseString = new ClassPathResource("get_repo.json").getContentAsString(StandardCharsets.UTF_8);
        assertThat(rawResponseString).isNotNull();
        GithubPageResponse githubPageResponse = jacksonGithubResponseDeserializer.deserialize(new GithubRawResponse(200, null, rawResponseString));
        assertThat(githubPageResponse.repositories()).isNotNull();
    }

    @Test
    void deserialize_whenIdFieldStructureChanged_thenThrowException() throws IOException {
        String rawResponseString = new ClassPathResource("get_repo_mismatch_id_structure.json").getContentAsString(StandardCharsets.UTF_8);
        assertThrows(MismatchedInputException.class, () -> jacksonGithubResponseDeserializer.deserialize(new GithubRawResponse(200, null, rawResponseString)));
    }

}