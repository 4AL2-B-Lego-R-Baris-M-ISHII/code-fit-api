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
}