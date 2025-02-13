pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}

plugins {
    id("bisq.gradle.toolchain_resolver.ToolchainResolverPlugin")
}

toolchainManagement {
    jvm {
        javaRepositories {
            repository("bisq_zulu") {
                resolverClass.set(bisq.gradle.toolchain_resolver.BisqToolchainResolver::class.java)
            }
        }
    }
}

rootProject.name = "bisq"

include("platforms:cli-platform")
include("platforms:common-platform")
include("platforms:network-platform")
include("platforms:test-platform")

include("account")
include("application")
include("bisq_easy")
include("bonded_roles")
include("chat")
include("common")
include("desktop")
include("desktop_app")
include("desktop_app_launcher")
include("contract")
include("i2p")
include("identity")
include("i18n")
include("offer")
include("oracle_node")
include("oracle_node_app")
include("persistence")
include("presentation")
include("trade")
include("rest_api_app")
include("security")
include("seed_node_app")
include("settings")
include("support")
include("user")

includeBuild("network")
includeBuild("wallets")
