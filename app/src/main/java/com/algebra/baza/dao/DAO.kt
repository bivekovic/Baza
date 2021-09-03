package com.algebra.baza.dao

import com.algebra.baza.model.Student

interface DAO {
    fun insert( s : Student ) : Boolean
    fun update( s : Student ) : Boolean
    fun delete( s : Student ) : Boolean
    fun getAll( )             : List< Student >
}