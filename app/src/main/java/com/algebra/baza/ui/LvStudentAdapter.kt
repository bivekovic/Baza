package com.algebra.baza.ui

import com.algebra.baza.R
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import com.algebra.baza.model.Student
import com.algebra.baza.podaci.*

class LvStudentAdapter( context: Context, cursor : Cursor, flags : Int ) : CursorAdapter( context, cursor, flags) {

    private val layoutInflater : LayoutInflater = context.getSystemService( Context.LAYOUT_INFLATER_SERVICE ) as LayoutInflater

    override fun newView( context: Context?, cursor: Cursor?, parent: ViewGroup? ): View {
        return layoutInflater.inflate( R.layout.student, parent, false )
    }

    override fun bindView( view: View?, context: Context?, cursor: Cursor? ) {
        val tvId       : TextView = view?.findViewById( R.id.tvID )!!
        val tvIme      : TextView = view?.findViewById( R.id.tvIme )
        val tvRodendan : TextView = view?.findViewById( R.id.tvRodendan )
        val tvGodina   : TextView = view?.findViewById( R.id.tvGodina )
        val tvSpol     : TextView = view?.findViewById( R.id.tvSpol )

        val id       = cursor?.getLong( cursor.getColumnIndex( COLUMN_ID ) )!!
        val ime      = cursor?.getString( cursor.getColumnIndex( COLUMN_IME ) )
        val rodendan = cursor?.getInt( cursor.getColumnIndex( COLUMN_DATUM_RODENJA ) )
        val godina   = cursor?.getInt( cursor.getColumnIndex( COLUMN_GODINA ) )
        val spol     = cursor?.getString( cursor.getColumnIndex( COLUMN_SPOL ) )

        val student = Student( id, ime, rodendan, godina, spol )

        tvId.text       = ""+ student.id
        tvIme.text      = student.ime
        tvRodendan.text = student.datumString( )
        tvGodina.text   = ""+ student.godina
        tvSpol.text     = student.spol
    }

}