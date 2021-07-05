package fr.esgi.pa.server.code.infrastructure.quality.action.by_language;

import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.cyclomatic_complexity.GetCyclomaticComplexityByLanguage;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_code.GetNbLinesCodeByLanguage;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_comment.GetNbLinesCommentByLanguage;
import fr.esgi.pa.server.language.core.LanguageName;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Component
public class ActionsByC implements ActionsByLanguage {
    private final GetNbLinesCodeByLanguage getNbLinesCodeByLanguage;
    private final GetNbLinesCommentByLanguage getNbLinesCommentByLanguage;
    private final GetCyclomaticComplexityByLanguage getCyclomaticComplexityByLanguage;
    private final Map<String, Boolean> mapNodeCorrespondCycloComplex;

    public ActionsByC(
            GetNbLinesCodeByLanguage getNbLinesCodeByLanguage,
            GetNbLinesCommentByLanguage getNbLinesCommentByLanguage,
            GetCyclomaticComplexityByLanguage getCyclomaticComplexityByLanguage) {
        this.getNbLinesCodeByLanguage = getNbLinesCodeByLanguage;
        this.getNbLinesCommentByLanguage = getNbLinesCommentByLanguage;
        this.getCyclomaticComplexityByLanguage = getCyclomaticComplexityByLanguage;

        var listNodeTextCorrespondCycloComplex = List.of(
                "if", "case", "for", "while"
        );
        mapNodeCorrespondCycloComplex = new Hashtable<>();
        listNodeTextCorrespondCycloComplex.forEach(nodeText -> mapNodeCorrespondCycloComplex.put(nodeText, true));
    }

    @Override
    public Long getNbLinesCode(String content) {
        return getNbLinesCodeByLanguage.execute(content);
    }

    @Override
    public Long getNbLinesComment(String content) {
        return getNbLinesCommentByLanguage.execute(content);
    }

    @Override
    public Long getCyclomaticComplexity(String content) {
        return getCyclomaticComplexityByLanguage.execute(
                LanguageName.C11,
                mapNodeCorrespondCycloComplex,
                content
        );
    }

    @Override
    public Boolean hasRedundantCode(String content) {
        return false;
    }
}
