package com.aim.lifemate.di

import android.content.Context
import com.aim.lifemate.services.*
import com.aim.lifemate.utils.LanguageManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindJobService(jobServiceImpl: JobServiceImpl): JobService
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRssFeedService(): RssFeedService {
        return RssFeedService()
    }

    @Provides
    @Singleton
    fun provideJsonParserService(@ApplicationContext context: Context): JsonParserService {
        return JsonParserService(context)
    }

    @Provides
    @Singleton
    fun provideVoiceSearchService(@ApplicationContext context: Context): VoiceSearchService {
        return VoiceSearchService(context)
    }

    @Provides
    @Singleton
    fun provideLanguageManager(@ApplicationContext context: Context): LanguageManager {
        return LanguageManager(context)
    }
}