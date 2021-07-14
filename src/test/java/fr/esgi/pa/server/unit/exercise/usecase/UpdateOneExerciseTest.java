package fr.esgi.pa.server.unit.exercise.usecase;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.dao.ExerciseDao;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise.core.exception.IncorrectExerciseException;
import fr.esgi.pa.server.exercise.usecase.UpdateOneExercise;
import fr.esgi.pa.server.user.core.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateOneExerciseTest {
    private final long userId = 123L;
    private final long exerciseId = 456L;
    private UpdateOneExercise sut;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private ExerciseDao mockExerciseDao;

    @BeforeEach
    void setup() {
        sut = new UpdateOneExercise(mockUserDao, mockExerciseDao);
    }

    @Test
    void when_user_with_userId_not_exists_should_throw_NotFoundException() {
        when(mockUserDao.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> sut.execute(userId, exerciseId, "title", "description"))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessage("%s : User with id '%d' not found", NotFoundException.class, userId);
    }

    @Test
    void when_found_exercise_and_param_title_and_description_not_null_should_update_with_new_params() throws NotFoundException, IncorrectExerciseException, ForbiddenException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setTitle("old title")
                .setDescription("old description")
                .setUserId(userId);
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);

        sut.execute(userId, exerciseId, "new title", "new description");

        var exerciseToUpdate = new Exercise()
                .setId(exerciseId)
                .setTitle("new title")
                .setDescription("new description")
                .setUserId(userId);
        verify(mockExerciseDao, times(1)).save(exerciseToUpdate);
    }

    @Test
    void when_found_exercise_and_param_title_null_should_not_update_title() throws NotFoundException, IncorrectExerciseException, ForbiddenException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setTitle("old title")
                .setDescription("old description")
                .setUserId(userId);
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);

        sut.execute(userId, exerciseId, null, "new description");

        var exerciseToUpdate = new Exercise()
                .setId(exerciseId)
                .setTitle("old title")
                .setDescription("new description")
                .setUserId(userId);
        verify(mockExerciseDao, times(1)).save(exerciseToUpdate);
    }

    @Test
    void when_found_exercise_and_param_description_null_should_not_update_description() throws NotFoundException, IncorrectExerciseException, ForbiddenException {
        when(mockUserDao.existsById(userId)).thenReturn(true);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setTitle("old title")
                .setDescription("old description")
                .setUserId(userId);
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);

        sut.execute(userId, exerciseId, "new title", null);

        var exerciseToUpdate = new Exercise()
                .setId(exerciseId)
                .setTitle("new title")
                .setDescription("old description")
                .setUserId(userId);
        verify(mockExerciseDao, times(1)).save(exerciseToUpdate);
    }

    @Test
    void when_found_exercise_userId_different_to_userId_param_should_throw_ForbiddenSaveExerciseException() throws NotFoundException {
        var otherUserId = 456L;
        when(mockUserDao.existsById(otherUserId)).thenReturn(true);
        var foundExercise = new Exercise()
                .setId(exerciseId)
                .setTitle("old title")
                .setDescription("old description")
                .setUserId(userId);
        when(mockExerciseDao.findById(exerciseId)).thenReturn(foundExercise);


        assertThat(otherUserId).isNotEqualTo(userId);

        assertThatThrownBy(() -> sut.execute(otherUserId, exerciseId, "new title", null))
                .isExactlyInstanceOf(ForbiddenException.class)
                .hasMessage("%s : Exercise can be update by only the creator", ForbiddenException.class);
    }
}