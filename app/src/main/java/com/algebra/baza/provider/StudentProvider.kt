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

const val CONTENT_AUTHORITY        = "com.algebra.baza.provider"

val CONTENT_AUTHORITY_URI : Uri      = Uri.parse( "content://$CONTENT_AUTHORITY" )
val CONTENT_URI           : Uri      = Uri.withAppendedPath( CONTENT_AUTHORITY_URI, TABLE_STUDENT )

class StudentProvider : ContentProvider( ) {

    private val TAG                      = "StudentProvider"

    private val STUDENTS                 = 100
    private val STUDENT_ID               = 101


    private val uriMatcher  : UriMatcher = UriMatcher( UriMatcher.NO_MATCH )

    init {
        Log.d( TAG, "buildUriMatcher: starts" )
        // e.g. content://com.algebra.myapplication.provider/Students
        uriMatcher.addURI( CONTENT_AUTHORITY, TABLE_STUDENT, STUDENTS )
        // e.g. content://com.algebra.myapplication.provider/Students/8
        uriMatcher.addURI( CONTENT_AUTHORITY, "$TABLE_STUDENT/#", STUDENT_ID )
    }

    override fun onCreate( ): Boolean {
        Log.d( TAG, "onCreate: Provider se kreira" )
        return true
    }

    override fun insert( uri: Uri, values: ContentValues? ): Uri? {
        Log.d(TAG, "insert: called with uri $uri")
        val match = uriMatcher.match( uri )
        Log.d(TAG, "insert: match is $match")

        val recordId: Long
        val returnUri: Uri

        if( match==STUDENTS ) {
                val db = context?.let { Baza.getInstance( it ).writableDatabase }
                recordId = db?.insert( TABLE_STUDENT, null, values) ?: 0L
                if ( recordId != -1L )
                    returnUri = ContentUris.withAppendedId( CONTENT_URI, recordId )
                else
                    throw SQLException( "Failed to insert, Uri was $uri" )
        } else
            throw IllegalArgumentException("Unknown uri: $uri")

        Log.d( TAG, "Exiting insert, returning $returnUri" )
        return returnUri
    }

    override fun delete( uri: Uri, selection: String?, selectionArgs: Array<out String>? ): Int {
        Log.d( TAG, "delete: called with uri $uri" )
        val match = uriMatcher.match(uri)
        Log.d( TAG, "delete: match is $match" )

        val count             : Int
        var selectionCriteria : String
        val db                = context?.let { Baza.getInstance( it ).writableDatabase }

        when ( match ) {

            STUDENTS -> {
                count = db?.delete( TABLE_STUDENT, selection, selectionArgs ) ?: 0
            }

            STUDENT_ID -> {
                selectionCriteria = "$COLUMN_ID = ${ContentUris.parseId( uri )}"

                if (selection != null && selection.isNotEmpty()) {
                    selectionCriteria += " AND ($selection)"
                }
                count = db?.delete( TABLE_STUDENT, selectionCriteria, selectionArgs ) ?: 0
            }
            else -> throw IllegalArgumentException( "Unknown uri: $uri" )
        }

        Log.d( TAG, "Exiting delete, returning $count" )
        return count
    }

    override fun update( uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>? ): Int {
        Log.d( TAG, "update: called with uri $uri" )
        val match = uriMatcher.match( uri )
        Log.d( TAG, "update: match is $match" )

        val count             : Int
        var selectionCriteria : String
        val db                = context?.let { Baza.getInstance( it ).writableDatabase }

        when ( match ) {
            STUDENTS -> {
                count = db?.update( TABLE_STUDENT, values, selection, selectionArgs ) ?: 0
            }
            STUDENT_ID -> {
                selectionCriteria = "$COLUMN_ID = ${ContentUris.parseId( uri )}"

                if ( selection!=null && selection.isNotEmpty( ) ) {
                    selectionCriteria += " AND ($selection)"
                }
                count = db?.update( TABLE_STUDENT, values, selectionCriteria, selectionArgs ) ?: 0
            }
            else -> throw IllegalArgumentException("Unknown uri: $uri")
        }

        Log.d( TAG, "Exiting update, returning $count" )
        return count
    }

    override fun query( uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String? ): Cursor? {
        Log.d( TAG, "query: called with uri $uri" )
        val match = uriMatcher.match( uri )
        Log.d( TAG, "query: match is $match" )

        val queryBuilder = SQLiteQueryBuilder( )
        queryBuilder.tables = TABLE_STUDENT
        if( match==STUDENT_ID ) {
            queryBuilder.appendWhere( "$COLUMN_ID=" )
            queryBuilder.appendWhereEscapeString( "${ContentUris.parseId(uri)}" )
        } else if( match!=STUDENTS )
            throw IllegalArgumentException("Unknown URI: $uri")

        val db = context?.let { Baza.getInstance( it ).readableDatabase }
        val cursor = queryBuilder.query( db, projection, selection, selectionArgs, null, null, sortOrder )
        Log.d( TAG, "query: rows in returned cursor = ${cursor.count}" )
        return cursor
    }

    override fun getType( uri: Uri ): String? {
        val match = uriMatcher.match(uri)

        return when ( match ) {
            STUDENTS   -> "vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY.$TABLE_STUDENT"
            STUDENT_ID -> "vnd.android.cursor.item/vnd.$CONTENT_AUTHORITY.$TABLE_STUDENT"
            else -> throw IllegalArgumentException( "unknown Uri: $uri" )
        }
    }
}