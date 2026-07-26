package com.adobe.marketing.mobile.messagingsample.dashboard

data class DashboardState(

    var connected:Boolean=false,

    var ecid:String="-",

    var customerId:String="-",

    var places:String="Inactive",

    var push:String="Inactive",

    var journey:String="Waiting",

    var inApp:String="Waiting"

)