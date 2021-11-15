package mx.tecnm.tepic.ladm_u3_ejercicio2_sql_firebase
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import javax.xml.transform.TransformerFactory

class BD(
    context: Context?,
    name:String?,
    factory:SQLiteDatabase.CursorFactory?,
    version:Int

):SQLiteOpenHelper(context,name,factory,version){
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE NOTA(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, TITULO VARCHAR(100),CONTENIDO VARCHAR(500),HORA VARCHAR(200),FECHA VARCHAR(200))")
    }
    //Lugar Hora Fecha Descripcion
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}



}