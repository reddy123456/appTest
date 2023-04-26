package com.example.barcodetest

import android.app.Activity
import android.app.Application
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
//import com.mysql.jdbc.Connection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val url = "jdbc:mysql://database-1.cgqlsbpdo8tb.ap-south-1.rds.amazonaws.com:3306/sys"
    private val user = "admin"
    private val password = "rootadmin"

    // Define a mutable list to store the scanned barcodes and their prices
    private val barcodePrices = mutableListOf<Pair<String, Double>>()

    // Define a LiveData object to hold the list of barcode-price pairs
    private val _barcodePriceList = MutableLiveData<MutableList<Pair<String, Double>>>(mutableListOf())
    val barcodePriceList: LiveData<MutableList<Pair<String, Double>>> = _barcodePriceList
    // Define a LiveData object to hold the total price
    private val _totalPrice = MutableLiveData<Double>()

    // Expose the LiveData objects to the UI
    //val barcodePriceList: LiveData<List<Pair<String, Double>>> = _barcodePriceList
    val totalPrice: LiveData<Double> = _totalPrice
    private val _barcodeList = MutableLiveData<List<Pair<String, Double>>>(emptyList())
    val barcodeList: LiveData<List<Pair<String, Double>>> = _barcodeList

    fun scanBarcode(activity: AppCompatActivity) {
        val integrator = IntentIntegrator(activity)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scan a barcode")
        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }


    fun handleScanResult(result: IntentResult, activity : Activity) {
        val barcode = result.contents
        var conn: Connection? = null

        _barcodeList.value = (barcodeList.value?.plus(barcode) ?: listOf(barcode)) as List<Pair<String, Double>>?
       viewModelScope.launch {
           var price: Double? = null
           withContext(Dispatchers.IO) {
               val connection = DriverManager.getConnection(url, user, password)
               val statement = connection.createStatement()
               val query = "SELECT price FROM sys.products WHERE barcode = '$barcode'"
               val resultSet = statement.executeQuery(query)
               if (resultSet.next()) {
                   price = resultSet.getDouble("price")
                   println("price after")
                   println(price)
               }
               resultSet.close()
               statement.close()
               connection.close()
           }
           if (price != null) {
               // Add the scanned barcode and its price to the list
               barcodePrices.add(Pair(barcode, price) as Pair<String, Double>)
               _barcodePriceList.postValue(barcodePrices)

               // Update the LiveData objects
             //  _barcodePriceList.value = barcodePrices.toList()
               _totalPrice.value = barcodePrices.sumOf { it.second }
               val priceTextView = activity.findViewById<TextView>(R.id.price_text)
               priceTextView.text = "Price: ${_totalPrice.value}"
               val recyclerView = activity.findViewById<RecyclerView>(R.id.barcode_price_list)
               val adapter = recyclerView.adapter as BarcodePriceAdapter
               adapter.setData(barcodePrices.toList())
           }
           else {
               // If the barcode is not found in the database, display an error message
               Toast.makeText(getApplication(), "Barcode not found: $barcode", Toast.LENGTH_SHORT).show()
           }
        }
    }


    }

