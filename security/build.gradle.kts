plugins {
    id("bisq.java-library")
    id("bisq.protobuf")
}

dependencies {
    implementation(project(":persistence"))

    implementation(libs.google.guava)
    implementation(libs.bouncycastle)
    implementation(libs.bouncycastle.pg)
    implementation(libs.typesafe.config)
}
