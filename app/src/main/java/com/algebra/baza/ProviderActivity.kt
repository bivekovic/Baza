package com.algebra.baza

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.algebra.baza.model.Student
import com.algebra.baza.podaci.*
import com.algebra.baza.ui.LvStudentAdapter
import com.algebra.baza.provider.CONTENT_URI
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ProviderActivity : AppCompatActivity() {

    private val TAG = "ProviderActivity"

    lateinit var tvId    : TextView
    lateinit var etIme   : EditText
    lateinit var etDatum : EditText
    lateinit var sGodina : Spinner
    lateinit var rgSpol  : RadioGroup
    lateinit var rbM     : RadioButton
    lateinit var rbZ     : RadioButton
    private lateinit var lvLista : ListView

    private lateinit var adapter : LvStudentAdapter

    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_provider )
        initWidgets( )
        setupListeners( )
    }

    override fun onCreateOptionsMenu( menu: Menu? ): Boolean {
        menuInflater.inflate( R.menu.main, menu )
        menu?.findItem( R.id.bPromijeniActivity )?.title = "Preko baze"
        return super.onCreateOptionsMenu( menu )
    }

    override fun onOptionsItemSelected( item: MenuItem ): Boolean {
        if( item.itemId==R.id.bPromijeniActivity ) {
            finish( )
            return true
        }
        return super.onOptionsItemSelected( item )
    }

    fun initWidgets( ) {
        tvId    = findViewById( R.id.tvId )
        etIme   = findViewById( R.id.etIme )
        etDatum = findViewById( R.id.etDatum )
        sGodina = findViewById( R.id.sGodina )
        rgSpol  = findViewById( R.id.rgSpol )
        rbM     = findViewById( R.id.rbM )
        rbZ     = findViewById( R.id.rbZ )

        lvLista = findViewById( R.id.lvListaStudenata )

        val cursor = contentResolver.query( CONTENT_URI, null, null, null, null )  // SELECT * FROM student
        adapter = cursor?.let{ LvStudentAdapter( this, it, 0 ) }!!
        lvLista.adapter = adapter
    }

    private fun setupListeners( ) {
        lvLista.setOnItemClickListener { av, view, i, l ->
            val id = view.findViewById< TextView >( R.id.tvID ).text.toString( )
            val cursor = contentResolver.query( Uri.parse( CONTENT_URI.toString( ) +"/"+ id ), null, null, null, null )

            if( cursor!=null && cursor.moveToFirst() ) {
                val id       = cursor?.getLong( cursor.getColumnIndex( COLUMN_ID ) )!!
                val ime      = cursor?.getString( cursor.getColumnIndex( COLUMN_IME ) )
                val rodendan = cursor?.getInt( cursor.getColumnIndex( COLUMN_DATUM_RODENJA ) )
                val godina   = cursor?.getInt( cursor.getColumnIndex( COLUMN_GODINA ) )
                val spol     = cursor?.getString( cursor.getColumnIndex( COLUMN_SPOL ) )

                val student = Student( id, ime, rodendan, godina, spol )

                tvId.text = ""+ id
                etIme.setText( ime )
                etDatum.setText( student.datumString() )
                sGodina.setSelection( student.godina-1 )
                rgSpol.clearCheck( )
                Log.i( TAG, "Spol studenta/ice: ${student.spol}")
                if( student.spol=="Ž" ) rgSpol.check( R.id.rbZ )
                else if( student.spol=="M" ) {
                    Log.i( TAG, "Postavljam RadioButton na M...")
                    rgSpol.check( R.id.rbM )
                }
            }
            cursor?.close( )
        }
    }

    private fun refreshData( ) {
        val cursor = contentResolver.query( CONTENT_URI, null, null, null, null )
        adapter.swapCursor( cursor )
    }

    fun insert( v : View ) {
        val student = ucitaj( )

        val values = ContentValues( )
        values.put( COLUMN_IME,           student.ime )
        values.put( COLUMN_DATUM_RODENJA, student.datum )
        values.put( COLUMN_GODINA,        student.godina )
        values.put( COLUMN_SPOL,          student.spol )

        contentResolver.insert( CONTENT_URI, values )
        refreshData( )
        ocistiFormu( )

    }

    fun update( v : View ) {
        val student = ucitaj( )

        val values = ContentValues( )
        values.put( COLUMN_IME,           student.ime )
        values.put( COLUMN_DATUM_RODENJA, student.datum )
        values.put( COLUMN_GODINA,        student.godina )
        values.put( COLUMN_SPOL,          student.spol )

        contentResolver.update( Uri.parse( CONTENT_URI.toString( ) +"/"+ student.id ), values, null, null )
        refreshData( )
        ocistiFormu( )

    }

    fun delete( v : View ) {
        val student = ucitaj( )

        contentResolver.delete( Uri.parse( CONTENT_URI.toString( ) +"/"+ student.id ), null, null )
        refreshData( )
        ocistiFormu( )

    }

    fun ucitaj( ) : Student {
        val sdfCitanje = SimpleDateFormat( "dd.MM.yyyy." )
        val sdfInt     = SimpleDateFormat( "yyyyMMdd" )
        val id : Long? = if( tvId.text=="" ) null else tvId.text.toString( ).toLong( )
        val ime = etIme.text.toString( )
        val rodendan = sdfInt.format( sdfCitanje.parse( etDatum.text.toString( ) ) ).toInt( )
        val godina = sGodina.selectedItemPosition+1
        val spol   = if( rbM.isChecked ) "M"
        else if( rbZ.isChecked ) "Ž"
        else ""

        return Student( id, ime, rodendan, godina, spol )
    }

    fun ocistiFormu( ) {
        tvId.setText( "" )
        etIme.setText( "" )
        etDatum.setText( "" )
        sGodina.setSelection( 0 )
        rgSpol.clearCheck( )
    }

    fun odabirdatuma( v : View ) {
        val dan = Calendar.getInstance( )
        try {
            dan.time = SimpleDateFormat( "dd.MM.yyyy." ).parse( etDatum.text.toString( ) )
        } catch ( e : Exception) { }
        DatePickerDialog(
            this,
            {
                    dp, g, m, d ->
                val mm = m+1      // Mjeseci su numerirani od 0 do 11 (ne od 1 do 12)
                etDatum.setText(
                    ( if( d<10 ) "0$d" else "$d" ) + "." +
                            ( if( mm<10 ) "0$mm" else "$mm" ) + "." +
                            "$g."
                )
            },
            dan.get( Calendar.YEAR ),
            dan.get( Calendar.MONTH ),
            dan.get( Calendar.DAY_OF_MONTH )
        ).show( )
    }
}