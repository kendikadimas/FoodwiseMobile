package com.unsoed.foodwise.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.unsoed.foodwise.R
import com.unsoed.foodwise.databinding.FragmentDiscoveryBinding

class DiscoveryFragment : Fragment() {

    private var _binding: FragmentDiscoveryBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var autoSlideRunnable: Runnable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDiscoveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFeaturedCarousel()

        val makananAdapter = FoodCarouselAdapter(emptyList())
        binding.rvMakanan.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvMakanan.adapter = makananAdapter

        val minumanAdapter = FoodCarouselAdapter(emptyList())
        binding.rvMinuman.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvMinuman.adapter = minumanAdapter

        mainViewModel.allFoodItems.observe(viewLifecycleOwner) { foodList ->
            Log.d("DiscoveryFragment", "Observed ${foodList.size} items from database.")
            if (foodList.isNotEmpty()) {
                val makanan = foodList.filter { !it.name.contains("Jus") && !it.name.contains("Susu") }
                val minuman = foodList.filter { it.name.contains("Jus") || it.name.contains("Susu") }

                makananAdapter.updateData(makanan)
                minumanAdapter.updateData(minuman)
            }
        }
    }

    private fun setupFeaturedCarousel() {
        val bannerData = listOf(
            BannerItem("Bakso Urat Pedas", "400 kkal", R.drawable.ic_launcher_background),
            BannerItem("Salad Sayur Segar", "210 kkal", R.drawable.ic_launcher_background),
            BannerItem("Ayam Bakar Madu", "350 kkal", R.drawable.ic_launcher_background)
        )
        binding.featuredViewPager.adapter = FeaturedBannerAdapter(bannerData)
        setupAutoSlider()
    }

    private fun setupAutoSlider() {
        autoSlideRunnable = Runnable {
            val currentItem = binding.featuredViewPager.currentItem
            val adapter = binding.featuredViewPager.adapter
            if (adapter != null) {
                binding.featuredViewPager.currentItem = (currentItem + 1) % adapter.itemCount
            }
            // Panggil kembali runnable ini setelah jeda
            handler.postDelayed(autoSlideRunnable, 3000)
        }
    }

    override fun onResume() {
        super.onResume()
        // Mulai auto-slide setelah 3 detik
        handler.postDelayed(autoSlideRunnable, 3000)
    }

    override fun onPause() {
        super.onPause()
        // Hentikan auto-slide untuk menghemat baterai
        handler.removeCallbacks(autoSlideRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}