package org.finos.legend.engine.language.pure.grammar.from;

import org.eclipse.collections.impl.utility.ListIterate;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.connection.RelationalDatabaseConnectionParserGrammar;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.connection.S3ConnectionParserGrammar;
import org.finos.legend.engine.language.pure.grammar.from.authentication.AuthenticationStrategySourceCode;
import org.finos.legend.engine.language.pure.grammar.from.connection.ConnectionParser;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.AwsPartition;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.AWS;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.AWS_US_GOV;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.AWS_CN;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.AwsPartitionVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.SourceInformation;
import org.finos.legend.engine.protocol.pure.v1.model.context.EngineErrorType;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.PackageableElement;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.section.ImportAwareCodeSection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.*;
import org.finos.legend.engine.shared.core.operational.errorManagement.EngineException;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class S3ConnectionParseTreeWalker
{
    private final ParseTreeWalkerSourceInformation walkerSourceInformation;

    public S3ConnectionParseTreeWalker(ParseTreeWalkerSourceInformation walkerSourceInformation)
    {
        this.walkerSourceInformation = walkerSourceInformation;
    }

    /**********
     * s3Connection
     **********/

    public void visitS3ConnectionValue(S3ConnectionParserGrammar.DefinitionContext ctx, S3Connection s3Connection)
    {
        // store (to change to not applicable?)
        S3ConnectionParserGrammar.ConnectionStoreContext storeContext = PureGrammarParserUtility.validateAndExtractOptionalField(ctx.connectionStore(), "store", s3Connection.sourceInformation);
        if (storeContext != null)
        {
            s3Connection.element = PureGrammarParserUtility.fromQualifiedName(storeContext.qualifiedName().packagePath() == null ? Collections.emptyList() : storeContext.qualifiedName().packagePath().identifier(), storeContext.qualifiedName().identifier());
            s3Connection.elementSourceInformation = this.walkerSourceInformation.getSourceInformation(storeContext.qualifiedName());
        }

        // partition
        S3ConnectionParserGrammar.PartitionContext partitionContext = PureGrammarParserUtility.validateAndExtractRequiredField(ctx.partition(), "partition", s3Connection.sourceInformation);
        s3Connection.partition = visitPartition(partitionContext);

        // region
        S3ConnectionParserGrammar.RegionContext regionContext = PureGrammarParserUtility.validateAndExtractRequiredField(ctx.region(), "region", s3Connection.sourceInformation);
        s3Connection.region = PureGrammarParserUtility.fromGrammarString(regionContext.STRING().getText(), true);

        // bucket
        S3ConnectionParserGrammar.BucketContext bucketContext = PureGrammarParserUtility.validateAndExtractRequiredField(ctx.bucket(), "bucket", s3Connection.sourceInformation);
        s3Connection.bucket = PureGrammarParserUtility.fromGrammarString(bucketContext.STRING().getText(), true);

    }

    /**********
     * partition
     **********/

    private AwsPartition visitPartition(S3ConnectionParserGrammar.PartitionContext ctx)
    {
        SourceInformation sourceInformation = walkerSourceInformation.getSourceInformation(ctx);

        if (ctx.AWS() != null)
        {
            return new AWS();
        }
        else if (ctx.AWS_CN() != null)
        {
            return new AWS_CN();
        }
        else if (ctx.AWS_US_GOV() != null)
        {
            return new AWS_US_GOV();
        }
        throw new EngineException("Unrecognized partition", sourceInformation, EngineErrorType.PARSER);
    }

}
