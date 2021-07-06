package fr.esgi.pa.server.code.infrastructure.quality.action.by_language;

import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.cyclomatic_complexity.GetCyclomaticComplexityByLanguage;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_code.GetNbLinesCodeByLanguage;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_comment.GetNbLinesCommentByLanguage;
import fr.esgi.pa.server.code.infrastructure.quality.factory.ParserAndTreeInfoFactory;
import fr.esgi.pa.server.code.infrastructure.quality.gen.c.CParser;
import fr.esgi.pa.server.language.core.LanguageName;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.stereotype.Component;

import java.util.*;

@FunctionalInterface
interface TriFunction<T1, T2, T3, R> {
    R apply(T1 t1, T2 t2, T3 t3);
}

@Component
public class ActionsByC implements ActionsByLanguage {
    private final GetNbLinesCodeByLanguage getNbLinesCodeByLanguage;
    private final GetNbLinesCommentByLanguage getNbLinesCommentByLanguage;
    private final GetCyclomaticComplexityByLanguage getCyclomaticComplexityByLanguage;
    private final Map<String, Boolean> mapNodeCorrespondCycloComplex;
    private final ParserAndTreeInfoFactory parserAndTreeInfoFactory;
    private final Map<Class<?>, TriFunction<ParseTree, Parser, Set<String>, Boolean>> mapRedundantCode;

    public ActionsByC(
            GetNbLinesCodeByLanguage getNbLinesCodeByLanguage,
            GetNbLinesCommentByLanguage getNbLinesCommentByLanguage,
            GetCyclomaticComplexityByLanguage getCyclomaticComplexityByLanguage,
            ParserAndTreeInfoFactory parserAndTreeInfoFactory) {
        this.getNbLinesCodeByLanguage = getNbLinesCodeByLanguage;
        this.getNbLinesCommentByLanguage = getNbLinesCommentByLanguage;
        this.getCyclomaticComplexityByLanguage = getCyclomaticComplexityByLanguage;
        this.parserAndTreeInfoFactory = parserAndTreeInfoFactory;

        var listNodeTextCorrespondCycloComplex = List.of(
                "if", "case", "for", "while"
        );
        mapNodeCorrespondCycloComplex = new Hashtable<>();
        listNodeTextCorrespondCycloComplex.forEach(nodeText -> mapNodeCorrespondCycloComplex.put(nodeText, true));

        mapRedundantCode = new Hashtable<>();
        mapRedundantCode.put(CParser.SelectionStatementContext.class, this::checkIfCurTreeRedundantElseAddOnTreeString);
        mapRedundantCode.put(CParser.IterationStatementContext.class, this::checkIfCurTreeRedundantElseAddOnTreeString);
        mapRedundantCode.put(CParser.CompoundStatementContext.class, this::checkIfCurTreeRedundantElseAddOnTreeString);
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
        var parserAndTree = parserAndTreeInfoFactory.getParserAndTreeInfo(LanguageName.C11, content);
        var treeString = new HashSet<String>();
        return treeHasRedundantCode(parserAndTree.getTree(), parserAndTree.getParser(), treeString);
    }

    private Boolean treeHasRedundantCode(ParseTree tree, Parser parser, Set<String> treeString) {
        var result = false;
        if (tree == null || tree.getChildCount() == 0) {
            return false;
        }
        if (isConcernedTreeClassToAddAndContainTreeString(tree, parser, treeString)) {
            return true;
        }

        for (int i = 0; i < tree.getChildCount() && !result; i++) {
            result = treeHasRedundantCode(tree.getChild(i), parser, treeString);
        }

        return result;
    }

    private boolean isConcernedTreeClassToAddAndContainTreeString(ParseTree tree, Parser parser, Set<String> treeString) {
        return mapRedundantCode.containsKey(tree.getClass())
                && mapRedundantCode.get(tree.getClass()).apply(tree, parser, treeString);
    }

    private boolean checkIfCurTreeRedundantElseAddOnTreeString(ParseTree tree, Parser parser, Set<String> treeString) {
        var currentTreeString = tree.toStringTree(parser);
        if (treeString.contains(currentTreeString)) {
            return true;
        }
        treeString.add(currentTreeString);
        return false;
    }

}
