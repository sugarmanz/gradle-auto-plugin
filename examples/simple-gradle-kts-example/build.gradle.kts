plugins {
    id("com.sugarmanz.auto")
}

val build by tasks.named("build") {
    doLast {
        println("hello")
    }
}
