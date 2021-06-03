package fr.esgi.pa.server.unit.exercise.infrastructure.dataprovider;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.dao.JpaExerciseDao;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.mapper.ExerciseMapper;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository.ExerciseRepository;
import fr.esgi.pa.server.user.core.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaExerciseDaoTest {

    private final String exerciseTitle = "exercise title";
    private final String description = "description";
    @Mock
    private UserDao mockUserDao;

    @Mock
    private ExerciseRepository mockExerciseRepository;

    private final ExerciseMapper exerciseMapper = new ExerciseMapper();

    private JpaExerciseDao sut;


    @BeforeEach
    void setup() {
        sut = new JpaExerciseDao(mockUserDao, mockExerciseRepository, exerciseMapper);
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
}