package com.example.bulbasaur

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
        val items = mutableListOf<Item>()
        val adapter = ItemAdapter(items)
        val db = openDb(requireContext())
        recyclerView.adapter = adapter
        val deleteButton = view.findViewById<Button>(R.id.delete_item)


        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonDelete.setOnClickListener {
            deleteFromDb(db)
        }

        binding.buttonAdd.setOnClickListener {
            val item = Item(amountEditText.text.toString(),amountEditText.text.toString())
            if (item.name.isNotEmpty() && item.amount.isNotEmpty()){
                val namet = nameEditText.text.toString()
                val amountt = amountEditText.text.toString()
                val amounttInt = amountt.toInt()
                insertIntoDb(db,namet,amounttInt)
                adapter.add(item)
                adapter.update(getAllItemsFromDb())
                // Limpia los campos de texto
                nameEditText.text.clear()
                amountEditText.text.clear()
            } else {
                Log.d("ItemAdapter", "Item name is empty")
            }
        }

        /*deleteButton.setOnClickListener {
            // Obtiene la posición del elemento a eliminar
            val index = getItemPosition(item)
            // Elimina el elemento de la lista de elementos y de la base de datos
            deleteItem(requireContext(), index)
        }*/


    }

    override fun onDestroyView() {
        val db = openDb(requireContext())
        super.onDestroyView()
        deleteFromDb(db)
        _binding = null
        activity?.deleteDatabase(ItemContract.DATABASE_NAME)
        closeDb(db)
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

    /*private fun getItemPosition(item: Item): Int {
        // Recorre la lista de elementos y devuelve la posición del elemento especificado
        for (i in items.indices) {
            if (items[i] == item) {
                return i
            }
        }
        // Si no se encuentra el elemento, se devuelve -1
        return -1
    }*/


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
        val position = items.size - 1
        items.add(item)
        notifyItemInserted(position)
    }

    fun deleteItem(context: Context, index: Int) {
        // Elimina el elemento de la lista de elementos
        items.removeAt(index)
        // Notifica al adaptador de que se ha producido un cambio
        notifyItemRemoved(index)

        // Abre una conexión a la base de datos
        val db = openDb(context)
        // Elimina el elemento de la tabla de la base de datos
        deleteItemFromDb(db, index)
        // Cierra la conexión a la base de datos
        closeDb(db)
    }


    fun update(items: List<Item>) {
        val position = items.size - 1
        this.items.clear()
        this.items.addAll(items)
        notifyItemChanged(position)
    }

    fun getItemPosition(item: Item): Int {
        return items.indexOf(item)
    }

}

class Item(val name: String, val amount: String)
