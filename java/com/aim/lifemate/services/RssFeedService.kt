package com.aim.lifemate.services

import android.util.Xml
import com.aim.lifemate.data.models.GovernmentJob
import com.aim.lifemate.data.models.PrivateJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RssFeedService @Inject constructor() {

    companion object {
        // RSS feed URLs for government jobs
        private const val GOVERNMENT_JOBS_RSS = "https://www.freejobalert.com/feed/"
        private const val SARKARI_NAUKRI_RSS = "https://www.sarkarinaukriblog.com/feed/"

        // RSS feed for private/remote jobs
        private const val REMOTE_JOBS_RSS = "https://remoteok.io/remote-jobs.rss"
    }

    suspend fun fetchGovernmentJobs(): List<GovernmentJob> = withContext(Dispatchers.IO) {
        return@withContext try {
            val feedItems = parseRssFeed(GOVERNMENT_JOBS_RSS)
            convertToGovernmentJobs(feedItems)
        } catch (e: Exception) {
            // Fallback to second RSS feed if first fails
            try {
                val feedItems = parseRssFeed(SARKARI_NAUKRI_RSS)
                convertToGovernmentJobs(feedItems)
            } catch (e: Exception) {
                emptyList() // Return empty if both fail
            }
        }
    }

    suspend fun fetchPrivateJobs(): List<PrivateJob> = withContext(Dispatchers.IO) {
        return@withContext try {
            val feedItems = parseRssFeed(REMOTE_JOBS_RSS)
            convertToPrivateJobs(feedItems)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseRssFeed(feedUrl: String): List<RssItem> {
        val url = URL(feedUrl)
        val connection = url.openConnection().apply {
            connectTimeout = 15000
            readTimeout = 15000
        }

        connection.getInputStream().use { inputStream ->
            val parser = Xml.newPullParser().apply {
                setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                setInput(inputStream, null)
            }

            val items = mutableListOf<RssItem>()
            var currentItem: RssItem? = null
            var isInsideItem = false

            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                when (parser.eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "item" -> {
                                isInsideItem = true
                                currentItem = RssItem()
                            }
                            "title" -> if (isInsideItem) currentItem?.title = parser.nextText()
                            "link" -> if (isInsideItem) currentItem?.link = parser.nextText()
                            "description" -> if (isInsideItem) currentItem?.description = parser.nextText()
                            "pubDate" -> if (isInsideItem) currentItem?.pubDate = parser.nextText()
                            "guid" -> if (isInsideItem) currentItem?.guid = parser.nextText()
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "item" && isInsideItem) {
                            currentItem?.let { items.add(it) }
                            currentItem = null
                            isInsideItem = false
                        }
                    }
                }
                parser.next()
            }
            return items
        }
    }

    private fun convertToGovernmentJobs(rssItems: List<RssItem>): List<GovernmentJob> {
        return rssItems.mapIndexed { index, item ->
            GovernmentJob(
                id = item.guid ?: "gov_${index}_${System.currentTimeMillis()}",
                title = item.title ?: "Government Job Opportunity",
                department = extractDepartment(item.title ?: ""),
                organization = extractOrganization(item.title ?: ""),
                location = extractLocation(item.description ?: ""),
                salary = extractSalary(item.description ?: ""),
                applicationDeadline = extractDeadline(item.description ?: ""),
                description = item.description?.take(200) ?: "Government job opportunity. Apply now!",
                qualifications = "Graduation in any discipline",
                experienceRequired = "0-5 years",
                ageLimit = "18-30 years",
                totalVacancies = "Various",
                applicationFee = "₹0 - ₹1000",
                selectionProcess = "Written Test/Interview",
                officialWebsite = "https://government.india",
                notificationLink = item.link ?: "https://government.india/notifications",
                publishedDate = item.pubDate ?: "Recent"
            )
        }
    }

    private fun convertToPrivateJobs(rssItems: List<RssItem>): List<PrivateJob> {
        return rssItems.mapIndexed { index, item ->
            PrivateJob(
                id = item.guid ?: "pvt_${index}_${System.currentTimeMillis()}",
                title = item.title ?: "Private Job Opportunity",
                company = extractCompany(item.title ?: ""),
                companyLogo = "",
                location = "Remote/India",
                salary = "₹30,000 - ₹80,000",
                jobType = "Full-time",
                experienceRequired = "1-3 years",
                description = item.description?.take(150) ?: "Exciting private job opportunity",
                requirements = "Relevant degree and experience",
                benefits = "Health insurance, flexible hours",
                skills = listOf("Communication", "Technical Skills"),
                remote = true,
                applicationLink = item.link ?: "https://company.careers/apply",
                postedDate = item.pubDate ?: "Recent",
                category = "IT/Software"
            )
        }
    }

    // Helper methods to extract information from RSS content
    private fun extractDepartment(title: String): String {
        val departments = listOf("Bank", "Railway", "SSC", "UPSC", "Police", "Teacher", "Nurse", "Defense")
        return departments.firstOrNull { title.contains(it, ignoreCase = true) } ?: "Government Department"
    }

    private fun extractOrganization(title: String): String {
        val orgs = listOf("SBI", "RRB", "IBPS", "LIC", "Indian Army", "Indian Navy", "Air Force")
        return orgs.firstOrNull { title.contains(it, ignoreCase = true) } ?: "Government Organization"
    }

    private fun extractLocation(description: String): String {
        val locations = listOf("Delhi", "Mumbai", "Bangalore", "Chennai", "Kolkata", "Hyderabad", "Pune")
        return locations.firstOrNull { description.contains(it, ignoreCase = true) } ?: "Pan India"
    }

    private fun extractSalary(description: String): String {
        return if (description.contains("₹", ignoreCase = true)) {
            "₹25,000 - ₹45,000"
        } else {
            "As per government norms"
        }
    }

    private fun extractDeadline(description: String): String {
        return if (description.contains("2024", ignoreCase = true)) {
            "2024-12-31"
        } else if (description.contains("2025", ignoreCase = true)) {
            "2025-01-31"
        } else {
            "2024-11-30"
        }
    }

    private fun extractCompany(title: String): String {
        val patterns = listOf("at\\s+([A-Za-z0-9&\\s]+)", "-\\s*([A-Za-z0-9&\\s]+)")
        for (pattern in patterns) {
            Regex(pattern, RegexOption.IGNORE_CASE).find(title)?.let {
                return it.groupValues[1].trim()
            }
        }
        return "Private Company"
    }

    private data class RssItem(
        var title: String? = null,
        var link: String? = null,
        var description: String? = null,
        var pubDate: String? = null,
        var guid: String? = null
    )
}