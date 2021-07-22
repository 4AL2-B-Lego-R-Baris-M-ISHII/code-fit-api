package fr.esgi.pa.server.unit.code.usecase;

import fr.esgi.pa.server.code.core.adapter.CodeAdapter;
import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.dto.CodeQualityType;
import fr.esgi.pa.server.code.core.dto.DtoCode;
import fr.esgi.pa.server.code.core.dto.DtoQualityCode;
import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.code.core.quality.ProcessQualityCode;
import fr.esgi.pa.server.code.core.quality.QualityCode;
import fr.esgi.pa.server.code.usecase.GetQualityCode;
import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.user.core.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
class GetQualityCodeTest {
    private final long userId = 61L;
    private final long codeId = 32L;
    private final long languageId = 613L;
    private final long exerciseId = 641L;
    private GetQualityCode sut;
    private Set<CodeQualityType> qualityTypeSet;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private CodeDao mockCodeDao;

    @Mock
    private ExerciseCaseDao mockExerciseCaseDao;

    @Mock
    private LanguageDao mockLanguageDao;

    @Mock
    private ProcessQualityCode mockProcessQualityCode;

    @Autowired
    private CodeAdapter codeAdapter;

    private final long exerciseCaseId = 94L;

    @BeforeEach
    void setup() {
        qualityTypeSet = Set.of(CodeQualityType.LINES_CODE);

        sut = new GetQualityCode(
                mockUserDao,
                mockCodeDao,
                mockExerciseCaseDao,
                mockLanguageDao,
                mockProcessQualityCode,
                codeAdapter
        );
    }

    @Test
    void when_user_not_exists_should_throw_not_found_exception() {
        when(mockUserDao.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> sut.execute(userId, codeId, qualityTypeSet))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(
                        "%s : User with id '%d' not found",
                        CommonExceptionState.NOT_FOUND,
                        userId
                );
    }

    @Test
    void when_code_found_and_is_not_user_one_should_throw_forbidden_exception() throws NotFoundException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var otherUserId = 31L;
        var foundCode = new Code()
                .setId(codeId)
                .setContent("code content")
                .setIsResolved(true)
                .setUserId(otherUserId)
                .setExerciseCaseId(exerciseCaseId);
        when(mockCodeDao.findById(codeId)).thenReturn(foundCode);
        assertThat(userId).isNotEqualTo(otherUserId);

        assertThatThrownBy(() -> sut.execute(userId, codeId, qualityTypeSet))
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(
                        "%s : Current user can't get quality code of other user's code",
                        CommonExceptionState.FORBIDDEN
                );
    }

    @Test
    void when_code_found_is_not_resolved_should_throw_forbidden_exception() throws NotFoundException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundCode = new Code()
                .setId(codeId)
                .setContent("code content")
                .setIsResolved(false)
                .setUserId(userId)
                .setExerciseCaseId(exerciseCaseId);
        when(mockCodeDao.findById(codeId)).thenReturn(foundCode);
        assertThatThrownBy(() -> sut.execute(userId, codeId, qualityTypeSet))
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage(
                        "%s : Code has to be resolved to get it quality",
                        CommonExceptionState.FORBIDDEN
                );
    }

    @Test
    void when_exercise_case_found_should_find_language_by_id() throws NotFoundException, ForbiddenException {
        var foundCode = new Code()
                .setId(codeId)
                .setContent("code content")
                .setIsResolved(true)
                .setUserId(userId)
                .setExerciseCaseId(exerciseCaseId);
        when(mockCodeDao.findById(codeId)).thenReturn(foundCode);
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setStartContent("start content")
                .setSolution("solution")
                .setLanguageId(languageId)
                .setIsValid(true)
                .setExerciseId(exerciseId);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);

        sut.execute(userId, codeId, qualityTypeSet);

        verify(mockLanguageDao, times(1)).findById(languageId);
    }

    @Test
    void when_language_found_should_call_process_quality_code() throws NotFoundException, ForbiddenException {
        var foundCode = new Code()
                .setId(codeId)
                .setContent("code content")
                .setIsResolved(true)
                .setUserId(userId)
                .setExerciseCaseId(exerciseCaseId);
        when(mockCodeDao.findById(codeId)).thenReturn(foundCode);
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setStartContent("start content")
                .setSolution("solution")
                .setLanguageId(languageId)
                .setIsValid(true)
                .setExerciseId(exerciseId);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var cLanguage = new Language()
                .setId(languageId)
                .setLanguageName(LanguageName.C11)
                .setFileExtension("c");
        when(mockLanguageDao.findById(languageId)).thenReturn(cLanguage);

        sut.execute(userId, codeId, qualityTypeSet);

        verify(mockProcessQualityCode, times(1)).process(
                foundCode.getContent(),
                cLanguage,
                qualityTypeSet
        );
    }

    @Test
    void when_code_process_and_get_quality_code_should_return_dto_with_quality_code_information() throws NotFoundException, ForbiddenException {
        var foundCode = new Code()
                .setId(codeId)
                .setContent("code content")
                .setIsResolved(true)
                .setUserId(userId)
                .setExerciseCaseId(exerciseCaseId);
        when(mockCodeDao.findById(codeId)).thenReturn(foundCode);
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setStartContent("start content")
                .setSolution("solution")
                .setLanguageId(languageId)
                .setIsValid(true)
                .setExerciseId(exerciseId);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var cLanguage = new Language()
                .setId(languageId)
                .setLanguageName(LanguageName.C11)
                .setFileExtension("c");
        when(mockLanguageDao.findById(languageId)).thenReturn(cLanguage);
        var qualityCode = new QualityCode()
                .setCodeContent(foundCode.getContent())
                .setLanguage(cLanguage)
                .setNbLinesCode(1L);
        when(mockProcessQualityCode.process(
                foundCode.getContent(),
                cLanguage,
                qualityTypeSet
        )).thenReturn(qualityCode);

        var result = sut.execute(userId, codeId, qualityTypeSet);

        var dtoCode = new DtoCode()
                .setCodeId(foundCode.getId())
                .setContent(foundCode.getContent())
                .setIsResolved(foundCode.getIsResolved())
                .setResolvedDateTimestampSec(Optional.ofNullable(foundCode.getResolvedDate())
                        .map(resolvedDate -> resolvedDate.getTime() / 1000)
                        .orElse(null));
        var expectedDtoQualityCode = new DtoQualityCode()
                .setCode(dtoCode)
                .setExerciseCaseId(exerciseCaseId)
                .setQualityCode(qualityCode);
        assertThat(result).isEqualTo(expectedDtoQualityCode);
    }
}