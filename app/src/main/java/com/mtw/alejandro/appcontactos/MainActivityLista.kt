package com.mtw.alejandro.appcontactos

import android.arch.lifecycle.LiveData
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.mtw.alejandro.appcontactos.database.AppDatabase
import com.mtw.alejandro.appcontactos.database.ContactoEntity
import com.mtw.alejandro.appcontactos.database.RegistroEntity
import com.mtw.alejandro.appcontactos.helper.doAsync

import kotlinx.android.synthetic.main.activity_main_lista.*
import kotlinx.android.synthetic.main.content_main_activity_lista.*
import java.util.*
import com.mtw.alejandro.appcontactos.registro.ObtenerWebService
import kotlinx.android.synthetic.main.content_registro.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONArray
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivityLista : AppCompatActivity() {
    companion object {
        // Extra for the task ID to be received in the intent
        val EXTRA_CONTACTO_CORREO = "extraContactoCorreo"
    }
    private lateinit var sCorreo: String
    private lateinit var sCont: String
    private lateinit var viewAdapter: ContactoAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    val contactoList: List<ContactoEntity> = ArrayList()
    var JSONParam: JSONObject? = null
    var hilo: ObtenerWebService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var reg: RegistroEntity? = null
        setContentView(R.layout.activity_main_lista)
        setSupportActionBar(toolbar)
        val intent = intent
        if (intent != null && intent.hasExtra(MainActivityLista.EXTRA_CONTACTO_CORREO)) {
            sCorreo = intent.getStringExtra(MainActivityLista.EXTRA_CONTACTO_CORREO)
        }

        try {
            com.mtw.alejandro.appcontactos.helper.doAsync{
                val regist = AppDatabase.getInstance(this@MainActivityLista)?.registroDao()?.loadAllRegistro()
                runOnUiThread{
                    if (regist == null){
                        val intent = Intent(this,registro::class.java)
                        startActivity(intent)
                    }
                    else {
                        sCorreo = regist.correo
                        sCont = regist.contrasena
                    }
                }
            }.execute()
        }
        catch (e : Exception ){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
        }

        viewManager = LinearLayoutManager(this)
        viewAdapter = ContactoAdapter(contactoList, this, { contacto: ContactoEntity -> onItemClickListener(contacto) })

        recyclerViewTasks.apply {
            setHasFixedSize(true)

            layoutManager = viewManager
            adapter = viewAdapter
            addItemDecoration(DividerItemDecoration(this@MainActivityLista, DividerItemDecoration.VERTICAL))
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                doAsync{
                    val position = viewHolder.adapterPosition
                    val contactos= viewAdapter.getTasks()
                    contactos[position].estado = "B"
                    AppDatabase.getInstance((this@MainActivityLista))?.contactoDao()?.updateContacto(contactos[position])
                    retrieveContactos()
                }.execute()

            }
        }).attachToRecyclerView(recyclerViewTasks)

        fab.setOnClickListener { view ->
            val addContactoIntent = Intent(this@MainActivityLista, MainActivityAddEdit::class.java)
            addContactoIntent.putExtra(MainActivityAddEdit.EXTRA_CONTACTO_CORREO, sCorreo)
            startActivity(addContactoIntent)
        }
    } // Fin del onCreate

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main_activity_lista, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.opcRespaldo -> {
                var n: Int = 0
                var i: Int = 0
                var contactList : List<ContactoEntity>? = ArrayList()
                var ContactoJson: JSONObject
                var jsonArray: JSONArray = JSONArray()
                var jsonParam = JSONObject()
                doAsync {
                    contactList = AppDatabase.getInstance((this@MainActivityLista))?.contactoDao()?.getBackup()
                    runOnUiThread{
                        try {
                            jsonParam.put("Correo",sCorreo)
                            jsonParam.put("contrasena",sCont)
                            for (item in contactList.orEmpty()){
                                ContactoJson = JSONObject()
                                ContactoJson.put("idContacto", item.id.toString())
                                ContactoJson.put("NomContacto", item.nomcontacto.toString())
                                ContactoJson.put("DomContacto", item.domcontacto.toString())
                                ContactoJson.put("Cp", item.cpcontacto.toString())
                                ContactoJson.put("CorContacto", item.corcontacto.toString())
                                ContactoJson.put("TelCasa", item.telcasa.toString())
                                ContactoJson.put("TelCelular", item.telcelular.toString())
                                ContactoJson.put("recFecha", item.recfecha.toString())
                                ContactoJson.put("Estado", item.estado.toString())
                                ContactoJson.put("FechaAlta", item.fechalta.toString())
                                jsonArray.put(ContactoJson)
                            }
                            jsonParam.put("contacto", jsonArray)
                            JSONParam = jsonParam
                        }
                        catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        hilo = ObtenerWebService() // Genera la instancia del hilos
                        hilo?.execute()
                    }
                }.execute()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onItemClickListener(contacto: ContactoEntity) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent
        val intent = Intent(this,MainActivityDetalle::class.java)
        intent.putExtra(MainActivityDetalle.EXTRA_CONTACTO_ID, contacto.id)
        startActivity(intent)
        //Toast.makeText(this, "Clicked item" + task.description, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        retrieveContactos()
    }

    private fun retrieveContactos() {
        doAsync {
            val tasks = AppDatabase.getInstance(this@MainActivityLista)?.contactoDao()?.getAll()
            runOnUiThread {
                viewAdapter.setTask(tasks!!)
            }
        }.execute()
    }


    //-----------------------------------------------------------------------------
    inner class ObtenerWebService() : AsyncTask<String, String, String>() {
        var correo : String = ""
        override fun doInBackground(vararg params: String?): String {
            var url: URL? = null
            var devuelve = ""
            try {
                val urlConn: HttpURLConnection
                val printout: DataOutputStream
                val input: DataInputStream
                url = URL("http://192.168.1.70/contactosk/ResContactos.php")
                //  Abrimos la conexión hacia el servicio web alojado en el servidor
                urlConn = url.openConnection() as HttpURLConnection
                urlConn.doInput = true
                urlConn.doOutput = true
                urlConn.useCaches = false
                urlConn.setRequestProperty("Content-Type", "application/json")
                urlConn.setRequestProperty("Accept", "application/json")
                urlConn.connect()
                // Creando parametros que vamos a enviar
                //Envio de parametros por el método post
                val os = urlConn.outputStream
                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                // Escribe los datos a través de los métodos flush() y write()
                writer.write(JSONParam.toString())
                writer.flush()
                writer.close()
                val respuesta = urlConn.responseCode

                val result = StringBuilder()
                // Preguntamos si se pudo conectar al servidor con exito
                if (respuesta == HttpURLConnection.HTTP_OK) {
                    // El siguiente proceso de hace por que JSONObject necesita un string para
                    // concatenar lo que envio el servicio web de regreso qu es un JSON
                    val inStream: InputStream = urlConn.inputStream
                    val isReader = InputStreamReader(inStream)
                    val bReader = BufferedReader(isReader)
                    var tempStr: String?
                    while (true) {
                        tempStr = bReader.readLine()
                        if (tempStr == null) {
                            break
                        }
                        result.append(tempStr)
                    }
                    urlConn.disconnect()
                    devuelve = result.toString() // Regresa un JSON al método onPostExecute
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return devuelve

        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            val devuelve = ""
            var resultado: String = ""
            var sentencia = ""

            try {
                val respuestaJSON = JSONObject(s.toString())
                val resultJSON = respuestaJSON.getString("success") //Obtiene el primer campo de JSON que es string y se llama estado

                when (resultJSON) {
                    "200"   // Respaldo de los contactos
                    -> {
                        val messageJSON1 = respuestaJSON.getString("message")
                        resultado = messageJSON1
                        doAsync {
                            AppDatabase.getInstance(this@MainActivityLista)!!.contactoDao().updByEdo()
                        }.execute()
                        Toast.makeText(this@MainActivityLista, resultado, Toast.LENGTH_SHORT).show()
                    }
                    "422"  // Falta Información en el web service
                    -> {
                        val messageJSON2 = respuestaJSON.getString("message")
                        Toast.makeText(baseContext, messageJSON2, Toast.LENGTH_LONG).show()
                    }
                    "500"  // Error al insertar el registro
                    -> {
                        val messageJSON3 = respuestaJSON.getString("message")
                        Toast.makeText(baseContext, messageJSON3, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: JSONException) {
                //e.printStackTrace();
                Toast.makeText(this@MainActivityLista, "Error:" + e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
