package com.rh;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Plural;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("k8s.keycloak.org")
@Version("v2alpha1")
@ShortNames("kc")
@Plural("keycloaks")
public class TestEmptyCrd extends CustomResource implements Namespaced {
}
