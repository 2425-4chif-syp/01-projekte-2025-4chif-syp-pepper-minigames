package com.example.mmg.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mmg.dto.MmgDto
import com.example.mmg.network.HttpInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MmgViewModel : ViewModel() {
    private val _mmgList = MutableStateFlow<List<MmgDto>>(emptyList())
    val mmgList = _mmgList.asStateFlow()

    fun loadMmgDtos() {
        viewModelScope.launch {
            val result = HttpInstance.fetchMmgDtos()
            Log.d("Result:", "$result")

            if (result != null) {
                // val enabledMmgList = result.filter { it.enabled == true }

                _mmgList.value = result //enabledMmgList
            }
        }
    }
}
