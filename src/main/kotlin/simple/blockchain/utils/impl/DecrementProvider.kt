package simple.blockchain.utils.impl

import simple.blockchain.utils.NonceProvider
import kotlin.random.Random

class DecrementProvider: NonceProvider {

    override fun createNonce(value: Int): Int = value - 1

    override fun clear() = Unit
}
