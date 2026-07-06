package org.engine.simulogic.android

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.engine.simulogic.R
import org.engine.simulogic.android.circuits.storage.UserSettings
import org.engine.simulogic.android.helpers.ActivityHelpers
import org.engine.simulogic.android.views.dialogs.ErrorDialog
import org.engine.simulogic.android.views.dialogs.InfoDialog
import org.engine.simulogic.android.views.dialogs.SuccessDialog
import androidx.core.net.toUri

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
            InfoDialog(this@PremiumPurchaseActivity, "You have cancelled the purchase!").show()
        }else if(billingResult.responseCode == BillingClient.BillingResponseCode.NETWORK_ERROR){
            ErrorDialog(this@PremiumPurchaseActivity,"Could not complete purchase due to a network issue!").show()
        } else if(billingResult.responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE || billingResult.responseCode == BillingClient.BillingResponseCode.DEVELOPER_ERROR){
            ErrorDialog(this@PremiumPurchaseActivity,"Billing unavailable for this application!").show()
        } else{
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
        val unlockPremiumButton = findViewById<MaterialButton>(R.id.unlockPremium)
        val priceInfoLoaderProgress = findViewById<ProgressBar>(R.id.priceInfoLoader)
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
                               unlockPremiumButton.text = buildString {
                                   append("Unlock Pro - ")
                                   append(it.oneTimePurchaseOfferDetails?.formattedPrice) }
                               premiumProductDetails.add(it)
                           }
                           priceInfoLoaderProgress.visibility = View.INVISIBLE
                       }
                   }
               }
            }

        })
       unlockPremiumButton.setOnClickListener {
            premiumProductDetails.forEach {
                purchasePremium(this,it)
            }
        }

        findViewById<MaterialButton>(R.id.restorePurchase).setOnClickListener {
            restorePurchases()
        }

        findViewById<MaterialTextView>(R.id.termsOfService).setOnClickListener {
            val url = "https://sites.google.com/view/simulogic/home"
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        }

        findViewById<MaterialTextView>(R.id.privacyPolicy).setOnClickListener {
            val url = "https://sites.google.com/view/laborisapps/home"
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        }

        findViewById<AppCompatImageButton>(R.id.closeActivity).setOnClickListener {
            finish()
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
                    CoroutineScope(Dispatchers.Main).launch {
                        userSettings.saveBooleanPref(this@PremiumPurchaseActivity,UserSettings.PREMIUM_USER,true)
                        SuccessDialog(this@PremiumPurchaseActivity, "You are now a premium user").also{
                            it.listener = object : SuccessDialog.OnCloseDialogListener{
                                override fun onClick() {
                                    val intent = Intent(this@PremiumPurchaseActivity, LauncherActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                    finishAffinity()
                                }
                            }
                            it.show()
                        }
                    }
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
            when (billingResult.responseCode) {

                BillingClient.BillingResponseCode.OK -> {

                    if(purchases.isEmpty()){
                        CoroutineScope(Dispatchers.Main).launch {
                            InfoDialog(
                                this@PremiumPurchaseActivity,
                                "You don't have any previous purchase!"
                            ).show()
                        }
                    }else {
                        purchases.forEach { purchase ->
                            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                unlockPremiumFeatures()
                                CoroutineScope(Dispatchers.Main).launch {
                                    userSettings.saveBooleanPref(
                                        this@PremiumPurchaseActivity,
                                        UserSettings.PREMIUM_USER,
                                        true
                                    )
                                    SuccessDialog(
                                        this@PremiumPurchaseActivity,
                                        "Your previous purchase has been restored!"
                                    ).also {
                                        it.listener = object : SuccessDialog.OnCloseDialogListener {
                                            override fun onClick() {
                                                finish()
                                            }
                                        }
                                        it.show()
                                    }
                                }
                            }
                        }
                    }

                }
                BillingClient.BillingResponseCode.NETWORK_ERROR -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        ErrorDialog(
                            this@PremiumPurchaseActivity,
                            "Could not complete purchase due to a network issue!"
                        ).show()
                    }
                }
                else -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        ErrorDialog(
                            this@PremiumPurchaseActivity,
                            "An unknown error occurred!"
                        ).show()
                    }
                }
            }
        }
    }

}
