package com.unsoed.foodwise

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.unsoed.foodwise.databinding.ActivityMainBinding
import com.unsoed.foodwise.ui.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    // Kita akan menyimpan semua container item navigasi di sini
    private lateinit var navItems: List<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Daftarkan semua item navigasi yang bisa diklik
        navItems = listOf(binding.navHome, binding.navProgress, binding.navKuliner, binding.navRecipe)
        navItems.forEach { it.setOnClickListener(this) }

        // 2. Atur listener khusus untuk tombol FAB (+)
        binding.fabAdd.setOnClickListener {
            loadFragment(DiaryFragment())
            // Nonaktifkan semua ikon lain karena FAB yang menjadi fokus
            setActive(null)
            Toast.makeText(this, "Membuka Halaman Catatan", Toast.LENGTH_SHORT).show()
        }

        // 3. Atur halaman awal saat aplikasi pertama kali dibuka
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
            setActive(binding.navHome) // Aktifkan ikon Home
        }
    }

    // 4. Fungsi ini akan dipanggil setiap kali salah satu item navigasi di-klik
    override fun onClick(clickedView: View?) {
        if (clickedView == null) return

        // Atur status visual ikon yang aktif
        setActive(clickedView)

        // Tentukan fragment mana yang akan dimuat
        val fragment: Fragment? = when (clickedView.id) {
            R.id.nav_home -> DashboardFragment()
            R.id.nav_progress -> ProgressFragment()
            R.id.nav_kuliner -> DiscoveryFragment()
            R.id.nav_recipe -> RecipeFragment()
            else -> null
        }

        // Muat fragment jika tidak null
        fragment?.let { loadFragment(it) }
    }

    /**
     * Mengatur status visual item navigasi.
     * Ikon & Teks item yang aktif akan dibuat terang dan terlihat.
     * Item lain akan dibuat redup dan teksnya disembunyikan.
     */
    private fun setActive(activeView: View?) {
        // Loop melalui semua item navigasi
        navItems.forEach { navItem ->
            val icon = navItem.getChildAt(0) as View // Bisa ImageView
            val text = navItem.getChildAt(1) as TextView

            if (navItem == activeView) {
                // Jika ini item yang aktif: ikon terang, teks muncul
                icon.alpha = 1.0f
                text.visibility = View.VISIBLE
            } else {
                // Jika tidak aktif: ikon redup, teks sembunyi
                icon.alpha = 0.7f
                text.visibility = View.GONE
            }
        }
    }

    /**
     * Fungsi helper untuk mengganti Fragment di dalam container.
     */
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}