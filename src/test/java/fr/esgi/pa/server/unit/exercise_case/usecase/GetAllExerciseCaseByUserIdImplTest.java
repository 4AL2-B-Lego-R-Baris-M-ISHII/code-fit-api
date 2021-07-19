package fr.esgi.pa.server.unit.exercise_case.usecase;

import fr.esgi.pa.server.code.core.adapter.CodeAdapter;
import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.dto.DtoCode;
import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseCase;
import fr.esgi.pa.server.exercise_case.core.dto.DtoExerciseTest;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter.ExerciseCaseAdapter;
import fr.esgi.pa.server.exercise_case.infrastructure.entrypoint.adapter.ExerciseTestAdapter;
import fr.esgi.pa.server.exercise_case.usecase.GetAllExerciseCaseByUserIdImpl;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageDao;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class GetAllExerciseCaseByUserIdImplTest {
    private final long userId = 61L;
    private GetAllExerciseCaseByUserIdImpl sut;

    @Mock
    private CodeDao mockCodeDao;

    @Mock
    private ExerciseCaseDao mockExerciseCaseDao;

    @Mock
    private LanguageDao mockLanguageDao;

    @Mock
    private ExerciseTestDao mockExerciseTestDao;

    @Autowired
    private ExerciseCaseAdapter exerciseCaseAdapter;

    @Autowired
    private ExerciseTestAdapter exerciseTestAdapter;

    @Autowired
    private CodeAdapter codeAdapter;

    @BeforeEach
    void setup() {
        sut = new GetAllExerciseCaseByUserIdImpl(
                mockCodeDao,
                mockExerciseCaseDao,
                mockLanguageDao,
                mockExerciseTestDao,
                exerciseCaseAdapter,
                exerciseTestAdapter,
                codeAdapter
        );
    }

    @Test
    void should_get_all_code_by_user_id() throws NotFoundException {
        sut.execute(userId);

        verify(mockCodeDao, times(1)).findAllByUserId(userId);
    }

    @Test
    void when_get_all_code_by_user_id_should_return_set_exercise_case() throws NotFoundException {
        var code7 = new Code().setId(7L)
                .setUserId(userId)
                .setExerciseCaseId(123L)
                .setContent("code content7")
                .setIsResolved(true);
        var code8 = new Code().setId(8L)
                .setUserId(userId)
                .setExerciseCaseId(123L)
                .setContent("code content8")
                .setIsResolved(true);
        var code9 = new Code().setId(9L)
                .setUserId(userId)
                .setExerciseCaseId(124L)
                .setContent("code content9")
                .setIsResolved(true);
        var setCode = Set.of(code7, code8, code9);
        when(mockCodeDao.findAllByUserId(userId)).thenReturn(setCode);

        sut.execute(userId);

        verify(mockExerciseCaseDao, times(1)).findAllByIdIn(Set.of(123L, 124L));
    }

    @Test
    void when_all_code_is_not_resolve_should_not_find_all_exercise_case_by_id_in_and_return_empty_set() throws NotFoundException {
        var code7 = new Code().setId(7L)
                .setUserId(userId)
                .setExerciseCaseId(123L)
                .setContent("code content7")
                .setIsResolved(false);
        var code8 = new Code().setId(8L)
                .setUserId(userId)
                .setExerciseCaseId(123L)
                .setContent("code content8")
                .setIsResolved(false);
        var code9 = new Code().setId(9L)
                .setUserId(userId)
                .setExerciseCaseId(124L)
                .setContent("code content9")
                .setIsResolved(false);
        var setCode = Set.of(code7, code8, code9);
        when(mockCodeDao.findAllByUserId(userId)).thenReturn(setCode);

        var result = sut.execute(userId);

        verify(mockExerciseCaseDao, never()).findAllByIdIn(Set.of(123L, 124L));
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void when_find_all_exercise_cases_by_ids_should_get_language_by_id() throws NotFoundException {
        var code7 = new Code().setId(7L)
                .setUserId(userId)
                .setExerciseCaseId(123L)
                .setContent("code content7")
                .setIsResolved(true);
        var code8 = new Code().setId(8L)
                .setUserId(userId)
                .setExerciseCaseId(123L)
                .setContent("code content8")
                .setIsResolved(true);
        var code9 = new Code().setId(9L)
                .setUserId(userId)
                .setExerciseCaseId(124L)
                .setContent("code content9")
                .setIsResolved(true);
        var setCode = Set.of(code7, code8, code9);
        when(mockCodeDao.findAllByUserId(userId)).thenReturn(setCode);
        var exerciseCase1 = new ExerciseCase()
                .setId(123L)
                .setLanguageId(1L)
                .setExerciseId(7L)
                .setStartContent("start content")
                .setSolution("solution");
        var exerciseCase2 = new ExerciseCase()
                .setId(124L)
                .setLanguageId(2L)
                .setExerciseId(8L)
                .setStartContent("start content2")
                .setSolution("solution2");
        var setExerciseCase = Set.of(
                exerciseCase1, exerciseCase2
        );
        when(mockExerciseCaseDao.findAllByIdIn(Set.of(123L, 124L))).thenReturn(setExerciseCase);

        sut.execute(userId);

        verify(mockLanguageDao, times(1)).findById(1L);
        verify(mockLanguageDao, times(1)).findById(2L);
    }

    @Test
    void when_get_language_by_id_of_all_exercise_cases_should_get_tests() throws NotFoundException {
        var code7 = new Code().setId(7L)
                .setUserId(userId)
                .setExerciseCaseId(123L)
                .setContent("code content7")
                .setIsResolved(true);
        var setCode = Set.of(code7);
        when(mockCodeDao.findAllByUserId(userId)).thenReturn(setCode);
        var exerciseCase1 = new ExerciseCase()
                .setId(123L)
                .setLanguageId(1L)
                .setExerciseId(7L)
                .setStartContent("start content")
                .setSolution("solution");
        var setExerciseCase = Set.of(exerciseCase1);
        when(mockExerciseCaseDao.findAllByIdIn(Set.of(123L))).thenReturn(setExerciseCase);
        var language = new Language().setId(7L).setLanguageName(LanguageName.C11).setFileExtension("c");
        when(mockLanguageDao.findById(1L)).thenReturn(language);

        sut.execute(userId);

        verify(mockExerciseTestDao, times(1)).findAllByExerciseCaseId(123L);
    }

    @Test
    void when_get_tests_of_all_exercise_cases_should_return_dto_exercise_case_with_language_dto_code_and_dto_tests() throws NotFoundException {
        var code7 = new Code().setId(7L)
                .setUserId(userId)
                .setExerciseCaseId(123L)
                .setContent("code content7")
                .setIsResolved(true);
        var setCode = Set.of(code7);
        when(mockCodeDao.findAllByUserId(userId)).thenReturn(setCode);
        var exerciseCase1 = new ExerciseCase()
                .setId(123L)
                .setLanguageId(1L)
                .setIsValid(true)
                .setExerciseId(7L)
                .setStartContent("start content")
                .setSolution("solution");
        var setExerciseCase = Set.of(exerciseCase1);
        when(mockExerciseCaseDao.findAllByIdIn(Set.of(123L))).thenReturn(setExerciseCase);
        var language = new Language().setId(7L).setLanguageName(LanguageName.C11).setFileExtension("c");
        when(mockLanguageDao.findById(1L)).thenReturn(language);
        var test = new ExerciseTest().setId(46L).setContent("test content").setExerciseCaseId(123L);
        when(mockExerciseTestDao.findAllByExerciseCaseId(123L)).thenReturn(Set.of(test));

        var expectedCode = new DtoCode().setCodeId(code7.getId()).setContent(code7.getContent())
                .setIsResolved(code7.getIsResolved());
        var expectedTest = new DtoExerciseTest().setId(test.getId()).setContent(test.getContent());
        var expectedDtoExerciseCase = new DtoExerciseCase()
                .setId(123L)
                .setLanguage(language)
                .setCodes(Set.of(expectedCode))
                .setTests(Set.of(expectedTest))
                .setIsValid(true)
                .setSolution(exerciseCase1.getSolution())
                .setStartContent(exerciseCase1.getStartContent());
        var expectedResult = new HashSet<DtoExerciseCase>();
        expectedResult.add(expectedDtoExerciseCase);


        var result = sut.execute(userId);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void when_get_few_codes_with_different_exercise_cases_should_return_appropriate_set_dto_exercise_case() throws NotFoundException {
        var code7 = new Code().setId(7L)
                .setUserId(userId)
                .setExerciseCaseId(123L)
                .setContent("code content7")
                .setIsResolved(true);
        var code8 = new Code().setId(8L)
                .setUserId(userId)
                .setExerciseCaseId(123L)
                .setContent("code content8")
                .setIsResolved(true);
        var code9 = new Code().setId(9L)
                .setUserId(userId)
                .setExerciseCaseId(124L)
                .setContent("code content9")
                .setIsResolved(true);
        var setCode = Set.of(code7, code8, code9);
        when(mockCodeDao.findAllByUserId(userId)).thenReturn(setCode);
        var exerciseCase1 = new ExerciseCase()
                .setId(123L)
                .setLanguageId(1L)
                .setIsValid(true)
                .setExerciseId(7L)
                .setStartContent("start content")
                .setSolution("solution");
        var exerciseCase2 = new ExerciseCase()
                .setId(124L)
                .setLanguageId(2L)
                .setIsValid(true)
                .setExerciseId(8L)
                .setStartContent("start content")
                .setSolution("solution");
        var setExerciseCase = Set.of(exerciseCase1, exerciseCase2);
        when(mockExerciseCaseDao.findAllByIdIn(Set.of(123L, 124L))).thenReturn(setExerciseCase);
        var language1 = new Language().setId(1L).setLanguageName(LanguageName.C11).setFileExtension("c");
        when(mockLanguageDao.findById(1L)).thenReturn(language1);
        var language2 = new Language().setId(2L).setLanguageName(LanguageName.JAVA8).setFileExtension("java");
        when(mockLanguageDao.findById(2L)).thenReturn(language2);
        var test1 = new ExerciseTest().setId(46L).setContent("test content").setExerciseCaseId(123L);
        when(mockExerciseTestDao.findAllByExerciseCaseId(123L)).thenReturn(Set.of(test1));
        var test2 = new ExerciseTest().setId(47L).setContent("test content2").setExerciseCaseId(124L);
        when(mockExerciseTestDao.findAllByExerciseCaseId(124L)).thenReturn(Set.of(test2));

        var expectedResult = new HashSet<DtoExerciseCase>();

        var expectedCode1 = new DtoCode().setCodeId(code7.getId()).setContent(code7.getContent())
                .setIsResolved(code7.getIsResolved());
        var expectedCode2 = new DtoCode().setCodeId(code8.getId()).setContent(code8.getContent())
                .setIsResolved(code8.getIsResolved());
        var expectedTest1 = new DtoExerciseTest().setId(test1.getId()).setContent(test1.getContent());
        var expectedDtoExerciseCase1 = new DtoExerciseCase()
                .setId(123L)
                .setLanguage(language1)
                .setCodes(Set.of(expectedCode1, expectedCode2))
                .setTests(Set.of(expectedTest1))
                .setIsValid(true)
                .setSolution(exerciseCase1.getSolution())
                .setStartContent(exerciseCase1.getStartContent());
        expectedResult.add(expectedDtoExerciseCase1);

        var expectedCode3 = new DtoCode().setCodeId(code9.getId()).setContent(code9.getContent())
                .setIsResolved(code9.getIsResolved());
        var expectedTest2 = new DtoExerciseTest().setId(test2.getId()).setContent(test2.getContent());
        var expectedDtoExerciseCase2 = new DtoExerciseCase()
                .setId(124L)
                .setLanguage(language2)
                .setCodes(Set.of(expectedCode3))
                .setTests(Set.of(expectedTest2))
                .setIsValid(true)
                .setSolution(exerciseCase2.getSolution())
                .setStartContent(exerciseCase2.getStartContent());
        expectedResult.add(expectedDtoExerciseCase2);

        var result = sut.execute(userId);

        assertThat(result).isEqualTo(expectedResult);
    }
}