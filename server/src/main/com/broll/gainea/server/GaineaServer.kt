package com.broll.gainea.server

import com.broll.gainea.MessageRegistry
import com.broll.gainea.server.init.ServerSetup
import com.broll.gainea.server.sites.CustomCliCommands
import com.broll.mpnll.ConnectionDefaults
import com.broll.mpnll.server.MpnllServer
import org.slf4j.LoggerFactory

class GaineaServer(version: String) {
    val server = MpnllServer()

    init {
        Log.info("Start Gainea Server $version")
        server.name = "GaineaServer"
        server.version = version
        server.registerMessages(MessageRegistry::register)
        ServerSetup.setup(server)
        ServerStatistic(server)
        val tcpPort =
            System.getenv("GAINEA_TCP_PORT")?.toIntOrNull() ?: ConnectionDefaults.DEFAULT_TCP_PORT
        val websocketPort =
            System.getenv("GAINEA_WS_PORT")?.toIntOrNull() ?: ConnectionDefaults.DEFAULT_WS_PORT
        server.open(tcpPort, websocketPort)
    }

    fun appendCLI() = CustomCliCommands(server).register()

    companion object {
        private val Log = LoggerFactory.getLogger(GaineaServer::class.java)
    }
}
