package mx.tecnm.tepic.ladm_u3_ejercicio2_sql_firebase

import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //firestore
    var BDF= FirebaseFirestore.getInstance();
    //sql
    var baseDatos=BD(this,"basedatos1",null,1)

    var idSeleccionadoEnLista=-1
    var listaID=ArrayList<String>()
    var datos=ArrayList<String>()
    var DATA= ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //se cargan las notas anteriores
        cargarNota()

        this.registerForContextMenu(listaNotas)

        btnAgregar.setOnClickListener {
            var ventana= Intent(this,MainActivity2::class.java)
            startActivity(ventana)
            finish()
        }
        listaNotas.setOnItemClickListener { adapterView, view, i, l ->
            idSeleccionadoEnLista=i
            Toast.makeText(this,"Se selecciono el elemento "+idSeleccionadoEnLista, Toast.LENGTH_LONG).show()
        }

        btnSincronizar.setOnClickListener {
            sincronizar()
        }

    }

    fun cargarNota(){
        datos.clear()
        listaID.clear()
        try{
            var trans=baseDatos.readableDatabase
            var notas=ArrayList<String>()
            var respuesta=trans.query("NOTA", arrayOf("*"),null,null,null,null,null)

            if (respuesta.moveToFirst()){
                do{
                    var concatenacion="Titulo: ${respuesta.getString(1)}\nContenido: ${respuesta.getString(2)}\nHora: ${respuesta.getString(3)}\n" +
                            "Fecha: ${respuesta.getString(4)}"
                    notas.add(concatenacion)
                    datos.add(concatenacion)
                    listaID.add(respuesta.getInt(0).toString())
                }while (respuesta.moveToNext())

            }else{
                notas.add("NO TIENES NOTAS AGREGADAS")
            }
            listaNotas.adapter=
                ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,notas)
            this.registerForContextMenu(listaNotas)
            trans.close()
        }catch (e: SQLiteException){mensaje("ERROR: "+e.message!!)}
    }

    fun mensaje(s:String){
        AlertDialog.Builder(this)
            .setTitle("ATENCIÓN")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->d.dismiss()}
            .show()
    }

    private fun sincronizar(){
        DATA.clear()///Traer la informacion de Firestores
        BDF.collection("NOTA").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                mensaje("Error! No se pudo recuperar data desde FireBase")
                return@addSnapshotListener
            }
            var cadena = ""
            for (registro in querySnapshot!!) {
                cadena = registro.id.toString()////IDS
                DATA.add(cadena)///.....IDS.....IDS
            }
            try {
                var trans = baseDatos.readableDatabase
                var respuesta = trans.query("NOTA", arrayOf("*"), null, null, null, null, null)
                if (respuesta.moveToFirst()) {
                    do{
                        BDF.waitForPendingWrites()
                        if (DATA.any{respuesta.getString(0).toString()==it})//////id de la tabla
                        {
                            DATA.remove(respuesta.getString(0).toString())
                            BDF.collection("NOTA")
                                .document(respuesta.getString(0))
                                .update("TITULO",respuesta.getString(1),
                                    "CONTENIDO",respuesta.getString(2),
                                    "HORA",respuesta.getString(3),"FECHA",respuesta.getString(4)
                                ).addOnSuccessListener {
                                   BDF.waitForPendingWrites()
                                }.addOnFailureListener {
                                    AlertDialog.Builder(this)
                                        .setTitle("Error")
                                        .setMessage("NO SE PUDO ACTUALIZAR\n${it.message!!}")
                                        .setPositiveButton("Ok"){d,i->}
                                        .show()
                                }
                        } else {
                            var datosInsertar = hashMapOf(
                                "TITULO" to respuesta.getString(1),
                                "CONTENIDO" to respuesta.getString(2),
                                "HORA" to respuesta.getString(3),
                                "FECHA" to respuesta.getString(4)
                            )
                            BDF.collection("NOTA").document("${respuesta.getString(0)}")
                                .set(datosInsertar as Any).addOnSuccessListener {
                                    //mensaje("SE INSERTÓ CORRECTAMENTE")
                                }
                                .addOnFailureListener {
                                    mensaje("ERROR AL INSERTAR:\n${it.message!!}")
                                }
                        }
                    }while (respuesta.moveToNext())

                } else {
                    datos.add("NO TIENES NOTAS AGREGADAS")
                }
                trans.close()
            } catch (e: SQLiteException) {
                mensaje("ERROR: " + e.message!!)
            }
            var el = DATA.subtract(listaID)
            //////1,2,3,4 data(fire)
            /////1,2,4      (sql)
            //////3
            if (el.isEmpty()) {

            } else {
                el.forEach {
                    BDF.collection("NOTA")
                        .document(it)
                        .delete()
                        .addOnSuccessListener {}
                        .addOnFailureListener { mensaje("ERROR: NO SE PUDO ELIMINAR\n" + it.message!!) }
                }
            }

        }
        mensaje("SE SINCRONIZÓ CON EXITO")
    }



    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        var inflaterOB=menuInflater
        //cargar  un XML y CONSTRUIR un objeto Kotlin a partir de esa carga= INFLATE
        inflaterOB.inflate(R.menu.menuopc,menu)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        if(idSeleccionadoEnLista==-1){
            mensaje("Error! Debes dar click primero en un item pra actualizar o borrar")
            return true
        }

        when(item.itemId){
            R.id.itemactualizar->{

                Toast.makeText(this,listaID.get(idSeleccionadoEnLista), Toast.LENGTH_LONG)

                var itent= Intent(this,MainActivity3::class.java)

                itent.putExtra("idactualizar",listaID.get(idSeleccionadoEnLista))
                itent.putExtra("ct",Bundle.CREATOR.javaClass)
                startActivity(itent)
                finish()
            }
            R.id.itemeliminar->{
                var idEliminar=listaID.get(idSeleccionadoEnLista)
                AlertDialog.Builder(this)
                    .setTitle("ATENCIÓN")
                    .setMessage("ESTAS SEGURO QUE DESEA ELIMINAR EL ID: "+idEliminar+"?")
                    .setPositiveButton("ELIMINAR"){d,i->
                        eliminar(idEliminar)
                        cargarNota()
                    }
                    .setNeutralButton("NO"){d,i->}
                    .show()
            }
        }
        idSeleccionadoEnLista=-1
        return true

    }

    fun eliminar(idEliminar:String):Boolean{
        val tabla = BD(this,"basedatos1",null,1)
        try {
            var trans=tabla.writableDatabase
            var resultado=trans.delete("NOTA","ID=?",
                arrayOf(idEliminar))
            trans.close()
            return resultado != 0

        }catch (e: SQLiteException){
            mensaje(e.message!!)
        }
        return false
    }

}