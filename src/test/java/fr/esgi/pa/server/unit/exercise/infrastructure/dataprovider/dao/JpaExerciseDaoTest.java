package fr.esgi.pa.server.unit.exercise.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise.core.exception.IncorrectExerciseException;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.dao.JpaExerciseDao;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper.ExerciseMapper;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.user.core.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaExerciseDaoTest {

    private final String exerciseTitle = "exercise title";
    private final String description = "description";
    @Mock
    private UserDao mockUserDao;

    @Mock
    private ExerciseRepository mockExerciseRepository;

    @Mock
    private ExerciseCaseDao mockExerciseCaseDao;

    private final ExerciseMapper exerciseMapper = new ExerciseMapper();

    private JpaExerciseDao sut;


    @BeforeEach
    void setup() {
        sut = new JpaExerciseDao(
                mockUserDao,
                mockExerciseCaseDao,
                mockExerciseRepository,
                exerciseMapper
        );
    }

    @Nested
    class CreateExercise {
        @Test
        void when_user_with_given_user_id_not_exists_should_throw() {
            when(mockUserDao.existsById(21L)).thenReturn(false);
            assertThatThrownBy(() -> sut.createExercise(exerciseTitle, description, 21L))
                    .isExactlyInstanceOf(NotFoundException.class)
                    .hasMessage(String.format("%s : User with user id '%d' not found", sut.getClass(), 21L));
        }

        @Test
        void when_exercise_saved_should_return_exercise() throws NotFoundException {
            when(mockUserDao.existsById(22L)).thenReturn(true);
            var exerciseToSave = new JpaExercise()
                    .setTitle(exerciseTitle)
                    .setDescription(description);
            var savedExercise = new JpaExercise()
                    .setId(1L)
                    .setUserId(22L)
                    .setTitle(exerciseTitle)
                    .setDescription(description);
            when(mockExerciseRepository.save(exerciseToSave)).thenReturn(savedExercise);

            var result = sut.createExercise(exerciseTitle, description, 22L);

            var expectedExercise = exerciseMapper.entityToDomain(savedExercise);
            assertThat(result).isEqualTo(expectedExercise);
        }
    }

    @Nested
    class FindExerciseById {
        @Test
        void when_exercise_not_found_should_throw_NotFoundException() {
            when(mockExerciseRepository.findById(20L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.findById(20L))
                    .isExactlyInstanceOf(NotFoundException.class)
                    .hasMessage(String.format("%s : Exercise with id '%d' not found", NotFoundException.class, 20L));
        }

        @Test
        void when_exercise_found_should_return_found_exercise() throws NotFoundException {
            var foundExercise = new JpaExercise()
                    .setId(20L)
                    .setTitle("title")
                    .setDescription("description")
                    .setUserId(33L);
            when(mockExerciseRepository.findById(20L)).thenReturn(Optional.of(foundExercise));

            var result = sut.findById(20L);

            var expectedExercise = exerciseMapper.entityToDomain(foundExercise);
            assertThat(result).isEqualTo(expectedExercise);
        }
    }

    @Nested
    class FindAllExercises {
        @Test
        void should_call_exercise_repository_to_get_all_exercises() {
            sut.findAll();

            verify(mockExerciseRepository, times(1)).findAll();
        }

        @Test
        void when_find_all_exercises_by_exercise_repository_should_return_all_set_exercise() {
            var jpaExercise22 = new JpaExercise()
                    .setId(22L)
                    .setTitle("title")
                    .setDescription("description")
                    .setUserId(3L);
            var jpaExercise77 = new JpaExercise()
                    .setId(33L)
                    .setTitle("title44")
                    .setDescription("description55")
                    .setUserId(66L);
            var foundSetExercise = List.of(jpaExercise22, jpaExercise77);
            when(mockExerciseRepository.findAll()).thenReturn(foundSetExercise);

            var result = sut.findAll();

            var expectedSetExercise = foundSetExercise.stream()
                    .map(exerciseMapper::entityToDomain)
                    .collect(Collectors.toSet());

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expectedSetExercise);
        }
    }

    @Nested
    class SaveExercise {

        @Test
        void when_exercise_is_null_should_throw_exception() {
            assertThatThrownBy(() -> sut.save(null))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("%s : Exercise is null", IllegalArgumentException.class);
        }

        @Test
        void when_exercise_title_is_null_should_throw_IncorrectExerciseException() {
            var exercise = new Exercise()
                    .setId(2L)
                    .setUserId(3L)
                    .setTitle(null)
                    .setDescription("description");

            assertThatThrownBy(() -> sut.save(exercise))
                    .isExactlyInstanceOf(IncorrectExerciseException.class)
                    .hasMessage("%s : Exercise title property is null", IncorrectExerciseException.class);
        }

        @Test
        void when_exercise_description_is_null_should_throw_IncorrectExerciseException() {
            var exercise = new Exercise()
                    .setId(2L)
                    .setUserId(3L)
                    .setTitle("title")
                    .setDescription(null);

            assertThatThrownBy(() -> sut.save(exercise))
                    .isExactlyInstanceOf(IncorrectExerciseException.class)
                    .hasMessage("%s : Exercise description property is null", IncorrectExerciseException.class);
        }

        @Test
        void when_exercise_save_should_return_appropriate_new_exercise() throws IncorrectExerciseException {
            var exercise = new Exercise()
                    .setId(2L)
                    .setUserId(3L)
                    .setTitle("title")
                    .setDescription("description");
            var jpaExercise = exerciseMapper.domainToEntity(exercise);
            when(mockExerciseRepository.save(jpaExercise)).thenReturn(jpaExercise);

            var result = sut.save(exercise);

            assertThat(result).isEqualTo(exercise);
        }
    }

    @Nested
    class DeleteExerciseTest {

        private final long exerciseId = 24L;

        @Test
        void when_exercise_with_given_id_not_exists_should_check_if_exercise_with_given_id_exists() {
            when(mockExerciseRepository.existsById(exerciseId)).thenReturn(false);

            assertThatThrownBy(() -> sut.deleteById(exerciseId))
                    .isExactlyInstanceOf(NotFoundException.class)
                    .hasMessage("%s : Exercise with id '%d' not found", CommonExceptionState.NOT_FOUND, exerciseId);
        }

        @Test
        void should_delete_all_cases_of_exercise_by_exerciseId() throws NotFoundException {
            when(mockExerciseRepository.existsById(exerciseId)).thenReturn(true);

            sut.deleteById(exerciseId);

            verify(mockExerciseCaseDao, times(1)).deleteAllByExerciseId(exerciseId);
        }

        @Test
        void should_delete_exercise_by_id() throws NotFoundException {
            when(mockExerciseRepository.existsById(exerciseId)).thenReturn(true);
            doNothing().when(mockExerciseCaseDao).deleteAllByExerciseId(exerciseId);

            sut.deleteById(exerciseId);

            verify(mockExerciseRepository, times(1)).deleteById(exerciseId);
        }
    }
}