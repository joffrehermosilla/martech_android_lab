package com.adobe.marketing.mobile.messagingsample.ui.identity

data class IdentityModel(

    val namespace: String,

    val id: String,

    val authenticated: Boolean = true

)