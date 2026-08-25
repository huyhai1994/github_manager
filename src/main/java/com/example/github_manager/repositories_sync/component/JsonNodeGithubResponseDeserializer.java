//package com.example.github_manager.repositories_sync.component;
//
//import com.example.github_manager.repositories_sync.dto.GithubPageResponse;
//import com.example.github_manager.repositories_sync.dto.GithubRawResponse;
//import com.example.github_manager.repositories_sync.dto.GithubRepositoryResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import tools.jackson.databind.JsonNode;
//import tools.jackson.databind.ObjectMapper;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class JsonNodeGithubResponseDeserializer implements GithubResponseDeserializer {
//
//    private final ObjectMapper objectMapper;
//
//    @Override
//    public GithubPageResponse deserialize(GithubRawResponse rawResponse) {
////        try {
////            JsonNode rootNode = objectMapper.readTree(rawResponse.body());
////
////            if (!rootNode.isArray()) {
////                throw new GithubResponseMalformedException(
////                        "GitHub response must be an array"
////                );
////            }
////
////            List<GithubRepositoryResponse> repositories = new ArrayList<>();
////
////            for (JsonNode repositoryNode : rootNode) {
////                repositories.add(deserializeRepository(repositoryNode));
////            }
////
////            return GithubPageResponse.builder()
////                    .repositories(repositories)
////                    .build();
////
////        } catch (JsonProcessingException exception) {
////            throw new GithubResponseMalformedException(
////                    "Cannot parse fallback GitHub response",
////                    exception
////            );
////        }
//        throw new UnsupportedOperationException();
//    }
//
//    private GithubRepositoryResponse deserializeRepository(JsonNode node) {
//        return GithubRepositoryResponse.builder()
//                .id(readRepositoryId(node))
//                .name(readRequiredText(node, "name"))
//                .fullName(readRequiredText(node, "full_name"))
//                .privateRepository(node.path("private").asBoolean())
//                .visibility(node.path("visibility").asString(null))
//                .htmlUrl(node.path("html_url").asString(null))
//                .build();
//    }
//
//    private long readRepositoryId(JsonNode repositoryNode) {
//        JsonNode idNode = repositoryNode.path("id");
//
//        if (idNode.isIntegralNumber()) {
//            return idNode.longValue();
//        }
//
//        if (idNode.isObject() && idNode.path("value").isIntegralNumber()) {
//            return idNode.path("value").longValue();
//        }
//
//        throw new GithubResponseMalformedException(
//                "Missing or malformed repository id"
//        );
//    }
//
//    private String readRequiredText(JsonNode node, String fieldName) {
//        JsonNode fieldNode = node.path(fieldName);
//
//        if (!fieldNode.isString() || fieldNode.asString().isBlank()) {
//            throw new GithubResponseMalformedException(
//                    "Missing or malformed field: " + fieldName
//            );
//        }
//
//        return fieldNode.asString();
//    }
//}
