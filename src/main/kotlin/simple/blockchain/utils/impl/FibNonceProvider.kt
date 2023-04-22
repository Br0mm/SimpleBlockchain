package simple.blockchain.utils.impl

import simple.blockchain.utils.NonceProvider

class FibNonceProvider: NonceProvider {
    private var previousValue = 1
    override fun createNonce(value: Int): Int {
        val currentValue = previousValue + value
        previousValue = value
        return currentValue
    }
}
