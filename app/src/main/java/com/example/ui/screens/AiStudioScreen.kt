package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.outlined.*
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
import com.example.model.*
import com.example.ui.components.BlockchainBadge
import com.example.ui.theme.ChainVerifiedGreen
import com.example.ui.viewmodel.TeacherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiStudioScreen(viewModel: TeacherViewModel) {
    val subTab by viewModel.aiSubTab.collectAsState()
    val isGenerating by viewModel.isAiGenerating.collectAsState()
    val previewNote by viewModel.previewNote.collectAsState()
    val previewAssessment by viewModel.previewAssessment.collectAsState()
    val activeScript by viewModel.activeScriptToGrade.collectAsState()
    val scriptResult by viewModel.scriptEvaluationResult.collectAsState()
    val lessonNotes by viewModel.lessonNotes.collectAsState()
    val assessments by viewModel.assessments.collectAsState()
    val scripts by viewModel.scripts.collectAsState()
    val copilotMessages by viewModel.copilotMessages.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sub-navigation tabs
        ScrollableTabRow(
            selectedTabIndex = subTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            val tabs = listOf("Lesson Notes", "Quiz & Exams", "Script Marker", "Live Voice (gemini-3.8-live)", "AI Co-pilot")
            val icons = listOf(Icons.Default.MenuBook, Icons.Default.Quiz, Icons.Default.AssignmentTurnedIn, Icons.Default.Mic, Icons.Default.AutoAwesome)

            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = subTab == index,
                    onClick = { viewModel.selectAiSubTab(index) },
                    text = { Text(title, fontWeight = if (subTab == index) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(icons[index], contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("ai_subtab_$index")
                )
            }
        }

        if (isGenerating) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (subTab) {
                0 -> LessonNotesTab(viewModel, lessonNotes, previewNote, isGenerating)
                1 -> AssessmentCreatorTab(viewModel, assessments, previewAssessment, isGenerating)
                2 -> ScriptMarkerTab(viewModel, scripts, activeScript, scriptResult, isGenerating)
                3 -> LiveVoiceTab(viewModel)
                4 -> AiCopilotTab(viewModel, copilotMessages, isGenerating)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonNotesTab(
    viewModel: TeacherViewModel,
    savedNotes: List<LessonNote>,
    previewNote: LessonNote?,
    isGenerating: Boolean
) {
    var topic by remember { mutableStateOf("Asymmetric Cryptography and Public Key Infrastructure") }
    var subject by remember { mutableStateOf("Computer Science") }
    var gradeLevel by remember { mutableStateOf("Grade 11 / A-Level") }
    var curriculum by remember { mutableStateOf("Cambridge CAIE") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Generator Card
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Lesson Plan & Notes Generator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Generates pedagogical instructional steps, learning outcomes, hook, and exit diagnostics aligned to your curriculum.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val transcribedText by viewModel.transcribedText.collectAsState()
                    val isTranscribing by viewModel.isTranscribing.collectAsState()

                    LaunchedEffect(transcribedText) {
                        transcribedText?.let {
                            topic = it
                            viewModel.clearTranscribedText()
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = topic,
                            onValueChange = { topic = it },
                            label = { Text("Lesson Topic") },
                            modifier = Modifier.weight(1f).testTag("lesson_topic_input")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilledTonalIconButton(
                            onClick = { viewModel.transcribeAudioInput() },
                            enabled = !isTranscribing,
                            modifier = Modifier.testTag("dictate_lesson_topic_button")
                        ) {
                            if (isTranscribing) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Mic, contentDescription = "Dictate with gemini-3.5-transcribe", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Subject") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = curriculum,
                            onValueChange = { curriculum = it },
                            label = { Text("Curriculum") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = { viewModel.generateLessonNote(topic, subject, gradeLevel, curriculum) },
                        enabled = !isGenerating && topic.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("generate_lesson_button")
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isGenerating) "AI Synthesizing Lesson..." else "Generate Lesson Plan")
                    }
                }
            }
        }

        // Preview Generated Note
        if (previewNote != null) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
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
                            Text(
                                text = "Preview: ${previewNote.topic}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = previewNote.duration,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "Learning Objectives:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        previewNote.learningObjectives.forEach { obj ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text("• ", fontWeight = FontWeight.Bold)
                                Text(obj, style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Text(
                            text = "Instructional Stages:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        previewNote.teachingSteps.forEach { step ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(step.stage, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text("${step.durationMin} mins", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(step.description, style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Teacher Tip: ${step.teacherNotes}", style = MaterialTheme.typography.bodySmall, color = Color.Gray, fontSize = 11.sp)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.discardPreviewNote() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Discard")
                            }
                            Button(
                                onClick = { viewModel.savePreviewNote() },
                                modifier = Modifier.weight(1f).testTag("save_lesson_button")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save Note")
                            }
                        }
                    }
                }
            }
        }

        // Saved Lesson Plans
        item {
            Text(
                text = "Saved Lesson Repository (${savedNotes.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(savedNotes) { note ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = note.topic,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        BlockchainBadge(note.blockchainHash)
                    }
                    Text(
                        text = "${note.subject} • ${note.gradeLevel} • ${note.curriculum}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Homework: ${note.homeworkAssignment}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AssessmentCreatorTab(
    viewModel: TeacherViewModel,
    savedAssessments: List<Assessment>,
    previewAssessment: Assessment?,
    isGenerating: Boolean
) {
    var title by remember { mutableStateOf("Paper 2: Algorithmic Logic & Verification") }
    var subject by remember { mutableStateOf("Computer Science") }
    var selectedType by remember { mutableStateOf(AssessmentType.EXAM) }
    var gradeLevel by remember { mutableStateOf("Grade 11") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Quiz, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Assessment, Test & Quiz Builder",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Assessment Title") },
                        modifier = Modifier.fillMaxWidth().testTag("assessment_title_input")
                    )

                    // Assessment Type Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AssessmentType.values().forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type.name, fontSize = 11.sp) }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.generateAssessment(title, subject, gradeLevel, selectedType, 3, "Cambridge CAIE")
                        },
                        enabled = !isGenerating && title.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("generate_assessment_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isGenerating) "AI Formulating Questions..." else "Generate $selectedType with Rubric")
                    }
                }
            }
        }

        if (previewAssessment != null) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = previewAssessment.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Total: ${previewAssessment.totalMarks} Marks",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        previewAssessment.questions.forEachIndexed { i, q ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Q${i + 1} [${q.type}]", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("${q.marks} Marks", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                                    }
                                    Text(q.questionText, style = MaterialTheme.typography.bodyMedium)
                                    if (q.options.isNotEmpty()) {
                                        q.options.forEach { opt ->
                                            Text("• $opt", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Marking Scheme: ${q.markingRubric}", style = MaterialTheme.typography.bodySmall, color = Color.Gray, fontSize = 11.sp)
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { viewModel.discardPreviewAssessment() }, modifier = Modifier.weight(1f)) {
                                Text("Discard")
                            }
                            Button(onClick = { viewModel.savePreviewAssessment() }, modifier = Modifier.weight(1f).testTag("save_assessment_button")) {
                                Text("Save Assessment")
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Active Curriculum Assessments (${savedAssessments.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(savedAssessments) { asmt ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(asmt.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        BlockchainBadge(asmt.blockchainHash)
                    }
                    Text(
                        "${asmt.type.name} • ${asmt.questions.size} Questions • ${asmt.totalMarks} Marks • Due ${asmt.dueDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ScriptMarkerTab(
    viewModel: TeacherViewModel,
    scripts: List<ScriptSubmission>,
    activeScript: ScriptSubmission?,
    scriptResult: com.example.ai.ScriptEvaluationResult?,
    isGenerating: Boolean
) {
    if (activeScript != null) {
        // Detailed Script Marking Sheet with AI Assistor
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { viewModel.closeScriptGrading() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back to Submissions")
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                        Text(
                            text = "Max Marks: ${activeScript.maxMarks}",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Candidate: ${activeScript.studentName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Assessment: ${activeScript.assessmentTitle}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Divider()
                        Text("Exam Question:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text(activeScript.questionPrompt, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Student Written Response (Script Text):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        }
                        Text(
                            text = activeScript.studentAnswer,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = { viewModel.runAiScriptEvaluation(activeScript) },
                    enabled = !isGenerating,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("ai_mark_script_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isGenerating) "AI Marking Script & Checking Rubric..." else "Run AI Script Marker & Rubric Evaluation")
                }
            }

            if (scriptResult != null) {
                item {
                    var awardedScore by remember { mutableStateOf(scriptResult.scoreAwarded) }
                    var feedbackText by remember { mutableStateOf(scriptResult.feedback) }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, ChainVerifiedGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = ChainVerifiedGreen)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("AI Evaluation & Marking", fontWeight = FontWeight.Bold)
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = ChainVerifiedGreen.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "$awardedScore / ${activeScript.maxMarks} Marks",
                                        fontWeight = FontWeight.Bold,
                                        color = ChainVerifiedGreen,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Text("Rubric Breakdown:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            scriptResult.rubricBreakdown.forEach { r ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = ChainVerifiedGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(r, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Divider()

                            Text("Teacher & AI Feedback:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            val isTranscribing by viewModel.isTranscribing.collectAsState()
                            val transcribedText by viewModel.transcribedText.collectAsState()
                            LaunchedEffect(transcribedText) {
                                transcribedText?.let {
                                    feedbackText = if (feedbackText.isBlank()) it else "$feedbackText $it"
                                    viewModel.clearTranscribedText()
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = feedbackText,
                                    onValueChange = { feedbackText = it },
                                    modifier = Modifier.weight(1f).testTag("script_feedback_input")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                FilledTonalIconButton(
                                    onClick = { viewModel.transcribeAudioInput() },
                                    enabled = !isTranscribing,
                                    modifier = Modifier.testTag("dictate_script_feedback_button")
                                ) {
                                    if (isTranscribing) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Default.Mic, contentDescription = "Dictate with gemini-3.5-transcribe", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.confirmScriptGrading(
                                        submissionId = activeScript.id,
                                        score = awardedScore,
                                        feedback = feedbackText,
                                        rubric = scriptResult.rubricBreakdown
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ChainVerifiedGreen),
                                modifier = Modifier.fillMaxWidth().testTag("confirm_and_seal_script_button")
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Confirm Grade & Seal on EduChain Blockchain")
                            }
                        }
                    }
                }
            }
        }
    } else {
        // List of scripts
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Exam Scripts to Mark (${scripts.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${scripts.count { it.status == "PENDING" }} Pending",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(scripts) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.openScriptGrading(item) }
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.studentName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            if (item.status == "MARKED") {
                                Surface(shape = RoundedCornerShape(8.dp), color = ChainVerifiedGreen.copy(alpha = 0.15f)) {
                                    Text(
                                        "${item.scoreAwarded}/${item.maxMarks} Sealed",
                                        color = ChainVerifiedGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            } else {
                                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)) {
                                    Text(
                                        "Needs Grading",
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Text(item.assessmentTitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "\"${item.studentAnswer}\"",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BlockchainBadge(item.blockHash)
                            Button(
                                onClick = { viewModel.openScriptGrading(item) },
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(if (item.status == "MARKED") "Review Grade" else "Grade Script", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AiCopilotTab(
    viewModel: TeacherViewModel,
    messages: List<Pair<String, Boolean>>,
    isGenerating: Boolean
) {
    var queryText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EduChain Copilot powered by Gemini 3.5 Flash: Ask pedagogical queries, rubric questions, or syllabus advice.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            items(messages) { (text, isUser) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Text(
                            text = text,
                            color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = queryText,
                onValueChange = { queryText = it },
                placeholder = { Text("Ask teacher assistant co-pilot...") },
                modifier = Modifier.weight(1f).testTag("copilot_input")
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    val q = queryText
                    queryText = ""
                    viewModel.sendCopilotMessage(q)
                },
                enabled = queryText.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .testTag("copilot_send_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

@Composable
fun LiveVoiceTab(viewModel: TeacherViewModel) {
    val isConnected by viewModel.isLiveVoiceConnected.collectAsState()
    val liveMessages by viewModel.liveVoiceMessages.collectAsState()
    val isGenerating by viewModel.isAiGenerating.collectAsState()
    val isTranscribing by viewModel.isTranscribing.collectAsState()
    val transcribedText by viewModel.transcribedText.collectAsState()

    var spokenInput by remember { mutableStateOf("") }

    LaunchedEffect(transcribedText) {
        transcribedText?.let {
            spokenInput = if (spokenInput.isBlank()) it else "$spokenInput $it"
            viewModel.clearTranscribedText()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Live Voice Engine Status Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isConnected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                else MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.5.dp,
                color = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            ),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (isConnected) ChainVerifiedGreen else Color.Gray,
                            modifier = Modifier.size(12.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "gemini-3.8-live Live API",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isConnected) ChainVerifiedGreen.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (isConnected) "SESSION ACTIVE" else "STANDBY",
                            color = if (isConnected) ChainVerifiedGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "Bidirectional spoken dialogue engine for real-time pedagogy coaching, oral rubric checks, and instant lecture formulation.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.toggleLiveVoice() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isConnected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("toggle_live_voice_button")
                    ) {
                        Icon(
                            imageVector = if (isConnected) Icons.Default.CallEnd else Icons.Default.RecordVoiceOver,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isConnected) "End Live Session" else "Connect Live Voice")
                    }

                    FilledTonalButton(
                        onClick = { viewModel.transcribeAudioInput() },
                        enabled = !isTranscribing,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("live_voice_dictate_button")
                    ) {
                        if (isTranscribing) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Mic, contentDescription = "Dictate with gemini-3.5-transcribe")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Transcribe Audio", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Quick prompts suggestions
        Text(
            text = "Suggested Spoken Prompts:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val suggestions = listOf(
                "Active learning for calculus",
                "Thesis defense rubric check",
                "Explain Byzantine Consensus"
            )
            suggestions.forEach { prompt ->
                SuggestionChip(
                    onClick = {
                        spokenInput = prompt
                        if (isConnected) {
                            viewModel.sendLiveVoiceUtterance(prompt)
                            spokenInput = ""
                        }
                    },
                    label = { Text(prompt, fontSize = 11.sp, maxLines = 1) }
                )
            }
        }

        // Live Conversation Transcript Stream
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(liveMessages) { msg ->
                val isTeacher = msg.sender == "teacher"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isTeacher) Alignment.End else Alignment.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isTeacher) 16.dp else 4.dp,
                            bottomEnd = if (isTeacher) 4.dp else 16.dp
                        ),
                        color = if (isTeacher) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 2.dp,
                        modifier = Modifier.widthIn(max = 320.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isTeacher) Icons.Default.Person else Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (isTeacher) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = if (isTeacher) "Educator (Voice)" else "gemini-3.8-live",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isTeacher) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = msg.timestamp,
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Audio utterance input bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = spokenInput,
                onValueChange = { spokenInput = it },
                placeholder = { Text("Speak or type voice utterance...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("live_voice_input")
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    val text = spokenInput
                    spokenInput = ""
                    if (!isConnected) {
                        viewModel.toggleLiveVoice()
                    }
                    viewModel.sendLiveVoiceUtterance(text)
                },
                enabled = spokenInput.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .testTag("live_voice_send_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Send, contentDescription = "Send utterance", tint = Color.White)
                }
            }
        }
    }
}

