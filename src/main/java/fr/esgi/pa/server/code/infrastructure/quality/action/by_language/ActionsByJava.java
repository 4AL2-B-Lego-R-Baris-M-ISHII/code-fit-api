package fr.esgi.pa.server.code.infrastructure.quality.action.by_language;

import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.cyclomatic_complexity.GetCyclomaticComplexityByLanguage;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_code.GetNbLinesCodeByLanguage;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_comment.GetNbLinesCommentByLanguage;
import fr.esgi.pa.server.code.infrastructure.quality.factory.ParserAndTreeInfoFactory;
import fr.esgi.pa.server.code.infrastructure.quality.gen.java8.Java8Parser;
import fr.esgi.pa.server.language.core.LanguageName;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ActionsByJava implements ActionsByLanguage {
    private final GetNbLinesCodeByLanguage getNbLinesCodeByLanguage;
    private final GetNbLinesCommentByLanguage getNbLinesCommentByLanguage;
    private final GetCyclomaticComplexityByLanguage getCyclomaticComplexityByLanguage;
    private final Map<String, Boolean> mapNodeCorrespondCycloComplex;
    private final ParserAndTreeInfoFactory parserAndTreeInfoFactory;

    public ActionsByJava(
            GetNbLinesCodeByLanguage getNbLinesCodeByLanguage,
            GetNbLinesCommentByLanguage getNbLinesCommentByLanguage,
            GetCyclomaticComplexityByLanguage getCyclomaticComplexityByLanguage,
            ParserAndTreeInfoFactory parserAndTreeInfoFactory) {
        this.getNbLinesCodeByLanguage = getNbLinesCodeByLanguage;
        this.getNbLinesCommentByLanguage = getNbLinesCommentByLanguage;
        this.getCyclomaticComplexityByLanguage = getCyclomaticComplexityByLanguage;
        this.parserAndTreeInfoFactory = parserAndTreeInfoFactory;

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
        return getCyclomaticComplexityByLanguage.execute(
                LanguageName.JAVA8,
                mapNodeCorrespondCycloComplex,
                content
        );
    }

    @Override
    public Boolean hasDuplicateCode(String content) {
        var parserAndTree = parserAndTreeInfoFactory.getParserAndTreeInfo(LanguageName.JAVA8, content);
        var treeString = new HashSet<String>();
        return treeHasRedundantCode(parserAndTree.getTree(), parserAndTree.getParser(), treeString);
    }

    private Boolean treeHasRedundantCode(ParseTree tree, Parser parser, Set<String> treeString) {
        if (tree.getClass().equals(Java8Parser.IfThenStatementContext.class) ||
                tree.getClass().equals(Java8Parser.IfThenElseStatementContext.class) ||
                tree.getClass().equals(Java8Parser.ForStatementContext.class)
        ) {
            var currentTreeString = tree.toStringTree(parser);
            if (treeString.contains(currentTreeString)) {
                return true;
            }
            treeString.add(currentTreeString);
        }
        var result = false;
        for (int i = 0; i < tree.getChildCount() && !result; i++) {
            result = treeHasRedundantCode(tree.getChild(i), parser, treeString);
        }
        return result;
    }
}
