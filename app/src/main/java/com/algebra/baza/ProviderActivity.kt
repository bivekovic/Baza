package com.algebra.baza

import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.algebra.baza.model.Student
import com.algebra.baza.podaci.COLUMN_DATUM_RODENJA
import com.algebra.baza.podaci.COLUMN_GODINA
import com.algebra.baza.podaci.COLUMN_ID
import com.algebra.baza.podaci.COLUMN_IME
import com.algebra.baza.provider.CONTENT_URI
import com.algebra.baza.ui.LvStudentAdapter
import java.text.SimpleDateFormat

class ProviderActivity : AppCompatActivity( ) {

    lateinit var tvId    : TextView
    lateinit var etIme   : EditText
    lateinit var etDatum : EditText
    lateinit var sGodina : Spinner
    lateinit var lvLista : ListView

    private lateinit var adapter: LvStudentAdapter

    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState)
        setContentView( R.layout.activity_provider )

        initWidgets( )
        setupListeners( )
    }

    private fun setupListeners( ) {


        lvLista.setOnItemClickListener { adapterView, view, i, l ->
            val id = view.findViewById< TextView >( R.id.tvID ).text.toString( )
            // contentResolver.delete(Uri.parse(TasksContract.CONTENT_URI.toString() + id), null, null)
            val cursor = contentResolver.query( Uri.parse( CONTENT_URI.toString( ) +"/"+ id ), null, null, null, null )

            if ( cursor != null && cursor.moveToFirst( ) ) {
                val ime = cursor?.getString( cursor.getColumnIndex( COLUMN_IME ) )!!
                val rodendan = cursor?.getInt( cursor.getColumnIndex( COLUMN_DATUM_RODENJA ) )
                val godina = cursor?.getInt( cursor.getColumnIndex( COLUMN_GODINA ) )
                val student = Student( id.toLong( ), ime, rodendan, godina)

                tvId.text = id
                etIme.setText( student.ime )
                etDatum.setText(student.datumString( ) )
                sGodina.setSelection( student.godina - 1 )
            }
            cursor?.close( )
        }
    }

    fun initWidgets( ) {
        tvId    = findViewById( R.id.tvId )
        etIme   = findViewById( R.id.etIme )
        etDatum = findViewById( R.id.etDatum )
        sGodina = findViewById( R.id.sGodina )
        lvLista = findViewById( R.id.lvListaStudenata )

        val cursor = contentResolver.query( CONTENT_URI, null, null, null, null )
        adapter = cursor?.let { LvStudentAdapter( this, it, 0 ) }!!
        lvLista.adapter = adapter
    }

    fun insert( v : View ) {
        val student = ucitaj( )
        val contentValues = ContentValues( )
        contentValues.put( COLUMN_IME, student.ime )
        contentValues.put( COLUMN_DATUM_RODENJA, student.datum )
        contentValues.put( COLUMN_GODINA, student.godina )

        contentResolver.insert( CONTENT_URI, contentValues )
        refreshData( )
        ocistiFormu( )
    }

    fun update( v : View ) {
        val student = ucitaj( )
        val contentValues = ContentValues( )
        contentValues.put( COLUMN_IME, student.ime )
        contentValues.put( COLUMN_DATUM_RODENJA, student.datum )
        contentValues.put( COLUMN_GODINA, student.godina )

        contentResolver.update( Uri.parse( CONTENT_URI.toString( ) +"/"+ student.id ), contentValues, null, null )
        refreshData( )
        ocistiFormu( )
    }

    fun delete( v : View ) {
        val student = ucitaj( )

        contentResolver.delete( Uri.parse( CONTENT_URI.toString( ) +"/"+ student.id ), null )
        refreshData( )
        ocistiFormu( )
    }



    private fun refreshData( ) {
        val cursor = contentResolver.query( CONTENT_URI, null, null, null, null )
        adapter.swapCursor( cursor )
    }

    fun ucitaj( ) : Student {
        val sdfCitanje = SimpleDateFormat( "dd.MM.yyyy." )
        val sdfInt     = SimpleDateFormat( "yyyyMMdd" )
        val id : Long? = if( tvId.text=="" ) null else tvId.text.toString( ).toLong( )
        val ime = etIme.text.toString( )
        val rodendan = sdfInt.format( sdfCitanje.parse( etDatum.text.toString( ) ) ).toInt( )
        val godina = sGodina.selectedItemPosition+1

        return Student( id, ime, rodendan, godina )
    }

    fun ocistiFormu( ) {
        tvId.setText( "" )
        etIme.setText( "" )
        etDatum.setText( "" )
        sGodina.setSelection( 0 )
    }
}