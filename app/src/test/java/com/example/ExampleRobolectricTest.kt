package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.blockchain.EduChain
import com.example.model.BlockchainTx
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("EduChain AI", appName)
    }

    @Test
    fun `verify blockchain hashing and mining`() {
        val genesis = EduChain.createGenesisBlock()
        assertEquals(0L, genesis.blockNumber)
        assertTrue(genesis.hash.isNotEmpty())

        val tx = BlockchainTx(
            txId = "0xTEST_1",
            type = "GRADE_SEAL",
            entitySummary = "Test student grade",
            payloadHash = EduChain.sha256("Student1:Score:95"),
            timestamp = System.currentTimeMillis()
        )

        val block1 = EduChain.mineBlock(
            blockNumber = 1L,
            previousHash = genesis.hash,
            transactions = listOf(tx)
        )

        assertEquals(1L, block1.blockNumber)
        assertEquals(genesis.hash, block1.previousHash)

        val verification = EduChain.verifyIntegrity(
            originalPayload = "Student1:Score:95",
            storedBlockHash = block1.hash,
            blockchain = listOf(genesis, block1)
        )

        assertTrue(verification.isValid)
    }

    @Test
    fun `verify grounded search data structures`() {
        val result = com.example.ai.GroundedSearchResult(
            text = "Official Cambridge 2026 changes",
            searchQueries = listOf("Cambridge 9618 2026 updates"),
            sources = listOf(
                com.example.ai.SearchGroundingSource(
                    title = "Cambridge International AS & A Level",
                    url = "https://www.cambridgeinternational.org"
                )
            )
        )
        assertEquals("Official Cambridge 2026 changes", result.text)
        assertEquals(1, result.searchQueries.size)
        assertEquals(1, result.sources.size)
        assertEquals("Cambridge International AS & A Level", result.sources.first().title)
    }

    @Test
    fun `verify maps grounding data structures`() {
        val venue = com.example.ai.AcademicVenue(
            name = "Science Museum London",
            category = "Science Museum",
            address = "Exhibition Rd, South Kensington, London SW7 2DD",
            description = "Interactive galleries covering computing and mathematics.",
            recommendedActivities = "Tour the Information Age gallery.",
            rating = "4.8"
        )
        val result = com.example.ai.MapsGroundingResult(
            summary = "Excellent destinations for Grade 11 STEM trips.",
            venues = listOf(venue)
        )
        assertEquals(1, result.venues.size)
        assertEquals("Science Museum London", result.venues.first().name)
        assertEquals("4.8", result.venues.first().rating)
    }

    @Test
    fun `verify live voice message representation`() {
        val msg = com.example.ai.LiveVoiceMessage(
            sender = "gemini-3.8-live",
            text = "Consider using reciprocal teaching to enhance student metacognition.",
            timestamp = "10:30 AM"
        )
        assertEquals("gemini-3.8-live", msg.sender)
        assertTrue(msg.text.contains("reciprocal teaching"))
        assertTrue(msg.audioWaveform.isNotEmpty())
    }

    @Test
    fun `verify educator profile model defaults`() {
        val profile = com.example.model.EducatorProfile(
            userId = "test-uid-123",
            displayName = "Dr. Ada Lovelace",
            email = "ada@cambridge.edu",
            institution = "Cambridge University",
            department = "Computer Science",
            subscriptionTier = "PRO"
        )
        assertEquals("test-uid-123", profile.userId)
        assertEquals("PRO", profile.subscriptionTier)
        assertEquals("Cambridge University", profile.institution)
    }
}
