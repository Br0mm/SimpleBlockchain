package simple.blockchain.utils

interface NonceProvider {

    fun createNonce(value: Int): Int
}
