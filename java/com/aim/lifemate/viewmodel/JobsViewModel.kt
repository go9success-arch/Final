package com.aim.lifemate.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aim.lifemate.data.models.GovernmentJob
import com.aim.lifemate.data.models.PrivateJob
import com.aim.lifemate.services.JobService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val jobService: JobService
) : ViewModel() {

    // Use separate mutable and immutable state
    private val _state = mutableStateOf(JobsState())
    val state: State<JobsState> get() = _state

    private var governmentJobsJob: kotlinx.coroutines.Job? = null
    private var privateJobsJob: kotlinx.coroutines.Job? = null
    private var searchJob: kotlinx.coroutines.Job? = null

    init {
        loadAllJobs()
    }

    fun loadAllJobs() {
        loadGovernmentJobs()
        loadPrivateJobs()
    }

    fun loadGovernmentJobs() {
        governmentJobsJob?.cancel()
        governmentJobsJob = viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoadingGovtJobs = true,
                error = null
            )
            try {
                val jobs = jobService.fetchGovernmentJobs()
                _state.value = _state.value.copy(
                    governmentJobs = jobs,
                    filteredGovernmentJobs = jobs,
                    isLoadingGovtJobs = false,
                    lastUpdated = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to load government jobs. Please check your internet connection.",
                    isLoadingGovtJobs = false
                )
            }
        }
    }

    fun loadPrivateJobs() {
        privateJobsJob?.cancel()
        privateJobsJob = viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoadingPrivateJobs = true,
                error = null
            )
            try {
                val jobs = jobService.fetchPrivateJobs()
                _state.value = _state.value.copy(
                    privateJobs = jobs,
                    filteredPrivateJobs = jobs,
                    isLoadingPrivateJobs = false,
                    lastUpdated = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to load private jobs. Please check your internet connection.",
                    isLoadingPrivateJobs = false
                )
            }
        }
    }

    fun setSelectedTab(tab: JobTab) {
        _state.value = _state.value.copy(selectedTab = tab)
        applySearchFilter(_state.value.searchQuery)
    }

    fun searchJobs(query: String) {
        _state.value = _state.value.copy(searchQuery = query)

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce search
            applySearchFilter(query)
        }
    }

    private fun applySearchFilter(query: String) {
        val trimmedQuery = query.trim().lowercase()

        if (trimmedQuery.isEmpty()) {
            _state.value = _state.value.copy(
                filteredGovernmentJobs = _state.value.governmentJobs,
                filteredPrivateJobs = _state.value.privateJobs
            )
            return
        }

        val filteredGovtJobs = _state.value.governmentJobs.filter { job ->
            job.title.lowercase().contains(trimmedQuery) ||
                    job.department.lowercase().contains(trimmedQuery) ||
                    job.organization.lowercase().contains(trimmedQuery) ||
                    job.location.lowercase().contains(trimmedQuery)
        }

        val filteredPrivateJobs = _state.value.privateJobs.filter { job ->
            job.title.lowercase().contains(trimmedQuery) ||
                    job.company.lowercase().contains(trimmedQuery) ||
                    job.location.lowercase().contains(trimmedQuery) ||
                    job.category.lowercase().contains(trimmedQuery)
        }

        _state.value = _state.value.copy(
            filteredGovernmentJobs = filteredGovtJobs,
            filteredPrivateJobs = filteredPrivateJobs
        )
    }

    fun clearSearch() {
        _state.value = _state.value.copy(
            searchQuery = "",
            filteredGovernmentJobs = _state.value.governmentJobs,
            filteredPrivateJobs = _state.value.privateJobs
        )
    }

    fun refreshAllJobs() {
        loadGovernmentJobs()
        loadPrivateJobs()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun getCurrentJobs(): List<Any> {
        return when (_state.value.selectedTab) {
            JobTab.GOVERNMENT -> _state.value.filteredGovernmentJobs
            JobTab.PRIVATE -> _state.value.filteredPrivateJobs
        }
    }

    fun isLoading(): Boolean {
        return _state.value.isLoadingGovtJobs || _state.value.isLoadingPrivateJobs
    }

    override fun onCleared() {
        super.onCleared()
        governmentJobsJob?.cancel()
        privateJobsJob?.cancel()
        searchJob?.cancel()
    }
}

data class JobsState(
    val governmentJobs: List<GovernmentJob> = emptyList(),
    val privateJobs: List<PrivateJob> = emptyList(),
    val filteredGovernmentJobs: List<GovernmentJob> = emptyList(),
    val filteredPrivateJobs: List<PrivateJob> = emptyList(),
    val selectedTab: JobTab = JobTab.GOVERNMENT,
    val searchQuery: String = "",
    val isLoadingGovtJobs: Boolean = false,
    val isLoadingPrivateJobs: Boolean = false,
    val error: String? = null,
    val lastUpdated: Long = 0
)

enum class JobTab {
    GOVERNMENT, PRIVATE
}