package simple.blockchain.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import simple.blockchain.domain.Handler
import simple.blockchain.models.Block
import simple.blockchain.models.Node

const val VALIDATE_URL = "/validate"
const val SEND_URL = "/send"
const val RESOLVE_NODE_URL = "/resolve_node"

fun Application.configureRouting(node: Node, handler: Handler) {
    routing {
        validate(node)
        sendBlock(handler)
        resolveNode(node)
    }
}

fun Route.validate(node: Node) {
    get(VALIDATE_URL) {
        call.respond(node.chain)
    }
}

fun Route.sendBlock(handler: Handler) {
    post(SEND_URL) {
        val block = call.receive(Block::class)
        val senderNodeUrl = call.request.headers["URL"]

        if (senderNodeUrl != null) {
            handler.handleBlock(block, senderNodeUrl)
        }

        call.respond(1)
    }
}

fun Route.resolveNode(node: Node) {
    get(RESOLVE_NODE_URL) {
        call.respond(node.chain.last())
    }
}