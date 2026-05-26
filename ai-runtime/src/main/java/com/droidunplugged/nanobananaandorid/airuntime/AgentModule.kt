package com.droidunplugged.nanobananaandorid.airuntime

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FakeNano

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RealNano

@Module
@InstallIn(SingletonComponent::class)
object AgentModule {

    @Provides
    @Singleton
    @FakeNano
    fun provideFakeGeminiNanoClient(): GeminiNanoClient {
        return FakeGeminiNanoClient()
    }

    @Provides
    @Singleton
    @RealNano
    fun provideRealGeminiNanoClient(
        @ApplicationContext context: Context
    ): GeminiNanoClient {
        return RealGeminiNanoClient(context)
    }
}
