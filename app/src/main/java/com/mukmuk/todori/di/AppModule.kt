package com.mukmuk.todori.di

import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.repository.UserRepository
import com.mukmuk.todori.data.repository.TodoCategoryRepository
import com.mukmuk.todori.data.service.TodoCategoryService
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

}
