package com.example.bulbasaur

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import com.example.bulbasaur.databinding.FragmentFirstBinding


class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        val adapter = ItemAdapter(viewModel.items.value ?: mutableListOf())
        binding.reclyclerview.adapter = adapter
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Repartija"
        val nameEditText = binding.name
        val amountEditText = binding.amount
        val recyclerView = binding.reclyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        val viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        viewModel.items.value = getAllItemsFromDb()
        val adapter = ItemAdapter(viewModel.items.value ?: mutableListOf())
        val db = openDb(requireContext())
        recyclerView.adapter = adapter

        binding.buttonFirst.setOnClickListener {
            val valueToPass = calculateAverageAmount(viewModel.items.value ?: mutableListOf())
            val bundle = Bundle()
            bundle.putString("key", valueToPass.toString())
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
        }

        binding.buttonDelete.setOnClickListener {
            viewModel.items.value?.clear()
            deleteFromDb(db)
            adapter.notifyItemRangeRemoved(0,viewModel.items.value!!.size)
        }

        binding.buttonAdd.setOnClickListener {
            val item = Item(amountEditText.text.toString(),amountEditText.text.toString())
            if (item.name.isNotEmpty() && item.amount.isNotEmpty()){
                val namet = nameEditText.text.toString()
                val amountt = amountEditText.text.toString()
                val amounttInt = amountt.toInt()
                insertIntoDb(db,namet,amounttInt)
                adapter.add(item)
                viewModel.items.value?.add(item)
                adapter.update(getAllItemsFromDb())
                // Limpia los campos de texto
                nameEditText.text.clear()
                amountEditText.text.clear()
            } else {
                Log.d("ItemAdapter", "Item name is empty")
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getAllItemsFromDb(): MutableList<Item> {
        val dbHelper = ItemDb(requireContext())
        val items = mutableListOf<Item>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${ItemContract.TABLE_NAME}", null)

        try {
            cursor.use {
                while (it.moveToNext()) {
                    val item = Item(
                        it.getString(abs(it.getColumnIndex(ItemContract.COLUMN_NAME))),
                        it.getInt(abs(it.getColumnIndex(ItemContract.COLUMN_AMOUNT))).toString()
                    )
                    items.add(item)
                }
            }
        } finally {
            cursor.close()
        }
        return items
    }

    private fun calculateAverageAmount(items: MutableList<Item>): Double {
        if (items.size == 0) return 0.0
        if (items.size == 1) return items[0].amount.toDouble()

        var totalAmount = 0
        for (item in items) {
            totalAmount += item.amount.toInt()
        }
        return totalAmount.toDouble() / (items.size)
    }


}

class ItemAdapter(private val items: MutableList<Item>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
        val amountTextView: TextView = itemView.findViewById(R.id.amount_text_view)
        val deleteButton: Button = itemView.findViewById(R.id.delete_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        if (item.name.isEmpty()) {
            Log.d("ItemAdapter", "Item name is empty onBindViewHolder")
        }
        if (item.amount.isEmpty()) {
            Log.d("ItemAdapter", "Item amount is empty onBindViewHolder")
        }
        holder.nameTextView.text = item.name
        holder.amountTextView.text = item.amount
        holder.deleteButton.setOnClickListener {
            remove(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun add(item: Item) {
        val position = items.size - 1
        items.add(item)
        notifyItemInserted(position)
    }

    fun remove(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun update(items: List<Item>) {
        val position = items.size - 1
        this.items.clear()
        this.items.addAll(items)
        notifyItemChanged(position)
    }

}

class Item(val name: String, val amount: String)
