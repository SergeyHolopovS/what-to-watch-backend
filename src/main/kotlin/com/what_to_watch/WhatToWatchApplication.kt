package com.what_to_watch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class WhatToWatchApplication

fun main(args: Array<String>) {
	runApplication<WhatToWatchApplication>(*args)
}
