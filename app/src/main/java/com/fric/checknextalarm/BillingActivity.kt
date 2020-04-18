package com.fric.checknextalarm

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.android.billingclient.api.*

class BillingActivity : AppCompatActivity(), PurchasesUpdatedListener {

    private lateinit var billingClient: BillingClient
    private val skuList = listOf("test_product_one", "test_product_two")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing)

        setupBillingClient()

    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this)
                .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is setup successfully
                    Log.i("MainActivity", "Setup Billing done")
                    loadAllSkus()
                }
            }

            private fun loadAllSkus() = if (billingClient.isReady) {
                val params = SkuDetailsParams
                        .newBuilder()
                        .setSkusList(skuList)
                        .setType(BillingClient.SkuType.INAPP)
                        .build()
                billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                    // Process the result.
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList.isNotEmpty()) {
                        Log.i("MainActivity", "Billing Result OK")
                        for (skuDetails in skuDetailsList) {
                            // This will return both the SKUs from Google Play Console
                            if (skuDetails.sku == "test_product_two") {
                                val buttonBuyProduct = findViewById(R.id.buttonBuyProduct) as Button
                                buttonBuyProduct.setOnClickListener {
                                    println("Purchase Clicked")
                                    Log.i("MainActivity", "Purchase Clicked")
                                    val billingFlowParams = BillingFlowParams
                                            .newBuilder()
                                            .setSkuDetails(skuDetails)
                                            .build()
                                    billingClient.launchBillingFlow(
                                            this@BillingActivity,
                                            billingFlowParams
                                    )

                                }
                            }
                        }
                    } else {
                        Log.w("MainActivity", "Problem loading billing client: ${billingResult.responseCode}.  Sku details list: ${skuDetailsList}")
                    }
                }

            } else {
                Log.i("MainActivity", "Billing Client not ready")
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.w("MainActivity", "Setup Billing Failed")
            }


        })

    }

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK && purchases != null)
            for (purchase in purchases) {
                acknowledgePurchase(purchase.purchaseToken)
            }
    }

    private fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build()
        billingClient.acknowledgePurchase(params) { billingResult ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
        }
    }

}
