package pl.wsei.pam.lab06

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import pl.wsei.pam.lab06.data.AppContainer
import pl.wsei.pam.lab06.data.AppViewModelProvider
import pl.wsei.pam.lab06.data.FormViewModel
import pl.wsei.pam.lab06.data.ListViewModel
import pl.wsei.pam.lab06.data.LocalDateConverter
import pl.wsei.pam.lab06.data.TodoApplication
import pl.wsei.pam.lab06.data.TodoTaskForm
import pl.wsei.pam.lab06.data.TodoTaskUiState
import pl.wsei.pam.lab06.ui.theme.Lab01Theme

const val notificationID = 121
const val channelID = "Lab06 channel"
const val titleExtra = "title"
const val messageExtra = "message"

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var container: AppContainer
    }

    fun scheduleAlarm(time: Long) {
        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java)
        intent.putExtra(titleExtra, "Deadline")
        intent.putExtra(messageExtra, "Zbliża się termin zakończenia zadania")

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    private fun createNotificationChannel() {
        val name = "Lab06 channel"
        val descriptionText = "Lab06 is channel for notifications for approaching tasks."
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel()
        container = (this.application as TodoApplication).container
        setContent {
            Lab01Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController,
    title: String,
    showBackIcon: Boolean,
    route: String,
    onSaveClick: () -> Unit = { }
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = { Text(text = title) },
        navigationIcon = {
            if (showBackIcon) {
                IconButton(onClick = { navController.navigate(route) }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (route !== "form") {
                OutlinedButton(
                    onClick = onSaveClick
                )
                {
                    Text(
                        text = "Zapisz",
                        fontSize = 18.sp
                    )
                }
            } else {
                IconButton(onClick = {
                    MainActivity.container.notificationHandler.showSimpleNotification()
                }) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "")
                }
            }
        }
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Lab01Theme {
        Greeting("Android")
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Lab01Theme {
        MainScreen(
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val postNotificationPermission =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(key1 = true) {
        if (!postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }
    NavHost(navController = navController, startDestination = "list") {
        composable("list") { ListScreen(navController = navController) }
        composable("form") { FormScreen(navController = navController) }
    }
}

enum class Priority() {
    High, Medium, Low
}

@Composable
fun ListItem(item: pl.wsei.pam.lab06.data.TodoTask, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(120.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        //dodaj pozostałe funkcje tworzące komponenty z danymi elementu listy
        Row {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Tytuł",

                    )
                Text(
                    text = item.title, fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Priorytet",
                )
                Text(
                    text = item.priority.toString(), fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(text = "Deadline")
                Text(
                    text = item.deadline.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Icon(
            imageVector = if (item.isDone) Icons.Filled.Check else Icons.Filled.Clear,
            contentDescription = "Status",
            modifier = Modifier
                .scale(2f)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp)
        )


    }
}

@Composable
fun ListScreen(
    navController: NavController,
    viewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val listUiState by viewModel.listUiState.collectAsState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add task",
                        modifier = Modifier.scale(1.5f)
                    )
                },
                onClick = {
                    navController.navigate("form")
                }
            )
        },
        topBar = {
            AppTopBar(
                navController = navController,
                title = "List",
                showBackIcon = false,
                route = "form"
            )
        },
        content = { it ->
            LazyColumn(
                modifier = Modifier.padding(it)
            ) {
                items(items = listUiState.items, key = { it.id }) {
                    ListItem(it)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTaskInputForm(
    item: TodoTaskForm,
    modifier: Modifier = Modifier,
    onValueChange: (TodoTaskForm) -> Unit = {},
//    enabled: Boolean = true
) {
    val priorities = Priority.entries.toList()

    Column(modifier = modifier) {

        // Tytuł
        Text("Tytuł zadania", fontWeight = FontWeight.Bold)
        TextField(
            value = item.title,
            onValueChange = { onValueChange(item.copy(title = it)) },
            modifier = Modifier
        )

        // Deadline
        val datePickerState = rememberDatePickerState(
            initialDisplayMode = DisplayMode.Picker,
            yearRange = IntRange(2000, 2030),
            initialSelectedDateMillis = item.deadline,
        )
        var showDialog by remember { mutableStateOf(false) }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true },
            text = "Deadline: ${
                LocalDateConverter.fromMillis(item.deadline)
            }",
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,

            )
        if (item.Error?.isNotBlank() == true) {
            Text(text = item.Error, color = MaterialTheme.colorScheme.error, fontSize = 22.sp)
        }
        if (showDialog) {
            DatePickerDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        onValueChange(item.copy(deadline = datePickerState.selectedDateMillis!!))
                    }) {
                        Text("Pick")
                    }
                }
            ) {
                DatePicker(state = datePickerState, showModeToggle = true)
            }
        }

        // isDone
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isDone,
                onCheckedChange = { onValueChange(item.copy(isDone = it)) }
            )
            Text("Ukończone")
        }

        // Priorytet
        Text("Priorytet", fontWeight = FontWeight.Bold)
        priorities.forEach { priority ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onValueChange(item.copy(priority = priority.name)) }
            ) {
                RadioButton(
                    selected = item.priority == priority.name,
                    onClick = { onValueChange(item.copy(priority = priority.name)) }
                )
                Text(priority.name)
            }
        }
    }
}

@Composable
fun TodoTaskInputBody(
    todoUiState: TodoTaskUiState,
    onItemValueChange: (TodoTaskForm) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
//        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TodoTaskInputForm(
            item = todoUiState.todoTask,
            onValueChange = onItemValueChange,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    navController: NavController,
    viewModel: FormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            AppTopBar(
                navController = navController,
                title = "Form",
                showBackIcon = true,
                route = "list",
                onSaveClick = {
                    if (viewModel.validate()) {
                        coroutineScope.launch {
                            viewModel.save()
                            navController.navigate("list")
                        }
                    }
                }
            )
        }
    )
    { paddingValues ->
        TodoTaskInputBody(
            todoUiState = viewModel.todoTaskUiState,
            onItemValueChange = viewModel::updateUiState,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FormScreen(navController: NavController) {
//    val datePickerState = rememberDatePickerState()
//    val showDialog = rememberSaveable { mutableStateOf(false) }
//    var title by rememberSaveable { mutableStateOf("") }
////    var priority by rememberSaveable { mutableStateOf("") }
//    var isDone by rememberSaveable { mutableStateOf(false) }
//
//    val priorities = listOf("Low", "Medium", "High")
//    var selectedPriority by rememberSaveable { mutableStateOf(priorities[0]) }
//    var expanded by remember { mutableStateOf(false) }
//    if (showDialog.value) {
//        DatePickerDialog(
//            onDismissRequest = { showDialog.value = false },
//            confirmButton = {
//                TextButton(onClick = { showDialog.value = false }) {
//                    Text("Ok")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDialog.value = false }) {
//                    Text("Cancel")
//                }
//            }
//        ) {
//            DatePicker(state = datePickerState)
//        }
//    }
//    Scaffold(
//        topBar = {
//            AppTopBar(
//                navController = navController,
//                title = "Form",
//                showBackIcon = true,
//                route = "list"
//            )
//        },
//        content = {
//            LazyColumn(modifier = Modifier.padding(it)) {
//                item() {
//                    Text("Tytuł")
//                    TextField(
//                        value = title,
//                        onValueChange = { title = it },
//                        modifier = Modifier.fillMaxWidth(),
//                        singleLine = true
//                    )
//                }
//                item() {
//
//
//                    Text("Priorytet")
//
//                    ExposedDropdownMenuBox(
//                        expanded = expanded,
//                        onExpandedChange = { expanded = !expanded }
//                    ) {
//                        OutlinedTextField(
//                            value = selectedPriority,
//                            onValueChange = {},
//                            readOnly = true,
//                            modifier = Modifier
//                                .menuAnchor()
//                                .fillMaxWidth(),
//                            trailingIcon = {
//                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
//                            }
//                        )
//
//                        ExposedDropdownMenu(
//                            expanded = expanded,
//                            onDismissRequest = { expanded = false }
//                        ) {
//                            priorities.forEach { priority ->
//                                DropdownMenuItem(
//                                    text = { Text(priority) },
//                                    onClick = {
//                                        selectedPriority = priority
//                                        expanded = false
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//                item() {
//                    Text("isDone")
//                    Checkbox(
//                        checked = isDone,
//                        onCheckedChange = { isDone = it }
//                    )
//                }
//
//                val selectedDateText = datePickerState.selectedDateMillis?.let {
//                    Instant.ofEpochMilli(it)
//                        .atZone(ZoneId.systemDefault())
//                        .toLocalDate()
//                        .toString()
//                } ?: "Select date"
//                item {
//                    Text("Deadline")
//
//                    OutlinedButton(
//                        onClick = { showDialog.value = true },
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text(selectedDateText)
//                    }
//                }
//
//            }
//        }
//    )
//}