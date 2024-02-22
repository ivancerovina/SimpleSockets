plugins {
    id("java")
}

group = "me.ivancerovina.simplesockets"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("org.reflections:reflections:0.10.2")
    implementation("com.github.jitpack:gradle-simple:1.0");
}

tasks.test {
    useJUnitPlatform()
}