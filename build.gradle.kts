plugins {
    id("net.ivoa.vo-dml.vodmltools") version "0.3.18"
    `maven-publish`
    application
}

group = "org.javastro.ivoa.dm"
version = "1.0-SNAPSHOT"

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
    api("org.javastro.ivoa.vo-dml:ivoa-base:1.0-SNAPSHOT") // IMPL using API so that it appears in transitive compile
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")

    implementation("org.slf4j:slf4j-api:1.7.32")
    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("org.apache.derby:derby:10.14.2.0")
    testImplementation("org.javastro:jaxbjpa-utils:0.1.2")
    testImplementation("org.javastro:jaxbjpa-utils:0.1.2:test")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

application {
    mainClass.set("Genschema")
}
