package com.example.di

import com.example.dao.user.UserDao
import com.example.dao.user.UserDaoImpl
import com.example.repository.user.UserRepository
import com.example.repository.user.UserRepositoryImpl
import org.koin.dsl.module

val appModule = module {
    single<UserRepository> { UserRepositoryImpl(get())}
    single<UserDao> { UserDaoImpl()}
}