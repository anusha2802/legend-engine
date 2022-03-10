package org.finos.legend.engine.language.pure.dsl.persistence.grammar.test;

import org.finos.legend.engine.language.pure.grammar.test.TestGrammarRoundtrip;
import org.junit.Test;

public abstract class TestPersistenceGrammarRoundtrip extends TestGrammarRoundtrip.TestGrammarRoundtripTestSuite
{
    protected abstract String targetFlat();
    protected abstract String targetMulti();
    protected abstract String targetOpaque();
    protected abstract String batchMode();
    protected abstract String flatTarget();
    protected abstract String parts();

    @Test
    public void persistence()
    {
        test("###Persistence\n" +
                "import test::*;\n" +
                "Persistence test::TestPersistence\n" +
                "{\n" +
                "  doc: 'test doc';\n" +
                "  owners: ['owner1', 'owner2'];\n" +
                "  trigger: OpaqueTrigger;\n" +
                "  reader: Service\n" +
                "  {\n" +
                "    service: test::service::Service;\n" +
                "  }\n" +
                "  persister: Batch\n" +
                "  {\n" +
                "    target: " + targetMulti() + "\n" +
                "    {\n" +
                "      modelClass: test::WrapperClass;\n" +
                "      transactionScope: ALL_TARGETS;\n" +
                "      " + parts() + ":\n" +
                "      [\n" +
                "        {\n" +
                "          property: property1;\n" +
                "          " + flatTarget() + ":\n" +
                "          {\n" +
                "            targetName: 'TestDataset1';\n" +
                "            partitionProperties: [propertyA, propertyB];\n" +
                "            deduplicationStrategy: MaxVersion\n" +
                "            {\n" +
                "              versionProperty: updateDateTime;\n" +
                "            }\n" +
                "            " + batchMode() + ": UnitemporalDelta\n" +
                "            {\n" +
                "              mergeStrategy: NoDeletes;\n" +
                "              transactionMilestoning: BatchIdOnly\n" +
                "              {\n" +
                "                batchIdInFieldName: 'batchIdIn';\n" +
                "                batchIdOutFieldName: 'batchIdOut';\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          property: property2;\n" +
                "          " + flatTarget() + ":\n" +
                "          {\n" +
                "            targetName: 'TestDataset2';\n" +
                "            deduplicationStrategy: OpaqueDeduplication;\n" +
                "            " + batchMode() + ": BitemporalDelta\n" +
                "            {\n" +
                "              mergeStrategy: DeleteIndicator\n" +
                "              {\n" +
                "                deleteProperty: deleted;\n" +
                "                deleteValues: ['Y', '1', 'true'];\n" +
                "              }\n" +
                "              transactionMilestoning: DateTimeOnly\n" +
                "              {\n" +
                "                dateTimeInFieldName: 'inZ';\n" +
                "                dateTimeOutFieldName: 'outZ';\n" +
                "              }\n" +
                "              validityMilestoning: DateTime\n" +
                "              {\n" +
                "                dateTimeFromFieldName: 'fromZ';\n" +
                "                dateTimeThruFieldName: 'thruZ';\n" +
                "                derivation: SourceSpecifiesFromAndThruDateTime\n" +
                "                {\n" +
                "                  sourceDateTimeFromProperty: businessDateFrom;\n" +
                "                  sourceDateTimeThruProperty: businessDateThru;\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      ];\n" +
                "    }\n" +
                "  }\n" +
                "}\n");
    }
}
