package simple.blockchain.utils

import java.security.MessageDigest

object HashProvider {

    fun calculateHash(concatString: String): String = MessageDigest
        .getInstance("SHA-256")
        .digest(concatString.toByteArray())
        .fold(StringBuilder()) { sb, it -> sb.append("%02x".format(it)) }.toString()

    fun validateHash(hash: String): Boolean = hash.endsWith("0000")
}