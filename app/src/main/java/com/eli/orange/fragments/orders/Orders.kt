package com.eli.orange.fragments.orders

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Orders(
        var orderIdentity: String = "",
        var productQuantity: String? = "",
        var userPhoneNumber: String ="",
        var sellerIdentity: String ="",
        var customerIdentity: String = "",
        var productIdentity: String = "",
        var productImage: String = "",
        var productTitle: String = "",
        var productPrice: String =""
)