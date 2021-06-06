package fr.esgi.pa.server.unit.exercise.usecase;

import fr.esgi.pa.server.common.core.exception.CommonExceptionState;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.usecase.DeleteOneExercise;
import fr.esgi.pa.server.user.core.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteOneExerciseTest {

    private final long userId = 123L;
    private final long exerciseId = 456L;
    private DeleteOneExercise sut;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private ExerciseDao mockExerciseDao;

    @BeforeEach
    void setup() {
        sut = new DeleteOneExercise(mockUserDao, mockExerciseDao);
    }

    @Test
    void when_user_not_exists_should_throw_NotFoundException() {
        when(mockUserDao.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> sut.execute(userId, exerciseId))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage("%s : User with id '%d' not found", CommonExceptionState.NOT_FOUND, userId);
    }

    @Test
    void when_exercise_not_found_should_throw_NotFoundException() throws NotFoundException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        when(mockExerciseDao.existsById(exerciseId)).thenReturn(false);

        assertThatThrownBy(() -> sut.execute(userId, exerciseId))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage("%s : Exercise with id '%d' not found", CommonExceptionState.NOT_FOUND, exerciseId);
    }

    @Test
    void when_exercise_exists_should_delete_by_id() throws NotFoundException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        when(mockExerciseDao.existsById(exerciseId)).thenReturn(true);

        sut.execute(userId, exerciseId);

        verify(mockExerciseDao, times(1)).deleteById(exerciseId);
    }
}