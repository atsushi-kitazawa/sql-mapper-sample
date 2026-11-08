plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(libs.mybatis.spring.boot.starter)
    implementation(libs.mybatis.dynamic.sql)
    runtimeOnly(libs.h2)
    testImplementation(libs.mybatis.spring.boot.starter.test)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.junit.platform.launcher)
}
