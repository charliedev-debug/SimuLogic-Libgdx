package org.engine.simulogic.android.utilities

import android.app.Activity
import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import kotlin.random.Random

class ReviewHelper {


    companion object{
        private var showReviewDialog = false
        private var hasShown = false
        fun seedReviewFlow(){
          if(showReviewDialog) return
            showReviewDialog = true//Random(50).nextInt() >= 50 && !hasShown
        }

        fun requestReviewFlow(reviewManager: ReviewManager): Task<ReviewInfo>{
            return reviewManager.requestReviewFlow()
        }

        fun launchReviewFlow(activity: Activity,reviewManager: ReviewManager, reviewInfo: ReviewInfo){
            if(showReviewDialog){
                reviewManager.launchReviewFlow(activity,reviewInfo).addOnCompleteListener{
                    //don't show again until relaunch
                    showReviewDialog = false
                    hasShown = true
                }
            }
        }
    }
}
