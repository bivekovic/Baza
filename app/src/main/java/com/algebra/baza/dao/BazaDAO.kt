package com.algebra.baza.dao

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.algebra.baza.model.Student
import com.algebra.baza.podaci.*
import java.lang.Exception

class BazaDAO( val context : Context ) : DAO {

    val TAG = "BazaDAO"

    override fun insert( s: Student ): Boolean {
        val db = Baza.getInstance( context ).writableDatabase

        val values = ContentValues( )
        values.put( COLUMN_IME,           s.ime )
        values.put( COLUMN_DATUM_RODENJA, s.datum )
        values.put( COLUMN_GODINA,        s.godina )
        try {
            db.insert( TABLE_STUDENT, null, values )
        } catch ( e : Exception ) {
            Log.e( TAG, "Desila se iznimka prilikom unosa novog studenta" )
            return false
        }

        return true
    }

    override fun update(s: Student): Boolean {
        val db = Baza.getInstance( context ).writableDatabase

        val values = ContentValues( )
        values.put( COLUMN_IME,           s.ime )
        values.put( COLUMN_DATUM_RODENJA, s.datum )
        values.put( COLUMN_GODINA,        s.godina )

        val selection = COLUMN_ID +"=?"
        val selectionArgs = arrayOf( ""+ s.id )

        val n = db.update( TABLE_STUDENT, values, selection, selectionArgs )

        return n!=0
    }

    override fun delete(s: Student): Boolean {
        val db = Baza.getInstance( context ).writableDatabase
        val selection = COLUMN_ID +"=?"
        val selectionArgs = arrayOf( ""+ s.id )

        val n = db.delete( TABLE_STUDENT, selection, selectionArgs )

        return n!=0
    }

    override fun getAll( ): List< Student > {
        val studenti = ArrayList< Student >( )
        val db = Baza.getInstance( context ).readableDatabase
        val cursor = db.rawQuery( "SELECT * FROM "+ TABLE_STUDENT, null )

        if( cursor!!.moveToFirst( ) ) {
            while( !cursor.isAfterLast ) {
                val id =       cursor.getLong( cursor.getColumnIndex( COLUMN_ID ) )
                val ime =      cursor.getString( cursor.getColumnIndex( COLUMN_IME ) )
                val rodendan = cursor.getInt( cursor.getColumnIndex( COLUMN_DATUM_RODENJA ) )
                val godina =   cursor.getInt( cursor.getColumnIndex( COLUMN_GODINA ) )

                studenti.add( Student( id, ime, rodendan, godina ) )

                cursor.moveToNext( )
            }
        }

        return studenti
    }


}

