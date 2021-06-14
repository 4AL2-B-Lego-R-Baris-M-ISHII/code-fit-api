package fr.esgi.pa.server.code.usecase;

import fr.esgi.pa.server.code.core.compiler.CodeResult;
import fr.esgi.pa.server.code.core.compiler.CodeState;
import fr.esgi.pa.server.code.core.compiler.Compiler;
import fr.esgi.pa.server.code.core.compiler.CompilerRepository;
import fr.esgi.pa.server.code.core.dao.CodeDao;
import fr.esgi.pa.server.code.core.dto.DtoCode;
import fr.esgi.pa.server.code.core.entity.Code;
import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.exception.ForbiddenException;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseCaseDao;
import fr.esgi.pa.server.exercise_case.core.dao.ExerciseTestDao;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;
import fr.esgi.pa.server.language.core.Language;
import fr.esgi.pa.server.language.core.LanguageDao;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompileCodeById {
    private final CodeDao codeDao;
    private final ExerciseCaseDao exerciseCaseDao;
    private final LanguageDao languageDao;
    private final CompilerRepository compilerRepository;
    private final ExerciseTestDao exerciseTestDao;

    public DtoCode execute(Long codeId) throws NotFoundException, ForbiddenException {
        var foundCode = codeDao.findById(codeId);
        var foundLanguage = getLanguage(foundCode);
        var currentCompiler = compilerRepository.findByLanguage(foundLanguage);
        var setTest = exerciseTestDao.findAllByExerciseCaseId(foundCode.getExerciseCaseId());
        var listCodeResult = compileAndGetListCodeResult(
                foundCode,
                foundLanguage,
                currentCompiler,
                setTest
        );
        var isResolved = isCodeResolvedExerciseCase(listCodeResult);
        foundCode.setIsResolved(isResolved);

        codeDao.save(foundCode);

        return buildDtoCode(foundCode, listCodeResult, isResolved);
    }

    private Language getLanguage(@NotNull Code foundCode) throws NotFoundException {
        var foundExerciseCase = exerciseCaseDao.findById(foundCode.getExerciseCaseId());
        return languageDao.findById(foundExerciseCase.getLanguageId());
    }

    private List<CodeResult> compileAndGetListCodeResult(
            Code foundCode,
            Language foundLanguage,
            Compiler currentCompiler,
            Set<ExerciseTest> setTest
    ) {
        var listCodeResult = new ArrayList<CodeResult>();
        for (ExerciseTest exerciseTest : setTest) {
            var contentToCompile = getContentToCompile(foundCode, exerciseTest);
            var result = currentCompiler.compile(contentToCompile, foundLanguage);
            result.setTestId(exerciseTest.getId());
            listCodeResult.add(result);
        }
        return listCodeResult.stream()
                .sorted(Comparator.comparing(CodeResult::getTestId))
                .collect(Collectors.toList());
    }

    private String getContentToCompile(Code foundCode, ExerciseTest exerciseTest) {
        return foundCode.getContent()
                + System.getProperty("line.separator")
                + exerciseTest.getContent();
    }

    private boolean isCodeResolvedExerciseCase(List<CodeResult> listCodeResult) {
        return listCodeResult.stream()
                .allMatch(codeResult -> codeResult.getCodeState() == CodeState.SUCCESS);
    }

    private DtoCode buildDtoCode(Code foundCode, List<CodeResult> listCodeResult, boolean isResolved) {
        return new DtoCode().setCodeId(foundCode.getId())
                .setListCodeResult(listCodeResult)
                .setIsResolved(isResolved);
    }
}
