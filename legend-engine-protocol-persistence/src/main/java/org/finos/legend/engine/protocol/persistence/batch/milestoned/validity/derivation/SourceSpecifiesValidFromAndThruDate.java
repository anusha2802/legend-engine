package org.finos.legend.engine.protocol.persistence.batch.milestoned.validity.derivation;

import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.domain.Property;

public class SourceSpecifiesValidFromAndThruDate extends ValidityDerivation
{
    public Property sourceValidDateTimeFromProperty;
    public Property sourceValidDateTimeThruProperty;

    public <T> T accept(ValidityDerivationVisitor<T> visitor)
    {
        return visitor.visit(this);
    }
}