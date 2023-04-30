package simple.blockchain.utils.impl

import simple.blockchain.utils.NonceProvider

class IncrementNonceProvider: NonceProvider {

    override fun createNonce(value: Int): Int = value + 1

    override fun clear() = Unit
}
