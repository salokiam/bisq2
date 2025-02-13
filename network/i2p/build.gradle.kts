plugins {
    id("bisq.java-library")
}

dependencies {
    implementation(libs.google.guava)

    // Exclude httpclient transitive dependency because it may override
    // I2P-specific impl of some Apache classes like org.apache.http.util.Args
    implementation(libs.apache.httpcomponents.core) {
        exclude(group = "org.apache.httpcomponents", module = "httpclient")
    }

    implementation(libs.bundles.i2p)
}
