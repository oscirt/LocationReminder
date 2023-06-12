package com.example.locationreminder.di

import android.content.Context
import com.example.data.storage.LocationClient
import com.example.data.storage.location.DefaultLocationClient
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    fun provideAuthUIInstance() = AuthUI.getInstance()

    @Provides
    fun provideLocationClient(
        @ApplicationContext context: Context,
        client: FusedLocationProviderClient
    ) : LocationClient {
        return DefaultLocationClient(context = context, client = client)
    }
}