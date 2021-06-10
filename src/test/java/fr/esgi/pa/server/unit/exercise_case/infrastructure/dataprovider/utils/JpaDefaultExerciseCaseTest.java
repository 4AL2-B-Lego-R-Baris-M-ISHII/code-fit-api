package fr.esgi.pa.server.unit.exercise_case.infrastructure.dataprovider.utils;

import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise_case.core.utils.DefaultExerciseCaseHelper;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseCase;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseTestRepository;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.utils.DefaultExerciseCaseValues;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.utils.JpaDefaultExerciseCase;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.language.infrastructure.dataprovider.LanguageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaDefaultExerciseCaseTest {
    private final long exerciseId = 45L;
    private JpaDefaultExerciseCase sut;

    @Mock
    private ExerciseRepository mockExerciseRepository;

    @Mock
    private LanguageRepository mockLanguageRepository;

    @Mock
    private DefaultExerciseCaseHelper mockDefaultExerciseCaseHelper;

    @Mock
    private ExerciseCaseRepository mockExerciseCaseRepository;

    @Mock
    private ExerciseTestRepository mockExerciseTestRepository;

    @BeforeEach
    void setup() {
        sut = new JpaDefaultExerciseCase(
                mockExerciseRepository,
                mockLanguageRepository,
                mockDefaultExerciseCaseHelper,
                mockExerciseCaseRepository,
                mockExerciseTestRepository
        );
    }

    @Test
    void when_exercise_not_exists_should_throw_NotFoundException() {
        when(mockExerciseRepository.existsById(exerciseId)).thenReturn(false);
        var language = new Language()
                .setId(2L)
                .setLanguageName(LanguageName.JAVA)
                .setFileExtension("java");
        assertThatThrownBy(() -> sut.createExerciseCase(exerciseId, language))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(
                        "%s : Exercise with id '%d' not found",
                        CommonExceptionState.NOT_FOUND,
                        exerciseId
                );
    }

    @Test
    void when_language_not_exists_should_throw_NotFoundException() {
        when(mockExerciseRepository.existsById(exerciseId)).thenReturn(true);
        var language = new Language()
                .setId(2L)
                .setLanguageName(LanguageName.JAVA)
                .setFileExtension("java");
        when(mockLanguageRepository.existsById(language.getId())).thenReturn(false);

        assertThatThrownBy(() -> sut.createExerciseCase(exerciseId, language))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(
                        "%s : Language with id '%d' not found",
                        CommonExceptionState.NOT_FOUND,
                        language.getId()
                );
    }

    @Test
    void when_default_exercise_case_saved_should_save_exercise_test_with_default_content() throws NotFoundException {
        when(mockExerciseRepository.existsById(exerciseId)).thenReturn(true);
        var language = new Language()
                .setId(2L)
                .setLanguageName(LanguageName.JAVA)
                .setFileExtension("java");
        when(mockLanguageRepository.existsById(language.getId())).thenReturn(true);
        var defaultValues = new DefaultExerciseCaseValues()
                .setStartContent("default start content")
                .setSolution("default solution")
                .setTestContent("default test content");
        when(mockDefaultExerciseCaseHelper.getValuesByLanguage(language))
                .thenReturn(defaultValues);
        var exerciseCaseToSave = new JpaExerciseCase()
                .setIsValid(false)
                .setLanguageId(language.getId())
                .setExerciseId(exerciseId)
                .setStartContent(defaultValues.getStartContent())
                .setSolution(defaultValues.getSolution());
        var savedExerciseCase = new JpaExerciseCase()
                .setId(90L)
                .setIsValid(false)
                .setLanguageId(language.getId())
                .setExerciseId(exerciseId)
                .setStartContent(defaultValues.getStartContent())
                .setSolution(defaultValues.getSolution());
        when(mockExerciseCaseRepository.save(exerciseCaseToSave)).thenReturn(savedExerciseCase);

        sut.createExerciseCase(exerciseId, language);

        var expectedExerciseTest = new JpaExerciseTest()
                .setExerciseCaseId(savedExerciseCase.getId())
                .setContent(defaultValues.getTestContent());
        verify(mockExerciseTestRepository, times(1)).save(expectedExerciseTest);
    }

    @Test
    void when_exercise_test_with_default_content_saved_should_return_saved_exercise_case_id() throws NotFoundException {
        when(mockExerciseRepository.existsById(exerciseId)).thenReturn(true);
        var language = new Language()
                .setId(2L)
                .setLanguageName(LanguageName.JAVA)
                .setFileExtension("java");
        when(mockLanguageRepository.existsById(language.getId())).thenReturn(true);
        var defaultValues = new DefaultExerciseCaseValues()
                .setStartContent("default start content")
                .setSolution("default solution")
                .setTestContent("default test content");
        when(mockDefaultExerciseCaseHelper.getValuesByLanguage(language))
                .thenReturn(defaultValues);
        var exerciseCaseToSave = new JpaExerciseCase()
                .setIsValid(false)
                .setLanguageId(language.getId())
                .setExerciseId(exerciseId)
                .setStartContent(defaultValues.getStartContent())
                .setSolution(defaultValues.getSolution());
        var savedExerciseCase = new JpaExerciseCase()
                .setId(90L)
                .setIsValid(false)
                .setLanguageId(language.getId())
                .setExerciseId(exerciseId)
                .setStartContent(defaultValues.getStartContent())
                .setSolution(defaultValues.getSolution());
        when(mockExerciseCaseRepository.save(exerciseCaseToSave)).thenReturn(savedExerciseCase);
        var expectedExerciseTest = new JpaExerciseTest()
                .setExerciseCaseId(savedExerciseCase.getId())
                .setContent(defaultValues.getTestContent());
        when(mockExerciseTestRepository.save(expectedExerciseTest)).thenReturn(null);

        var result = sut.createExerciseCase(exerciseId, language);

        assertThat(result).isEqualTo(savedExerciseCase.getId());
    }
}