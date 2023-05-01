package simple.blockchain.domain

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import simple.blockchain.currentUrl
import simple.blockchain.models.Block

class Repository(
    private val client: HttpClient,
) {

    suspend fun sendBlock(block: Block, url: String): Boolean {
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            header("URL", currentUrl)
            setBody(block)
        }
        return response.bodyAsText() == "1"
    }

    suspend fun validateChain(url: String): MutableList<Block> {
        val response = client.get(url) {
            contentType(ContentType.Application.Json)
        }
        return response.body()
    }

    suspend fun getResolvingNode(url: String): Block {
        val response = client.get(url) {
            contentType(ContentType.Application.Json)
        }
        return response.body()
    }
}