package fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_comment;

import org.springframework.stereotype.Component;

@Component
public class SlashesNbLinesComment implements GetNbLinesCommentByLanguage {

    @Override
    public Long execute(String content) {
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

//    private void chooseAction(String line) {
//        if (line.contains("//") && line.contains("/*")) {
//            if (line.indexOf("/*") < line.indexOf("//")) {
//                isInMultiLinesComments = !line.contains("*/");
//            }
//            result++;
//        } else if (line.contains("//")) {
//            result++;
//        } else if (line.contains("/*")) {
//            result++;
//            isInMultiLinesComments = !line.contains("*/");
//        } else if (line.contains("*/")) {
//            isInMultiLinesComments = false;
//            result++;
//        } else if (isInMultiLinesComments) {
//            result++;
//        }
//    }
//
//    private static class ResultNbLinesComment {
//        private Long result = 0L;
//        private Boolean isInMultiLinesComments = false;
//
//        public void isMultilinesCommentsStart(String line) {
//            isInMultiLinesComments = line.contains("*/");
//        }
//
//        public void isMultilinesCommentsFinish(String line) {
//            isInMultiLinesComments = line.contains("*/");
//        }
//
//        public void incrementResult() {
//            result++;
//        }
//
//        public void updateResult(Consumer<ResultNbLinesComment> action) {
//            action.accept(this);
//        }
//    }
}
