package org.finos.legend.engine.language.pure.dsl.persistence.compiler.toPureGraph;

import org.eclipse.collections.api.factory.Lists;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.extension.CompilerExtension;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.extension.Processor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.ServicePersistence;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.runtime.PackageableRuntime;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.service.Service;
import org.finos.legend.pure.generated.Root_meta_pure_persist_metamodel_ServicePersistence_Impl;

import java.util.Collections;

public class PersistenceCompilerExtension implements CompilerExtension {
    @Override
    public Iterable<? extends Processor<?>> getExtraProcessors() {
        return Collections.singletonList(Processor.newProcessor(
                ServicePersistence.class,
                Lists.fixedSize.with(Service.class, PackageableRuntime.class),
                (servicePersistence, context) ->
                        new Root_meta_pure_persist_metamodel_ServicePersistence_Impl("")
                                ._documentation(servicePersistence.documentation)
                                ._ownersAddAll(Lists.immutable.ofAll(servicePersistence.owners))
                                ._trigger(HelperPersistenceBuilder.buildEventType(servicePersistence.trigger))
                                ._persistence(HelperPersistenceBuilder.buildPersistence(servicePersistence.persistence, context)),
                (servicePersistence, context) -> {}
        ));
    }
}
