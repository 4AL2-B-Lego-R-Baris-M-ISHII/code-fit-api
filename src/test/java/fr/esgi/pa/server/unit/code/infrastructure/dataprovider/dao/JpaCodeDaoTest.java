package fr.esgi.pa.server.unit.code.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.code.infrastructure.dataprovider.dao.JpaCodeDao;
import fr.esgi.pa.server.code.infrastructure.dataprovider.entity.JpaCode;
import fr.esgi.pa.server.code.infrastructure.dataprovider.mapper.CodeMapper;
import fr.esgi.pa.server.code.infrastructure.dataprovider.repository.CodeRepository;
import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.common.core.utils.date.DateHelper;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaCodeDaoTest {
    private final long userId = 43L;
    private final long exerciseCaseId = 31L;
    private final String content = "a content";
    private JpaCodeDao sut;

    private final CodeMapper codeMapper = new CodeMapper();

    @Mock
    private CodeRepository mockCodeRepository;

    @Mock
    private DateHelper mockDateHelper;

    @BeforeEach
    void setup() {
        sut = new JpaCodeDao(mockCodeRepository, codeMapper, mockDateHelper);
    }

    @Nested
    class SaveCodeTest {
        @Test
        void when_code_not_contain_userId_should_throw_ForbiddenException() {
            var codeToSave = new Code()
                    .setUserId(null)
                    .setContent(content)
                    .setExerciseCaseId(exerciseCaseId);
            assertThatThrownBy(() -> sut.save(codeToSave))
                    .isExactlyInstanceOf(ForbiddenException.class)
                    .hasMessage(
                            "%s : Code with null user id can't be saved",
                            CommonExceptionState.FORBIDDEN
                    );
        }

        @Test
        void when_code_not_contain_exerciseCaseId_should_throw_ForbiddenException() {
            var codeToSave = new Code()
                    .setUserId(userId)
                    .setContent(content)
                    .setExerciseCaseId(null);
            assertThatThrownBy(() -> sut.save(codeToSave))
                    .isExactlyInstanceOf(ForbiddenException.class)
                    .hasMessage(
                            "%s : Code with null exercise case id can't be saved",
                            CommonExceptionState.FORBIDDEN
                    );
        }

        @Test
        void when_found_code_by_userId_and_exerciseCaseId_and_code_saved_should_return_saved_code() throws ForbiddenException {
            var codeToSave = new Code()
                    .setUserId(userId)
                    .setContent(content)
                    .setExerciseCaseId(exerciseCaseId);
            var foundCode = new JpaCode()
                    .setId(9L)
                    .setContent("old content")
                    .setUserId(userId)
                    .setExerciseCaseId(exerciseCaseId)
                    .setIsResolved(false);
            when(mockCodeRepository.findByUserIdAndExerciseCaseId(userId, exerciseCaseId)).thenReturn(Optional.of(foundCode));
            var jpaCodeToSave = new JpaCode()
                    .setId(9L)
                    .setContent(content)
                    .setUserId(userId)
                    .setExerciseCaseId(exerciseCaseId)
                    .setIsResolved(false);
            var savedCode = new JpaCode()
                    .setId(9L)
                    .setContent(content)
                    .setUserId(userId)
                    .setExerciseCaseId(exerciseCaseId)
                    .setIsResolved(false);
            when(mockCodeRepository.save(jpaCodeToSave)).thenReturn(savedCode);

            var result = sut.save(codeToSave);

            var expectedSavedCode = codeMapper.entityToDomain(savedCode);

            assertThat(result).isEqualTo(expectedSavedCode);
        }

        @Test
        void when_not_found_code_by_userId_and_exerciseCaseId_and_code_saved_should_return_saved_code() throws ForbiddenException {
            var codeToSave = new Code()
                    .setUserId(userId)
                    .setContent(content)
                    .setExerciseCaseId(exerciseCaseId);
            var jpaCodeToSave = codeMapper.domainToEntity(codeToSave);
            when(mockCodeRepository.findByUserIdAndExerciseCaseId(userId, exerciseCaseId)).thenReturn(Optional.empty());
            var savedCode = new JpaCode()
                    .setId(9L)
                    .setContent(content)
                    .setUserId(userId)
                    .setExerciseCaseId(exerciseCaseId)
                    .setIsResolved(false);
            when(mockCodeRepository.save(jpaCodeToSave)).thenReturn(savedCode);

            var result = sut.save(codeToSave);

            var expectedSavedCode = codeMapper.entityToDomain(savedCode);

            assertThat(result).isEqualTo(expectedSavedCode);
        }

        @Test
        void when_found_code_by_userId_and_exerciseCaseId_and_not_resolved_and_code_on_param_is_resolved_is_true_should_saved_code_with_isResolved_to_true_return_saved_code() throws ForbiddenException {
            var codeToSave = new Code()
                    .setUserId(userId)
                    .setContent(content)
                    .setIsResolved(true)
                    .setExerciseCaseId(exerciseCaseId);
            var foundCode = new JpaCode()
                    .setId(9L)
                    .setContent("old content")
                    .setUserId(userId)
                    .setExerciseCaseId(exerciseCaseId)
                    .setIsResolved(false);
            when(mockCodeRepository.findByUserIdAndExerciseCaseId(userId, exerciseCaseId)).thenReturn(Optional.of(foundCode));
            var jpaCodeToSave = new JpaCode()
                    .setId(9L)
                    .setContent(content)
                    .setUserId(userId)
                    .setExerciseCaseId(exerciseCaseId)
                    .setIsResolved(true);
            var savedCode = new JpaCode()
                    .setId(9L)
                    .setContent(content)
                    .setUserId(userId)
                    .setExerciseCaseId(exerciseCaseId)
                    .setIsResolved(true);
            when(mockCodeRepository.save(jpaCodeToSave)).thenReturn(savedCode);

            var result = sut.save(codeToSave);

            var expectedSavedCode = codeMapper.entityToDomain(savedCode);

            assertThat(result).isEqualTo(expectedSavedCode);
        }
    }

    @Nested
    class FindByIdTest {

        private final long codeId = 315L;

        @Test
        void when_code_not_found_should_throw_NotFoundException() {
            when(mockCodeRepository.findById(codeId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.findById(codeId))
                    .isExactlyInstanceOf(NotFoundException.class)
                    .hasMessage(
                            "%s : Code with id '%d' not found",
                            CommonExceptionState.NOT_FOUND,
                            codeId
                    );
        }

        @Test
        void when_code_found_should_return_domain_code() throws NotFoundException {
            var foundCode = new JpaCode()
                    .setId(codeId)
                    .setUserId(userId)
                    .setExerciseCaseId(exerciseCaseId)
                    .setContent(content)
                    .setIsResolved(false);
            when(mockCodeRepository.findById(codeId)).thenReturn(Optional.of(foundCode));

            var result = sut.findById(codeId);

            var expectedCode = codeMapper.entityToDomain(foundCode);

            assertThat(result).isEqualTo(expectedCode);
        }
    }

    @Nested
    class FindAllByUserId {
        @Test
        void should_call_findAllByUserId_of_repository() {
            sut.findAllByUserId(userId);

            verify(mockCodeRepository, times(1)).findAllByUserId(userId);
        }

        @Test
        void when_repository_return_all_codes_of_user_should_return_set_domain_code() {
            var setJpaCode = Set.of(new JpaCode().setId(1L).setContent("content").setUserId(userId).setExerciseCaseId(exerciseCaseId).setIsResolved(true));

            when(mockCodeRepository.findAllByUserId(userId)).thenReturn(setJpaCode);

            var result = sut.findAllByUserId(userId);

            var expectedSetCode = setJpaCode.stream().map(codeMapper::entityToDomain).collect(Collectors.toSet());
            assertThat(result).isEqualTo(expectedSetCode);
        }
    }

    @Nested
    class FindAllByExerciseIdAndUserId {
        @Test
        void should_call_findAllByExerciseCaseIdAndUserId_of_repository() {
            sut.findByUserIdAndExerciseCaseId(7L, 8L);

            verify(mockCodeRepository, times(1)).findByUserIdAndExerciseCaseId(7L, 8L);
        }

        @Test
        void should_get_optional_code_when_found_one_by_repository() {
            var foundJpaCode = new JpaCode()
                    .setId(6L)
                    .setContent("code content")
                    .setExerciseCaseId(35L)
                    .setUserId(456L)
                    .setIsResolved(true);
            when(mockCodeRepository.findByUserIdAndExerciseCaseId(456L, 35L)).thenReturn(Optional.of(foundJpaCode));

            var result = sut.findByUserIdAndExerciseCaseId(456L, 35L);
            var expectedCode = codeMapper.entityToDomain(foundJpaCode);
            var expected = Optional.of(expectedCode);
            assertThat(result).isEqualTo(expected);
            assertThat(result.isPresent()).isTrue();
            assertThat(result.get()).isEqualTo(expectedCode);
        }

        @Test
        void should_get_empty_optional_code_when_not_found_by_reposity() {
            when(mockCodeRepository.findByUserIdAndExerciseCaseId(456L, 35L)).thenReturn(Optional.empty());

            var result = sut.findByUserIdAndExerciseCaseId(456L, 35L);
            assertThat(result.isEmpty()).isTrue();
        }
    }
}