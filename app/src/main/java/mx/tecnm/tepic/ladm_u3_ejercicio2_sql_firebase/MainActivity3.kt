package mx.tecnm.tepic.ladm_u3_ejercicio2_sql_firebase

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_main2.btnInsertar
import kotlinx.android.synthetic.main.activity_main2.btnRegresar
import kotlinx.android.synthetic.main.activity_main2.txtContenido
import kotlinx.android.synthetic.main.activity_main2.txtFecha
import kotlinx.android.synthetic.main.activity_main2.txtHora
import kotlinx.android.synthetic.main.activity_main2.txtTitulo
import kotlinx.android.synthetic.main.activity_main3.*

class MainActivity3 : AppCompatActivity() {

    var id=""
    var baseDatos=BD(this,"basedatos1",null,1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        var extra=intent.extras
        id=extra!!.getString("idactualizar")!!


        try {
            var base=baseDatos.readableDatabase
            var respuesta=base.query("NOTA", arrayOf("TITULO","CONTENIDO","HORA","FECHA"),"ID=?",
                arrayOf(id),null,null,null,null)
            if(respuesta.moveToFirst()){
                txtTitulo.setText(respuesta.getString(0))
                txtContenido.setText(respuesta.getString(1))
                txtHora.setText(respuesta.getString(2))
                txtFecha.setText(respuesta.getString(3))
            }else{
                mensaje("ERROR! no se encontro ID")
            }
            base.close()

        }catch (e: SQLiteException){
            mensaje(e.message!!)
        }

        btnActualizar.setOnClickListener {
            actualizar(id)
        }

        btnRegresar.setOnClickListener {
            var ventana= Intent(this,MainActivity::class.java)
            startActivity(ventana)
            finish()
        }

    }



    private fun actualizar (id:String){
        try {

            var trans=baseDatos.writableDatabase
            var valores= ContentValues()

            valores.put("TITULO",txtTitulo.text.toString())
            valores.put("CONTENIDO",txtContenido.text.toString())
            valores.put("HORA",txtHora.text.toString())
            valores.put("FECHA",txtFecha.text.toString())

            var res=trans.update("NOTA",valores,"ID=?", arrayOf(id))

            if(res>0){
                Toast.makeText(this,"Se actualizo correctamente la nota",Toast.LENGTH_LONG).show()
                //mensaje("Se actualizo correctamente la nota")
                var itent= Intent(this,MainActivity::class.java)
                startActivity(itent)
                finish()
            }else{
                mensaje("No se pudo actualizar")
            }

        }catch (e:SQLiteException){
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