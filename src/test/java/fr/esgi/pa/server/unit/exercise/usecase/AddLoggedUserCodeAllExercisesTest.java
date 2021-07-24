package fr.esgi.pa.server.unit.exercise.usecase;

import fr.esgi.pa.server.exercise.usecase.AddLoggedUserCodeAllExercises;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddLoggedUserCodeAllExercisesTest {
    private AddLoggedUserCodeAllExercises sut;

    @BeforeEach
    void setup() {
        sut = new AddLoggedUserCodeAllExercises();
    }

    @Test
    void when_setDtoExercise_contain_one_dto_exercise_with_one_exercise_case_should_call() {

    }
}