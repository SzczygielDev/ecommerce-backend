plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "ecommerce-backend"
include("shared")
include("order")
include("product")
include("external")