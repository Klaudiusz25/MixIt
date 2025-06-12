package com.apkmob.mixit.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cocktail(
    val id: Int,
    val name: String,
    val ingredients: List<String>,
    val instructions: String,
    val timer: Int,
    val category: String,
    val alcoholic: Boolean,
    var notes: String = "",
    val imageUrl: String? = null // Dodaj URL zdjęcia
) : Parcelable