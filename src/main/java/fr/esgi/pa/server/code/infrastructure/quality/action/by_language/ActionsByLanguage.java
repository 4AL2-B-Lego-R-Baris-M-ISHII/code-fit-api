package fr.esgi.pa.server.code.infrastructure.quality.action.by_language;

public interface ActionsByLanguage {
    Long getNbLinesCode(String content);

    Long getNbLinesComment(String content);

    Long getCyclomaticComplexity(String content);

    Boolean hasRedundantCode(String content);
}
