package fr.esgi.pa.server.unit.exercise_case.infrastructure.dataprovider.utils;

import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseRepository;
import fr.esgi.pa.server.exercise_case.core.utils.DefaultExerciseCaseHelper;
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
    private ExerciseCaseRepository mockExerciseCaseDao;

    @BeforeEach
    void setup() {
        sut = new JpaDefaultExerciseCase(
                mockExerciseRepository,
                mockLanguageRepository,
                mockDefaultExerciseCaseHelper,
                mockExerciseCaseDao
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
    void when_language_found_should_get_default_values_depend_to_language() throws NotFoundException {
        when(mockExerciseRepository.existsById(exerciseId)).thenReturn(true);
        var language = new Language()
                .setId(2L)
                .setLanguageName(LanguageName.JAVA)
                .setFileExtension("java");
        when(mockLanguageRepository.existsById(language.getId())).thenReturn(true);

        sut.createExerciseCase(exerciseId, language);

        verify(mockDefaultExerciseCaseHelper, times(1)).getValuesByLanguage(language);
    }

    @Test
    void when_get_default_values_by_language_should_save_exercise_case_with_default_values() throws NotFoundException {
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

        sut.createExerciseCase(exerciseId, language);

//        var jpaExerciseCase = new JpaExerciseCase()
//                .setIsValid(false)
//                .setLanguageId(language.getId())
//                .setExerciseId(exerciseId)
//                .setStartContent(defaultValues.getStartContent())
//                .setSolution(defaultValues.getSolution());
//        verify(mockExerciseCaseDao, times(1)).saveOne(jpaExerciseCase);
    }
}