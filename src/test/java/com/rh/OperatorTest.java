package com.rh;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
import io.javaoperatorsdk.operator.junit.E2EOperatorExtension;
import io.javaoperatorsdk.operator.junit.OperatorExtension;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;
import java.util.Objects;

@QuarkusTest
public class OperatorTest {

    @Inject
    KubernetesClient client;

    public enum OperatorDeployment {local,remote}

    protected static OperatorDeployment operatorDeployment;
    static final Logger log = Logger.getLogger(OperatorTest.class);
    boolean isLocal() {
        operatorDeployment = ConfigProvider.getConfig().getOptionalValue("test.operator.deployment", OperatorDeployment.class).orElse(OperatorDeployment.local);
        boolean isRemote = operatorDeployment == OperatorDeployment.remote;
        log.info("Running the operator  locally: " + !isRemote);
        return !isRemote;
    }

    @RegisterExtension
    AbstractOperatorExtension operator = isLocal() ? OperatorExtension.builder()
            .waitForNamespaceDeletion(true)
            .withReconciler(new TestReconciler())
            //.withReconciler(new KeycloakRealmImportController())
            .build()
            : E2EOperatorExtension.builder()
            //.waitForNamespaceDeletion(true)
            //.withOperatorDeployment()
            .build();

    @Test
    public void testBasicKeycloakDeploymentAndDeletion() {
        Log.info("Creating new Keycloak CR example");
        var kc = getResourceFromFile("example-keycloak.yaml", TestEmptyCrd.class);
    }

    public static <T extends HasMetadata> T getResourceFromFile(String fileName, Class<T> type) {
        return Serialization.unmarshal(Objects.requireNonNull(OperatorTest.class.getResourceAsStream("/" + fileName)), type);
    }
}
