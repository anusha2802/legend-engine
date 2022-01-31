package org.finos.legend.engine.language.pure.dsl.persistence.compiler.toPureGraph;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.impl.utility.ListIterate;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.CompileContext;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.Persistence;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.PersistenceVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.BatchDatasetSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.BatchDatastoreSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.BatchPersistence;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.audit.*;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.deduplication.*;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.mode.BatchMilestoningMode;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.mode.BatchMilestoningModeVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.mode.appendonly.AppendOnly;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.mode.delta.BitemporalDelta;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.mode.delta.NonMilestonedDelta;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.mode.delta.UnitemporalDelta;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.mode.delta.merge.DeleteIndicatorMergeScheme;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.mode.delta.merge.MergeScheme;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.mode.delta.merge.MergeSchemeVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.mode.delta.merge.NoDeletesMergeScheme;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.mode.snapshot.BitemporalSnapshot;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.mode.snapshot.NonMilestonedSnapshot;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.batch.mode.snapshot.UnitemporalSnapshot;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.event.EventType;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.event.EventTypeVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.event.RegistryDatasetAvailable;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.event.ScheduleTriggered;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.persistence.streaming.StreamingPersistence;
import org.finos.legend.pure.generated.*;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.Class;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.Enum;

public class HelperPersistenceBuilder
{
    private static final String PERSIST_PACKAGE_PREFIX = "meta::pure::persist::metamodel";
    private static final EventTypeBuilder EVENT_TYPE_BUILDER = new EventTypeBuilder();
    private static final AuditSchemeBuilder AUDIT_SCHEME_BUILDER = new AuditSchemeBuilder();

    private HelperPersistenceBuilder()
    {
    }

    public static Root_meta_pure_persist_metamodel_event_EventType buildEventType(EventType eventType)
    {
        return eventType.accept(EVENT_TYPE_BUILDER);
    }

    public static Root_meta_pure_persist_metamodel_Persistence buildPersistence(Persistence persistence, CompileContext context)
    {
        Enum inputShape = context.resolveEnumValue(PERSIST_PACKAGE_PREFIX + "::DataShape", persistence.inputShape.name());
        Class<?> inputClass = context.resolveClass(persistence.inputClass.getPath());

        return persistence.accept(new PersistenceBuilder(inputShape, inputClass, context));
    }

    public static Root_meta_pure_persist_metamodel_batch_BatchDatastoreSpecification buildDatastoreSpecification(BatchDatastoreSpecification specification, Class<?> inputClass, CompileContext context)
    {
        return new Root_meta_pure_persist_metamodel_batch_BatchDatastoreSpecification_Impl("")
                ._datastoreName(specification.datastoreName)
                ._datasets(ListIterate.collect(specification.datasets, d -> buildDatasetSpecification(d, inputClass, context)));
    }

    public static Root_meta_pure_persist_metamodel_batch_BatchDatasetSpecification buildDatasetSpecification(BatchDatasetSpecification specification, Class<?> inputClass, CompileContext context)
    {
        return new Root_meta_pure_persist_metamodel_batch_BatchDatasetSpecification_Impl("")
                ._datasetName(specification.datasetName)
                ._partitionProperties(ListIterate.collect(specification.partitionProperties, p -> resolveInputClassProperty(p, inputClass, context)))
                ._deduplicationStrategy(buildDeduplicationStrategy(specification.deduplicationStrategy, inputClass, context))
                ._milestoningMode(buildMilestoningMode(specification.milestoningMode, inputClass, context));
    }

    public static Root_meta_pure_persist_metamodel_batch_deduplication_DeduplicationStrategy buildDeduplicationStrategy(DeduplicationStrategy deduplicationStrategy, Class<?> inputClass, CompileContext context)
    {
        return deduplicationStrategy.accept(new DeduplicationStrategyBuilder(inputClass, context));
    }

    public static Root_meta_pure_persist_metamodel_batch_mode_BatchMilestoningMode buildMilestoningMode(BatchMilestoningMode milestoningMode, Class<?> inputClass, CompileContext context)
    {
        return null;
    }

    public static Root_meta_pure_persist_metamodel_batch_audit_AuditScheme buildAuditScheme(AuditScheme auditScheme)
    {
        return auditScheme.accept(AUDIT_SCHEME_BUILDER);
    }

    public static Root_meta_pure_persist_metamodel_batch_mode_delta_merge_MergeScheme buildMergeScheme(MergeScheme mergeScheme, Class<?> inputClass, CompileContext context)
    {
        return mergeScheme.accept(new MergeSchemeBuilder(inputClass, context));
    }

    // helper methods

    private static org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.function.property.Property<?, ?> resolveInputClassProperty(org.finos.legend.engine.protocol.pure.v1.model.packageableElement.domain.Property property, Class<?> inputClass, CompileContext context)
    {
        return inputClass._properties().detect(p -> p._name().equals(property.name));
    }

    // helper visitors for class hierarchies

    private static class EventTypeBuilder implements EventTypeVisitor<Root_meta_pure_persist_metamodel_event_EventType>
    {
        @Override
        public Root_meta_pure_persist_metamodel_event_EventType visit(RegistryDatasetAvailable val)
        {
            return new Root_meta_pure_persist_metamodel_event_RegistryDatasetAvailable_Impl("");
        }

        @Override
        public Root_meta_pure_persist_metamodel_event_EventType visit(ScheduleTriggered val)
        {
            return new Root_meta_pure_persist_metamodel_event_ScheduleTriggered_Impl("");
        }
    }

    private static class PersistenceBuilder implements PersistenceVisitor<Root_meta_pure_persist_metamodel_Persistence>
    {
        private final Enum inputShape;
        private final Class<?> inputClass;
        private final CompileContext context;

        private PersistenceBuilder(Enum inputShape, Class<?> inputClass, CompileContext context)
        {
            this.inputShape = inputShape;
            this.inputClass = inputClass;
            this.context = context;
        }

        @Override
        public Root_meta_pure_persist_metamodel_Persistence visit(BatchPersistence val)
        {
            return new Root_meta_pure_persist_metamodel_batch_BatchPersistence_Impl("")
                    ._inputShape(inputShape)
                    ._inputClass(inputClass)
                    ._transactionMode(context.resolveEnumValue(PERSIST_PACKAGE_PREFIX + "::batch::BatchTransactionMode", val.transactionMode.name()))
                    ._targetSpecification(buildDatastoreSpecification(val.targetSpecification, inputClass, context));
        }

        @Override
        public Root_meta_pure_persist_metamodel_Persistence visit(StreamingPersistence val)
        {
            return new Root_meta_pure_persist_metamodel_streaming_StreamingPersistence_Impl("");
        }
    }

    private static class DeduplicationStrategyBuilder implements DeduplicationStrategyVisitor<Root_meta_pure_persist_metamodel_batch_deduplication_DeduplicationStrategy>
    {
        private final Class<?> inputClass;
        private final CompileContext context;

        private DeduplicationStrategyBuilder(Class<?> inputClass, CompileContext context)
        {
            this.inputClass = inputClass;
            this.context = context;
        }

        @Override
        public Root_meta_pure_persist_metamodel_batch_deduplication_DeduplicationStrategy visit(AnyDeduplicationStrategy val)
        {
            return new Root_meta_pure_persist_metamodel_batch_deduplication_AnyDeduplicationStrategy_Impl("");
        }

        @Override
        public Root_meta_pure_persist_metamodel_batch_deduplication_DeduplicationStrategy visit(CountDeduplicationStrategy val)
        {
            return new Root_meta_pure_persist_metamodel_batch_deduplication_CountDeduplicationStrategy_Impl("")
                    ._duplicateCountPropertyName(val.duplicateCountPropertyName);
        }

        @Override
        public Root_meta_pure_persist_metamodel_batch_deduplication_DeduplicationStrategy visit(MaxVersionDeduplicationStrategy val)
        {
            return new Root_meta_pure_persist_metamodel_batch_deduplication_MaxVersionDeduplicationStrategy_Impl("")
                    ._versionProperty(resolveInputClassProperty(val.versionProperty, inputClass, context));

        }

        @Override
        public Root_meta_pure_persist_metamodel_batch_deduplication_DeduplicationStrategy visit(NoDeduplicationStrategy val)
        {
            return new Root_meta_pure_persist_metamodel_batch_deduplication_NoDeduplicationStrategy_Impl("");
        }
    }

    private static class BatchMilestoningModeBuilder implements BatchMilestoningModeVisitor<Root_meta_pure_persist_metamodel_batch_mode_BatchMilestoningMode>
    {
        private final Class<?> inputClass;
        private final CompileContext context;

        private BatchMilestoningModeBuilder(Class<?> inputClass, CompileContext context)
        {
            this.inputClass = inputClass;
            this.context = context;
        }

        @Override
        public Root_meta_pure_persist_metamodel_batch_mode_BatchMilestoningMode visit(AppendOnly val)
        {
            return new Root_meta_pure_persist_metamodel_batch_mode_appendonly_AppendOnly_Impl("")
                    ._auditScheme(buildAuditScheme(val.auditScheme))
                    ._filterDuplicates(val.filterDuplicates);
        }

        @Override
        public Root_meta_pure_persist_metamodel_batch_mode_BatchMilestoningMode visit(BitemporalDelta val)
        {
            return null;
        }

        @Override
        public Root_meta_pure_persist_metamodel_batch_mode_BatchMilestoningMode visit(BitemporalSnapshot val)
        {
            return null;
        }

        @Override
        public Root_meta_pure_persist_metamodel_batch_mode_BatchMilestoningMode visit(NonMilestonedDelta val)
        {
            return new Root_meta_pure_persist_metamodel_batch_mode_delta_NonMilestonedDelta_Impl("")
                    ._auditScheme(buildAuditScheme(val.auditScheme));
        }

        @Override
        public Root_meta_pure_persist_metamodel_batch_mode_BatchMilestoningMode visit(NonMilestonedSnapshot val)
        {
            return new Root_meta_pure_persist_metamodel_batch_mode_snapshot_NonMilestonedSnapshot_Impl("")
                    ._auditScheme(buildAuditScheme(val.auditScheme));
        }

        @Override
        public Root_meta_pure_persist_metamodel_batch_mode_BatchMilestoningMode visit(UnitemporalDelta val)
        {
            return null;
        }

        @Override
        public Root_meta_pure_persist_metamodel_batch_mode_BatchMilestoningMode visit(UnitemporalSnapshot val)
        {
            return null;
        }
    }

    private static class AuditSchemeBuilder implements AuditSchemeVisitor<Root_meta_pure_persist_metamodel_batch_audit_AuditScheme>
    {
        @Override
        public Root_meta_pure_persist_metamodel_batch_audit_AuditScheme visit(BatchDateTimeAuditScheme val)
        {
            return new Root_meta_pure_persist_metamodel_batch_audit_BatchDateTimeAuditScheme_Impl("")
                    ._transactionDateTimePropertyName(val.transactionDateTimePropertyName);
        }

        @Override
        public Root_meta_pure_persist_metamodel_batch_audit_AuditScheme visit(NoAuditScheme val)
        {
            return new Root_meta_pure_persist_metamodel_batch_audit_NoAuditScheme_Impl("");
        }

        @Override
        public Root_meta_pure_persist_metamodel_batch_audit_AuditScheme visit(OpaqueAuditScheme val)
        {
            return new Root_meta_pure_persist_metamodel_batch_audit_OpaqueAuditScheme_Impl("");
        }
    }

    private static class MergeSchemeBuilder implements MergeSchemeVisitor<Root_meta_pure_persist_metamodel_batch_mode_delta_merge_MergeScheme>
    {
        private final Class<?> inputClass;
        private final CompileContext context;

        private MergeSchemeBuilder(Class<?> inputClass, CompileContext context)
        {
            this.inputClass = inputClass;
            this.context = context;
        }

        @Override
        public Root_meta_pure_persist_metamodel_batch_mode_delta_merge_MergeScheme visit(DeleteIndicatorMergeScheme val)
        {
            return new Root_meta_pure_persist_metamodel_batch_mode_delta_merge_DeleteIndicatorMergeScheme_Impl("")
                    ._deleteProperty(resolveInputClassProperty(val.deleteProperty, inputClass, context))
                    ._deleteValues(Lists.immutable.ofAll(val.deleteValues));
        }

        @Override
        public Root_meta_pure_persist_metamodel_batch_mode_delta_merge_MergeScheme visit(NoDeletesMergeScheme val)
        {
            return new Root_meta_pure_persist_metamodel_batch_mode_delta_merge_NoDeletesMergeScheme_Impl("");
        }
    }
}
