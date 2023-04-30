package simple.blockchain.domain

import simple.blockchain.currentNodePort
import simple.blockchain.host
import simple.blockchain.models.Block
import simple.blockchain.models.Node
import simple.blockchain.nodePort1
import simple.blockchain.nodePort2
import simple.blockchain.plugins.RESOLVE_NODE_URL
import simple.blockchain.plugins.SEND_URL
import simple.blockchain.plugins.VALIDATE_URL

class Handler(
    private val node: Node,
    private val repository: Repository,
) {

    suspend fun handleBlock(block: Block, senderNodePort: String) {
        val lastBlock = node.chain.lastOrNull()
        if (handleAddBlock(lastBlock, block)) return
        if (senderNodePort != currentNodePort) {
            handleResolveNode(block, senderNodePort)
        }
    }

    private suspend fun handleAddBlock(lastBlock: Block?, newBlock: Block): Boolean {
        val shouldAdd = lastBlock == null || (lastBlock.index == newBlock.index - 1 && lastBlock.currentHash == newBlock.previousHash)
        if (shouldAdd) {
            node.addBlock(newBlock)
            runCatching {
                repository.sendBlock(newBlock, "$host$nodePort1$SEND_URL")
                repository.sendBlock(newBlock, "$host$nodePort2$SEND_URL")
            }
        }
        return shouldAdd
    }

    private suspend fun handleResolveNode(block: Block, senderNodePort: String) {
        val url = if (senderNodePort == nodePort1) "$host$nodePort2" else "$host$nodePort1"
        val thirdNodeBlock = repository.getResolvingNode("$url$RESOLVE_NODE_URL")
        if (thirdNodeBlock.currentHash == block.currentHash) {
            node.chain = repository.validateChain("$url$VALIDATE_URL")
        }
    }
}