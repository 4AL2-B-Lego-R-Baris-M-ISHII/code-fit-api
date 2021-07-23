package fr.esgi.pa.server.exercise_case.usecase;

import fr.esgi.pa.server.code.core.compiler.CodeResult;
import fr.esgi.pa.server.code.core.compiler.CodeState;
import fr.esgi.pa.server.code.core.compiler.Compiler;
import fr.esgi.pa.server.code.core.compiler.CompilerRepository;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.dto.DtoVerifyExerciseCase;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseCase;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;

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
        var result = compileExerciseCaseAndTests(exerciseCase, foundLanguage, setTest, compiler);

        exerciseCase.setIsValid(result.getIsExerciseCaseValid());
        exerciseCaseDao.saveOne(exerciseCase);

        return result;
    }

    private DtoVerifyExerciseCase compileExerciseCaseAndTests(ExerciseCase exerciseCase, Language foundLanguage, Set<ExerciseTest> setTest, Compiler compiler) {
        var result = new DtoVerifyExerciseCase();
        var codeList = new ArrayList<CodeResult>();
        result.setIsExerciseCaseValid(true);
        for (ExerciseTest exerciseTest : setTest) {
            var contentToCompile = exerciseCase.getSolution() + System.getProperty("line.separator") + exerciseTest.getContent();
            var resultCode = compiler.compile(contentToCompile, foundLanguage);
            if (resultCode.getCodeState() != CodeState.SUCCESS) {
                result.setIsExerciseCaseValid(false);
            }
            resultCode.setTestId(exerciseTest.getId());
            codeList.add(codeList.size(), resultCode);
        }
        result.setCodeResultList(codeList);
        return result;
    }
}
