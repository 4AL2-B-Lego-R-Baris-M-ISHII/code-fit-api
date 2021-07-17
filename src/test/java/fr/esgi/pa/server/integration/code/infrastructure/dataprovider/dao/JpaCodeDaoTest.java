package fr.esgi.pa.server.integration.code.infrastructure.dataprovider.dao;

import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.common.core.utils.date.DateHelper;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import fr.esgi.pa.server.role.core.RoleDao;
import fr.esgi.pa.server.role.core.RoleName;
import fr.esgi.pa.server.user.core.dao.UserDao;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class JpaCodeDaoTest {
    @Autowired
    private CodeDao codeDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ExerciseCaseDao exerciseCaseDao;

    @MockBean
    private DateHelper mockDateHelper;

    @Nested
    class SaveTest {
        @Test
        void when_code_resolved_should_set_resolved_date_to_now_date() throws NotFoundException, ForbiddenException {
            var userId = userDao.createUser(
                    "toto",
                    "toto@gmail.com",
                    "toto",
                    Set.of(roleDao.findByRoleName(RoleName.ROLE_USER).get())
            );
            var exerciseCaseToSave = new ExerciseCase()
                    .setIsValid(true)
                    .setExerciseId(7L)
                    .setSolution("Solution")
                    .setStartContent("start content")
                    .setLanguageId(73L);
            var exerciseCase = exerciseCaseDao.saveOne(exerciseCaseToSave);
            var codeToSave = new Code()
                    .setContent("content")
                    .setIsResolved(true)
                    .setUserId(userId)
                    .setExerciseCaseId(exerciseCase.getId());
            codeDao.save(codeToSave);

            verify(mockDateHelper, times(1)).timestampNow();
        }

        @Test
        void when_get_date_of_code_resolved_should_save_code_with_resolved_date() throws NotFoundException, ForbiddenException {
            var userId = userDao.createUser(
                    "toto",
                    "toto@gmail.com",
                    "toto",
                    Set.of(roleDao.findByRoleName(RoleName.ROLE_USER).get())
            );
            var exerciseCaseToSave = new ExerciseCase()
                    .setIsValid(true)
                    .setExerciseId(7L)
                    .setSolution("Solution")
                    .setStartContent("start content")
                    .setLanguageId(73L);
            var exerciseCase = exerciseCaseDao.saveOne(exerciseCaseToSave);
            var codeToSave = new Code()
                    .setContent("content")
                    .setIsResolved(true)
                    .setUserId(userId)
                    .setExerciseCaseId(exerciseCase.getId());
            var timestampNow = new Timestamp(System.currentTimeMillis());
            when(mockDateHelper.timestampNow()).thenReturn(timestampNow);

            var result = codeDao.save(codeToSave);

            assertThat(result).isNotNull();
            assertThat(result.getResolvedDate()).isNotNull();
            assertThat(result.getResolvedDate()).isEqualTo(timestampNow);
        }

        @Test
        void when_code_is_not_resolved_should_not_set_resolved_date() throws NotFoundException, ForbiddenException {
            var userId = userDao.createUser(
                    "toto",
                    "toto@gmail.com",
                    "toto",
                    Set.of(roleDao.findByRoleName(RoleName.ROLE_USER).get())
            );
            var exerciseCaseToSave = new ExerciseCase()
                    .setIsValid(true)
                    .setExerciseId(7L)
                    .setSolution("Solution")
                    .setStartContent("start content")
                    .setLanguageId(73L);
            var exerciseCase = exerciseCaseDao.saveOne(exerciseCaseToSave);
            var codeToSave = new Code()
                    .setContent("content")
                    .setIsResolved(false)
                    .setUserId(userId)
                    .setExerciseCaseId(exerciseCase.getId());

            codeDao.save(codeToSave);

            verify(mockDateHelper, never()).timestampNow();
        }

        @Test
        void when_code_is_resolved_but_same_code_after_not_resolved_should_keep_first_resolved_date() throws ForbiddenException, NotFoundException {
            var userId = userDao.createUser(
                    "toto",
                    "toto@gmail.com",
                    "toto",
                    Set.of(roleDao.findByRoleName(RoleName.ROLE_USER).get())
            );

            var exerciseCaseToSave = new ExerciseCase()
                    .setIsValid(true)
                    .setExerciseId(7L)
                    .setSolution("Solution")
                    .setStartContent("start content")
                    .setLanguageId(73L);
            var exerciseCase = exerciseCaseDao.saveOne(exerciseCaseToSave);
            var codeToSave = new Code()
                    .setContent("content")
                    .setIsResolved(true)
                    .setUserId(userId)
                    .setExerciseCaseId(exerciseCase.getId());
            var timestampNow = new Timestamp(System.currentTimeMillis());
            when(mockDateHelper.timestampNow()).thenReturn(timestampNow);

            var savedCode = codeDao.save(codeToSave);

            savedCode.setIsResolved(false);

            var result = codeDao.save(savedCode);

            verify(mockDateHelper, times(1)).timestampNow();
            assertThat(result).isNotNull();
            assertThat(result.getResolvedDate()).isEqualTo(timestampNow);
        }
    }
}