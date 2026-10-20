package com.project.myshopeegw.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.project.myshopeegw.databinding.FragmentCategoryBinding
import com.project.myshopeegw.ui.adapter.CategoryAdapter
import com.project.myshopeegw.ui.product.ProductListActivity
import com.project.myshopeegw.viewmodel.ProductViewModel

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val productViewModel: ProductViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCategoryGrid.layoutManager = GridLayoutManager(requireContext(), 2)

        productViewModel.categories.observe(viewLifecycleOwner) { list ->
            val adapter = CategoryAdapter(list, isGridMode = true) { category ->
                val intent = Intent(requireContext(), ProductListActivity::class.java).apply {
                    putExtra(ProductListActivity.EXTRA_CATEGORY_ID, category.id)
                    putExtra(ProductListActivity.EXTRA_CATEGORY_NAME, category.name)
                }
                startActivity(intent)
            }
            binding.rvCategoryGrid.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
