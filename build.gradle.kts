val javaVersion = JavaLanguageVersion.of(17)
val familieProsesseringVersion = "2.20230926054831_994885a"
val tilleggsstønaderLibsVersion = "2023.09.14-10.25.400ea92abb53"
val tilleggsstønaderKontrakterVersion = "2023.09.26-09.37.899354321766"
val familieTjenestespesifikasjonerVersion = "1.0_20230718100517_1e1beb0"
val tokenSupportVersion = "3.1.5"
val apacheCxfVersion = "4.0.2"
val wiremockVersion = "2.35.0"
val mockkVersion = "1.13.8"
val testcontainerVersion = "1.19.0"

group = "no.nav.tilleggsstonader.oppdrag"
version = "1.0.0"

plugins {
    application

    kotlin("jvm") version "1.9.10"
    id("com.diffplug.spotless") version "6.21.0"
    id("com.github.ben-manes.versions") version "0.48.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.18"

    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "1.9.10"

    id("org.cyclonedx.bom") version "1.7.4"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://build.shibboleth.net/maven/releases/")
    }
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
    mavenLocal()
}

apply(plugin = "com.diffplug.spotless")

spotless {
    kotlin {
        ktlint("0.50.0")
    }
}

configurations.all {
    resolutionStrategy {
        failOnNonReproducibleResolution()
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.retry:spring-retry")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")

    // Logging
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    implementation("io.micrometer:micrometer-registry-prometheus")

    // implementation("no.nav.familie:prosessering-core:$familieProsesseringVersion")

    // Tillegggsstønader libs
    implementation("no.nav.tilleggsstonader-libs:util:$tilleggsstønaderLibsVersion")
    implementation("no.nav.tilleggsstonader-libs:log:$tilleggsstønaderLibsVersion")
    implementation("no.nav.tilleggsstonader-libs:http-client:$tilleggsstønaderLibsVersion")
    implementation("no.nav.tilleggsstonader-libs:sikkerhet:$tilleggsstønaderLibsVersion")

    implementation("no.nav.tilleggsstonader.kontrakter:tilleggsstonader-kontrakter:$tilleggsstønaderKontrakterVersion")

    // TODO slett disse og bytt ut med utbetalingsgeneratorn og felles fra tilleggsstonader
    implementation("no.nav.familie.kontrakter:felles:3.0_20230808083340_ced4750")
    implementation("no.nav.familie.kontrakter:barnetrygd:3.0_20230808083340_ced4750")
    implementation("no.nav.familie.felles:log:2.20230508082643_6b28bd8")
    implementation("no.nav.familie.felles:http-client:2.20230508082643_6b28bd8")
    implementation("no.nav.familie.felles:sikkerhet:2.20230508082643_6b28bd8")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.0")

    // XML
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")
    implementation("org.glassfish.jaxb:jaxb-core:4.0.3")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.3")

    // MQ/jms
    implementation("com.ibm.mq:com.ibm.mq.jakarta.client:9.3.3.0")
    implementation("jakarta.jms:jakarta.jms-api")
    implementation("org.springframework:spring-jms")
    implementation("org.messaginghub:pooled-jms:3.1.0")

    // Tjenestespesifikasjoner
    implementation("no.nav.familie.tjenestespesifikasjoner:avstemming-v1-tjenestespesifikasjon:$familieTjenestespesifikasjonerVersion")
    implementation("no.nav.familie.tjenestespesifikasjoner:tilbakekreving-v1-tjenestespesifikasjon:$familieTjenestespesifikasjonerVersion")
    implementation("no.nav.familie.tjenestespesifikasjoner:nav-virksomhet-oppdragsbehandling-v1-meldingsdefinisjon:$familieTjenestespesifikasjonerVersion")
    implementation("no.nav.familie.tjenestespesifikasjoner:nav-system-os-simuler-fp-service-tjenestespesifikasjon:$familieTjenestespesifikasjonerVersion")

    // Cxf
    implementation("no.nav.common:cxf:3.2023.09.13_04.55-a8ff452fbd94")
    implementation("org.apache.cxf:cxf-core:$apacheCxfVersion")
    implementation("org.apache.cxf:cxf-rt-frontend-jaxws:$apacheCxfVersion")
    implementation("org.apache.cxf:cxf-rt-transports-http:$apacheCxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-security:$apacheCxfVersion")
    implementation("org.apache.cxf:cxf-rt-features-logging:$apacheCxfVersion")
    implementation("org.apache.cxf:cxf-rt-bindings-soap:$apacheCxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-policy:$apacheCxfVersion")
    implementation("org.apache.cxf:cxf-rt-frontend-simple:$apacheCxfVersion")

    runtimeOnly("com.sun.xml.ws:jaxws-ri:4.0.1@pom")
    implementation("io.github.threeten-jaxb:threeten-jaxb-core:2.1.0")

    implementation("org.springdoc:springdoc-openapi-starter-common:2.2.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:$wiremockVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")

    testImplementation("org.testcontainers:postgresql:$testcontainerVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainerVersion")

    testImplementation("no.nav.security:token-validation-spring-test:$tokenSupportVersion")
    testImplementation("no.nav.tilleggsstonader-libs:test-util:$tilleggsstønaderLibsVersion")

    // testImplementation("org.testcontainers:junit-jupiter:1.18.3")
}

kotlin {
    jvmToolchain(javaVersion.asInt())

    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

application {
    mainClass.set("no.nav.tilleggsstonader.oppdrag.AppKt")
}

if (project.hasProperty("skipLint")) {
    gradle.startParameter.excludedTaskNames += "spotlessKotlinCheck"
}

tasks.test {
    useJUnitPlatform()
}

tasks.bootJar {
    archiveFileName.set("app.jar")
}
