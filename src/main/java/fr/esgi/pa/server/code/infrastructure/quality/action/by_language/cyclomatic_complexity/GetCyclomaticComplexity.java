package fr.esgi.pa.server.code.infrastructure.quality.action.by_language.cyclomatic_complexity;

import fr.esgi.pa.server.language.core.LanguageName;

import java.util.Map;

public interface GetCyclomaticComplexity {
    /**
     * Get cyclomatic depend to code content
     *
     * @param languageName : Enum correspond to language
     * @param mapNode      : map of node text correspond to cyclomatic complexity
     * @param content      : code content
     * @return cyclomatic complexity
     */
    Long execute(LanguageName languageName, Map<String, Boolean> mapNode, String content);
}
