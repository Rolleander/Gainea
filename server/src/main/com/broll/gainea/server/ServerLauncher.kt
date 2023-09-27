package com.broll.gainea.server

import com.broll.gainea.server.core.utils.ProcessingUtils
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.charset.StandardCharsets

object ServerLauncher {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("log4j.configuration", "log4j_server.properties")
        if (args.size > 0 && "test" == args[0]) {
            ProcessingUtils.MAX_PAUSE = 250
        }
        val Log = LoggerFactory.getLogger(ServerLauncher::class.java)
        val input = ServerLauncher::class.java.getResourceAsStream("/version.txt")
        val version = IOUtils.readLines(input, StandardCharsets.UTF_8)[0]
        input.close()
        try {
            val server = GaineaServer(version)
            server.appendCLI()
        } catch (e: Exception) {
            Log.error("Failed to launch server!", e)
        }
    }
}
