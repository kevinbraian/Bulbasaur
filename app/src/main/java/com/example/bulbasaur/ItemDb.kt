package com.example.bulbasaur

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ItemDb(context: Context) : SQLiteOpenHelper(context, ItemContract.DATABASE_NAME, null, ItemContract.DATABASE_VERSION) {

        // Métodos de SQLiteOpenHelper
        override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_ITEMS_TABLE =
            "CREATE TABLE ${ItemContract.TABLE_NAME} (" +
                    "${ItemContract.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${ItemContract.COLUMN_NAME} TEXT," +
                    "${ItemContract.COLUMN_AMOUNT} TEXT)"
            db.execSQL(SQL_CREATE_ITEMS_TABLE)
        }

        // Método que se llama al actualizar la base de datos a una nueva versión
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            val dropTableStatement = "DROP TABLE IF EXISTS $ItemContract.TABLE_NAME"
            db.execSQL(dropTableStatement)
            onCreate(db)
        }

        // Método para obtener todos los items de la base de datos
        fun getAllItems(): ArrayList<Item> {
            val items = ArrayList<Item>()
            val selectQuery = "SELECT * FROM ${ItemContract.TABLE_NAME}"
            val db = this.readableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            try {if (cursor.moveToFirst()) {
                do {
                    val item = Item(cursor.getString(
                        kotlin.math.abs(
                            cursor.getColumnIndex(
                                ItemContract.COLUMN_NAME
                            )
                        )
                    ),
                        cursor.getString(kotlin.math.abs(cursor.getColumnIndex(ItemContract.COLUMN_AMOUNT))))
                    items.add(item)
                } while (cursor.moveToNext())
            }} finally {
                cursor.close()
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

fun openDb(context: Context): SQLiteDatabase {
    val dbHelper = ItemDb(context)
    return dbHelper.writableDatabase
}

fun closeDb(db: SQLiteDatabase) {
    db.close()
}

fun deleteFromDb(db: SQLiteDatabase) {
    db.execSQL("DELETE FROM items")
}
fun deleteItemFromDb(db: SQLiteDatabase, id: Int) {
    val selection = "${ItemContract.COLUMN_ID} = ?"
    val selectionArgs = arrayOf(id.toString())
    db.delete(ItemContract.TABLE_NAME, selection, selectionArgs)
}


fun insertIntoDb(db: SQLiteDatabase, name: String, amount: Int) {
    val values = ContentValues().apply {
        put(ItemContract.COLUMN_NAME, name)
        put(ItemContract.COLUMN_AMOUNT, amount)
    }
    db.insert(ItemContract.TABLE_NAME, null, values)
}

