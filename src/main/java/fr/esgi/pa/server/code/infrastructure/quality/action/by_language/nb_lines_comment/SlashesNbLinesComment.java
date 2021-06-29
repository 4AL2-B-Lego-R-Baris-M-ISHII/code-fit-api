package fr.esgi.pa.server.code.infrastructure.quality.action.by_language.nb_lines_comment;

import org.springframework.stereotype.Component;

@Component
public class SlashesNbLinesComment implements GetNbLinesCommentByLanguage {

    @Override
    public Long execute(String content) {
        var linesContent = content.split("\\r\\n|\\r|\\n");
        var resultNbLinesComment = new ResultNbLinesComment();
        for (String line : linesContent) {
            setResultDependToLine(resultNbLinesComment, line);
        }
        return resultNbLinesComment.getResult();
    }

    private void setResultDependToLine(ResultNbLinesComment resultNbLinesComment, String line) {
        if (line.contains("//") && line.contains("/*")) {
            if (line.indexOf("/*") < line.indexOf("//")) {
                resultNbLinesComment.finishMultiLinesCommentIfEnd(line);
            }
            resultNbLinesComment.incrementResult();
        } else if (line.contains("/*") || line.contains("*/")) {
            resultNbLinesComment.incrementResult();
            resultNbLinesComment.finishMultiLinesCommentIfEnd(line);
        } else if (line.contains("//") || resultNbLinesComment.getInMultiLinesComments()) {
            resultNbLinesComment.incrementResult();
        }
    }

    private static class ResultNbLinesComment {
        private Long result = 0L;
        private Boolean isInMultiLinesComments = false;

        public Long getResult() {
            return result;
        }

        public Boolean getInMultiLinesComments() {
            return isInMultiLinesComments;
        }

        public void finishMultiLinesCommentIfEnd(String line) {
            isInMultiLinesComments = !line.contains("*/");
        }

        public void incrementResult() {
            result++;
        }
    }
}
