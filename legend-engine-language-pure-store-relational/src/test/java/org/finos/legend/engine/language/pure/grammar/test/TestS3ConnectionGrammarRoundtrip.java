package org.finos.legend.engine.language.pure.grammar.test;

import org.junit.Test;

public class TestS3ConnectionGrammarRoundtrip extends TestGrammarRoundtrip.TestGrammarRoundtripTestSuite {
    @Test
    public void testRelationalDatabaseConnection() {
        test("###Connection\n" +
                "S3Connection meta::mySimpleConnection\n" +
                "{\n" +
                "  store: store::Store;\n" +
                "  partition: AWS;\n" +
                "  region: 'US';\n" +
                "  bucket: 'abc';\n" +
                "}\n");
    }
}