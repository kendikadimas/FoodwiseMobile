package com.unsoed.foodwise.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.unsoed.foodwise.databinding.ActivityLandingBinding

class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    // Siapkan teks yang akan berubah sesuai slide
    private val slogans = listOf(
        "Lacak Kalorimu, Raih Tujuanmu.",
        "Temukan Nutrisi di Setiap Makanan.",
        "Buat Resep Sehat Versimu Sendiri.",
        "Lihat Progres, Rayakan Kemenangan."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Setup Carousel (ViewPager2)
        binding.viewPager.adapter = CarouselAdapter()

        // 2. Hubungkan Indikator Titik dengan Carousel
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            // Tidak perlu set teks atau ikon, hanya butuh titiknya
        }.attach()

        // 3. Atur listener untuk mengubah teks saat slide digeser
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tvSlogan.text = slogans[position]
            }
        })

        // 4. Atur listener untuk tombol
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}