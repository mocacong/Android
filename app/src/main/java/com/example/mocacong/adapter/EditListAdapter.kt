package com.example.mocacong.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mocacong.databinding.EditSelectItemBinding

class EditListAdapter(
    val typeLabelList: Array<String>, //[와이파이는, 주차장은 ...]
    val levelLists: ArrayList<Array<String>> // 와이파이는 [빵빵해요, 적당해요 ...] ...
) : RecyclerView.Adapter<EditListAdapter.MyViewHolder>() {

    //val selectedRVs = post 보낼 리뷰값 key-value로 저장하기 012중 머?
    val selectedRVs = HashMap<String, String>()
    //[(와이파이는, 빵빵해요), (주차장은, 보통이에요) ... ]

    inner class MyViewHolder(private val binding: EditSelectItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(labelStr: String, levels: Array<String>) {
            binding.labelText.text = labelStr
            binding.bestBtn.setLabelText(levels[0])
            binding.sosoBtn.setLabelText(levels[1])
            binding.badBtn.setLabelText(levels[2])

            binding.radioGroup.setOnCheckedChangeListener { group, i ->
                when (i) {
                    binding.bestBtn.id -> {
                        selectedRVs.put(labelStr, levels[0])
                    }
                    binding.sosoBtn.id -> {
                        selectedRVs.put(labelStr, levels[1])
                    }
                    binding.badBtn.id -> {
                        selectedRVs.put(labelStr, levels[2])
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

    override fun getItemCount(): Int = typeLabelList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(
            typeLabelList[position] //와이파이는
            , levelLists[position] //[빵빵해요, 적당해요, 안좋아요]
        )
    }

}