package fr.esgi.pa.server.code.infrastructure.quality.action.by_language;

import fr.esgi.pa.server.code.infrastructure.gen.c.CLexer;
import fr.esgi.pa.server.code.infrastructure.gen.c.CParser;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_code.GetNbLinesCodeByLanguage;
import fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_comment.GetNbLinesCommentByLanguage;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ActionsByC implements ActionsByLanguage {
    private final GetNbLinesCodeByLanguage getNbLinesCodeByLanguage;
    private final GetNbLinesCommentByLanguage getNbLinesCommentByLanguage;

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
        CLexer lexer = new CLexer(CharStreams.fromString(content));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CParser parser = new CParser(tokens);
        ParseTree tree = parser.compilationUnit();
        Long result = 0L;
        var listNodeTextCorrespondCycloComplex = List.of(
                "if", "case", "for", "while"
        );
        Map<String, Boolean> mapNodeCorrespondCycloComplex = new Hashtable<>();
        listNodeTextCorrespondCycloComplex.forEach(nodeText -> {
            mapNodeCorrespondCycloComplex.put(nodeText, true);
        });

        return searchNodeCorrespondCyclomaticComplexity(tree, parser, result, mapNodeCorrespondCycloComplex);
    }

    private Long searchNodeCorrespondCyclomaticComplexity(ParseTree currentTree, Parser parser, Long result, Map<String, Boolean> mapNodeCorrespondCycloComplex) {
//        System.out.printf("toStringtree : %s%n", currentTree.toStringTree(parser));
//        System.out.printf("getText : %s%n", currentTree.getText());
//        System.out.printf("getClass : %s%n", currentTree.getClass());
//        System.out.println();
        for (int i = 0; i < currentTree.getChildCount(); i++) {
            result = searchNodeCorrespondCyclomaticComplexity(currentTree.getChild(i), parser, result, mapNodeCorrespondCycloComplex);
        }
        var textNode = currentTree.getText();
        if (!textNode.trim().isBlank() && mapNodeCorrespondCycloComplex.containsKey(textNode)) {
            result++;
        }
        return result;
    }
}
