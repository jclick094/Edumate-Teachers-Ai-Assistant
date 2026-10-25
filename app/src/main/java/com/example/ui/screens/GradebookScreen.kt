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
import com.example.model.AssessmentType
import com.example.model.GradeEntry
import com.example.ui.components.BlockchainBadge
import com.example.ui.components.GradeChip
import com.example.ui.theme.ChainVerifiedGreen
import com.example.ui.viewmodel.TeacherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradebookScreen(viewModel: TeacherViewModel) {
    val grades by viewModel.grades.collectAsState()
    val students by viewModel.students.collectAsState()
    val currentClass by viewModel.currentClass.collectAsState()

    var showAddGradeDialog by remember { mutableStateOf(false) }

    val avgScore = if (grades.isNotEmpty()) {
        grades.map { it.percentage }.average().toFloat()
    } else 0f

    val gpa = (avgScore / 100f) * 4.0f
    val isAllVerified = grades.isNotEmpty() && grades.all { it.isVerifiedOnChain }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Analytics Card
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
                                text = "Gradebook & Transcripts",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$currentClass • ${grades.size} Graded Entries",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "GPA: ${"%.2f".format(gpa)} / 4.0",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Score bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Class Weighted Average:", style = MaterialTheme.typography.bodyMedium)
                        Text("${"%.1f".format(avgScore)}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    LinearProgressIndicator(
                        progress = { avgScore / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    // Grade distribution
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        GradeCount("A+ / A", grades.count { it.letterGrade.startsWith("A") })
                        GradeCount("B", grades.count { it.letterGrade == "B" })
                        GradeCount("C", grades.count { it.letterGrade == "C" })
                        GradeCount("D / F", grades.count { it.letterGrade in listOf("D", "F") })
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddGradeDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("open_add_grade_dialog")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Record Grade", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.sealGradebook() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isAllVerified) ChainVerifiedGreen else MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1.4f).testTag("certify_grades_blockchain_button")
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isAllVerified) "Audit Certified" else "Certify on Chain", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Student Assessment Transcripts",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(grades) { grade ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GradeChip(letterGrade = grade.letterGrade)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(grade.studentName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Text(grade.assessmentTitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${grade.score} / ${grade.maxScore}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${"%.1f".format(grade.percentage)}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }

                    if (grade.feedback.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Feedback: ${grade.feedback}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Marked: ${grade.markedDate} • ${grade.assessmentType.name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        BlockchainBadge(
                            blockHash = grade.blockHash,
                            onClick = if (grade.blockHash != null) {
                                {
                                    viewModel.verifyTamperStatus(
                                        payload = "${grade.studentId}:${grade.assessmentId}:${grade.score}/${grade.maxScore}",
                                        blockHash = grade.blockHash
                                    )
                                }
                            } else null
                        )
                    }
                }
            }
        }
    }

    if (showAddGradeDialog) {
        AddGradeDialog(
            students = students,
            onDismiss = { showAddGradeDialog = false },
            onConfirm = { studentId, studentName, title, type, score, maxScore, feedback ->
                viewModel.addManualGrade(studentId, studentName, title, type, score, maxScore, feedback)
                showAddGradeDialog = false
            }
        )
    }
}

@Composable
fun GradeCount(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "$count", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGradeDialog(
    students: List<com.example.model.Student>,
    onDismiss: () -> Unit,
    onConfirm: (studentId: String, studentName: String, title: String, type: AssessmentType, score: Float, maxScore: Float, feedback: String) -> Unit
) {
    var selectedStudentIndex by remember { mutableStateOf(0) }
    var title by remember { mutableStateOf("Quiz 4: Boolean Algebra & Logic") }
    var selectedType by remember { mutableStateOf(AssessmentType.QUIZ) }
    var scoreStr by remember { mutableStateOf("9.5") }
    var maxScoreStr by remember { mutableStateOf("10") }
    var feedback by remember { mutableStateOf("Accurate deduction of truth tables. Very well done.") }

    var studentDropdownExpanded by remember { mutableStateOf(false) }
    val currentStudent = students.getOrElse(selectedStudentIndex) { students.first() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Assessment Grade", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Student Picker
                Box {
                    OutlinedButton(
                        onClick = { studentDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Student: ${currentStudent.name}")
                    }
                    DropdownMenu(
                        expanded = studentDropdownExpanded,
                        onDismissRequest = { studentDropdownExpanded = false }
                    ) {
                        students.forEachIndexed { idx, s ->
                            DropdownMenuItem(
                                text = { Text(s.name) },
                                onClick = {
                                    selectedStudentIndex = idx
                                    studentDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Assessment Title") },
                    modifier = Modifier.fillMaxWidth().testTag("add_grade_title_input")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = scoreStr,
                        onValueChange = { scoreStr = it },
                        label = { Text("Score") },
                        modifier = Modifier.weight(1f).testTag("add_grade_score_input")
                    )
                    OutlinedTextField(
                        value = maxScoreStr,
                        onValueChange = { maxScoreStr = it },
                        label = { Text("Max") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    label = { Text("Feedback & Teacher Remarks") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val s = scoreStr.toFloatOrNull() ?: 0f
                    val m = maxScoreStr.toFloatOrNull() ?: 10f
                    onConfirm(currentStudent.id, currentStudent.name, title, selectedType, s, m, feedback)
                },
                modifier = Modifier.testTag("submit_grade_button")
            ) {
                Text("Save Grade")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
