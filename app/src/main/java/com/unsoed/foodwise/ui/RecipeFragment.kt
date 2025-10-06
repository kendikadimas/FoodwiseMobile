package com.unsoed.foodwise.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.unsoed.foodwise.R
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

        setupToggleButtons()
        setupRecyclerViews()
        setupListeners()
        observeViewModel()
    }

    private fun setupToggleButtons() {
        binding.toggleButtonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_buat_resep -> {
                        binding.createRecipeContent.visibility = View.VISIBLE
                        binding.savedRecipesContent.visibility = View.GONE
                    }
                    R.id.btn_resep_tersimpan -> {
                        binding.createRecipeContent.visibility = View.GONE
                        binding.savedRecipesContent.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        // Adapter untuk hasil pencarian bahan
        searchAdapter = IngredientSearchAdapter(emptyList())
        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchResults.adapter = searchAdapter

        // Adapter untuk bahan yang sudah ditambahkan ke resep
        addedAdapter = AddedIngredientAdapter(mutableMapOf())
        binding.rvAddedIngredients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddedIngredients.adapter = addedAdapter

        // Adapter untuk menampilkan daftar resep yang disimpan
        savedAdapter = SavedRecipeAdapter(emptyList())
        binding.rvSavedRecipes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSavedRecipes.adapter = savedAdapter
    }

    private fun setupListeners() {
        // Tombol untuk mencari bahan
        binding.btnSearchIngredient.setOnClickListener {
            val query = binding.etSearchIngredient.text.toString()
            if (query.isNotBlank()) {
                recipeViewModel.searchIngredients(query)
            } else {
                Toast.makeText(requireContext(), "Masukkan nama bahan", Toast.LENGTH_SHORT).show()
            }
        }

        // Update jumlah porsi saat pengguna mengetik
        binding.etServings.addTextChangedListener {
            recipeViewModel.updateServingCount(it.toString().toIntOrNull() ?: 1)
        }

        // Aksi saat tombol '+' di hasil pencarian diklik
        searchAdapter.setOnAddClickListener { foodItem ->
            recipeViewModel.addIngredient(foodItem)
            Toast.makeText(requireContext(), "${foodItem.name} ditambahkan", Toast.LENGTH_SHORT).show()
        }

        // Aksi saat tombol hapus di daftar bahan diklik
        addedAdapter.setOnRemoveClickListener { foodItem ->
            recipeViewModel.removeIngredient(foodItem)
        }

        // Aksi saat jumlah gram bahan diubah
        addedAdapter.setOnQuantityChangedListener { foodItem, grams ->
            recipeViewModel.updateIngredientQuantity(foodItem, grams)
        }

        // Tombol untuk menyimpan resep
        binding.btnSaveRecipe.setOnClickListener {
            val recipeName = binding.etRecipeName.text.toString()
            if (recipeName.isNotBlank() && recipeViewModel.addedIngredients.value?.isNotEmpty() == true) {
                recipeViewModel.saveRecipe(recipeName)
                Toast.makeText(requireContext(), "$recipeName berhasil disimpan!", Toast.LENGTH_SHORT).show()
                // Pindah ke tab resep tersimpan
                binding.toggleButtonGroup.check(R.id.btn_resep_tersimpan)
                resetCreateRecipeForm()
            } else if (recipeName.isBlank()){
                Toast.makeText(requireContext(), "Nama resep tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Tambahkan minimal satu bahan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        // Mengamati hasil pencarian dan menampilkannya di searchAdapter
        recipeViewModel.searchResults.observe(viewLifecycleOwner) { results ->
            searchAdapter.updateData(results)
        }

        // Mengamati bahan yang ditambahkan dan menampilkannya di addedAdapter
        recipeViewModel.addedIngredients.observe(viewLifecycleOwner) { addedMap ->
            addedAdapter.updateData(addedMap)
        }

        // Mengamati total nutrisi dan memperbarui tampilan kalori
        recipeViewModel.nutritionSummary.observe(viewLifecycleOwner) { summary ->
            binding.tvTotalRecipeCalories.text = "${summary.totalCalories} kkal"
            binding.tvCaloriesPerServing.text = "${summary.caloriesPerServing} kkal"
        }

        // Mengamati daftar resep yang disimpan dari database
        recipeViewModel.savedRecipes.observe(viewLifecycleOwner) {
            mainViewModel.allFoodItems.value?.let { allFoods ->
                recipeViewModel.calculateSavedRecipesDetails(allFoods)
            }
        }

        // Mengamati resep tersimpan yang sudah diolah (dengan detail nutrisi)
        recipeViewModel.savedRecipesWithDetails.observe(viewLifecycleOwner) { detailedList ->
            savedAdapter.updateData(detailedList)
            // Tampilkan pesan jika daftar kosong
            binding.tvEmptySavedRecipe.isVisible = detailedList.isEmpty()
        }
    }

    // Fungsi untuk membersihkan form setelah resep disimpan
    private fun resetCreateRecipeForm() {
        binding.etRecipeName.text.clear()
        binding.etServings.setText("1")
        binding.etSearchIngredient.text.clear()
        recipeViewModel.clearRecipeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}