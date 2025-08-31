package com.aim.lifemate.ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor(private val context: Context) {

    private val db = Firebase.firestore
    private val adRequest = AdRequest.Builder().build()

    companion object {
        const val BANNER_AD_ID = "ca-app-pub-3940256099942544/6300978111"
        const val INTERSTITIAL_AD_ID = "ca-app-pub-3940256099942544/1033173712"
        const val REWARDED_AD_ID = "ca-app-pub-3940256099942544/5224354917"
    }

    fun loadBannerAd(adView: AdView) {
        adView.loadAd(adRequest)
        trackAdImpression("banner")
    }

    fun showInterstitialAd(userId: String, onDismissed: () -> Unit = {}) {
        InterstitialAd.load(context, INTERSTITIAL_AD_ID, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                ad.show(context as? androidx.appcompat.app.AppCompatActivity)
                trackAdRevenue(userId, "interstitial", 0.01)
                onDismissed()
            }
        })
    }

    fun showRewardedAd(userId: String, onReward: (Int) -> Unit, onDismissed: () -> Unit = {}) {
        RewardedAd.load(context, REWARDED_AD_ID, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                ad.show(context as? androidx.appcompat.app.AppCompatActivity) { rewardItem ->
                    val rewardAmount = rewardItem.amount
                    onReward(rewardAmount)
                    trackAdRevenue(userId, "rewarded", 0.05)
                    onDismissed()
                }
            }
        })
    }

    private fun trackAdRevenue(userId: String, adType: String, revenue: Double) {
        val userShare = revenue * 0.01 // 1% to user
        val platformShare = revenue * 0.99 // 99% to platform

        db.collection("ad_revenue").document(UUID.randomUUID().toString()).set(
            mapOf(
                "userId" to userId,
                "adType" to adType,
                "revenue" to revenue,
                "userShare" to userShare,
                "platformShare" to platformShare,
                "timestamp" to System.currentTimeMillis()
            )
        )

        // Add user share to wallet
        db.collection("users").document(userId)
            .update("wallet_balance", FieldValue.increment(userShare))
    }

    private fun trackAdImpression(adType: String) {
        // Track ad impressions for analytics
        db.collection("ad_impressions").document(UUID.randomUUID().toString()).set(
            mapOf(
                "adType" to adType,
                "timestamp" to System.currentTimeMillis()
            )
        )
    }
}