package fr.esgi.pa.server.unit.exercise_case.usecase;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.core.Compiler;
import fr.esgi.pa.server.code.core.CompilerRepository;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.dto.DtoVerifyExerciseCase;
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

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerifyExerciseCaseTest {
    private final long exerciseCaseId = 5L;
    private final long userId = 12L;
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
    void when_set_exerciseTest_found_should_findCompiler_by_compilerRepository() throws NotFoundException {
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
                .setLanguageName(LanguageName.JAVA)
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

        sut.execute(exerciseCaseId);

        verify(mockCompilerRepository, times(1)).findByLanguage(foundLanguage);
    }

    @Test
    void when_compiler_found_should_execute_all_tests_with_concerned_language() throws NotFoundException {
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
                .setLanguageName(LanguageName.JAVA)
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

        sut.execute(exerciseCaseId);

        verify(mockCompiler, times(1)).compile(test1.getContent(), foundLanguage);
        verify(mockCompiler, times(1)).compile(test2.getContent(), foundLanguage);
    }

    @Test
    void when_compiler_compiled_all_tests_and_get_at_least_one_none_success_state_should_throw_exception() throws NotFoundException {
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
                .setLanguageName(LanguageName.JAVA)
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
        var code1 = new Code()
                .setLanguage(foundLanguage)
                .setCodeState(CodeState.SUCCESS)
                .setOutput("output code 1");
        var code2 = new Code()
                .setLanguage(foundLanguage)
                .setCodeState(CodeState.COMPILATION_ERROR)
                .setOutput("output code 2");
        when(mockCompiler.compile(test1.getContent(), foundLanguage)).thenReturn(code1);
        when(mockCompiler.compile(test2.getContent(), foundLanguage)).thenReturn(code2);

        var result = sut.execute(exerciseCaseId);

        var expected = new DtoVerifyExerciseCase()
                .setIsExerciseCaseValid(false)
                .setSetCode(Set.of(code1, code2));
        assertThat(result).isEqualTo(expected);
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
                .setLanguageName(LanguageName.JAVA)
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
        var code1 = new Code()
                .setLanguage(foundLanguage)
                .setCodeState(CodeState.SUCCESS)
                .setOutput("output code 1");
        var code2 = new Code()
                .setLanguage(foundLanguage)
                .setCodeState(CodeState.SUCCESS)
                .setOutput("output code 2");
        when(mockCompiler.compile(test1.getContent(), foundLanguage)).thenReturn(code1);
        when(mockCompiler.compile(test2.getContent(), foundLanguage)).thenReturn(code2);

        var result = sut.execute(exerciseCaseId);

        var expected = new DtoVerifyExerciseCase()
                .setIsExerciseCaseValid(true)
                .setSetCode(Set.of(code1, code2));
        assertThat(result).isEqualTo(expected);
    }
}