package com.example.bulbasaur

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
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
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nameEditText = binding.name
        val amountEditText = binding.amount
        val recyclerView = binding.reclycerview
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.buttonAdd.setOnClickListener {
            val namet = nameEditText.text.toString()
            val amountt = amountEditText.text.toString()
            val item = Item(namet, amountt)
            val items = mutableListOf(item)
            if (recyclerView.adapter == null) {
                // Crea un nuevo adaptador y establecelo en el RecyclerView si aún no tiene uno
                val adapter = ItemAdapter(items) // Crea un nuevo adaptador y pasa los datos necesarios para inicializarlo
                recyclerView.setAdapter(adapter)
            } else {
                // Obtén el adaptador existente y agrega el elemento
                val adapter = recyclerView.adapter as ItemAdapter
                adapter.add(item)

                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        holder.nameTextView.setText(item.name)
        holder.amountTextView.setText(item.amount)
    }
    override fun getItemCount(): Int {
        return items.size
    }
    fun add(item: Item) {
        items.add(item)
        notifyDataSetChanged()
    }


}
class Item(val name: String, val amount: String)