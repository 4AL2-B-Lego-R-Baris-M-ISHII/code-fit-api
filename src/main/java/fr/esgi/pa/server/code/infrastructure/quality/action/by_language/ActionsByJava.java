package fr.esgi.pa.server.code.infrastructure.quality.action.by_language;

import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.cyclomatic_complexity.GetCyclomaticComplexity;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_code.GetNbLinesCodeByLanguage;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_comment.GetNbLinesCommentByLanguage;
import fr.esgi.pa.server.language.core.LanguageName;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Component
public class ActionsByJava implements ActionsByLanguage {
    private final GetNbLinesCodeByLanguage getNbLinesCodeByLanguage;
    private final GetNbLinesCommentByLanguage getNbLinesCommentByLanguage;
    private final GetCyclomaticComplexity getCyclomaticComplexity;
    private final Map<String, Boolean> mapNodeCorrespondCycloComplex;

    public ActionsByJava(
            GetNbLinesCodeByLanguage getNbLinesCodeByLanguage,
            GetNbLinesCommentByLanguage getNbLinesCommentByLanguage,
            GetCyclomaticComplexity getCyclomaticComplexity) {
        this.getNbLinesCodeByLanguage = getNbLinesCodeByLanguage;
        this.getNbLinesCommentByLanguage = getNbLinesCommentByLanguage;
        this.getCyclomaticComplexity = getCyclomaticComplexity;

        var listNodeTextCorrespondCycloComplex = List.of(
                "if", "case", "for", "while", "forEach"
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
        return getCyclomaticComplexity.execute(
                LanguageName.JAVA8,
                mapNodeCorrespondCycloComplex,
                content
        );
    }
}
