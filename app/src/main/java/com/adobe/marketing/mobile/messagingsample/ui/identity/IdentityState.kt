package com.adobe.marketing.mobile.messagingsample.ui.identity

data class IdentityState(

    val ecid: String = "-",

    val customerId: String = "-",

    val namespace: String = "customerid",

    val pushToken: String = "-",

    val sdkVersion: String = "-",

    val connected: Boolean = false

)