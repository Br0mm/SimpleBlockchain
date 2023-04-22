package simple.blockchain.models

data class Block(
    val index: Int = 1,
    val previousHash: String,
    val currentHash: String,
    val data: String,
    val nonce: Int,
)
