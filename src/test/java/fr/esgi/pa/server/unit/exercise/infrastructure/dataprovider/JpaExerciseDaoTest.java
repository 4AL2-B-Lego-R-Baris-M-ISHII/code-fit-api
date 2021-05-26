package fr.esgi.pa.server.unit.exercise.infrastructure.dataprovider;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.JpaExerciseDao;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageName;
import fr.esgi.pa.server.user.core.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaExerciseDaoTest {

    @Mock
    private UserDao mockUserDao;

    private JpaExerciseDao sut;

    private Language language;

    @BeforeEach
    void setup() {
        sut = new JpaExerciseDao(mockUserDao);
        language = new Language().setId(1L).setLanguageName(LanguageName.JAVA).setFileExtension("java");
    }

    @Nested
    class SaveExercise {
        @Test
        void should_check_if_user_exist() throws NotFoundException {
            sut.saveExercise("exercise title", "description", language, 21L);
            Mockito.verify(mockUserDao).findById(21L);
        }
    }
}