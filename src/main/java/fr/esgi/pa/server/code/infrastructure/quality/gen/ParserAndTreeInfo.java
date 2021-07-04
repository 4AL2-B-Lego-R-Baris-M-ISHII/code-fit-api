package fr.esgi.pa.server.code.infrastructure.quality.gen;

import lombok.Data;
import lombok.experimental.Accessors;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;

@Data
@Accessors(chain = true)
public class ParserAndTreeInfo {
    private Parser parser;
    private ParseTree tree;
}
