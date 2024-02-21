plugins {
    id("java")
}

group = "me.ivancerovina.simplesockets"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1");
    implementation("org.reflections:reflections:0.10.2")
}

tasks.test {
    useJUnitPlatform()
}