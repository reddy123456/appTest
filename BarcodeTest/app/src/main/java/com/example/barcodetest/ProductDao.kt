package com.example.barcodetest

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ProductDao {
    @Query("SELECT * FROM Product WHERE barcode = :barcode")
    fun getProductByBarcode(barcode: String): Product?
}