package com.unsoed.foodwise.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.unsoed.foodwise.databinding.FragmentRecipeBinding

class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    private val recipeViewModel: RecipeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var searchAdapter: IngredientSearchAdapter
    private lateinit var addedAdapter: AddedIngredientAdapter
    private lateinit var savedAdapter: SavedRecipeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabs()
        setupRecyclerViews()
        setupListeners()
        observeViewModel()
    }

    private fun setupTabs() {
        binding.tabLayoutRecipe.addTab(binding.tabLayoutRecipe.newTab().setText("Buat Resep Baru"))
        binding.tabLayoutRecipe.addTab(binding.tabLayoutRecipe.newTab().setText("Resep Tersimpan"))

        binding.tabLayoutRecipe.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    binding.createRecipeContent.visibility = View.VISIBLE
                    binding.savedRecipesContent.visibility = View.GONE
                } else {
                    binding.createRecipeContent.visibility = View.GONE
                    binding.savedRecipesContent.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerViews() {
        searchAdapter = IngredientSearchAdapter(emptyList())
        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchResults.adapter = searchAdapter

        addedAdapter = AddedIngredientAdapter(mutableMapOf())
        binding.rvAddedIngredients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddedIngredients.adapter = addedAdapter

        savedAdapter = SavedRecipeAdapter(emptyList())
        binding.rvSavedRecipes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSavedRecipes.adapter = savedAdapter
    }

    private fun setupListeners() {
        binding.btnSearchIngredient.setOnClickListener {
            val query = binding.etSearchIngredient.text.toString()
            recipeViewModel.searchIngredients(query)
        }

        binding.etServings.addTextChangedListener {
            recipeViewModel.updateServingCount(it.toString().toIntOrNull() ?: 1)
        }

        searchAdapter.setOnAddClickListener { foodItem ->
            recipeViewModel.addIngredient(foodItem)
        }

        addedAdapter.setOnRemoveClickListener { foodItem ->
            recipeViewModel.removeIngredient(foodItem)
        }

        addedAdapter.setOnQuantityChangedListener { foodItem, grams ->
            recipeViewModel.updateIngredientQuantity(foodItem, grams)
        }

        binding.btnSaveRecipe.setOnClickListener {
            val recipeName = binding.etRecipeName.text.toString()
            if (recipeName.isNotBlank()) {
                recipeViewModel.saveRecipe(recipeName)
                Toast.makeText(requireContext(), "$recipeName berhasil disimpan!", Toast.LENGTH_SHORT).show()
                binding.tabLayoutRecipe.getTabAt(1)?.select()
            } else {
                Toast.makeText(requireContext(), "Nama resep tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ================== FUNGSI YANG DIPERBAIKI ==================
    private fun observeViewModel() {
        recipeViewModel.searchResults.observe(viewLifecycleOwner) { results ->
            searchAdapter.updateData(results)
        }

        recipeViewModel.addedIngredients.observe(viewLifecycleOwner) { addedMap ->
            addedAdapter.updateData(addedMap)
        }

        recipeViewModel.nutritionSummary.observe(viewLifecycleOwner) { summary ->
            binding.tvTotalRecipeCalories.text = "${summary.totalCalories} kkal"
            binding.tvCaloriesPerServing.text = "${summary.caloriesPerServing} kkal"
        }

        // 1. Amati daftar resep dasar
        recipeViewModel.savedRecipes.observe(viewLifecycleOwner) {
            // 2. Setiap kali daftar resep dasar berubah, panggil fungsi untuk menghitung detailnya
            mainViewModel.allFoodItems.value?.let { allFoods ->
                recipeViewModel.calculateSavedRecipesDetails(allFoods)
            }
        }

        // 3. Amati hasil perhitungan detail
        recipeViewModel.savedRecipesWithDetails.observe(viewLifecycleOwner) { detailedList ->
            // 4. Perbarui adapter dengan data yang sudah diolah dan memiliki tipe yang benar
            savedAdapter.updateData(detailedList)
        }
    }
    // ==========================================================

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}