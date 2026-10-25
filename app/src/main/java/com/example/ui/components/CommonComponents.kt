package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blockchain.VerificationResult
import com.example.model.AttendanceStatus
import com.example.model.TierType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EduChainTopBar(
    currentClass: String,
    availableClasses: List<String>,
    onSelectClass: (String) -> Unit,
    activeTier: TierType,
    aiQuotaUsed: Int,
    onSubscriptionClick: () -> Unit,
    userEmail: String? = null,
    onSignOut: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Class Selector Dropdown
                Box {
                    Surface(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.testTag("class_selector_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Active Class",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentClass,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 190.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        availableClasses.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = item,
                                        fontWeight = if (item == currentClass) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Class,
                                        contentDescription = null,
                                        tint = if (item == currentClass) MaterialTheme.colorScheme.primary else Color.Gray
                                    )
                                },
                                onClick = {
                                    onSelectClass(item)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Subscription Badge & AI Quota Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Subscription Tier Pill
                    Surface(
                        onClick = onSubscriptionClick,
                        shape = RoundedCornerShape(20.dp),
                        color = when (activeTier) {
                            TierType.FREE_TRIAL -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                            TierType.EDUCATOR_PRO -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            TierType.SCHOOL_INSTITUTION -> ChainVerifiedGreen.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            when (activeTier) {
                                TierType.FREE_TRIAL -> MaterialTheme.colorScheme.tertiary
                                TierType.EDUCATOR_PRO -> MaterialTheme.colorScheme.primary
                                TierType.SCHOOL_INSTITUTION -> ChainVerifiedGreen
                                else -> Color.LightGray
                            }
                        ),
                        modifier = Modifier.testTag("subscription_header_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = when (activeTier) {
                                    TierType.FREE_TRIAL -> Icons.Default.HourglassTop
                                    TierType.EDUCATOR_PRO -> Icons.Default.Star
                                    TierType.SCHOOL_INSTITUTION -> Icons.Default.CorporateFare
                                    else -> Icons.Default.CheckCircle
                                },
                                contentDescription = "Plan Tier",
                                tint = when (activeTier) {
                                    TierType.FREE_TRIAL -> MaterialTheme.colorScheme.tertiary
                                    TierType.EDUCATOR_PRO -> MaterialTheme.colorScheme.primary
                                    TierType.SCHOOL_INSTITUTION -> ChainVerifiedGreen
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (activeTier) {
                                    TierType.FREE_TRIAL -> "Trial (12d)"
                                    TierType.EDUCATOR_STARTER -> "Starter"
                                    TierType.EDUCATOR_PRO -> "PRO Tier"
                                    TierType.SCHOOL_INSTITUTION -> "Campus"
                                    TierType.ENTERPRISE -> "Enterprise"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // AI Quota Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                        modifier = Modifier.testTag("ai_quota_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Quota",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$aiQuotaUsed AI",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    if (onSignOut != null) {
                        IconButton(
                            onClick = onSignOut,
                            modifier = Modifier.size(32.dp).testTag("sign_out_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Sign Out",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BlockchainBadge(
    blockHash: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    if (blockHash != null) {
        Surface(
            onClick = { onClick?.invoke() },
            enabled = onClick != null,
            shape = RoundedCornerShape(12.dp),
            color = ChainVerifiedGreen.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, ChainVerifiedGreen.copy(alpha = 0.5f)),
            modifier = modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Blockchain Verified",
                    tint = ChainVerifiedGreen,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "EduChain #${blockHash.take(8)}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    fontWeight = FontWeight.Bold,
                    color = ChainVerifiedGreen,
                    fontSize = 11.sp
                )
            }
        }
    } else {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LockClock,
                    contentDescription = "Unsealed",
                    tint = Color.Gray,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Unsealed",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun AttendanceStatusPill(
    status: AttendanceStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        AttendanceStatus.PRESENT -> Triple(ChainVerifiedGreen.copy(alpha = 0.15f), ChainVerifiedGreen, "Present")
        AttendanceStatus.LATE -> Triple(AttendanceLate.copy(alpha = 0.15f), AttendanceLate, "Late")
        AttendanceStatus.ABSENT -> Triple(AttendanceAbsent.copy(alpha = 0.15f), AttendanceAbsent, "Absent")
        AttendanceStatus.EXCUSED -> Triple(AttendanceExcused.copy(alpha = 0.15f), AttendanceExcused, "Excused")
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun GradeChip(
    letterGrade: String,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when {
        letterGrade.startsWith("A") -> ChainVerifiedGreen to Color.White
        letterGrade.startsWith("B") -> CyanSecondaryLight to Color.White
        letterGrade.startsWith("C") -> AttendanceLate to Color.White
        else -> AttendanceAbsent to Color.White
    }

    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letterGrade,
            color = fg,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun VerificationDialog(
    result: VerificationResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_verification_modal")
            ) {
                Text("Close Audit")
            }
        },
        icon = {
            Icon(
                imageVector = if (result.isValid) Icons.Default.Shield else Icons.Default.Warning,
                contentDescription = null,
                tint = if (result.isValid) ChainVerifiedGreen else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = if (result.isValid) "EduChain Integrity: Verified" else "Integrity Alert: Hash Mismatch",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = result.reason,
                    style = MaterialTheme.typography.bodyMedium
                )
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = "Payload SHA-256 Hash:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = result.calculatedHash,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                if (result.blockNumber != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Block Height:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "#${result.blockNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                if (result.txId != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Transaction ID:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = result.txId,
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    )
}
