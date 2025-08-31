package com.aim.lifemate.services

import com.aim.lifemate.data.models.GovernmentJob
import com.aim.lifemate.data.models.PrivateJob
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobServiceImpl @Inject constructor(
    private val rssFeedService: RssFeedService
) : JobService {

    override suspend fun fetchGovernmentJobs(): List<GovernmentJob> {
        return rssFeedService.fetchGovernmentJobs()
    }

    override suspend fun fetchPrivateJobs(): List<PrivateJob> {
        return rssFeedService.fetchPrivateJobs()
    }
}