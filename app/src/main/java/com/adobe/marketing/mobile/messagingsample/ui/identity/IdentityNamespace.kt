package com.adobe.marketing.mobile.messagingsample.ui.identity

/**
 * Namespaces de identidad homologados con la configuración de Adobe Experience Platform.
 */
enum class IdentityNamespace(val display: String, val code: String) {
    CUSTOMER_ID("Customer ID", "CUSTOMER"),
    EMAIL("Email", "EMAIL"),
    PHONE("Phone", "PHONE"),
    DNI("DNI", "DNI")
}
