package com.aim.lifemate.services

import com.aim.lifemate.data.models.GovernmentJob
import com.aim.lifemate.data.models.PrivateJob

interface JobService {
    suspend fun fetchGovernmentJobs(): List<GovernmentJob>
    suspend fun fetchPrivateJobs(): List<PrivateJob>
}