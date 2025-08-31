package com.aim.lifemate.data.models

data class PrivateJob(
    val id: String,
    val title: String,
    val company: String,
    val companyLogo: String,
    val location: String,
    val companyName: String = "",
    val salary: String,
    val jobType: String,
    val experienceRequired: String,
    val description: String,
    val requirements: String,
    val benefits: String,
    val skills: List<String>,
    val remote: Boolean,
    val applicationEmail: String,
    val postedDate: String,
    val category: String
)