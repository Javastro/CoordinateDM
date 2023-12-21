plugins {
    id("net.ivoa.vo-dml.vodmltools") version "0.4.2"
    `maven-publish`
    application
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    signing
}

group = "org.javastro.ivoa.dm"
version = "1.1-SNAPSHOT"

vodml {
    vodmlDir.set(file("vo-dml"))
    vodslDir.set(file("model"))
    bindingFiles.setFrom(file("vo-dml/coord.vodml-binding.xml")
    )

}
/* uncomment below to run the generation of vodml from vodsl automatically */
tasks.named("vodmlJavaGenerate") {
    dependsOn("vodslToVodml")
}

tasks.register("UmlToVodml", net.ivoa.vodml.gradle.plugin.XmiTask::class.java) {
    xmiScript.set("xmi2vo-dml_Modelio3.7_UML2.4.1.xsl") // the conversion script
    xmiFile.set(file("./model/coords_1.0_uml2p4p1.xmi")) //the UML XMI to convert
    vodmlFile.set(file("./vo-dml/Coords-v1.0.vo-dml.xml")) // the output VO-DML file.
    description = "convert UML to VO-DML"
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    api("org.javastro.ivoa.vo-dml:ivoa-base:1.1-SNAPSHOT") // IMPL using API so that it appears in transitive compile
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")

    implementation("org.slf4j:slf4j-api:1.7.32")
    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("org.apache.derby:derby:10.14.2.0")
    testImplementation("org.javastro:jaxbjpa-utils:0.1.2")
    testImplementation("org.javastro:jaxbjpa-utils:0.1.2:test")
}

tasks.named<Jar>("jar") {
    exclude("META-INF/persistence.xml")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("VO-DML IVOA Base Model")
                description.set("The code generated from the IVOA base model that is included in most other models")
                url.set("https://www.ivoa.net/documents/CoordinateDM/")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers { //FIXME should add the other authors...
                    developer {
                        id.set("pahjbo")
                        name.set("Paul Harrison")
                        email.set("paul.harrison@manchester.ac.uk")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/ivoa-std/CoordinateDM.git")
                    developerConnection.set("scm:git:ssh://github.com/ivoa-std/CoordinateDM.git")
                    url.set("https://github.com/ivoa-std/CoordinateDM")
                }
            }
        }
    }
}

//publishing - IMPL would be nice to factor this out in some way....
nexusPublishing {
    repositories {
        sonatype()
    }
}
signing {
    setRequired { !project.version.toString().endsWith("-SNAPSHOT") && !project.hasProperty("skipSigning") }

    if (!project.hasProperty("skipSigning")) {
        useGpgCmd()
        sign(publishing.publications["mavenJava"])
    }
}
//do not generate extra load on Nexus with new staging repository if signing fails
tasks.withType<io.github.gradlenexus.publishplugin.InitializeNexusStagingRepository>().configureEach{
    shouldRunAfter(tasks.withType<Sign>())
}


application {
    mainClass.set("Genschema")
}
