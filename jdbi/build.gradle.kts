dependencies {
    implementation(kotlin("reflect"))
    implementation(libs.jdbi3.core)
    implementation(libs.jdbi3.kotlin)
    implementation(libs.jdbi3.kotlin.sqlobject)
    implementation(libs.jdbi3.sqlobject)
    runtimeOnly(libs.h2)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj.core)
    testImplementation(libs.jdbi3.testing)
    testRuntimeOnly(libs.junit.platform.launcher)
}
