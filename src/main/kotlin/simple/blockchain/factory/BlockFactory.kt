package simple.blockchain.factory

import simple.blockchain.models.Block
import simple.blockchain.utils.HashProvider
import simple.blockchain.utils.NonceProvider
import kotlin.random.Random

class BlockFactory(
    private val nonceProvider: NonceProvider,
) {

    private val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun generateBlock(previousBlock: Block, index: Int): Block {
        val data = generateBlockData()
        var currentNonce = 1
        var concatString: String
        var hash = ""

        while (!HashProvider.validateHash(hash)) {
            currentNonce = nonceProvider.createNonce(currentNonce)
            concatString = with (previousBlock) {
                "${index}${previousHash}${data}${currentNonce}"
            }
            hash = HashProvider.calculateHash(concatString)
        }
        nonceProvider.clear()

        return Block(
            index = index,
            previousHash = previousBlock.currentHash,
            currentHash = hash,
            data = data,
            nonce = currentNonce,
        )
    }

    private fun generateBlockData(): String = (1..LENGTH)
        .map { Random.nextInt(0, chars.size).let { chars[it] } }
        .joinToString("")

    private companion object {
        private const val LENGTH = 256
    }
}