package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.ai.GroundedSearchResult
import com.example.ai.MapsGroundingResult
import com.example.ai.AcademicVenue
import com.example.model.CurriculumModel
import com.example.model.SyllabusUnit
import com.example.model.Textbook
import com.example.model.TextbookChapter
import com.example.ui.theme.ChainVerifiedGreen
import com.example.ui.viewmodel.TeacherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurriculumScreen(viewModel: TeacherViewModel) {
    val curricula by viewModel.curricula.collectAsState()
    val textbooks by viewModel.textbooks.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Curriculum & Syllabi, 1: Digital Textbooks, 2: Search Grounding, 3: Maps Field Trips
    var selectedCurriculumId by remember { mutableStateOf(curricula.firstOrNull()?.id ?: "") }
    var selectedTextbookId by remember { mutableStateOf(textbooks.firstOrNull()?.id ?: "") }
    var selectedChapter by remember { mutableStateOf<TextbookChapter?>(null) }

    val activeCurriculum = curricula.find { it.id == selectedCurriculumId } ?: curricula.first()
    val activeTextbook = textbooks.find { it.id == selectedTextbookId } ?: textbooks.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab header
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("Curricula & Syllabi", fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.AccountTree, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("curriculum_tab_syllabi")
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("Digital Textbooks (${textbooks.size})", fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("curriculum_tab_textbooks")
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("Search Grounding", fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("curriculum_tab_search")
            )
            Tab(
                selected = activeTab == 3,
                onClick = { activeTab = 3 },
                text = { Text("Maps Field Trips", fontWeight = if (activeTab == 3) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("curriculum_tab_maps")
            )
        }

        when (activeTab) {
            0 -> {
                // Curriculum & Syllabi View
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Curriculum selector chips
                item {
                    Text(
                        text = "Standard Educational Frameworks",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(curricula) { cur ->
                            FilterChip(
                                selected = cur.id == activeCurriculum.id,
                                onClick = { selectedCurriculumId = cur.id },
                                label = { Text(cur.name.split("(").first().trim(), maxLines = 1) }
                            )
                        }
                    }
                }

                // Curriculum Overview Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = activeCurriculum.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                    Text(
                                        text = activeCurriculum.region,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Text(
                                text = activeCurriculum.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text("Covered Subjects & Codes:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                activeCurriculum.subjects.take(3).forEach { subj ->
                                    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                        Text(subj, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Syllabus Breakdown & Learning Units (${activeCurriculum.units.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(activeCurriculum.units) { unit ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Unit ${unit.unitNumber}: ${unit.title}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)) {
                                    Text(
                                        text = unit.examWeightage,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Text(unit.description, style = MaterialTheme.typography.bodySmall)

                            Text("Core Learning Outcomes:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            unit.keyLearningOutcomes.forEach { outcome ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(outcome, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.selectTab(0)
                                        viewModel.selectAiSubTab(0)
                                        viewModel.generateLessonNote(
                                            topic = unit.title,
                                            subject = activeCurriculum.subjects.firstOrNull() ?: "General Science",
                                            gradeLevel = "High School / Pre-U",
                                            curriculum = activeCurriculum.name
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Generate Lesson Plan", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.selectTab(0)
                                        viewModel.selectAiSubTab(1)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Create Exam Quiz", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
            }
            1 -> {
                // Digital Textbooks Reader View
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Textbook Selector
                item {
                    Text(
                        text = "Curriculum Textbooks & Reference Library",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(textbooks) { book ->
                            Card(
                                onClick = {
                                    selectedTextbookId = book.id
                                    selectedChapter = null
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (book.id == activeTextbook.id)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                ),
                                border = if (book.id == activeTextbook.id)
                                    androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                else null,
                                modifier = Modifier.width(220.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = book.subject,
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = book.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = book.edition,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Active Textbook Header
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = activeTextbook.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Authors: ${activeTextbook.authors}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Curriculum: ${activeTextbook.curriculum} • ${activeTextbook.edition}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Chapter list or reader
                if (selectedChapter != null) {
                    val chap = selectedChapter!!
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = { selectedChapter = null }) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("All Chapters")
                                    }
                                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                        Text(
                                            text = "Chapter ${chap.chapterNumber}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = chap.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Text("Chapter Summary:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                Text(chap.summary, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)

                                Text("Core Theorems & Axioms:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                chap.coreTheories.forEach { theory ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(theory, style = MaterialTheme.typography.bodySmall)
                                    }
                                }

                                Text("Curated Exam Exercises:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                chap.sampleExercises.forEachIndexed { idx, ex ->
                                    Text("${idx + 1}. $ex", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Button(
                                    onClick = {
                                        viewModel.selectTab(0)
                                        viewModel.selectAiSubTab(0)
                                        viewModel.generateLessonNote(
                                            topic = chap.title,
                                            subject = activeTextbook.subject,
                                            gradeLevel = "Advanced High School",
                                            curriculum = activeTextbook.curriculum
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("generate_lesson_from_chapter")
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Synthesize AI Lesson Plan for this Chapter")
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "Interactive Chapters (${activeTextbook.chapters.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(activeTextbook.chapters) { ch ->
                        Card(
                            onClick = { selectedChapter = ch },
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
                                    Text(
                                        text = "Chapter ${ch.chapterNumber}: ${ch.title}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                                }
                                Text(
                                    text = ch.summary,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            }
            2 -> SearchGroundingTab(viewModel)
            3 -> MapsGroundingTab(viewModel)
        }
    }
}

@Composable
fun SearchGroundingTab(viewModel: TeacherViewModel) {
    val searchResult by viewModel.groundedSearchResult.collectAsState()
    val isGenerating by viewModel.isAiGenerating.collectAsState()
    var queryText by remember { mutableStateOf("Cambridge CAIE 9618 Computer Science 2026 syllabus updates") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
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
                            Icon(Icons.Default.TravelExplore, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Live Curriculum Search Grounding",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "gemini-3.8-flash + googleSearch",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "Queries Google Search Grounding to fetch the latest official exam board releases, revised rubric weightages, and accredited syllabus updates.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        label = { Text("Curriculum or Syllabus Query") },
                        modifier = Modifier.fillMaxWidth().testTag("curriculum_search_input")
                    )

                    // Suggestion Chips
                    Text(
                        text = "Suggested Curriculum Inquiries:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val suggestions = listOf(
                            "Cambridge CAIE 9618",
                            "IB DP Physics 2026",
                            "AP Computer Science"
                        )
                        suggestions.forEach { prompt ->
                            SuggestionChip(
                                onClick = {
                                    queryText = "$prompt syllabus updates and assessment criteria"
                                    viewModel.searchCurriculumWithGoogle(queryText)
                                },
                                label = { Text(prompt, fontSize = 11.sp) }
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.searchCurriculumWithGoogle(queryText) },
                        enabled = !isGenerating && queryText.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("search_curriculum_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Grounding with Google Search...")
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retrieve Grounded Syllabus Data")
                        }
                    }
                }
            }
        }

        if (searchResult != null) {
            val result = searchResult!!

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = ChainVerifiedGreen)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Grounded Intelligence", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            }
                            TextButton(onClick = { viewModel.clearGroundedSearchResult() }) {
                                Text("Clear")
                            }
                        }

                        if (result.searchQueries.isNotEmpty()) {
                            Text("Google Search Queries Executed:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                result.searchQueries.forEach { q ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "\"$q\"",
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Divider()

                        Text("Syllabus Synthesis & Updates:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = result.text,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )

                        if (result.sources.isNotEmpty()) {
                            Divider()
                            Text("Grounding Citations & Web Sources:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            result.sources.forEach { source ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Link, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        Column {
                                            Text(source.title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                            Text(source.url, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapsGroundingTab(viewModel: TeacherViewModel) {
    val mapsResult by viewModel.mapsGroundingResult.collectAsState()
    val isGenerating by viewModel.isAiGenerating.collectAsState()

    var destinationQuery by remember { mutableStateOf("Science museums and STEM labs for academic field trips") }
    var regionQuery by remember { mutableStateOf("London & Cambridge Region") }
    var itineraryFeedback by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
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
                            Icon(Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Educational Maps Grounding",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "gemini-3.8-flash + googleMaps",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "Grounds queries in real geographic locations using Google Maps data. Discover curriculum-aligned field trips, STEM laboratories, and accredited exam test centers.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = destinationQuery,
                        onValueChange = { destinationQuery = it },
                        label = { Text("Venue / Activity Type") },
                        modifier = Modifier.fillMaxWidth().testTag("maps_destination_input")
                    )

                    OutlinedTextField(
                        value = regionQuery,
                        onValueChange = { regionQuery = it },
                        label = { Text("Geographic Location / City") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Suggestion Chips
                    Text(
                        text = "Academic Destination Categories:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val suggestions = listOf(
                            "Science Museums",
                            "Exam Test Centers",
                            "STEM Robotics Labs"
                        )
                        suggestions.forEach { prompt ->
                            SuggestionChip(
                                onClick = {
                                    destinationQuery = prompt
                                    viewModel.searchVenuesWithMaps(destinationQuery, regionQuery)
                                },
                                label = { Text(prompt, fontSize = 11.sp) }
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.searchVenuesWithMaps(destinationQuery, regionQuery) },
                        enabled = !isGenerating && destinationQuery.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("search_maps_venues_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Grounding with Google Maps...")
                        } else {
                            Icon(Icons.Default.Place, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Locate Verified Academic Venues")
                        }
                    }
                }
            }
        }

        if (itineraryFeedback != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ChainVerifiedGreen.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ChainVerifiedGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = itineraryFeedback!!,
                            color = ChainVerifiedGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        if (mapsResult != null) {
            val result = mapsResult!!

            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Itinerary & Pedagogical Overview", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            TextButton(onClick = { viewModel.clearMapsGroundingResult() }) {
                                Text("Clear")
                            }
                        }
                        Text(
                            text = result.summary,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Grounded Locations (${result.venues.size} Venues)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            items(result.venues) { venue ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = venue.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(venue.rating, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = venue.category,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(venue.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }

                        Text(
                            text = venue.description,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text("Recommended Activities:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = venue.recommendedActivities,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = {
                                itineraryFeedback = "Added ${venue.name} to class field trip itinerary & sealed on blockchain!"
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AddLocationAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add to Class Trip Itinerary")
                        }
                    }
                }
            }
        }
    }
}

