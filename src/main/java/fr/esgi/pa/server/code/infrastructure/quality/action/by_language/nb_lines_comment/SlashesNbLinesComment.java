package fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_comment;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class SlashesNbLinesComment implements GetNbLinesCommentByLanguage {
    private final Map<Function<String, Boolean>, Consumer<String>> mapAction;
    private ResultNbLinesComment resultNbLinesComment;

    public SlashesNbLinesComment() {
        mapAction = new HashMap<>();
        mapAction.put(this::isLineContainStartsOfMultilineCommentAndSimpleComment, (line) -> {
            if (line.indexOf("/*") < line.indexOf("//")) {
                resultNbLinesComment.finishMultiLinesCommentIfEnd(line);
            }
            resultNbLinesComment.incrementResult();
        });
        mapAction.put(this::isLineContainMultilineCommentStartOrEnd, (line) -> {
            resultNbLinesComment.finishMultiLinesCommentIfEnd(line);
            resultNbLinesComment.incrementResult();
        });
        mapAction.put(this::isLineContainStartSimpleComment, (line) -> {
            resultNbLinesComment.incrementResult();
        });
    }

    @Override
    public Long execute(String content) {
        var linesContent = content.split("\\r\\n|\\r|\\n");
        resultNbLinesComment = new ResultNbLinesComment();
        for (String line : linesContent) {
            this.actionDependToLineContent(line, resultNbLinesComment);
        }
        return resultNbLinesComment.getResult();
    }

    private void actionDependToLineContent(String line, ResultNbLinesComment resultNbLinesComment) {
        var maybeActionResult = getOneActionDependToLineContent(line);
        var actionOnResult = maybeActionResult.map(Map.Entry::getValue).orElse((map) -> {
            if (resultNbLinesComment.isInMultiLinesComments) {
                resultNbLinesComment.incrementResult();
            }
        });
        actionOnResult.accept(line);
    }

    @NotNull
    private Optional<Map.Entry<Function<String, Boolean>, Consumer<String>>> getOneActionDependToLineContent(String line) {
        return mapAction.entrySet().stream()
                .filter((verifyLine) -> verifyLine.getKey().apply(line))
                .findFirst();
    }

    private boolean isLineContainStartsOfMultilineCommentAndSimpleComment(String line) {
        return line.contains("//") && line.contains("/*");
    }

    private boolean isLineContainMultilineCommentStartOrEnd(String line) {
        return line.contains("/*") || line.contains("*/");
    }

    private boolean isLineContainStartSimpleComment(String line) {
        return line.contains("//");
    }

    private static class ResultNbLinesComment {
        private Long result = 0L;
        private Boolean isInMultiLinesComments = false;

        public Long getResult() {
            return result;
        }

        public void finishMultiLinesCommentIfEnd(String line) {
            isInMultiLinesComments = !line.contains("*/");
        }

        public void incrementResult() {
            result++;
        }
    }
}
