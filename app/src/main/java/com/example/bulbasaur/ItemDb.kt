package com.example.bulbasaur

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ItemDb(context: Context) : SQLiteOpenHelper(context, ItemContract.DATABASE_NAME, null, ItemContract.DATABASE_VERSION) {
        val values = ContentValues().apply {
            put(ItemContract.COLUMN_NAME, "nombre del item")
            put(ItemContract.COLUMN_AMOUNT, "monto del item")
        }

        // Métodos de SQLiteOpenHelper
        override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_ITEMS_TABLE =
            "CREATE TABLE ${ItemContract.TABLE_NAME} (" +
                    "${ItemContract.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${ItemContract.COLUMN_NAME} TEXT," +
                    "${ItemContract.COLUMN_AMOUNT} TEXT)"
            db?.execSQL(SQL_CREATE_ITEMS_TABLE)
        }

        // Método que se llama al actualizar la base de datos a una nueva versión
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            val dropTableStatement = "DROP TABLE IF EXISTS $ItemContract.TABLE_NAME"
            db.execSQL(dropTableStatement)
            onCreate(db)
        }

        // Método para insertar un nuevo item en la base de datos
        fun insertItem(item: Item): Long {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(ItemContract.COLUMN_NAME, item.name)
            contentValues.put(ItemContract.COLUMN_AMOUNT, item.amount)
            val newRowId = db?.insert(ItemContract.TABLE_NAME, null, contentValues)
            return newRowId!!
        }

        // Método para obtener todos los items de la base de datos
        fun getAllItems(): ArrayList<Item> {
            val items = ArrayList<Item>()
            val selectQuery = "SELECT * FROM ${ItemContract.TABLE_NAME}"
            val db = this.readableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val item = Item(cursor.getString(cursor.getColumnIndex(ItemContract.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(ItemContract.COLUMN_AMOUNT)))
                    items.add(item)
                } while (cursor.moveToNext())
            }
            return items
        }
    }

class ItemContract {
    companion object {
        const val DATABASE_NAME = "database.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "items"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_AMOUNT = "amount"
    }
}
