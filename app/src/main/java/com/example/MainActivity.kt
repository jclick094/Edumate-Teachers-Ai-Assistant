package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.AuthManager
import com.example.data.FirestoreTeacherRepository
import com.example.data.TeacherRepository
import com.example.ui.components.EduChainTopBar
import com.example.ui.components.VerificationDialog
import com.example.ui.screens.*
import com.example.ui.theme.EduChainTheme
import com.example.ui.viewmodel.TeacherViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EduChainTheme {
                EduChainRoot()
            }
        }
    }
}

@Composable
fun EduChainRoot() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentUser by remember { mutableStateOf(AuthManager.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        AuthManager.auth.addAuthStateListener(listener)
        onDispose {
            AuthManager.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null) {
        AuthScreen(
            onAuthSuccess = { user ->
                currentUser = user
            }
        )
    } else {
        val user = currentUser!!
        val firestoreRepo = remember(user.uid) {
            try {
                FirestoreTeacherRepository(context)
            } catch (e: Exception) {
                null
            }
        }

        val viewModel: TeacherViewModel = viewModel(
            key = user.uid,
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val vm = TeacherViewModel(
                        repository = TeacherRepository(),
                        firestoreRepository = firestoreRepo
                    )
                    vm.initUserSession(user)
                    return vm as T
                }
            }
        )

        EduChainApp(
            viewModel = viewModel,
            currentUser = user,
            onSignOut = {
                val credentialManager = CredentialManager.create(context)
                AuthManager.signOut(
                    context = context,
                    credentialManager = credentialManager,
                    onSignOutComplete = {
                        currentUser = null
                    },
                    scope = coroutineScope
                )
            }
        )
    }
}

@Composable
fun EduChainApp(
    viewModel: TeacherViewModel,
    currentUser: FirebaseUser? = null,
    onSignOut: (() -> Unit)? = null
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val currentClass by viewModel.currentClass.collectAsState()
    val activeTier by viewModel.activeTier.collectAsState()
    val aiQuotaUsed by viewModel.aiQuotaUsed.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val verificationResult by viewModel.verificationResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    BackHandler(enabled = selectedTab != 0) {
        viewModel.selectTab(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            EduChainTopBar(
                currentClass = currentClass,
                availableClasses = viewModel.availableClasses,
                onSelectClass = { viewModel.switchClass(it) },
                activeTier = activeTier,
                aiQuotaUsed = aiQuotaUsed,
                onSubscriptionClick = { viewModel.selectTab(4) },
                userEmail = currentUser?.email,
                onSignOut = onSignOut
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                val navItems = listOf(
                    Triple("AI Studio", Icons.Default.AutoAwesome, "tab_ai_studio"),
                    Triple("Attendance", Icons.Default.CoPresent, "tab_attendance"),
                    Triple("Gradebook", Icons.Default.Analytics, "tab_gradebook"),
                    Triple("Curriculum", Icons.Default.MenuBook, "tab_curriculum"),
                    Triple("Ledger & Plans", Icons.Default.Token, "tab_blockchain")
                )

                navItems.forEachIndexed { index, (label, icon, tag) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        modifier = Modifier.testTag(tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> AiStudioScreen(viewModel = viewModel)
                1 -> AttendanceScreen(viewModel = viewModel)
                2 -> GradebookScreen(viewModel = viewModel)
                3 -> CurriculumScreen(viewModel = viewModel)
                4 -> BlockchainAndSubscriptionScreen(viewModel = viewModel)
            }
        }
    }

    verificationResult?.let { result ->
        VerificationDialog(
            result = result,
            onDismiss = { viewModel.dismissVerificationModal() }
        )
    }
}
