package com.example.bulbasaur

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bulbasaur.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Repartija"
        val nameEditText = binding.name
        val amountEditText = binding.amount
        val recyclerView = binding.reclycerview
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        val items = mutableListOf<Item>()
        val dbHelper = ItemDb(requireContext())
        val db = dbHelper.writableDatabase
        val adapter = ItemAdapter(items)
        recyclerView.adapter = adapter

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonDelete.setOnClickListener {
            db.execSQL("DELETE FROM items")

        }

        binding.buttonAdd.setOnClickListener {
            val namet = nameEditText.text.toString()
            val amountt = amountEditText.text.toString()
            val amounttInt = amountt.toInt()
            val values = ContentValues().apply {
                put(ItemContract.COLUMN_NAME, namet)
                put(ItemContract.COLUMN_AMOUNT, amounttInt)
            }
            val item = Item(amountEditText.text.toString(), amountEditText.text.toString())
                if (item.name.isNotEmpty() && item.amount.isNotEmpty()){
                    db.insert(ItemContract.TABLE_NAME, null, values)
                    adapter.add(item)
                    adapter.update(getAllItemsFromDb())
                    adapter.notifyDataSetChanged()
                    // Limpia los campos de texto
                    nameEditText.text.clear()
                    amountEditText.text.clear()
                } else {
                    Log.d("ItemAdapter", "Item name is empty")
                }
        }
    }

    override fun onDestroyView() {
        val dbHelper = ItemDb(requireContext())
        val db = dbHelper.writableDatabase
        super.onDestroyView()
        db.close()
        _binding = null
        activity?.deleteDatabase(ItemContract.DATABASE_NAME)
    }

    private fun getAllItemsFromDb(): MutableList<Item> {
        val dbHelper = ItemDb(requireContext())
        val items = mutableListOf<Item>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${ItemContract.TABLE_NAME}", null)
        cursor.use {
            while (it.moveToNext()) {
                val item = Item(
                    it.getString(it.getColumnIndex(ItemContract.COLUMN_NAME)),
                    it.getInt(it.getColumnIndex(ItemContract.COLUMN_AMOUNT)).toString()
                )
                items.add(item)
            }
        }
        return items
    }

}

class ItemAdapter(private val items: MutableList<Item>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
        val amountTextView: TextView = itemView.findViewById(R.id.amount_text_view)
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
    }

    override fun getItemCount(): Int {
        return items.size
    }
    fun add(item: Item) {
        items.add(item)
        notifyDataSetChanged()
    }
    fun update(items: List<Item>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }


}

class Item(val name: String, val amount: String)
