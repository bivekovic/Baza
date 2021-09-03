package com.algebra.baza.podaci

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

const val DATABASE             = "Studenti.db"
const val VERSION              = 1
const val TABLE_STUDENT        = "student"
const val COLUMN_ID            = "_ID"
const val COLUMN_IME           = "ime"
const val COLUMN_DATUM_RODENJA = "rodendan"
const val COLUMN_GODINA        = "godina"


class Baza private constructor( context : Context ) : SQLiteOpenHelper( context, DATABASE, null, VERSION ) {
    override fun onCreate( db: SQLiteDatabase? ) {
        val create_sql = "CREATE TABLE \""+ TABLE_STUDENT +"\" (" +
	                        "\"" + COLUMN_ID + "\"	INTEGER,"+
                            "\"" + COLUMN_IME + "\"	TEXT,"+
                            "\"" + COLUMN_DATUM_RODENJA + "\"	INTEGER,"+
                            "\""+ COLUMN_GODINA +"\"	INTEGER,"+
                            "PRIMARY KEY(\""+ COLUMN_ID +"\")"+
                        ");"
        db?.execSQL( create_sql )
    }

    override fun onUpgrade( db: SQLiteDatabase?, oldVersion: Int, newVersion: Int ) {
        val delete_sql = "DROP TABLE IF EXISTS "+ TABLE_STUDENT
        db?.execSQL( delete_sql )
        onCreate( db )
    }

    override fun onDowngrade( db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade( db, oldVersion, newVersion )
    }

    companion object singletonHolder : SingletonHolder< Baza, Context >( ::Baza )
}

open class SingletonHolder<out T: Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized( this ) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}