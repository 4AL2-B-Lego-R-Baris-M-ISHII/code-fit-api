package fr.esgi.pa.server.exercise_case.usecase;

import fr.esgi.pa.server.code.core.Code;
import fr.esgi.pa.server.code.core.CodeState;
import fr.esgi.pa.server.code.core.CompilerRepository;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.dto.DtoVerifyExerciseCase;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;
import fr.esgi.pa.server.language.core.LanguageDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class VerifyExerciseCase {
    private final ExerciseCaseDao exerciseCaseDao;
    private final ExerciseTestDao exerciseTestDao;
    private final LanguageDao languageDao;
    private final CompilerRepository compilerRepository;

    public DtoVerifyExerciseCase execute(Long exerciseCaseId) throws NotFoundException {
        var exerciseCase = exerciseCaseDao.findById(exerciseCaseId);
        var foundLanguage = languageDao.findById(exerciseCase.getLanguageId());
        var setTest = exerciseTestDao.findAllByExerciseCaseId(exerciseCaseId);

        var compiler = compilerRepository.findByLanguage(foundLanguage);
        var result = new DtoVerifyExerciseCase();
        var codeList = new ArrayList<Code>();
        var isValid = true;
        for (ExerciseTest exerciseTest : setTest) {
            var contentToCompile = exerciseCase.getSolution() + System.getProperty("line.separator") + exerciseTest.getContent();
            var resultCode = compiler.compile(contentToCompile, foundLanguage);
            if (resultCode.getCodeState() != CodeState.SUCCESS) {
                isValid = false;
            }
            codeList.add(resultCode);
        }
        result.setIsExerciseCaseValid(isValid);
        result.setCodeList(codeList);
        return result;
    }
}
