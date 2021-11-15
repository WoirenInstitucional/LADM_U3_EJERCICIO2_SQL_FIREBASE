package mx.tecnm.tepic.ladm_u3_ejercicio2_sql_firebase

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {


    var baseDatos=BD(this,"basedatos1",null,1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        btnInsertar.setOnClickListener {
            insertar()
        }

        btnRegresar.setOnClickListener {
            var ventana= Intent(this,MainActivity::class.java)
            startActivity(ventana)
            finish()
        }

    }
    fun insertar(){
        try {
            var trans=baseDatos.writableDatabase
            var variables= ContentValues()
            variables.put("TITULO",txtTitulo.text.toString())
            variables.put("CONTENIDO",txtContenido.text.toString())
            variables.put("HORA",txtHora.text.toString())
            variables.put("FECHA",txtFecha.text.toString())

            var respuesta =trans.insert("NOTA",null,variables)

            if(respuesta==-1L){
                mensaje("FALLO AL INSERTAR")
            }else{
                mensaje("INSERCION EXITOSA")
                var ventana= Intent(this,MainActivity::class.java)
                startActivity(ventana)
                finish()
            }
            trans.close()
        }catch (e: SQLiteException){
            mensaje(e.message!!)
        }

    }
    private fun mensaje(s:String){
        AlertDialog.Builder(this)
            .setTitle("ATENCIÓN")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->d.dismiss()}
            .show()
    }

}