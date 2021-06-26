package fr.esgi.pa.server.unit.exercise_case.usecase;

import fr.esgi.pa.server.code.core.compiler.CodeResult;
import fr.esgi.pa.server.code.core.compiler.CodeState;
import fr.esgi.pa.server.code.core.compiler.Compiler;
import fr.esgi.pa.server.code.core.compiler.CompilerRepository;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;
import fr.esgi.pa.server.exercise_case.usecase.VerifyExerciseCase;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerifyExerciseCaseTest {
    private final long exerciseCaseId = 5L;
    private final long exerciseId = 32L;
    private final long languageId = 946L;
    private final String solution = "solution of exercise";
    private final String startContent = "start content";
    private VerifyExerciseCase sut;

    @Mock
    private ExerciseCaseDao mockExerciseCaseDao;

    @Mock
    private ExerciseTestDao mockExerciseTestDao;

    @Mock
    private LanguageDao mockLanguageDao;

    @Mock
    private CompilerRepository mockCompilerRepository;

    @Mock
    private Compiler mockCompiler;

    @BeforeEach
    void setup() {
        sut = new VerifyExerciseCase(
                mockExerciseCaseDao,
                mockExerciseTestDao,
                mockLanguageDao,
                mockCompilerRepository
        );
    }

    @Test
    void when_exerciseCase_found_should_find_all_tests_by_exerciseCaseId() throws NotFoundException {
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setIsValid(false)
                .setSolution(solution)
                .setStartContent(startContent);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);

        sut.execute(exerciseCaseId);

        verify(mockExerciseTestDao, times(1)).findAllByExerciseCaseId(exerciseCaseId);
    }

    @Test
    void when_exerciseCase_found_should_find_language_of_exerciseCase() throws NotFoundException {
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setIsValid(false)
                .setSolution(solution)
                .setStartContent(startContent);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);

        sut.execute(exerciseCaseId);

        verify(mockLanguageDao, times(1)).findById(foundExerciseCase.getLanguageId());
    }

    @Test
    void when_compiler_compiled_all_tests_and_get_at_least_one_none_success_state_should_return_result_with_isExerciseCaseValid_to_false() throws NotFoundException {
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setIsValid(false)
                .setSolution(solution)
                .setStartContent(startContent);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var foundLanguage = new Language()
                .setId(78L)
                .setLanguageName(LanguageName.JAVA8)
                .setFileExtension("java");
        when(mockLanguageDao.findById(foundExerciseCase.getLanguageId())).thenReturn(foundLanguage);
        var test1 = new ExerciseTest()
                .setId(7L)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("content test 7");
        var test2 = new ExerciseTest()
                .setId(8L)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("content test 8");
        var setTest = Set.of(test1, test2);
        when(mockExerciseTestDao.findAllByExerciseCaseId(exerciseCaseId)).thenReturn(setTest);
        when(mockCompilerRepository.findByLanguage(foundLanguage)).thenReturn(mockCompiler);
        var code1 = new CodeResult()
                .setLanguage(foundLanguage)
                .setCodeState(CodeState.SUCCESS)
                .setOutput("output code 1");
        var code2 = new CodeResult()
                .setLanguage(foundLanguage)
                .setCodeState(CodeState.COMPILATION_ERROR)
                .setOutput("output code 2");
        var contentToCompile1 = foundExerciseCase.getSolution() + System.getProperty("line.separator") + test1.getContent();
        var contentToCompile2 = foundExerciseCase.getSolution() + System.getProperty("line.separator") + test2.getContent();
        when(mockCompiler.compile(contentToCompile1, foundLanguage)).thenReturn(code1);
        when(mockCompiler.compile(contentToCompile2, foundLanguage)).thenReturn(code2);

        var result = sut.execute(exerciseCaseId);

        var expectedCodeList = List.of(code1, code2);
        assertThat(result.getIsExerciseCaseValid()).isFalse();
        assertThat(result.getCodeResultList()).isNotNull();
        assertThat(result.getCodeResultList().size()).isEqualTo(expectedCodeList.size());
        assertThat(result.getCodeResultList().containsAll(expectedCodeList)).isTrue();
    }

    @Test
    void when_compiler_compiled_all_tests_and_get_at_least_one_none_success_state_should_save_with_isValid_to_false() throws NotFoundException {
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setIsValid(false)
                .setSolution(solution)
                .setStartContent(startContent);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var foundLanguage = new Language()
                .setId(78L)
                .setLanguageName(LanguageName.JAVA8)
                .setFileExtension("java");
        when(mockLanguageDao.findById(foundExerciseCase.getLanguageId())).thenReturn(foundLanguage);
        var test1 = new ExerciseTest()
                .setId(7L)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("content test 7");
        var test2 = new ExerciseTest()
                .setId(8L)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("content test 8");
        var setTest = Set.of(test1, test2);
        when(mockExerciseTestDao.findAllByExerciseCaseId(exerciseCaseId)).thenReturn(setTest);
        when(mockCompilerRepository.findByLanguage(foundLanguage)).thenReturn(mockCompiler);
        var code1 = new CodeResult()
                .setLanguage(foundLanguage)
                .setCodeState(CodeState.SUCCESS)
                .setOutput("output code 1");
        var code2 = new CodeResult()
                .setLanguage(foundLanguage)
                .setCodeState(CodeState.COMPILATION_ERROR)
                .setOutput("output code 2");
        var contentToCompile1 = foundExerciseCase.getSolution() + System.getProperty("line.separator") + test1.getContent();
        var contentToCompile2 = foundExerciseCase.getSolution() + System.getProperty("line.separator") + test2.getContent();
        when(mockCompiler.compile(contentToCompile1, foundLanguage)).thenReturn(code1);
        when(mockCompiler.compile(contentToCompile2, foundLanguage)).thenReturn(code2);

        sut.execute(exerciseCaseId);

        var expectedExerciseCaseToSave = new ExerciseCase()
                .setId(foundExerciseCase.getId())
                .setExerciseId(foundExerciseCase.getExerciseId())
                .setIsValid(false)
                .setSolution(foundExerciseCase.getSolution())
                .setStartContent(foundExerciseCase.getStartContent())
                .setLanguageId(foundExerciseCase.getLanguageId());
        verify(mockExerciseCaseDao, times(1)).saveOne(expectedExerciseCaseToSave);
    }

    @Test
    void when_compiler_compiled_all_tests_and_get_all_success_state_should_valid_exercise_case() throws NotFoundException {
        var foundExerciseCase = new ExerciseCase()
                .setId(exerciseCaseId)
                .setExerciseId(exerciseId)
                .setLanguageId(languageId)
                .setIsValid(false)
                .setSolution(solution)
                .setStartContent(startContent);
        when(mockExerciseCaseDao.findById(exerciseCaseId)).thenReturn(foundExerciseCase);
        var foundLanguage = new Language()
                .setId(78L)
                .setLanguageName(LanguageName.JAVA8)
                .setFileExtension("java");
        when(mockLanguageDao.findById(foundExerciseCase.getLanguageId())).thenReturn(foundLanguage);
        var test1 = new ExerciseTest()
                .setId(7L)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("content test 7");
        var test2 = new ExerciseTest()
                .setId(8L)
                .setExerciseCaseId(exerciseCaseId)
                .setContent("content test 8");
        var setTest = Set.of(test1, test2);
        when(mockExerciseTestDao.findAllByExerciseCaseId(exerciseCaseId)).thenReturn(setTest);
        when(mockCompilerRepository.findByLanguage(foundLanguage)).thenReturn(mockCompiler);
        var code1 = new CodeResult()
                .setLanguage(foundLanguage)
                .setCodeState(CodeState.SUCCESS)
                .setOutput("output code 1");
        var code2 = new CodeResult()
                .setLanguage(foundLanguage)
                .setCodeState(CodeState.SUCCESS)
                .setOutput("output code 2");
        var contentToCompile1 = foundExerciseCase.getSolution() + System.getProperty("line.separator") + test1.getContent();
        var contentToCompile2 = foundExerciseCase.getSolution() + System.getProperty("line.separator") + test2.getContent();
        when(mockCompiler.compile(contentToCompile1, foundLanguage)).thenReturn(code1);
        when(mockCompiler.compile(contentToCompile2, foundLanguage)).thenReturn(code2);
        var expectedExerciseCaseToSave = new ExerciseCase()
                .setId(foundExerciseCase.getId())
                .setExerciseId(foundExerciseCase.getExerciseId())
                .setIsValid(true)
                .setSolution(foundExerciseCase.getSolution())
                .setStartContent(foundExerciseCase.getStartContent())
                .setLanguageId(foundExerciseCase.getLanguageId());
        when(mockExerciseCaseDao.saveOne(expectedExerciseCaseToSave)).thenReturn(null);

        var result = sut.execute(exerciseCaseId);

        var expectedCodeList = List.of(code1, code2);
        assertThat(result.getIsExerciseCaseValid()).isTrue();
        assertThat(result.getCodeResultList()).isNotNull();
        assertThat(result.getCodeResultList().size()).isEqualTo(expectedCodeList.size());
        assertThat(result.getCodeResultList().containsAll(expectedCodeList)).isTrue();
    }
}