package fr.esgi.pa.server.unit.exercise.usecse;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.usecase.FindOneExercise;
import fr.esgi.pa.server.user.core.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindOneExerciseTest {

    private final long userId = 2L;
    private final long exerciseId = 11L;
    private FindOneExercise sut;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private ExerciseDao mockExerciseDao;

    @BeforeEach
    void setup() {
        sut = new FindOneExercise(mockUserDao, mockExerciseDao);
    }

    @Test
    void when_user_with_given_userId_not_exists_should_throw_NotFoundException() {
        when(mockUserDao.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> sut.execute(exerciseId, userId))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage(String.format("%s : User with userId '%d' not found", sut.getClass(), userId));
    }

    @Test
    void should_call_exerciseDao_findById() throws NotFoundException {
        when(mockUserDao.existsById(userId)).thenReturn(true);

        sut.execute(exerciseId, userId);

        verify(mockExerciseDao, times(1)).findById(exerciseId);
    }
}