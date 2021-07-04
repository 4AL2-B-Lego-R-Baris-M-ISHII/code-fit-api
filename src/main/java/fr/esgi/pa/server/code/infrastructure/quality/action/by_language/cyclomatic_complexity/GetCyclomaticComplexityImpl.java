package fr.esgi.pa.server.code.infrastructure.quality.action.by_language.cyclomatic_complexity;


import fr.esgi.pa.server.code.infrastructure.quality.gen.c.CLexer;
import fr.esgi.pa.server.code.infrastructure.quality.gen.c.CParser;
import fr.esgi.pa.server.code.infrastructure.quality.gen.java8.Java8Lexer;
import fr.esgi.pa.server.code.infrastructure.quality.gen.java8.Java8Parser;
import fr.esgi.pa.server.language.core.LanguageName;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GetCyclomaticComplexityImpl implements GetCyclomaticComplexity {
    @Override
    public Long execute(LanguageName languageName, Map<String, Boolean> mapNode, String content) {
        ParseTree tree = getParseTreeDependToLanguageName(languageName, content);
        Long result = 0L;

        return searchNodeCorrespondCyclomaticComplexity(tree, result, mapNode);
    }

    private ParseTree getParseTreeDependToLanguageName(LanguageName languageName, String content) {
        if (languageName.equals(LanguageName.JAVA8)) {
            Java8Lexer lexer = new Java8Lexer(CharStreams.fromString(content));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Java8Parser parser = new Java8Parser(tokens);
            return parser.compilationUnit();
        }
        CLexer lexer = new CLexer(CharStreams.fromString(content));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CParser parser = new CParser(tokens);
        return parser.compilationUnit();
    }

    private Long searchNodeCorrespondCyclomaticComplexity(ParseTree currentTree, Long result, Map<String, Boolean> mapNodeCorrespondCycloComplex) {
        for (int i = 0; i < currentTree.getChildCount(); i++) {
            result = searchNodeCorrespondCyclomaticComplexity(currentTree.getChild(i), result, mapNodeCorrespondCycloComplex);
        }
        var textNode = currentTree.getText();
        if (!textNode.trim().isBlank() && mapNodeCorrespondCycloComplex.containsKey(textNode)) {
            result++;
        }
        return result;
    }
}
