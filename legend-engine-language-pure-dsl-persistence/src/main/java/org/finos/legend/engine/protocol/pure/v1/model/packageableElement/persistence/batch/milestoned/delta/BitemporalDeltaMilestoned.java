package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.milestoned.delta;

public class BitemporalDeltaMilestoned extends DeltaMilestoned
{
    public org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.milestoned.validity.ValidityMilestoningScheme validityMilestoningScheme;
    public org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.milestoned.validity.derivation.ValidityDerivation validityDerivation;

    public <T> T accept(org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.BatchMilestoningModeVisitor<T> visitor)
    {
        return visitor.visit(this);
    }
}