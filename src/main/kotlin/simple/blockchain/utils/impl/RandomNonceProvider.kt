package simple.blockchain.utils.impl

import simple.blockchain.utils.NonceProvider
import kotlin.random.Random

class RandomNonceProvider: NonceProvider {
    override fun createNonce(value: Int): Int = Random.nextInt(0, 10000)
}
