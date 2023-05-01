package simple.blockchain

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import simple.blockchain.domain.Handler
import simple.blockchain.domain.Repository
import simple.blockchain.factory.BlockFactory
import simple.blockchain.models.Block
import simple.blockchain.models.Node
import simple.blockchain.plugins.configureMonitoring
import simple.blockchain.plugins.configureRouting
import simple.blockchain.plugins.configureSerialization
import simple.blockchain.utils.impl.FibNonceProvider
import simple.blockchain.utils.impl.IncrementNonceProvider
import simple.blockchain.utils.impl.RandomNonceProvider

val node = Node()
var url1 = ""
var url2 = ""
var currentUrl = ""
var isMain = false
val log = LoggerFactory.getLogger("Logger")

lateinit var blockFactory: BlockFactory

private val repository = Repository(
    HttpClient(CIO) {
        install(ContentNegotiation) {
            gson()
        }
    }
)
private val handler = Handler(node, repository)
private var block = Block(1, "2", "3", "4", 5)

fun main(args: Array<String>) {
    val currentPort = args[0]
    currentUrl = args[1]
    url1 = args[2]
    url2 = args[3]
    isMain = args[4] == "1"

    blockFactory = when (args[4]) {
        "1" -> BlockFactory(IncrementNonceProvider())
        "2" -> BlockFactory(RandomNonceProvider())
        else -> BlockFactory(FibNonceProvider())
    }

    start()
    embeddedServer(io.ktor.server.cio.CIO, port = currentPort.toInt(), host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun start() {
    CoroutineScope(Dispatchers.IO).launch {
        if (!isMain) awaitGenesis()
        else delay(500)
        generateBlock()
    }
}

private suspend fun awaitGenesis() {
    while (node.chain.isEmpty()) {
        delay(300)
    }
}

private suspend fun generateBlock() {
    while (true) {
        block = blockFactory.generateBlock(block, block.index + 1)
        handler.handleBlock(block, currentUrl)
    }
}

fun Application.module() {
    configureRouting(node, handler)
    configureMonitoring()
    configureSerialization()
}
