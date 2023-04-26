package com.example.barcodetest

import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.budiyev.android.codescanner.CodeScanner

import com.example.barcodetest.databinding.ActivityMainBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

//import me.dm7.barcodescanner.zxing.ZXingScannerView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: BarcodePriceAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        println("before-Satish")

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        println("after-Satish")
        adapter = BarcodePriceAdapter()
        binding.barcodeList.adapter = adapter
        binding.barcodeList.layoutManager = LinearLayoutManager(this)

        binding.scanButton.setOnClickListener {
            viewModel.scanBarcode(this)
        }

        viewModel.barcodeList.observe(this, { barcodeList ->
            adapter.setData(barcodeList)
        })

        IntentIntegrator(this).setOrientationLocked(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            viewModel.handleScanResult(result, this)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}