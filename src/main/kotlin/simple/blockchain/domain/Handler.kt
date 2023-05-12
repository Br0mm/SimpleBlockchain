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

    suspend fun handleRecievedBlock(block: Block, senderNodeUrl: String) {
        log.debug("Received Block (index = ${block.index}, previous hash = ${block.previousHash}, current hash = ${block.currentHash})")
        val lastBlock = node.chain.lastOrNull()
        if (handleAddBlock(lastBlock, block)) return
        handleResolveNode(lastBlock, block, senderNodeUrl)
        log.debug("Minority resolved")
    }

    suspend fun handledMinedBlock(block: Block) {
        log.debug("Mined Block (index = ${block.index}, previous hash = ${block.previousHash}, current hash = ${block.currentHash})")
        val lastBlock = node.chain.lastOrNull()
        val shouldSend = handleAddBlock(lastBlock, block)
        if (shouldSend) sendBlock(block)
    }

    private fun handleAddBlock(lastBlock: Block?, newBlock: Block): Boolean {
        val shouldAdd = lastBlock == null || (lastBlock.index == newBlock.index - 1 && lastBlock.currentHash == newBlock.previousHash)
        if (shouldAdd) {
            node.addBlock(newBlock)
            log.debug("Block added")
        } else {
            log.debug("Block not added")
        }
        return shouldAdd
    }

    private suspend fun sendBlock(block: Block) {
        log.debug("Block send")
        runCatching {
            repository.sendBlock(block, "$url1:8080$SEND_URL")
            repository.sendBlock(block, "$url2:8080$SEND_URL")
        }
    }

    private suspend fun handleResolveNode(lastBlock: Block?, block: Block, senderNodeUrl: String) {
        val url = if (senderNodeUrl == url1) url2 else url1
        val thirdNodeBlock = repository.getResolvingNode("$url:8080$RESOLVE_NODE_URL")

        when {
            lastBlock != null && lastBlock.index > block.index && lastBlock.index > thirdNodeBlock.index -> Unit

            block.index > thirdNodeBlock.index -> node.chain = repository.validateChain("$senderNodeUrl:8080$VALIDATE_URL")

            thirdNodeBlock.index > block.index -> node.chain = repository.validateChain("$url:8080$VALIDATE_URL")

            thirdNodeBlock.currentHash == block.currentHash -> node.chain = repository.validateChain("$url:8080$VALIDATE_URL")
        }
    }
}