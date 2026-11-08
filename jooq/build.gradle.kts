val jooqCodegen: Configuration by configurations.creating

dependencies {
    implementation(libs.jooq)
    implementation(libs.jooq.kotlin)
    runtimeOnly(libs.h2)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.junit.platform.launcher)

    jooqCodegen(libs.jooq.codegen)
    jooqCodegen(libs.jooq.meta)
    jooqCodegen(libs.jooq.meta.extensions)
    jooqCodegen(libs.h2)
}

val generateJooq = tasks.register<JavaExec>("generateJooq") {
    workingDir = projectDir
    classpath = jooqCodegen
    mainClass.set("org.jooq.codegen.GenerationTool")
    args("src/main/resources/jooq-codegen.xml")
}

tasks.named("compileKotlin") {
    dependsOn(generateJooq)
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("src/main/generated")
    }
}
