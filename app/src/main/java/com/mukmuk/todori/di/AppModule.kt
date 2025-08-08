package com.mukmuk.todori.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.local.datastore.HomeSettingRepository
import com.mukmuk.todori.data.repository.GoalRepository
import com.mukmuk.todori.data.repository.GoalTodoRepository
import com.mukmuk.todori.data.repository.HomeRepository
import com.mukmuk.todori.data.repository.StudyRepository
import com.mukmuk.todori.data.repository.TodoCategoryRepository
import com.mukmuk.todori.data.repository.TodoRepository
import com.mukmuk.todori.data.repository.UserRepository
import com.mukmuk.todori.data.service.GoalService
import com.mukmuk.todori.data.service.GoalTodoService
import com.mukmuk.todori.data.service.HomeService
import com.mukmuk.todori.data.service.StudyService
import com.mukmuk.todori.data.service.TodoCategoryService
import com.mukmuk.todori.data.service.TodoService
import com.mukmuk.todori.data.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Firestore 인스턴스
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    // TodoCategoryService
    @Provides
    @Singleton
    fun provideTodoCategoryService(
        firestore: FirebaseFirestore
    ): TodoCategoryService = TodoCategoryService(firestore)

    @Provides
    @Singleton
    fun provideTodoCategoryRepository(
        todoCategoryService: TodoCategoryService
    ): TodoCategoryRepository = TodoCategoryRepository(todoCategoryService)

    @Provides
    @Singleton
    fun provideStudyService(
        firestore: FirebaseFirestore
    ): StudyService = StudyService(firestore)

    @Provides
    @Singleton
    fun provideStudyRepository(
        studyService: StudyService
    ): StudyRepository = StudyRepository(studyService)

    // MyPageProfile
    @Provides
    @Singleton
    fun provideUserService(
        firestore: FirebaseFirestore
    ): UserService = UserService(firestore)

    @Provides
    @Singleton
    fun provideUserRepository(
        userService: UserService
    ): UserRepository = UserRepository(userService)
    
    @Provides
    @Singleton
    fun provideGoalService(firestore: FirebaseFirestore): GoalService =
        GoalService(firestore)

    @Provides
    @Singleton
    fun provideGoalRepository(goalService: GoalService): GoalRepository =
        GoalRepository(goalService)

    @Provides
    @Singleton
    fun provideTodoService(firestore: FirebaseFirestore): TodoService =
        TodoService(firestore)

    @Provides
    @Singleton
    fun provideTodoRepository(todoService: TodoService): TodoRepository =
        TodoRepository(todoService)

    @Provides
    @Singleton
    fun provideGoalTodoService(firestore: FirebaseFirestore): GoalTodoService =
        GoalTodoService(firestore)

    @Provides
    @Singleton
    fun provideGoalTodoRepository(goalTodoService: GoalTodoService): GoalTodoRepository =
        GoalTodoRepository(goalTodoService)

    @Provides
    @Singleton
    fun provideHomeService(firestore: FirebaseFirestore): HomeService =
        HomeService(firestore)

    @Provides
    @Singleton
    fun provideHomeRepository(homeService: HomeService): HomeRepository =
        HomeRepository(homeService)

    @Provides
    @Singleton
    fun provideHomeSettingRepository(@ApplicationContext context: Context): HomeSettingRepository {
        return HomeSettingRepository(context)
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "todori_prefs")
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.dataStore

}
