package com.example.mocacong.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mocacong.databinding.EditSelectItemBinding

class EditListAdapter(val typeLabelList: Array<String>, val levelLists : ArrayList<Array<String>>) : RecyclerView.Adapter<EditListAdapter.MyViewHolder>() {

    //val selectedRVs =  key-value로 저장하기 012중 머?
    val selectedRVs = HashMap<String, String>()

    inner class MyViewHolder(private val binding : EditSelectItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(labelStr: String, levels: Array<String>) {
            binding.labelText.text = labelStr
            binding.text1.text = levels[0]
            binding.text2.text = levels[1]
            binding.text3.text = levels[2]

            binding.radioGroup.setOnCheckedChangeListener { _, i ->
                selectedRVs[labelStr] = levels[i]
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditListAdapter.MyViewHolder {
        val binding = EditSelectItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = typeLabelList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(typeLabelList[position], levelLists[position])
    }

}