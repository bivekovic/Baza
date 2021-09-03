package com.algebra.baza.ui

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.algebra.baza.MainActivity
import com.algebra.baza.R
import com.algebra.baza.model.Student


class StudentAdapter( val context : Context, val studenti : List< Student > ) : RecyclerView.Adapter< StudentHolder >( ) {

    val TAG = "StudentAdapter"

    init{
        Log.i( TAG, "Init: dobio sam ${studenti.size} studenata" )
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentHolder {
        return StudentHolder( LayoutInflater.from( context ), parent, context )
    }

    override fun onBindViewHolder( holder: StudentHolder, position: Int ) {
        holder.bind( studenti[position] )
        if( position%2==0 )
            holder.itemView.setBackgroundColor( Color.parseColor( "#ffeeee" ) )
        else
            holder.itemView.setBackgroundColor( Color.parseColor( "#bbbbbb" ) )
    }

    override fun getItemCount( ): Int {
        Log.i( TAG, "getItemCount: imam ${studenti.size} studenata" )
        return studenti.size
    }

}

class StudentHolder(inflater: LayoutInflater, parent : ViewGroup?, val context : Context )
    : RecyclerView.ViewHolder( inflater.inflate( R.layout.student, parent, false ) ), View.OnClickListener {

    private val TAG = "StudentHolder"

    private var tvID              : TextView
    private var tvIme             : TextView
    private var tvRodendan        : TextView
    private var tvGodina          : TextView
    private lateinit var student  : Student

    init {
        tvID       = itemView.findViewById( R.id.tvID )
        tvIme      = itemView.findViewById( R.id.tvIme )
        tvRodendan = itemView.findViewById( R.id.tvRodendan )
        tvGodina   = itemView.findViewById( R.id.tvGodina )
        itemView.setOnClickListener( this )
    }

    fun bind( student : Student ) {
        Log.i( TAG, "init: Pišem redak za studenta ${student} studenata" )
        this.student = student
        tvID.text       = "${student.id}"
        tvIme.text      = student.ime
        tvRodendan.text = student.datumString( )
        tvGodina.text   = "${student.godina}"
    }

    override fun onClick( v: View? ) {
        val act   = context as MainActivity
        act.tvId.setText( student.id.toString( ) )
        act.etIme.setText( student.ime.toString( ) )
        act.etDatum.setText( student.datumString( ) )
        act.sGodina.setSelection( student.godina-1 )
    }
}