package simple.blockchain.domain

import simple.blockchain.*
import simple.blockchain.models.Block
import simple.blockchain.models.Node
import simple.blockchain.plugins.RESOLVE_NODE_URL
import simple.blockchain.plugins.SEND_URL
import simple.blockchain.plugins.VALIDATE_URL

class Handler(
    private val node: Node,
    private val repository: Repository,
) {

    suspend fun handleBlock(block: Block, senderNodeUrl: String) {
        log.debug("Block received")
        val lastBlock = node.chain.lastOrNull()
        if (handleAddBlock(lastBlock, block)) return
        if (senderNodeUrl != currentUrl) {
            handleResolveNode(block, senderNodeUrl)
            log.debug("Minority resolved")
        }
    }

    private suspend fun handleAddBlock(lastBlock: Block?, newBlock: Block): Boolean {
        val shouldAdd = lastBlock == null || (lastBlock.index == newBlock.index - 1 && lastBlock.currentHash == newBlock.previousHash)
        if (shouldAdd) {
            node.addBlock(newBlock)
            log.debug("Block added and send")
            runCatching {
                repository.sendBlock(newBlock, "$url1$SEND_URL")
                repository.sendBlock(newBlock, "$url2$SEND_URL")
            }
        }
        return shouldAdd
    }

    private suspend fun handleResolveNode(block: Block, senderNodeUrl: String) {
        val url = if (senderNodeUrl == url1) url2 else url1
        val thirdNodeBlock = repository.getResolvingNode("$url$RESOLVE_NODE_URL")
        if (thirdNodeBlock.currentHash == block.currentHash) {
            node.chain = repository.validateChain("$url$VALIDATE_URL")
        }
    }
}