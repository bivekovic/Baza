package com.algebra.baza.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import com.algebra.baza.podaci.Baza
import com.algebra.baza.podaci.COLUMN_ID
import com.algebra.baza.podaci.TABLE_STUDENT
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

const val CONTENT_AUTHORITY = "com.algebra.baza.provider"

val CONTENT_AUTHORITY_URI = Uri.parse( "content://$CONTENT_AUTHORITY" )
val CONTENT_URI           = Uri.withAppendedPath( CONTENT_AUTHORITY_URI, TABLE_STUDENT )

class StudentProvider : ContentProvider( ) {

    val TAG = "StudentProvider"

    private val STUDENTS   = 100
    private val STUDENT_ID = 101

    private val uriMatcher = UriMatcher( UriMatcher.NO_MATCH )

    init {
        // Prihvacam samo dvije vrste zahtjeva prema svom Content Provideru:
        // 1. content://com.algebra.baza.provider/student
        uriMatcher.addURI( CONTENT_AUTHORITY, TABLE_STUDENT, STUDENTS )
        // 2. content://com.algebra.baza.provider/student/<neki broj>
        uriMatcher.addURI( CONTENT_AUTHORITY, "$TABLE_STUDENT/#", STUDENT_ID )
    }

    override fun onCreate( ): Boolean {
        Log.d( TAG, "onCreate: Provider se kreira" )
        return true
    }

    override fun insert( uri: Uri, cv: ContentValues? ): Uri? {
        Log.d( TAG, "insert: Pozvan insert sa uri-em: $uri" )
        val match = uriMatcher.match( uri )
        Log.d( TAG, "insert: prepoznao sam operaciju: $match" )

        val recordId  : Long
        val returnUri : Uri

        if( match==STUDENTS ) {
            val db = context?.let { Baza.getInstance( it ).writableDatabase }
            recordId = db?.insert( TABLE_STUDENT, null, cv ) ?: 0
            if( recordId==-1L )
                throw SQLException( "Failed to insert; Uri was $uri" )
            else
                returnUri = ContentUris.withAppendedId( CONTENT_URI, recordId )
        } else
            throw IllegalArgumentException( "Unknown uri for INSERT operation: $uri" )

        Log.d( TAG, "Exiting insert; returning $returnUri" )
        return returnUri
    }

    override fun update( uri: Uri, cv: ContentValues?, selection: String?, selectionArgs: Array<out String>? ): Int {
        Log.d( TAG, "insert: Pozvan insert sa uri-em: $uri" )
        val match = uriMatcher.match( uri )
        Log.d( TAG, "insert: prepoznao sam operaciju: $match" )

        val count             : Int
        var selectionCriteria : String
        val db                         = context?.let { Baza.getInstance( it ).writableDatabase }

        when( match ) {
            STUDENTS -> {
                count = db?.update( TABLE_STUDENT, cv, selection, selectionArgs ) ?: 0
            }
            STUDENT_ID -> {
                selectionCriteria = "$COLUMN_ID = ${ContentUris.parseId( uri )}"
                if( selection!=null && selection.isNotEmpty( ) )
                    selectionCriteria += " AND ($selection)"
                count = db?.update( TABLE_STUDENT, cv, selectionCriteria, selectionArgs ) ?: 0
            } else ->
            throw IllegalArgumentException( "Unknown uri for UPDATE operation: $uri" )
        }

        Log.d( TAG, "Exiting update; returning $count" )
        return count
    }

    override fun delete( uri: Uri, selection: String?, selectionArgs: Array<out String>? ): Int {
        Log.d( TAG, "delete: Pozvan update sa uri-em: $uri" )
        val match = uriMatcher.match( uri )
        Log.d( TAG, "delete: prepoznao sam operaciju: $match" )

        val count             : Int
        var selectionCriteria : String
        val db                         = context?.let { Baza.getInstance( it ).writableDatabase }

        when( match ) {
            STUDENTS -> {
                count = db?.delete( TABLE_STUDENT, selection, selectionArgs ) ?: 0
            }
            STUDENT_ID -> {
                selectionCriteria = "$COLUMN_ID = ${ContentUris.parseId( uri )}"
                if( selection!=null && selection.isNotEmpty( ) )
                    selectionCriteria += " AND ($selection)"
                count = db?.delete( TABLE_STUDENT, selectionCriteria, selectionArgs ) ?: 0
            } else ->
            throw IllegalArgumentException( "Unknown uri for DELETE operation: $uri" )
        }

        Log.d( TAG, "Exiting delete; returning $count" )
        return count
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d( TAG, "query: Pozvan select sa uri-em: $uri" )
        val match = uriMatcher.match( uri )
        Log.d( TAG, "query: prepoznao sam operaciju: $match" )

        val queryBuilder = SQLiteQueryBuilder( )
        queryBuilder.tables = TABLE_STUDENT
        if( match==STUDENT_ID ) {
            queryBuilder.appendWhere( "$COLUMN_ID=")
            queryBuilder.appendWhereEscapeString( "${ContentUris.parseId( uri )}")
        } else if( match!=STUDENTS )
            throw IllegalArgumentException( "Unknown uri for QUERY operation: $uri" )

        val db = context?.let { Baza.getInstance( it ).readableDatabase }
        val cursor = queryBuilder.query( db, projection, selection, selectionArgs, null, null, sortOrder )

        Log.d( TAG, "Exiting query; returning ${cursor.count} rows" )
        return cursor

    }

    override fun getType( uri: Uri ): String? {
        val match = uriMatcher.match( uri )

        return when( match ) {
            STUDENTS   -> "vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY.$TABLE_STUDENT"
            STUDENT_ID -> "vnd.android.cursor.item/vnd.$CONTENT_AUTHORITY.$TABLE_STUDENT"
            else       -> throw IllegalArgumentException( "Unknown uri: $uri" )
        }
    }
}