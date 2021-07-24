package fr.esgi.pa.server.unit.exercise.usecase;

import fr.esgi.pa.server.exercise.core.dto.DtoExercise;
import fr.esgi.pa.server.exercise.usecase.FilterExercisesByCreator;
import fr.esgi.pa.server.user.core.dto.DtoUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class FilterExercisesByCreatorTest {
    private FilterExercisesByCreator sut;

    @BeforeEach
    void setup() {
        sut = new FilterExercisesByCreator();
    }

    @Test
    void should_given_exercise_not_correspond_to_user_with_creator_id_should_return_empty_set_exercise() {
        var setDtoExercise = Set.of(new DtoExercise().setId(9L).setUser(new DtoUser().setId(7L)));

        var result = sut.execute(setDtoExercise, 6L);

        assertThat(result).isEqualTo(Set.of());
    }

    @Test
    void when_set_exercise_contain_one_exercise_with_user_creator_id_should_return_set_exercise_with_concerned_one() {
        var setDtoExercise = Set.of(new DtoExercise().setId(9L).setUser(new DtoUser().setId(6L)));

        var result = sut.execute(setDtoExercise, 6L);

        assertThat(result).isEqualTo(setDtoExercise);
    }

    @Test
    void when_set_exercise_contain_2_exercise_with_only_one_user_creator_id_should_return_set_exercise_with_concerned_one() {
        var setDtoExercise = Set.of(
                new DtoExercise().setId(9L).setUser(new DtoUser().setId(6L)),
                new DtoExercise().setId(10L).setUser(new DtoUser().setId(7L))
        );

        var result = sut.execute(setDtoExercise, 6L);

        var expectedSetDtoExercise = Set.of(
                new DtoExercise().setId(9L).setUser(new DtoUser().setId(6L))
        );
        assertThat(result).isEqualTo(expectedSetDtoExercise);
    }
}