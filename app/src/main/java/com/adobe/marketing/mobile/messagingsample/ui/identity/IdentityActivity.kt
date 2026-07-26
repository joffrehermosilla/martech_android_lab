package com.adobe.marketing.mobile.messagingsample.ui.identity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.adobe.marketing.mobile.messagingsample.R
import com.adobe.marketing.mobile.messagingsample.adobe.AdobeIdentityManager

class IdentityActivity : AppCompatActivity() {

    private val vm: IdentityViewModel by viewModels()

    private lateinit var txtECID: TextView
    private lateinit var txtSDK: TextView
    private lateinit var editCustomer: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnResetIdentity: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identity)

        supportActionBar?.title = "Identity Console - CDP Stitching"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        txtECID = findViewById(R.id.txtECID)
        txtSDK = findViewById(R.id.txtSDK)
        editCustomer = findViewById(R.id.editCustomerId)
        btnUpdate = findViewById(R.id.btnUpdateCustomerId)
        btnResetIdentity = findViewById(R.id.btnResetIdentity)

        vm.state.observe(this) { state ->
            txtECID.text = state.ecid
            txtSDK.text = state.sdkVersion
        }

        btnUpdate.setOnClickListener {
            val customerId = editCustomer.text.toString().trim()
            if (customerId.isBlank()) {
                Toast.makeText(this, "Ingrese un CustomerID (ej: DNI/HASH)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AdobeIdentityManager.setCustomerID(customerId, "CRM") {
                runOnUiThread {
                    Toast.makeText(this, "CustomerID asociado exitosamente a IdentityMap", Toast.LENGTH_SHORT).show()
                    vm.load()
                }
            }
        }

        btnResetIdentity.setOnClickListener {
            AdobeIdentityManager.resetIdentities()
            Toast.makeText(this, "Identidades reseteadas", Toast.LENGTH_SHORT).show()
            vm.load()
        }

        vm.load()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}