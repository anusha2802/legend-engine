// Copyright 2021 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.engine.language.pure.grammar.from.authentication;

import org.finos.legend.engine.language.pure.grammar.from.PureGrammarParserUtility;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.connection.authentication.AuthenticationStrategyParserGrammar;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.authentication.*;

public class AuthenticationStrategyParseTreeWalker
{
    public DefaultH2AuthenticationStrategy visitDefaultH2AuthenticationStrategy(AuthenticationStrategySourceCode code, AuthenticationStrategyParserGrammar.DefaultH2AuthContext authCtx)
    {
        DefaultH2AuthenticationStrategy authStrategy = new DefaultH2AuthenticationStrategy();
        authStrategy.sourceInformation = code.getSourceInformation();
        return authStrategy;
    }

    public TestDatabaseAuthenticationStrategy visitTestDatabaseAuthenticationStrategy(AuthenticationStrategySourceCode code, AuthenticationStrategyParserGrammar.TestDBAuthContext authCtx)
    {
        TestDatabaseAuthenticationStrategy authStrategy = new TestDatabaseAuthenticationStrategy();
        authStrategy.sourceInformation = code.getSourceInformation();
        return authStrategy;
    }

    public DelegatedKerberosAuthenticationStrategy visitDelegatedKerberosAuthenticationStrategy(AuthenticationStrategySourceCode code, AuthenticationStrategyParserGrammar.DelegatedKerberosAuthContext authCtx)
    {
        DelegatedKerberosAuthenticationStrategy authStrategy = new DelegatedKerberosAuthenticationStrategy();
        if (authCtx.delegatedKerberosAuthConfig() != null)
        {
            AuthenticationStrategyParserGrammar.ServerPrincipalConfigContext accessCtx = PureGrammarParserUtility.validateAndExtractOptionalField(authCtx.delegatedKerberosAuthConfig().serverPrincipalConfig(), "serverPrincipal", authStrategy.sourceInformation);
            authStrategy.serverPrincipal = PureGrammarParserUtility.fromGrammarString(accessCtx.STRING().getText(), true);
        }
        authStrategy.sourceInformation = code.getSourceInformation();
        return authStrategy;
    }

    public ApiTokenAuthenticationStrategy visitApiTokenAuthenticationStrategy(AuthenticationStrategySourceCode code, AuthenticationStrategyParserGrammar.ApiTokenAuthContext apiTokenAuthContext)
    {
        ApiTokenAuthenticationStrategy apiTokenAuthenticationStrategy = new ApiTokenAuthenticationStrategy();
        apiTokenAuthenticationStrategy.sourceInformation = code.getSourceInformation();
        AuthenticationStrategyParserGrammar.ApiTokenContext apiToken = PureGrammarParserUtility.validateAndExtractRequiredField(apiTokenAuthContext.apiToken(), "apiToken", code.getSourceInformation());
        apiTokenAuthenticationStrategy.apiToken = PureGrammarParserUtility.fromGrammarString(apiToken.STRING().getText(), true);
        return apiTokenAuthenticationStrategy;
    }

    public UserNamePasswordAuthenticationStrategy visitUserNamePasswordAuthenticationStrategy(AuthenticationStrategySourceCode code, AuthenticationStrategyParserGrammar.UserNamePasswordAuthContext authCtx)
    {
        UserNamePasswordAuthenticationStrategy authStrategy = new UserNamePasswordAuthenticationStrategy();
        authStrategy.sourceInformation = code.getSourceInformation();
        AuthenticationStrategyParserGrammar.UserNamePasswordAuthBaseVaultRefContext baseVaultRef = PureGrammarParserUtility.validateAndExtractOptionalField(authCtx.userNamePasswordAuthBaseVaultRef(), "baseVaultReference", authStrategy.sourceInformation);
        authStrategy.baseVaultReference = baseVaultRef == null ? null : PureGrammarParserUtility.fromGrammarString(baseVaultRef.STRING().getText(), true);
        AuthenticationStrategyParserGrammar.UserNamePasswordAuthUserNameVaultRefContext userNameVaultRef = PureGrammarParserUtility.validateAndExtractRequiredField(authCtx.userNamePasswordAuthUserNameVaultRef(), "userNameVaultReference", authStrategy.sourceInformation);
        authStrategy.userNameVaultReference = PureGrammarParserUtility.fromGrammarString(userNameVaultRef.STRING().getText(), true);
        AuthenticationStrategyParserGrammar.UserNamePasswordAuthPasswordVaultRefContext passwordVaultRef = PureGrammarParserUtility.validateAndExtractRequiredField(authCtx.userNamePasswordAuthPasswordVaultRef(), "passwordVaultReference", authStrategy.sourceInformation);
        authStrategy.passwordVaultReference = PureGrammarParserUtility.fromGrammarString(passwordVaultRef.STRING().getText(), true);
        return authStrategy;
    }

    public SnowflakePublicAuthenticationStrategy visitSnowflakePublicAuthenticationStrategy(AuthenticationStrategySourceCode code, AuthenticationStrategyParserGrammar.SnowflakePublicAuthContext snowflakePublicAuth)
    {
        SnowflakePublicAuthenticationStrategy snowflakePublicAuthenticationStrategy = new SnowflakePublicAuthenticationStrategy();
        snowflakePublicAuthenticationStrategy.sourceInformation = code.getSourceInformation();
        AuthenticationStrategyParserGrammar.SnowflakePublicAuthUserNameContext publicUserName = PureGrammarParserUtility.validateAndExtractRequiredField(snowflakePublicAuth.snowflakePublicAuthUserName(), "publicUserName", code.getSourceInformation());
        snowflakePublicAuthenticationStrategy.publicUserName = PureGrammarParserUtility.fromGrammarString(publicUserName.STRING().getText(), true);
        AuthenticationStrategyParserGrammar.SnowflakePublicAuthKeyVaultRefContext snowflakePublicAuthKeyVaultRef = PureGrammarParserUtility.validateAndExtractRequiredField(snowflakePublicAuth.snowflakePublicAuthKeyVaultRef(), "privateKeyVaultReference", code.getSourceInformation());
        snowflakePublicAuthenticationStrategy.privateKeyVaultReference = PureGrammarParserUtility.fromGrammarString(snowflakePublicAuthKeyVaultRef.STRING().getText(), true);
        AuthenticationStrategyParserGrammar.SnowflakePublicAuthPassPhraseVaultRefContext snowflakePublicAuthPassPhraseVaultRef = PureGrammarParserUtility.validateAndExtractRequiredField(snowflakePublicAuth.snowflakePublicAuthPassPhraseVaultRef(), "passPhraseVaultReference", code.getSourceInformation());
        snowflakePublicAuthenticationStrategy.passPhraseVaultReference = PureGrammarParserUtility.fromGrammarString(snowflakePublicAuthPassPhraseVaultRef.STRING().getText(), true);
        return snowflakePublicAuthenticationStrategy;
    }

    public GCPApplicationDefaultCredentialsAuthenticationStrategy visitGCPApplicationDefaultCredentialsAuthenticationStrategy(AuthenticationStrategySourceCode code, AuthenticationStrategyParserGrammar.GcpApplicationDefaultCredentialsAuthContext authCtx)
    {
        GCPApplicationDefaultCredentialsAuthenticationStrategy authStrategy = new GCPApplicationDefaultCredentialsAuthenticationStrategy();
        authStrategy.sourceInformation = code.getSourceInformation();
        return authStrategy;
    }

    //added new
    public AwsOAuthAuthenticationStrategy visitAwsOAuthAuthenticationStrategy(AuthenticationStrategySourceCode code, AuthenticationStrategyParserGrammar.AwsOAuthContext awsOAuth) {

        AwsOAuthAuthenticationStrategy awsOAuthAuthenticationStrategy = new AwsOAuthAuthenticationStrategy();
        awsOAuthAuthenticationStrategy.sourceInformation = code.getSourceInformation();

        //secret arn
        AuthenticationStrategyParserGrammar.AwsAuthSecretArnContext secretArn = PureGrammarParserUtility.validateAndExtractRequiredField(awsOAuth.awsAuthSecretArn(), "secretArn", code.getSourceInformation());
        awsOAuthAuthenticationStrategy.secretArn = PureGrammarParserUtility.fromGrammarString(secretArn.STRING().getText(), true);

        // token url
        AuthenticationStrategyParserGrammar.AwsOAuthDiscoveryUrlContext discoveryUrl = PureGrammarParserUtility.validateAndExtractRequiredField(awsOAuth.awsOAuthDiscoveryUrl(), "discoveryUrl", code.getSourceInformation());
        awsOAuthAuthenticationStrategy.discoveryUrl = PureGrammarParserUtility.fromGrammarString(discoveryUrl.STRING().getText(), true);

        return awsOAuthAuthenticationStrategy;
    }

    public AwsPKAuthenticationStrategy visitAwsPKAuthenticationStrategy(AuthenticationStrategySourceCode code, AuthenticationStrategyParserGrammar.AwsPKContext awsPK) {

        AwsPKAuthenticationStrategy awsPKAuthenticationStrategy = new AwsPKAuthenticationStrategy();
        awsPKAuthenticationStrategy.sourceInformation = code.getSourceInformation();

        //secret arn
        AuthenticationStrategyParserGrammar.AwsAuthSecretArnContext secretArn = PureGrammarParserUtility.validateAndExtractRequiredField(awsPK.awsAuthSecretArn(), "secretArn", code.getSourceInformation());
        awsPKAuthenticationStrategy.secretArn = PureGrammarParserUtility.fromGrammarString(secretArn.STRING().getText(), true);

        // user
        AuthenticationStrategyParserGrammar.AwsPKUserContext user = PureGrammarParserUtility.validateAndExtractRequiredField(awsPK.awsPKUser(), "user", code.getSourceInformation());
        awsPKAuthenticationStrategy.user = PureGrammarParserUtility.fromGrammarString(user.STRING().getText(), true);

        return awsPKAuthenticationStrategy;
    }
}