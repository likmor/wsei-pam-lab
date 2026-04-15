package pl.wsei.pam.lab06.data

import android.content.Context
import pl.wsei.pam.lab06.NotificationHandler
import java.time.LocalDate

interface CurrentDateProvider {
    val currentDate: LocalDate
}

class RealDateProvider : CurrentDateProvider {
    override val currentDate: LocalDate = LocalDate.now()
}

interface AppContainer {
    val todoTaskRepository: TodoTaskRepository
    val dateProvider: CurrentDateProvider
    val notificationHandler: NotificationHandler
}

class AppDataContainer(private val context: Context): AppContainer{
    override val todoTaskRepository: TodoTaskRepository by lazy{
        DatabaseTodoTaskRepository(AppDatabase.getInstance(context).taskDao())
    }
    override val dateProvider: CurrentDateProvider by lazy {
        RealDateProvider()
    }
    override val notificationHandler: NotificationHandler by lazy {
        NotificationHandler(context)
    }
}