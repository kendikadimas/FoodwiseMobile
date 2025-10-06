package com.unsoed.foodwise.util

import android.widget.ImageView
import coil.load
import com.unsoed.foodwise.R

/**
 * Extension untuk memuat gambar makanan.
 * Prioritas:
 * 1. Jika nama cocok dengan resource drawable lokal -> gunakan drawable.
 * 2. Jika string mengandung skema URL (http/https) -> load via Coil.
 * 3. Jika gagal / null -> tampilkan placeholder default.
 */
fun ImageView.loadFoodImage(nameOrUrl: String?) {
    if (nameOrUrl.isNullOrBlank()) {
        setImageResource(R.drawable.ic_launcher_foreground)
        return
    }
    val lower = nameOrUrl.lowercase()
    val isLikelyResourceName = lower.matches(Regex("[a-z0-9_]+"))
    if (isLikelyResourceName) {
        val resId = resources.getIdentifier(lower, "drawable", context.packageName)
        if (resId != 0) {
            setImageResource(resId)
            return
        }
    }
    val looksLikeUrl = nameOrUrl.startsWith("http://", true) || nameOrUrl.startsWith("https://", true)
    if (looksLikeUrl) {
        this.load(nameOrUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_foreground)
            error(R.drawable.ic_launcher_foreground)
        }
    } else {
        setImageResource(R.drawable.ic_launcher_foreground)
    }
}
