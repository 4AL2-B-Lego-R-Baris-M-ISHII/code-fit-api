package fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_comment;

import org.springframework.stereotype.Component;

@Component
public class SlashesNbLinesComment implements GetNbLinesCommentByLanguage {
//    private final Map<Function<String, Boolean>, Consumer<String>> mapAction;
//    private ResultNbLinesComment resultNbLinesComment;

//    public SlashesNbLinesComment() {
//        mapAction = new HashMap<>();
//        mapAction.put(this::isLineContainStartsOfMultilineCommentAndSimpleComment, (line) -> {
//            if (line.indexOf("/*") < line.indexOf("//")) {
//                resultNbLinesComment.finishMultiLinesCommentIfEnd(line);
//            }
//            resultNbLinesComment.incrementResult();
//        });
//        mapAction.put(this::isLineContainMultilineCommentStartOrEnd, (line) -> {
//            resultNbLinesComment.finishMultiLinesCommentIfEnd(line);
//            resultNbLinesComment.incrementResult();
//        });
//        mapAction.put(this::isLineContainStartSimpleComment, (line) -> {
//            resultNbLinesComment.incrementResult();
//        });
//    }

    @Override
    public Long execute(String content) {
//        var linesContent = content.split("\\r\\n|\\r|\\n");
//        resultNbLinesComment = new ResultNbLinesComment();
//        for (String line : linesContent) {
//            this.actionDependToLineContent(line, resultNbLinesComment);
//        }
//        return resultNbLinesComment.getResult();
        var linesContent = content.split("\\r\\n|\\r|\\n");
        //var resultNbLinesComment = new ResultNbLinesComment();
        var result = 0L;
        var isInMultiLinesComments = false;
        for (String line : linesContent) {
            if (line.contains("//") && line.contains("/*")) {
                if (line.indexOf("/*") < line.indexOf("//")) {
                    isInMultiLinesComments = !line.contains("*/");
                }
                result++;
            } else if (line.contains("//")) {
                result++;
            } else if (line.contains("/*")) {
                result++;
                isInMultiLinesComments = !line.contains("*/");
            } else if (line.contains("*/")) {
                isInMultiLinesComments = false;
                result++;
            } else if (isInMultiLinesComments) {
                result++;
            }
        }
        return result;
    }

    //    private void actionDependToLineContent(String line, ResultNbLinesComment resultNbLinesComment) {
//        var maybeActionResult = getOneActionDependToLineContent(line);
//        var actionOnResult = maybeActionResult.map(Map.Entry::getValue).orElse((map) -> {
//            if (resultNbLinesComment.isInMultiLinesComments) {
//                resultNbLinesComment.incrementResult();
//            }
//        });
//        actionOnResult.accept(line);
//    }
//
//    @NotNull
//    private Optional<Map.Entry<Function<String, Boolean>, Consumer<String>>> getOneActionDependToLineContent(String line) {
//        return mapAction.entrySet().stream()
//                .filter((verifyLine) -> verifyLine.getKey().apply(line))
//                .findFirst();
//    }
//
//    private boolean isLineContainStartsOfMultilineCommentAndSimpleComment(String line) {
//        return line.contains("//") && line.contains("/*");
//    }
//
//    private boolean isLineContainMultilineCommentStartOrEnd(String line) {
//        return line.contains("/*") || line.contains("*/");
//    }
//
//    private boolean isLineContainStartSimpleComment(String line) {
//        return line.contains("//");
//    }
//
    private class ResultNbLinesComment {
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
