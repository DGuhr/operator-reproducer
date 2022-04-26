package com.rh;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.javaoperatorsdk.operator.OperatorException;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.ErrorStatusHandler;
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext;
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.RetryInfo;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.javaoperatorsdk.operator.processing.event.EventHandler;
import io.javaoperatorsdk.operator.processing.event.source.EventSource;
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource;
import io.javaoperatorsdk.operator.processing.event.source.informer.Mappers;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ControllerConfiguration
public class TestReconciler implements Reconciler<TestEmptyCrd>, EventSourceInitializer<TestEmptyCrd>, ErrorStatusHandler<TestEmptyCrd> {

    @Inject
    KubernetesClient client;

    @Override
    public List<EventSource> prepareEventSources(EventSourceContext<TestEmptyCrd> ctx) {
        String namespace = ctx.getConfigurationService().getClientConfiguration().getNamespace();

        SharedIndexInformer<Deployment> deploymentInformer =
                client.apps().deployments().inNamespace(namespace)
                        .withLabels(Map.of("app","foodeployment", "app.kubernetes.io/managed-by", "foocompany"))
                        .runnableInformer(0);

        SharedIndexInformer<Service> servicesInformer =
                client.services().inNamespace(ctx.getConfigurationService().getClientConfiguration().getNamespace())
                        .withLabels(Map.of("app","foodeployment", "app.kubernetes.io/managed-by", "foocompany"))
                        .runnableInformer(0);

        SharedIndexInformer<Ingress> ingressesInformer =
                client.network().v1().ingresses().inNamespace(ctx.getConfigurationService().getClientConfiguration().getNamespace())
                        .withLabels(Map.of("app","foodeployment", "app.kubernetes.io/managed-by", "foocompany"))
                        .runnableInformer(0);

        EventSource deploymentEvent = new InformerEventSource<>(deploymentInformer, Mappers.fromOwnerReference());
        EventSource servicesEvent = new InformerEventSource<>(servicesInformer, Mappers.fromOwnerReference());
        EventSource ingressesEvent = new InformerEventSource<>(ingressesInformer, Mappers.fromOwnerReference());

        return List.of(deploymentEvent,
                servicesEvent,
                ingressesEvent);
    }

    @Override
    public UpdateControl<TestEmptyCrd> reconcile(TestEmptyCrd testEmptyCrd, Context context) {
        return null;
    }

    @Override
    public Optional<TestEmptyCrd> updateErrorStatus(TestEmptyCrd testEmptyCrd, RetryInfo retryInfo, RuntimeException e) {
        return Optional.empty();
    }
}
