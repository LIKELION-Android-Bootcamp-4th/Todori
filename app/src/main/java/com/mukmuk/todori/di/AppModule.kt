package com.mukmuk.todori.di

import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.repository.GoalRepository
import com.mukmuk.todori.data.repository.StudyRepository
import com.mukmuk.todori.data.repository.TodoCategoryRepository
import com.mukmuk.todori.data.repository.TodoRepository
import com.mukmuk.todori.data.repository.UserRepository
import com.mukmuk.todori.data.service.GoalService
import com.mukmuk.todori.data.service.StudyService
import com.mukmuk.todori.data.service.TodoCategoryService
import com.mukmuk.todori.data.service.TodoService
import com.mukmuk.todori.data.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

}
