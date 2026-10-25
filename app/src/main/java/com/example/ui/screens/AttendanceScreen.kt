package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttendanceRecord
import com.example.model.AttendanceStatus
import com.example.model.Student
import com.example.ui.components.AttendanceStatusPill
import com.example.ui.components.BlockchainBadge
import com.example.ui.theme.ChainVerifiedGreen
import com.example.ui.viewmodel.TeacherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(viewModel: TeacherViewModel) {
    val attendanceRecords by viewModel.attendance.collectAsState()
    val students by viewModel.students.collectAsState()
    val currentClass by viewModel.currentClass.collectAsState()

    val presentCount = attendanceRecords.count { it.status == AttendanceStatus.PRESENT }
    val lateCount = attendanceRecords.count { it.status == AttendanceStatus.LATE }
    val absentCount = attendanceRecords.count { it.status == AttendanceStatus.ABSENT }
    val excusedCount = attendanceRecords.count { it.status == AttendanceStatus.EXCUSED }
    val total = attendanceRecords.size
    val presentRate = if (total > 0) (presentCount.toFloat() / total) * 100f else 0f

    val isAllSealed = attendanceRecords.all { it.isSealed }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Attendance Analytics Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Today's Roll Call",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentClass,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ChainVerifiedGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${"%.1f".format(presentRate)}% Present",
                                color = ChainVerifiedGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Linear progress
                    LinearProgressIndicator(
                        progress = { presentRate / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = ChainVerifiedGreen,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    // Breakdown chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatPill("Present", presentCount, ChainVerifiedGreen)
                        StatPill("Late", lateCount, MaterialTheme.colorScheme.tertiary)
                        StatPill("Absent", absentCount, MaterialTheme.colorScheme.error)
                        StatPill("Excused", excusedCount, Color.Gray)
                    }

                    Divider()

                    // Quick Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.markAllPresent() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("mark_all_present_button")
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("All Present", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.sealAttendance() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isAllSealed) ChainVerifiedGreen else MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1.3f).testTag("seal_attendance_blockchain_button")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isAllSealed) "Re-seal on Chain" else "Seal on Blockchain", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Student Roster Roll Call
        item {
            Text(
                text = "Student Roll Call (${attendanceRecords.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(attendanceRecords) { record ->
            val student = students.find { it.id == record.studentId }
            StudentAttendanceCard(
                record = record,
                student = student,
                onStatusChange = { newStatus ->
                    viewModel.updateAttendance(record.studentId, newStatus)
                },
                onVerifyClick = {
                    if (record.blockHash != null) {
                        viewModel.verifyTamperStatus(
                            payload = "${record.studentId}:${record.status}:${record.date}",
                            blockHash = record.blockHash
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun StatPill(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 16.sp
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun StudentAttendanceCard(
    record: AttendanceRecord,
    student: Student?,
    onStatusChange: (AttendanceStatus) -> Unit,
    onVerifyClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student?.avatarInitials ?: "ST",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.studentName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Roll: ${student?.rollNumber ?: "N/A"} • ${"%.0f".format((student?.attendanceRate ?: 0.95f) * 100)}% Term Avg",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                BlockchainBadge(
                    blockHash = record.blockHash,
                    onClick = if (record.blockHash != null) onVerifyClick else null
                )
            }

            // Quick status buttons (P, L, A, E)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatusButton(
                    label = "Present",
                    isSelected = record.status == AttendanceStatus.PRESENT,
                    activeColor = ChainVerifiedGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusChange(AttendanceStatus.PRESENT) }
                )
                StatusButton(
                    label = "Late",
                    isSelected = record.status == AttendanceStatus.LATE,
                    activeColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusChange(AttendanceStatus.LATE) }
                )
                StatusButton(
                    label = "Absent",
                    isSelected = record.status == AttendanceStatus.ABSENT,
                    activeColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusChange(AttendanceStatus.ABSENT) }
                )
                StatusButton(
                    label = "Excused",
                    isSelected = record.status == AttendanceStatus.EXCUSED,
                    activeColor = Color.Gray,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatusChange(AttendanceStatus.EXCUSED) }
                )
            }

            if (record.note.isNotBlank()) {
                Text(
                    text = "Note: ${record.note}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun StatusButton(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) activeColor else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp
            )
        }
    }
}
