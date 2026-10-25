package com.example.blockchain

import com.example.model.BlockchainBlock
import com.example.model.BlockchainTx
import java.security.MessageDigest

object EduChain {

    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun computeMerkleRoot(transactions: List<BlockchainTx>): String {
        if (transactions.isEmpty()) return sha256("EMPTY_TREE")
        var currentLevel = transactions.map { it.payloadHash }

        while (currentLevel.size > 1) {
            val nextLevel = mutableListOf<String>()
            for (i in currentLevel.indices step 2) {
                val left = currentLevel[i]
                val right = if (i + 1 < currentLevel.size) currentLevel[i + 1] else left
                nextLevel.add(sha256(left + right))
            }
            currentLevel = nextLevel
        }
        return currentLevel.first()
    }

    fun mineBlock(
        blockNumber: Long,
        previousHash: String,
        transactions: List<BlockchainTx>,
        validator: String = "EduChain Node #04 (Proof-of-Authority)"
    ): BlockchainBlock {
        val timestamp = System.currentTimeMillis()
        val merkleRoot = computeMerkleRoot(transactions)
        var nonce = 0L
        var blockHash = ""

        // Simple proof of authority / work simulation: header hash
        val header = "$blockNumber:$previousHash:$merkleRoot:$timestamp"
        blockHash = sha256("$header:$nonce")

        return BlockchainBlock(
            blockNumber = blockNumber,
            timestamp = timestamp,
            previousHash = previousHash,
            merkleRoot = merkleRoot,
            hash = blockHash,
            nonce = nonce,
            transactions = transactions,
            validator = validator
        )
    }

    fun createGenesisBlock(): BlockchainBlock {
        val genesisTx = BlockchainTx(
            txId = "0xGENESIS_00000000001",
            type = "GENESIS_NODE_INITIALIZATION",
            entitySummary = "EduChain Academic Ledger Genesis - Secure Teacher Verification Network",
            payloadHash = sha256("GENESIS_PAYLOAD_ACADEMIC_INTEGRITY"),
            timestamp = 1729000000000L,
            teacherSigner = "System Root Authority"
        )
        return BlockchainBlock(
            blockNumber = 0L,
            timestamp = 1729000000000L,
            previousHash = "0000000000000000000000000000000000000000000000000000000000000000",
            merkleRoot = sha256("GENESIS_TREE_ROOT"),
            hash = sha256("0:0000000000000000:GENESIS"),
            nonce = 42L,
            transactions = listOf(genesisTx),
            validator = "EduChain Genesis Core"
        )
    }

    fun verifyIntegrity(
        originalPayload: String,
        storedBlockHash: String,
        blockchain: List<BlockchainBlock>
    ): VerificationResult {
        val currentHash = sha256(originalPayload)
        val matchingBlock = blockchain.find { it.hash == storedBlockHash }
            ?: return VerificationResult(
                isValid = false,
                reason = "Block hash not found on EduChain ledger.",
                calculatedHash = currentHash
            )

        val tx = matchingBlock.transactions.find { it.payloadHash == currentHash }
        return if (tx != null) {
            VerificationResult(
                isValid = true,
                reason = "Cryptographic seal verified on Block #${matchingBlock.blockNumber}. Record is 100% tamper-evident.",
                calculatedHash = currentHash,
                blockNumber = matchingBlock.blockNumber,
                timestamp = matchingBlock.timestamp,
                txId = tx.txId
            )
        } else {
            VerificationResult(
                isValid = false,
                reason = "TAMPER ALERT! Current data hash ($currentHash) does not match the sealed transaction hash in Block #${matchingBlock.blockNumber}.",
                calculatedHash = currentHash,
                blockNumber = matchingBlock.blockNumber
            )
        }
    }
}

data class VerificationResult(
    val isValid: Boolean,
    val reason: String,
    val calculatedHash: String,
    val blockNumber: Long? = null,
    val timestamp: Long? = null,
    val txId: String? = null
)
