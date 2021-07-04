package fr.esgi.pa.server.code.infrastructure.quality.factory;

import fr.esgi.pa.server.code.infrastructure.quality.gen.ParserAndTreeInfo;
import fr.esgi.pa.server.code.infrastructure.quality.gen.c.CLexer;
import fr.esgi.pa.server.code.infrastructure.quality.gen.c.CParser;
import fr.esgi.pa.server.code.infrastructure.quality.gen.java8.Java8Lexer;
import fr.esgi.pa.server.code.infrastructure.quality.gen.java8.Java8Parser;
import fr.esgi.pa.server.language.core.LanguageName;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.stereotype.Component;

@Component
public class ParserAndTreeInfoFactory {

    public ParserAndTreeInfo getParserAndTreeInfo(LanguageName languageName, String content) {
        if (languageName.equals(LanguageName.JAVA8)) {
            Java8Lexer lexer = new Java8Lexer(CharStreams.fromString(content));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Java8Parser parser = new Java8Parser(tokens);
            return new ParserAndTreeInfo().setParser(parser).setTree(parser.compilationUnit());
        }
        CLexer lexer = new CLexer(CharStreams.fromString(content));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CParser parser = new CParser(tokens);
        return new ParserAndTreeInfo().setParser(parser).setTree(parser.compilationUnit());
    }
}
