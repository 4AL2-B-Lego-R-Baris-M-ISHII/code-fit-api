package fr.esgi.pa.server.code.infrastructure.quality.action.by_language.cyclomatic_complexity;


import fr.esgi.pa.server.code.infrastructure.quality.factory.ParserAndTreeInfoFactory;
import fr.esgi.pa.server.code.infrastructure.quality.gen.ParserAndTreeInfo;
import fr.esgi.pa.server.language.core.LanguageName;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GetCyclomaticComplexityImpl implements GetCyclomaticComplexity {
    private final ParserAndTreeInfoFactory parserAndTreeInfoFactory;
    @Override
    public Long execute(LanguageName languageName, Map<String, Boolean> mapNode, String content) {
        ParserAndTreeInfo info = parserAndTreeInfoFactory.getParserAndTreeInfo(languageName, content);
        Long result = 0L;

        return searchNodeCorrespondCyclomaticComplexity(info.getTree(), result, mapNode);
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
