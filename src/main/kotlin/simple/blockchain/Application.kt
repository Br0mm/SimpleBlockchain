package simple.blockchain

import simple.blockchain.factory.BlockFactory
import simple.blockchain.models.Block
import simple.blockchain.utils.impl.FibNonceProvider

fun main(args: Array<String>) {

    var block = Block(1, "2", "3", "4", 5)

    val concatString = with (block) {
        "${index}${previousHash}${data}${nonce}"
    }

    val blockFactory = BlockFactory(FibNonceProvider())

    for (i in 1..10) {
        block = blockFactory.generateBlock(block, block.index + 1)
        println(block)
    }
}
