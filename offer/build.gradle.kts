plugins {
    id("bisq.java-library")
    id("bisq.protobuf")
}

dependencies {
    implementation(project(":persistence"))
    implementation(project(":security"))
    implementation(project(":i18n"))
    implementation(project(":identity"))
    implementation(project(":account"))
    implementation(project(":presentation"))
    implementation(project(":bonded_roles"))

    implementation("network:network")
    implementation("network:network-identity")

    implementation(libs.google.gson)
    implementation(libs.google.guava)
    implementation(libs.chimp.jsocks)
}
