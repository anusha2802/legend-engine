package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.authentication;

public class SnowflakePublicCloudAuthenticationStrategy extends AuthenticationStrategy
{
    public String publicUserName;
    public String secretArn;
    public String tokenUrl;

    @Override
    public <T> T accept(AuthenticationStrategyVisitor<T> authenticationStrategyVisitor)
    {
        return authenticationStrategyVisitor.visit(this);
    }
}
