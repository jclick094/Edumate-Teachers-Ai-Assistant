package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blockchain.EduChain
import com.example.data.MockData
import com.example.model.BlockchainBlock
import com.example.model.SubscriptionPlan
import com.example.model.TierType
import com.example.ui.theme.ChainVerifiedGreen
import com.example.ui.viewmodel.TeacherViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockchainAndSubscriptionScreen(viewModel: TeacherViewModel) {
    val blockchain by viewModel.blockchain.collectAsState()
    val activeTier by viewModel.activeTier.collectAsState()
    val isAnnual by viewModel.isAnnualBilling.collectAsState()
    val aiQuotaUsed by viewModel.aiQuotaUsed.collectAsState()

    var sectionIndex by remember { mutableStateOf(0) } // 0: Blockchain Ledger, 1: Subscriptions & Pricing
    var testHashInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = sectionIndex,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = sectionIndex == 0,
                onClick = { sectionIndex = 0 },
                text = { Text("EduChain Ledger (${blockchain.size})", fontWeight = if (sectionIndex == 0) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_blockchain_ledger")
            )
            Tab(
                selected = sectionIndex == 1,
                onClick = { sectionIndex = 1 },
                text = { Text("Plans & Pricing", fontWeight = if (sectionIndex == 1) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.CardMembership, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_subscription_plans")
            )
            Tab(
                selected = sectionIndex == 2,
                onClick = { sectionIndex = 2 },
                text = { Text("Educator Profile", fontWeight = if (sectionIndex == 2) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_educator_profile")
            )
        }

        if (sectionIndex == 0) {
            // Blockchain Explorer
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = ChainVerifiedGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "EduChain Academic Integrity Protocol",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Immutable cryptographic distributed ledger securing attendance roll calls, examination grading, student transcripts, and certificates against unauthorized tampering.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Block Height", fontSize = 11.sp, color = Color.Gray)
                                    Text("#${blockchain.lastOrNull()?.blockNumber ?: 0}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Column {
                                    Text("Consensus Engine", fontSize = 11.sp, color = Color.Gray)
                                    Text("Proof-of-Authority", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                Column {
                                    Text("Total Transactions", fontSize = 11.sp, color = Color.Gray)
                                    Text("${blockchain.sumOf { it.transactions.size }} Sealed", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ChainVerifiedGreen)
                                }
                            }
                        }
                    }
                }

                // Interactive Verification Tool
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Tamper-Proof Audit Tool", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Inspect any block on the ledger to confirm mathematical hash alignment.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            OutlinedTextField(
                                value = testHashInput,
                                onValueChange = { testHashInput = it },
                                placeholder = { Text("Paste payload text or leave blank to test latest block") },
                                modifier = Modifier.fillMaxWidth().testTag("audit_tool_input")
                            )

                            Button(
                                onClick = {
                                    val targetBlock = blockchain.last()
                                    val testPayload = if (testHashInput.isNotBlank()) testHashInput
                                    else targetBlock.transactions.firstOrNull()?.payloadHash ?: "GENESIS_PAYLOAD"

                                    viewModel.verifyTamperStatus(
                                        payload = testPayload,
                                        blockHash = targetBlock.hash
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("run_audit_verification_button")
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Verify Current Block Integrity")
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Mined EduChain Blocks (${blockchain.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(blockchain.reversed()) { block ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                        Text(
                                            text = "Block #${block.blockNumber}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(block.timestamp)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }

                                Surface(shape = RoundedCornerShape(6.dp), color = ChainVerifiedGreen.copy(alpha = 0.15f)) {
                                    Text(
                                        text = "${block.transactions.size} Tx",
                                        fontWeight = FontWeight.Bold,
                                        color = ChainVerifiedGreen,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Text("Block Hash:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = block.hash,
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }

                            Text("Merkle Root:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(
                                text = block.merkleRoot,
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            // Transactions
                            block.transactions.forEach { tx ->
                                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(tx.type, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                            Text(tx.txId, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(tx.entitySummary, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (sectionIndex == 1) {
            // Subscriptions & Tiered Pricing View
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Active Plan Card
                item {
                    val currentPlan = MockData.subscriptionTiers.find { it.type == activeTier } ?: MockData.subscriptionTiers.first()
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Current Active Subscription",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = currentPlan.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                if (currentPlan.badge != null) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = currentPlan.badge,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Classes Limit", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                    Text(currentPlan.maxClasses, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                                Column {
                                    Text("AI Daily Quota", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                    Text("$aiQuotaUsed / ${currentPlan.aiQuotaPerDay.split(" ").first()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                                Column {
                                    Text("Blockchain Ledger", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                    Text("Active", fontWeight = FontWeight.Bold, color = ChainVerifiedGreen)
                                }
                            }
                        }
                    }
                }

                // Billing Toggle (Monthly vs Annual -20%)
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Billing Frequency", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    text = if (isAnnual) "Annual Billing (Save 20% + 2 Months Free)" else "Standard Monthly Billing",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isAnnual) ChainVerifiedGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Switch(
                                checked = isAnnual,
                                onCheckedChange = { viewModel.toggleBillingCycle() },
                                modifier = Modifier.testTag("billing_cycle_toggle")
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Educator & School Pricing Tiers",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // List of Plans
                items(MockData.subscriptionTiers) { plan ->
                    val isCurrent = plan.type == activeTier
                    val price = if (isAnnual) plan.priceAnnualMonthly else plan.priceMonthly

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isCurrent)
                            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = plan.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                if (plan.badge != null) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                    ) {
                                        Text(
                                            text = plan.badge,
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            // Price display
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = if (price == 0.0) "FREE" else "$${"%.2f".format(price)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (price > 0.0) {
                                    Text(
                                        text = "/ month",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                                    )
                                }
                            }

                            Text(
                                text = plan.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Divider()

                            // Features list
                            plan.features.forEach { feature ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = ChainVerifiedGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(feature, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            if (isCurrent) {
                                OutlinedButton(
                                    onClick = { },
                                    enabled = false,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Current Plan")
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.changeSubscriptionTier(plan.type) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("select_tier_${plan.type.name}")
                                ) {
                                    Text(if (plan.type == TierType.FREE_TRIAL) "Activate Free Trial" else "Switch to ${plan.name}")
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Educator Profile View
            EducatorProfileView(viewModel)
        }
    }
}

@Composable
fun EducatorProfileView(viewModel: TeacherViewModel) {
    val profile by viewModel.educatorProfile.collectAsState()
    val activeTier by viewModel.activeTier.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Educator Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.displayName.take(2).uppercase().ifEmpty { "ED" },
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Text(
                        text = profile.displayName.ifEmpty { "Educator" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = ChainVerifiedGreen.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ChainVerifiedGreen)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = ChainVerifiedGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Firebase Auth Verified",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ChainVerifiedGreen
                            )
                        }
                    }

                    Text(
                        text = profile.email.ifEmpty { "Authenticated Educator" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = { showEditDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("edit_educator_profile_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Educator Details")
                    }
                }
            }
        }

        // Institutional Affiliation
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Academic Affiliation & Role", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)

                    ProfileRow("Academic Title", profile.title)
                    ProfileRow("Institution", profile.institution)
                    ProfileRow("Department", profile.department)
                    ProfileRow("Staff / Faculty ID", profile.staffId)
                    ProfileRow("Subscription Tier", activeTier.name.replace("_", " "))
                }
            }
        }

        // Professional Bio
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pedagogical Bio & Specialization", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = profile.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Blockchain & Firebase Cloud Status
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cloud & Blockchain Sync State", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    }

                    ProfileRow("Firestore Database", "ai-studio-android-educhain")
                    ProfileRow("Security Rules", "Zero-Trust Authenticated")
                    ProfileRow("Blockchain Signer Key", profile.blockchainSignerKey)
                    ProfileRow("Last Synced", profile.lastSynced)
                }
            }
        }
    }

    if (showEditDialog) {
        EditEducatorProfileDialog(
            currentProfile = profile,
            onDismiss = { showEditDialog = false },
            onSave = { title, institution, department, staffId, bio ->
                viewModel.updateEducatorProfile(title, institution, department, staffId, bio)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.widthIn(max = 200.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEducatorProfileDialog(
    currentProfile: com.example.model.EducatorProfile,
    onDismiss: () -> Unit,
    onSave: (title: String, institution: String, department: String, staffId: String, bio: String) -> Unit
) {
    var title by remember { mutableStateOf(currentProfile.title) }
    var institution by remember { mutableStateOf(currentProfile.institution) }
    var department by remember { mutableStateOf(currentProfile.department) }
    var staffId by remember { mutableStateOf(currentProfile.staffId) }
    var bio by remember { mutableStateOf(currentProfile.bio) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Educator Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Academic Title") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_title_input")
                )
                OutlinedTextField(
                    value = institution,
                    onValueChange = { institution = it },
                    label = { Text("School / Institution") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_institution_input")
                )
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Department / Faculty") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = staffId,
                    onValueChange = { staffId = it },
                    label = { Text("Staff / Teacher ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Pedagogical Bio") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, institution, department, staffId, bio) },
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text("Save & Sync to Firestore")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

