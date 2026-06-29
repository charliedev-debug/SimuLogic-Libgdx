package org.engine.simulogic.android

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.runBlocking
import org.engine.simulogic.R
import org.engine.simulogic.android.circuits.storage.UserSettings
import org.engine.simulogic.android.helpers.ActivityHelpers
import org.engine.simulogic.android.views.dialogs.ErrorDialog
import org.engine.simulogic.android.views.dialogs.InfoDialog

class PremiumPurchaseActivity : AppCompatActivity() {
    private val userSettings = UserSettings()
    private val premiumProductDetails = mutableListOf<ProductDetails>()
    private val purchaseUpdatedListener = PurchasesUpdatedListener{
        billingResult, purchases ->

        if(billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null){
            purchases.forEach {
                acknowledgePurchase(it)
            }
        }else if(billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED){
            InfoDialog(this@PremiumPurchaseActivity, "You have cancelled the purchase!")
        }else if(billingResult.responseCode == BillingClient.BillingResponseCode.NETWORK_ERROR){
            ErrorDialog(this@PremiumPurchaseActivity,"Could not complete purchase due to a network issue!").show()
        }else{
            ErrorDialog(this@PremiumPurchaseActivity, "An unknown error occurred!").show()
        }
    }
    private lateinit var billingClient : BillingClient
    override fun onCreate(savedInstanceState: Bundle?) {
        runBlocking{
            ActivityHelpers.getTheme(userSettings, this@PremiumPurchaseActivity)
        }
        super.onCreate(savedInstanceState)
       /* enableEdgeToEdge()*/
        setContentView(R.layout.activity_premium_purchase)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(scrim = Color.WHITE),
            navigationBarStyle = SystemBarStyle.dark(scrim = Color.WHITE))
        ActivityHelpers.setStatusBarColor(window, ActivityHelpers.getThemeResourceID(this, com.google.android.material.R.attr.backgroundColor))

        val priceTextView = findViewById<MaterialTextView>(R.id.price)
        billingClient = BillingClient.newBuilder(this)
            .setListener(purchaseUpdatedListener).enablePendingPurchases(PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts().build()).enableAutoServiceReconnection().build()

        billingClient.startConnection(object : BillingClientStateListener{
            override fun onBillingServiceDisconnected() {

            }

            override fun onBillingSetupFinished(result: BillingResult) {
               if(result.responseCode == BillingClient.BillingResponseCode.OK){

                   val queryProductsDetailsParams = QueryProductDetailsParams.newBuilder()
                       .setProductList(listOf(QueryProductDetailsParams.Product.newBuilder().setProductId("unlock_premium").setProductType(
                           BillingClient.ProductType.INAPP).build())).build()
                   billingClient.queryProductDetailsAsync(queryProductsDetailsParams){
                       billingResult, productDetailsResult->
                       if(billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                           // display product pricing and other details
                           premiumProductDetails.clear()
                           productDetailsResult.productDetailsList.forEach {
                               priceTextView.text = "${it.oneTimePurchaseOfferDetails?.formattedPrice}"
                               premiumProductDetails.add(it)
                           }
                       }
                   }
               }
            }

        })
        findViewById<MaterialButton>(R.id.unlockPremium).setOnClickListener {
            premiumProductDetails.forEach {
                purchasePremium(this,it)
            }
        }

        findViewById<MaterialButton>(R.id.restorePurchase).setOnClickListener {
            restorePurchases()
        }

        findViewById<MaterialTextView>(R.id.termsOfService).setOnClickListener {

        }

        findViewById<MaterialTextView>(R.id.privacyPolicy).setOnClickListener {

        }
       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
    }

    private fun unlockPremiumFeatures(){

    }

    private fun purchasePremium(activity: PremiumPurchaseActivity, productDetails: ProductDetails){
        val productDetailsParams = listOf(BillingFlowParams
            .ProductDetailsParams.newBuilder().setProductDetails(productDetails).build())
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParams).build()
        val billingResult = billingClient.launchBillingFlow(activity,billingFlowParams)
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    unlockPremiumFeatures()
                    InfoDialog(this@PremiumPurchaseActivity, "Congratulations you are now a premium user!").show()
                }
            }
        }
    }

    fun restorePurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        unlockPremiumFeatures()
                        InfoDialog(this@PremiumPurchaseActivity, "Congratulations you are now a premium user!").show()
                    }
                }
            }
        }
    }

}
