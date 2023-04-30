package simple.blockchain.models

class Node {
    @Volatile
    var chain: MutableList<Block> = mutableListOf()

    @Synchronized
    fun addBlock(block: Block) = chain.add(block)
}