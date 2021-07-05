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


@Component
public class ActionsByC implements ActionsByLanguage {
    private final GetNbLinesCodeByLanguage getNbLinesCodeByLanguage;
    private final GetNbLinesCommentByLanguage getNbLinesCommentByLanguage;
    private final GetCyclomaticComplexityByLanguage getCyclomaticComplexityByLanguage;
    private final Map<String, Boolean> mapNodeCorrespondCycloComplex;
    private final ParserAndTreeInfoFactory parserAndTreeInfoFactory;

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
        if (tree == null || tree.getChildCount() == 0) {
            return false;
        }
        if (tree.getClass().equals(CParser.BlockItemContext.class)) {
            tree = tree.getChild(0).getChild(0);

            if (tree.getClass().equals(CParser.SelectionStatementContext.class)
                    || tree.getClass().equals(CParser.IterationStatementContext.class)
            ) {
                var currentTreeString = tree.toStringTree(parser);
                if (treeString.contains(currentTreeString)) {
                    return true;
                }
                treeString.add(currentTreeString);
            }
        } else if (tree.getClass().equals(CParser.FunctionDefinitionContext.class)) {
            // TODO : manage when function definition contain same statements should save in another set
            tree = ((CParser.FunctionDefinitionContext) tree).getChild(CParser.CompoundStatementContext.class, 0);
            var currentTreeString = tree.toStringTree(parser);
            if (treeString.contains(currentTreeString)) {
                return true;
            }
            treeString.add(currentTreeString);
        }


        var result = false;
        for (int i = 0; i < tree.getChildCount(); i++) {
            if (treeHasRedundantCode(tree.getChild(i), parser, treeString)) {
                result = true;
            }
        }

        return result;
    }


}
