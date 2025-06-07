package com.apkmob.mixit

import android.content.Context
import com.apkmob.mixit.data.Cocktail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CocktailStorage {
    private const val FILE_NAME = "cocktails.json"

    fun saveCocktails(context: Context, cocktails: List<Cocktail>) {
        val json = Gson().toJson(cocktails)
        context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    fun loadCocktails(context: Context): List<Cocktail> {
        return try {
            val json = context.openFileInput(FILE_NAME).bufferedReader().use { it.readText() }
            Gson().fromJson(json, object : TypeToken<List<Cocktail>>() {}.type)
        } catch (e: Exception) {
            // Jeśli plik nie istnieje, wczytaj z assets
            val json = context.assets.open(FILE_NAME).bufferedReader().use { it.readText() }
            Gson().fromJson(json, object : TypeToken<List<Cocktail>>() {}.type)
        }
    }
}