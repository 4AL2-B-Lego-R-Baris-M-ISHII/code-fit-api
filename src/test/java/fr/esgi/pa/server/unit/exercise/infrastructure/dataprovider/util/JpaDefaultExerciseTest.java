package fr.esgi.pa.server.unit.exercise.infrastructure.dataprovider.util;

import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.util.DefaultExerciseHelper;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.util.DefaultExerciseValues;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.util.JpaDefaultExercise;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseCase;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseTest;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository.ExerciseTestRepository;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaDefaultExerciseTest {

    private final String title = "title";
    private final String description = "description";
    private final long userId = 3L;
    private JpaDefaultExercise sut;

    @Mock
    private ExerciseRepository mockExerciseRepository;

    @Mock
    private ExerciseCaseRepository mockExerciseCaseRepository;

    @Mock
    private ExerciseTestRepository mockExerciseTestRepository;

    @Mock
    private DefaultExerciseHelper mockDefaultExerciseHelper;

    private final Language language = new Language().setId(1L)
            .setLanguageName(LanguageName.JAVA)
            .setFileExtension("java");

    @BeforeEach
    void setup() {
        sut = new JpaDefaultExercise(mockExerciseRepository, mockExerciseCaseRepository, mockExerciseTestRepository, mockDefaultExerciseHelper);
    }

    @Test
    void when_exercise_case_saved_should_save_exercise_test() {
        var exerciseToSave = new JpaExercise()
                .setTitle(title)
                .setDescription(description)
                .setUserId(userId);
        var savedExercise = new JpaExercise()
                .setTitle(title)
                .setDescription(description)
                .setUserId(userId)
                .setId(2L);
        when(mockExerciseRepository.save(exerciseToSave)).thenReturn(savedExercise);
        var defaultValues = new DefaultExerciseValues()
                .setSolution("a solution")
                .setStartContent("a start content")
                .setTestContent("one test");
        when(mockDefaultExerciseHelper.getValuesByLanguage(language)).thenReturn(defaultValues);
        var exerciseCaseToSave = new JpaExerciseCase()
                .setExerciseId(savedExercise.getId())
                .setIsValid(false)
                .setStartContent(defaultValues.getStartContent())
                .setSolution(defaultValues.getSolution())
                .setLanguageId(language.getId());
        var savedExerciseCase = new JpaExerciseCase()
                .setExerciseId(exerciseCaseToSave.getExerciseId())
                .setIsValid(exerciseCaseToSave.getIsValid())
                .setSolution(exerciseCaseToSave.getSolution())
                .setLanguageId(exerciseCaseToSave.getLanguageId())
                .setStartContent(exerciseCaseToSave.getStartContent())
                .setId(5L);
        when(mockExerciseCaseRepository.save(exerciseCaseToSave)).thenReturn(savedExerciseCase);

        sut.createDefaultExercise(title, description, language, userId);

        var expectedExerciseTest = new JpaExerciseTest()
                .setExerciseCaseId(savedExerciseCase.getId())
                .setContent(defaultValues.getTestContent());

        verify(mockExerciseTestRepository, times(1)).save(expectedExerciseTest);
    }

    @Test
    void when_exercise_test_saved_should_return_exercise_id() {
        var exerciseToSave = new JpaExercise()
                .setTitle(title)
                .setDescription(description)
                .setUserId(userId);
        var savedExercise = new JpaExercise()
                .setTitle(title)
                .setDescription(description)
                .setUserId(userId)
                .setId(2L);
        when(mockExerciseRepository.save(exerciseToSave)).thenReturn(savedExercise);
        var defaultValues = new DefaultExerciseValues()
                .setSolution("a solution")
                .setStartContent("a start content")
                .setTestContent("one test");
        when(mockDefaultExerciseHelper.getValuesByLanguage(language)).thenReturn(defaultValues);
        var exerciseCaseToSave = new JpaExerciseCase()
                .setExerciseId(savedExercise.getId())
                .setIsValid(false)
                .setStartContent(defaultValues.getStartContent())
                .setSolution(defaultValues.getSolution())
                .setLanguageId(language.getId());
        var savedExerciseCase = new JpaExerciseCase()
                .setExerciseId(exerciseCaseToSave.getExerciseId())
                .setIsValid(exerciseCaseToSave.getIsValid())
                .setSolution(exerciseCaseToSave.getSolution())
                .setLanguageId(exerciseCaseToSave.getLanguageId())
                .setStartContent(exerciseCaseToSave.getStartContent())
                .setId(5L);
        when(mockExerciseCaseRepository.save(exerciseCaseToSave)).thenReturn(savedExerciseCase);
        var exerciseTestToSave = new JpaExerciseTest()
                .setExerciseCaseId(savedExerciseCase.getId())
                .setContent(defaultValues.getTestContent());
        var savedExerciseTest = new JpaExerciseTest()
                .setExerciseCaseId(exerciseTestToSave.getExerciseCaseId())
                .setContent(exerciseTestToSave.getContent())
                .setId(3L);
        when(mockExerciseTestRepository.save(exerciseTestToSave)).thenReturn(savedExerciseTest);

        var result = sut.createDefaultExercise(title, description, language, userId);

        assertThat(result).isEqualTo(savedExercise.getId());
    }
}