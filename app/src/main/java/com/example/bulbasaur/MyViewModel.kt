package com.example.bulbasaur

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MyViewModel: ViewModel() {
    // una variable para almacenar el estado de la aplicación
    private var _appState = MutableLiveData<String>()
    val items = MutableLiveData<MutableList<Item>>()

    val appState: LiveData<String>
    get() = _appState

    // una función para actualizar el estado de la aplicación
    fun updateAppState(newState: String) {
        _appState.value = newState
    }
}