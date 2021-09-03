package com.algebra.baza.model

import java.lang.Exception

class Student( var id : Long?, var ime : String, var datum : Int, var godina : Int ) {

    fun datumString( ) : String {
        try {
            val d = datum % 100
            val m = datum % 10000 / 100
            val g = datum / 10000
            return ( if( d<10 ) "0$d" else "$d" ) + "." +
                   ( if( m<10 ) "0$m" else "$m" ) + "." +
                   "$g."
        } catch ( e : Exception ) { }
        return ""
    }

    override fun toString(): String {
        return "{ id:$id, ime:\"$ime\", datum:\"$datum\", godina:$godina }"
    }

    fun copy( ) : Student {
        return Student( id, ime, datum, godina )
    }
}

// 20010506 06.05.2021.