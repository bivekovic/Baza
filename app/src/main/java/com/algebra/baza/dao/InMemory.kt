package com.algebra.baza.dao

import android.util.Log
import com.algebra.baza.model.Student

class InMemory : DAO {

    private val TAG = "InMemory"

    private val lista = mutableListOf< Student >( )

    init {
        lista.add( Student( 1, "Pero",   20001211, 3, "M" ) )
        lista.add( Student( 2, "Mate",   19990112, 4, "M" ) )
        lista.add( Student( 3, "Ana",    20010803, 1, "Ž" ) )
        lista.add( Student( 4, "Marija", 19970622, 5, "Ž" ) )
    }

    override fun insert( s : Student ): Boolean {
        var maxId = 0L
        for( student in lista ) {
            val sId = student.id
            if( sId!=null && sId>maxId )
                maxId = sId
        }
        s.id = maxId+1
        lista.add( s )
        return true
    }

    override fun update( s : Student ): Boolean {
        for( student in lista ) {
            if( student.id == s.id ) {
                student.ime    = s.ime
                student.datum  = s.datum
                student.godina = s.godina
                student.spol   = s.spol
                return true
            }
        }
        return false
    }

    override fun delete( s : Student ): Boolean {
        val n = lista.size
        lista.removeAll { it.id == s.id }
        return n != lista.size
    }

    override fun getAll( ): List< Student > {
        val l = mutableListOf< Student >( )
        for( s in lista )
            l.add( s.copy( ) )
        Log.i( TAG, "Vraćam ${l.size} studenata" )
        return l

    }
}