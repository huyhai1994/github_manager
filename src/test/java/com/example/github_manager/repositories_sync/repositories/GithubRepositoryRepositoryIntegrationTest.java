package com.example.github_manager.repositories_sync.repositories;

import com.example.github_manager.repositories_sync.entity.GithubRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import support.AbstractIntegrationTest;
import support.mock.MockGithubRepositoryEntity;


import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class GithubRepositoryRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    GithubRepositoryRepository githubRepository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void setup() {
        githubRepository.deleteAllInBatch();
    }


    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void save_whenSaveAndEntity_thenReturnCountAs1() {
        Long id = saveEntity1AndReturnId();
        GithubRepository persistedGithub = githubRepository.findById(id).orElseThrow();
        assertThat(persistedGithub).isNotNull();
        assertThat(persistedGithub.getId()).isNotNull();
        assertThat(persistedGithub.getFullName()).isEqualTo(MockGithubRepositoryEntity.Entity1.FULL_NAME);
        assertThat(persistedGithub.getGithubId()).isEqualTo(MockGithubRepositoryEntity.Entity1.GITHUB_ID);
    }

    @Test
    void saveAll_whenEntity1GotUpdatedAndEntity2Exist_thenUpdateEntity1AndSaveEntity2() {
        Long id = saveEntity1AndReturnId();
        GithubRepository persistedGithub = githubRepository.findById(id).orElseThrow();
        persistedGithub.setOwnerLogin(MockGithubRepositoryEntity.Entity1.OWNER_LOGIN + "_1");
        GithubRepository entity2 = MockGithubRepositoryEntity.Entity2.create();
        githubRepository.saveAll(List.of(persistedGithub, entity2));
        entityManager.flush();
        entityManager.clear();

        List<GithubRepository> updatedList =
                githubRepository.findAll()
                        .stream()
                        .sorted(Comparator.comparing(GithubRepository::getGithubId))
                        .toList();
        assertThat(
                updatedList
                        .stream()
                        .findFirst()
                        .orElseThrow()
                        .getOwnerLogin())
                .isEqualTo(MockGithubRepositoryEntity.Entity1.OWNER_LOGIN + "_1");
        assertThat(updatedList).hasSize(2);
    }

    private Long saveEntity1AndReturnId() {
        GithubRepository entity1 = MockGithubRepositoryEntity.Entity1.create();
        GithubRepository savedEntity1 =
                githubRepository.saveAndFlush(entity1);
        return savedEntity1.getId();
    }
}