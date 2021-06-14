package fr.esgi.pa.server.integration.code.infrastructure.dataprovider.repository;

import fr.esgi.pa.server.code.infrastructure.dataprovider.entity.JpaCode;
import fr.esgi.pa.server.code.infrastructure.dataprovider.repository.CodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class CodeRepositoryTest {
    @Autowired
    private CodeRepository sut;

    @Test
    void test_findByUserIdAndExerciseCaseId() {
        long userId = 5L;
        long exerciseCaseId = 3L;
        var codeToSave = new JpaCode()
                .setContent("code content")
                .setExerciseCaseId(exerciseCaseId)
                .setUserId(userId)
                .setIsResolved(false);

        var savedCode = sut.save(codeToSave);

        var result = sut.findByUserIdAndExerciseCaseId(userId, exerciseCaseId);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(savedCode);
    }
}