package com.example.mocacong.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mocacong.data.objects.CafeDetailData
import com.example.mocacong.data.objects.CafeDetailData.labels
import com.example.mocacong.data.objects.CafeDetailData.levels
import com.example.mocacong.databinding.EditSelectItemBinding

class EditListAdapter(
    val checkedLists: HashMap<String, String?>
    //({"wifi", "빵빵해요"}, {"parking", null}
) : RecyclerView.Adapter<EditListAdapter.MyViewHolder>() {

    val selectedRVs = HashMap<String, String?>()
    //[(wifi, 빵빵해요), (parking, 보통이에요) ... ]

    inner class MyViewHolder(private val binding: EditSelectItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(key: String) {
            binding.labelText.text = labels[key]
            binding.bestBtn.setLabelText(levels[key]?.get(0) ?: "")
            binding.sosoBtn.setLabelText(levels[key]?.get(1) ?: "")
            binding.badBtn.setLabelText(levels[key]?.get(2) ?: "")

            when (checkedLists[key]) {
                levels[key]?.get(0) -> {
                    binding.radioGroup.check(binding.bestBtn.id)
                    selectedRVs[key] = levels[key]?.get(0)
                }
                levels[key]?.get(1) -> {
                    binding.radioGroup.check(binding.sosoBtn.id)
                    selectedRVs[key] = levels[key]?.get(1)
                }
                levels[key]?.get(2) -> {
                    binding.radioGroup.check(binding.badBtn.id)
                    selectedRVs[key] = levels[key]?.get(2)
                }
            }

            binding.radioGroup.setOnCheckedChangeListener { _, i ->
                when (i) {
                    binding.bestBtn.id -> {
                        selectedRVs[key] = levels[key]?.get(0)
                    }
                    binding.sosoBtn.id -> {
                        selectedRVs[key] = levels[key]?.get(1)
                    }
                    binding.badBtn.id -> {
                        selectedRVs[key] = levels[key]?.get(2)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditListAdapter.MyViewHolder {
        val binding =
            EditSelectItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = CafeDetailData.keys.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(CafeDetailData.keys[position])
    }

}