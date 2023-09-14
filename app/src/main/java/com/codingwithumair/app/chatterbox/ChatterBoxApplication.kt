package com.codingwithumair.app.chatterbox

import android.app.Application

class ChatterBoxApplication(): Application() {

	lateinit var appContainer: AppContainer

	override fun onCreate() {
		super.onCreate()
		appContainer = AppContainer(this)
	}

}