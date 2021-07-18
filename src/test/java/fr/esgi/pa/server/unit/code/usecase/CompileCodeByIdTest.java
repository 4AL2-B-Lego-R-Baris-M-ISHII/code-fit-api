package fr.esgi.pa.server.unit.code.usecase;

import fr.esgi.pa.server.code.core.compiler.CodeResult;
import fr.esgi.pa.server.code.core.compiler.CodeState;
import fr.esgi.pa.server.code.core.compiler.Compiler;
import fr.esgi.pa.server.code.core.compiler.CompilerRepository;
import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.dto.DtoCode;
import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.code.usecase.CompileCodeById;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompileCodeByIdTest {

    private final long codeId = 61L;
    private final long exerciseCaseId = 684L;
    private final long userId = 9L;
    private final long exerciseId = 32L;
    private final long languageId = 2L;
    private CompileCodeById sut;

    @Mock
    private CodeDao mockCodeDao;

    @Mock
    private ExerciseCaseDao mockExerciseCaseDao;

    @Mock
    private LanguageDao mockLanguageDao;

    @Mock
    private CompilerRepository mockCompilerRepository;

    @Mock
    private Compiler mockCompiler;

    @Mock
    private ExerciseTestDao mockExerciseTestDao;

    @BeforeEach
    void setup() {
        sut = new CompileCodeById(
                mockCodeDao,
                mockExerciseCaseDao,
                mockLanguageDao,
                mockCompilerRepository,
                mockExerciseTestDao
        );
    }

    @Test
    void when_found_exercise_case_should_find_language_by_id() throws NotFoundException, ForbiddenException {
        var foundCode = new Code()
                .setId(codeId)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("content of code")
                .setUserId(userId)
                .setIsResolved(false);
        when(mockCodeDao.findById(codeId)).thenReturn(foundCode);
        var exerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setSolution("a solution")
                .setIsValid(true)
                .setLanguageId(languageId)
                .setStartContent("start content");
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(exerciseCase);

        sut.execute(codeId);

        verify(mockLanguageDao, times(1)).findById(languageId);
    }

    @Test
    void when_language_found_should_get_compiler_by_compiler_repository() throws NotFoundException, ForbiddenException {
        var foundCode = new Code()
                .setId(codeId)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("content of code")
                .setUserId(userId)
                .setIsResolved(false);
        when(mockCodeDao.findById(codeId)).thenReturn(foundCode);
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setSolution("a solution")
                .setIsValid(true)
                .setLanguageId(languageId)
                .setStartContent("start content");
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var foundLanguage = new Language()
                .setId(languageId)
                .setLanguageName(LanguageName.JAVA8)
                .setFileExtension("java");
        when(mockLanguageDao.findById(languageId)).thenReturn(foundLanguage);

        sut.execute(codeId);

        verify(mockCompilerRepository, times(1)).findByLanguage(foundLanguage);
    }

    @Test
    void when_code_found_by_id_should_find_all_tests_by_exercise_case_id() throws NotFoundException, ForbiddenException {
        var foundCode = new Code()
                .setId(codeId)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("content of code")
                .setUserId(userId)
                .setIsResolved(false);
        when(mockCodeDao.findById(codeId)).thenReturn(foundCode);
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setSolution("a solution")
                .setIsValid(true)
                .setLanguageId(languageId)
                .setStartContent("start content");
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var foundLanguage = new Language()
                .setId(languageId)
                .setLanguageName(LanguageName.JAVA8)
                .setFileExtension("java");
        when(mockLanguageDao.findById(languageId)).thenReturn(foundLanguage);
        when(mockCompilerRepository.findByLanguage(foundLanguage)).thenReturn(mockCompiler);

        sut.execute(codeId);

        verify(mockExerciseTestDao, times(1)).findAllByExerciseCaseId(foundCode.getExerciseCaseId());
    }

    @Test
    void when_all_content_and_tests_are_compiled_and_are_success_should_save_code_with_isResolved_property_to_true() throws NotFoundException, ForbiddenException {
        var foundCode = new Code()
                .setId(codeId)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("content of code")
                .setUserId(userId)
                .setIsResolved(false);
        when(mockCodeDao.findById(codeId)).thenReturn(foundCode);
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setSolution("a solution")
                .setIsValid(true)
                .setLanguageId(languageId)
                .setStartContent("start content");
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var foundLanguage = new Language()
                .setId(languageId)
                .setLanguageName(LanguageName.JAVA8)
                .setFileExtension("java");
        when(mockLanguageDao.findById(languageId)).thenReturn(foundLanguage);
        when(mockCompilerRepository.findByLanguage(foundLanguage)).thenReturn(mockCompiler);
        var test1 = new ExerciseTest()
                .setId(1L)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("test 1");
        var test2 = new ExerciseTest()
                .setId(2L)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("test 2");
        when(mockExerciseTestDao.findAllByExerciseCaseId(exerciseCaseId)).thenReturn(Set.of(test1, test2));

        var contentToCompile1 = foundCode.getContent() + System.getProperty("line.separator") + test1.getContent();
        var codeResult1 = new CodeResult()
                .setLanguage(foundLanguage)
                .setOutput("code 1 output")
                .setCodeState(CodeState.SUCCESS);
        when(mockCompiler.compile(contentToCompile1, foundLanguage)).thenReturn(codeResult1);
        var contentToCompile2 = foundCode.getContent() + System.getProperty("line.separator") + test2.getContent();
        var codeResult2 = new CodeResult()
                .setLanguage(foundLanguage)
                .setOutput("code 2 output")
                .setCodeState(CodeState.SUCCESS);
        when(mockCompiler.compile(contentToCompile2, foundLanguage)).thenReturn(codeResult2);

        sut.execute(codeId);

        var expectedCode = new Code()
                .setId(foundCode.getId())
                .setContent(foundCode.getContent())
                .setUserId(foundCode.getUserId())
                .setExerciseCaseId(foundCode.getExerciseCaseId())
                .setIsResolved(true);
        verify(mockCodeDao, times(1)).save(expectedCode);
    }

    @Test
    void when_all_content_and_tests_are_compiled_and_are_success_should_return_DtoCode_with_isResolved_property_to_true() throws NotFoundException, ForbiddenException {
        var foundCode = new Code()
                .setId(codeId)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("content of code")
                .setUserId(userId)
                .setIsResolved(false);
        when(mockCodeDao.findById(codeId)).thenReturn(foundCode);
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setSolution("a solution")
                .setIsValid(true)
                .setLanguageId(languageId)
                .setStartContent("start content");
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var foundLanguage = new Language()
                .setId(languageId)
                .setLanguageName(LanguageName.JAVA8)
                .setFileExtension("java");
        when(mockLanguageDao.findById(languageId)).thenReturn(foundLanguage);
        when(mockCompilerRepository.findByLanguage(foundLanguage)).thenReturn(mockCompiler);
        var test1 = new ExerciseTest()
                .setId(1L)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("test 1");
        var test2 = new ExerciseTest()
                .setId(2L)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("test 2");
        when(mockExerciseTestDao.findAllByExerciseCaseId(exerciseCaseId)).thenReturn(Set.of(test1, test2));

        var contentToCompile1 = foundCode.getContent() + System.getProperty("line.separator") + test1.getContent();
        var codeResult1 = new CodeResult()
                .setLanguage(foundLanguage)
                .setOutput("code 1 output")
                .setCodeState(CodeState.SUCCESS);
        when(mockCompiler.compile(contentToCompile1, foundLanguage)).thenReturn(codeResult1);
        var contentToCompile2 = foundCode.getContent() + System.getProperty("line.separator") + test2.getContent();
        var codeResult2 = new CodeResult()
                .setLanguage(foundLanguage)
                .setOutput("code 2 output")
                .setCodeState(CodeState.SUCCESS);
        when(mockCompiler.compile(contentToCompile2, foundLanguage)).thenReturn(codeResult2);

        var result = sut.execute(codeId);

        var expectedCodeResult1 = new CodeResult()
                .setCodeState(codeResult1.getCodeState())
                .setLanguage(codeResult1.getLanguage())
                .setOutput(codeResult1.getOutput())
                .setTestId(test1.getId());
        var expectedCodeResult2 = new CodeResult()
                .setCodeState(codeResult2.getCodeState())
                .setLanguage(codeResult2.getLanguage())
                .setOutput(codeResult2.getOutput())
                .setTestId(test2.getId());
        var expectedDtoCode = new DtoCode()
                .setCodeId(foundCode.getId())
                .setIsResolved(true)
                .setListCodeResult(List.of(expectedCodeResult1, expectedCodeResult2));

        assertThat(result).isEqualTo(expectedDtoCode);
    }

    @Test
    void when_all_content_and_tests_are_compiled_and_at_least_one_not_success_should_return_DtoCode_with_isResolved_property_to_false() throws NotFoundException, ForbiddenException {
        var foundCode = new Code()
                .setId(codeId)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("content of code")
                .setUserId(userId)
                .setIsResolved(false);
        when(mockCodeDao.findById(codeId)).thenReturn(foundCode);
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setSolution("a solution")
                .setIsValid(true)
                .setLanguageId(languageId)
                .setStartContent("start content");
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var foundLanguage = new Language()
                .setId(languageId)
                .setLanguageName(LanguageName.JAVA8)
                .setFileExtension("java");
        when(mockLanguageDao.findById(languageId)).thenReturn(foundLanguage);
        when(mockCompilerRepository.findByLanguage(foundLanguage)).thenReturn(mockCompiler);
        var test1 = new ExerciseTest()
                .setId(1L)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("test 1");
        var test2 = new ExerciseTest()
                .setId(2L)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("test 2");
        when(mockExerciseTestDao.findAllByExerciseCaseId(exerciseCaseId)).thenReturn(Set.of(test1, test2));

        var contentToCompile1 = foundCode.getContent() + System.getProperty("line.separator") + test1.getContent();
        var codeResult1 = new CodeResult()
                .setLanguage(foundLanguage)
                .setOutput("code 1 output")
                .setCodeState(CodeState.SUCCESS);
        when(mockCompiler.compile(contentToCompile1, foundLanguage)).thenReturn(codeResult1);
        var contentToCompile2 = foundCode.getContent() + System.getProperty("line.separator") + test2.getContent();
        var codeResult2 = new CodeResult()
                .setLanguage(foundLanguage)
                .setOutput("code 2 error")
                .setCodeState(CodeState.RUNTIME_ERROR);
        when(mockCompiler.compile(contentToCompile2, foundLanguage)).thenReturn(codeResult2);

        var result = sut.execute(codeId);

        var expectedCodeResult1 = new CodeResult()
                .setCodeState(codeResult1.getCodeState())
                .setLanguage(codeResult1.getLanguage())
                .setOutput(codeResult1.getOutput())
                .setTestId(test1.getId());
        var expectedCodeResult2 = new CodeResult()
                .setCodeState(codeResult2.getCodeState())
                .setLanguage(codeResult2.getLanguage())
                .setOutput(codeResult2.getOutput())
                .setTestId(test2.getId());
        var expectedDtoCode = new DtoCode()
                .setCodeId(foundCode.getId())
                .setIsResolved(false);
        var listCodeResult = List.of(expectedCodeResult1, expectedCodeResult2);
        var sortedListCodeResult = listCodeResult.stream()
                .sorted(Comparator.comparing(CodeResult::getTestId))
                .collect(Collectors.toList());
        expectedDtoCode.setListCodeResult(sortedListCodeResult);

        assertThat(result).isEqualTo(expectedDtoCode);
    }
}