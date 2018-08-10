package com.mtw.alejandro.appcontactos

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.mtw.alejandro.appcontactos.R.id.etCorreoR
import com.mtw.alejandro.appcontactos.database.AppDatabase
import com.mtw.alejandro.appcontactos.database.ContactoEntity
import com.mtw.alejandro.appcontactos.database.RegistroEntity
import com.mtw.alejandro.appcontactos.helper.doAsync

import kotlinx.android.synthetic.main.activity_registro.*
import kotlinx.android.synthetic.main.content_registro.*
import org.jetbrains.anko.startActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class registro : AppCompatActivity() {
    var hilo: ObtenerWebService? = null
    var resultado: String? = null
    var corr : String =""
    var codErr : String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            if (etCorreoR.text.length == 0 || etNombreR.text.length == 0 || etPwdR.text.length == 0)
            {
                Snackbar.make(view, "Error: Faltan datos", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                etCorreoR.requestFocus()
            }
            else
            {
                corr = etCorreoR.text.toString()
                val nom = etNombreR.text.toString()
                val pwd = etPwdR.text.toString()
                //val registroEntity = RegistroEntity(correo = corr,nomregistro = nom, contrasena = pwd ,fechalta = Date())
                //val contactoEntity = ContactoEntity(nomcontacto = "Emergencias", telcasa = "911", telcelular = "911",estado = "E",fechalta = Date(),correo = corr,recfecha = Date())
                doAsync{
                    hilo = ObtenerWebService()
                    hilo?.execute("Insert", "3", corr, nom,pwd)
                    runOnUiThread {
                    }
                }.execute()
            }
        }
    }

    fun Recupera(view: View){
        if (etCorreoR.text.length == 0)
        {
            Snackbar.make(view, "Error: es necesario el correo para recuperar contraseña", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            etCorreoR.requestFocus()
        }
        else
        {
            corr = etCorreoR.text.toString()
            val nom = ""
            val pwd = ""
            //val registroEntity = RegistroEntity(correo = corr,nomregistro = nom, contrasena = pwd ,fechalta = Date())
            //val contactoEntity = ContactoEntity(nomcontacto = "Emergencias", telcasa = "911", telcelular = "911",estado = "E",fechalta = Date(),correo = corr,recfecha = Date())
            doAsync{
                hilo = ObtenerWebService()
                hilo?.execute("Insert", "3", corr, nom,pwd)
                runOnUiThread {

                }
            }.execute()
        }
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
            if (params[3] == "" && params[4] == "") {
                url = URL("http://192.168.1.70/contactosk/recuperaCorreo.php")
            } else {
                url = URL("http://192.168.1.70/contactosk/RegContactos.php")
            }
            //  Abrimos la conexión hacia el servicio web alojado en el servidor
            urlConn = url.openConnection() as HttpURLConnection
            urlConn.doInput = true
            urlConn.doOutput = true
            urlConn.useCaches = false
            urlConn.setRequestProperty("Content-Type", "application/json")
            urlConn.setRequestProperty("Accept", "application/json")
            urlConn.connect()
            // Creando parametros que vamos a enviar
            val jsonParam = JSONObject()

            correo = params[2].toString()
            jsonParam.put("Correo", params[2])
            jsonParam.put("NomRegistro", params[3])
            jsonParam.put("contrasena", params[4])

            //Envio de parametros por el método post
            val os = urlConn.outputStream
            val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
            // Escribe los datos a través de los métodos flush() y write()
            Log.d("ZAZUETA1", jsonParam.toString())
            writer.write(jsonParam.toString())
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
        var nom: String
        var dom: String
        var cp: String
        var corr: String
        var telCa: String
        var telCe: String
        var rf: String
        var drf:Date
        var edo: String
        var fec: String
        var dfec: Date
        var sentencia: String
        val inte = Intent(this@registro, MainActivityLista::class.java)
        inte.putExtra(MainActivityLista.EXTRA_CONTACTO_CORREO, correo)

        //Toast.makeText(Registro.this, s, Toast.LENGTH_SHORT).show();
        try {
            val respuestaJSON = JSONObject(s.toString())
            val resultJSON = respuestaJSON.getString("success") //Obtiene el primer campo de JSON que es string y se llama estado
            //resultado.setText(s.toString());
            codErr = resultJSON
            when (resultJSON) {
                "205"  // Recuperación de contraseña
                -> {
                    val messageJSON3 = respuestaJSON.getString("message")
                    Toast.makeText(baseContext, "" + messageJSON3, Toast.LENGTH_LONG).show()
                }
                "201"   // Se inserto correctamente uno nuevo
                -> {
                    doAsync {
                        val registroEntity = RegistroEntity(correo = etCorreoR.text.toString(), nomregistro = etNombreR.text.toString(), contrasena = etPwdR.text.toString(), fechalta = Date())
                        val contactoEntity = ContactoEntity(nomcontacto = "Emergencias", telcasa = "911", telcelular = "911", estado = "E", fechalta = Date(), correo = etCorreoR.text.toString(), recfecha = Date())
                        AppDatabase.getInstance(this@registro)!!.registroDao().insertRegistro(registroEntity)
                        AppDatabase.getInstance(this@registro)!!.contactoDao().insertContacto(contactoEntity)
                    }.execute()
                    val messageJSON1 = respuestaJSON.getString("message")
                    resultado = messageJSON1
                    startActivity(inte)
                }
                "202"   // Ya esta dado de alta y no tiene contactos
                -> {
                    doAsync {
                        val registroEntity = RegistroEntity(correo = etCorreoR.text.toString(), nomregistro = etNombreR.text.toString(), contrasena = etPwdR.text.toString(), fechalta = Date())
                        val contactoEntity = ContactoEntity(nomcontacto = "Emergencias", telcasa = "911", telcelular = "911", estado = "E", fechalta = Date(), correo = etCorreoR.text.toString(), recfecha = Date())
                        AppDatabase.getInstance(this@registro)!!.registroDao().insertRegistro(registroEntity)
                        AppDatabase.getInstance(this@registro)!!.contactoDao().insertContacto(contactoEntity)
                    }.execute()
                    val messageJSON2 = respuestaJSON.getString("message")
                    resultado = messageJSON2
                    startActivity(inte)
                }
                "200"  //Ya esta dada de alta y recupera contactos
                -> {
                    doAsync {
                        val registroEntity = RegistroEntity(correo = etCorreoR.text.toString(), nomregistro = etNombreR.text.toString(), contrasena = etPwdR.text.toString(), fechalta = Date())
                        val contactoEntity = ContactoEntity(nomcontacto = "Emergencias", telcasa = "911", telcelular = "911", estado = "E", fechalta = Date(), correo = etCorreoR.text.toString(), recfecha = Date())
                        AppDatabase.getInstance(this@registro)!!.registroDao().insertRegistro(registroEntity)
                        AppDatabase.getInstance(this@registro)!!.contactoDao().insertContacto(contactoEntity)
                    }.execute()
                    val alumnosJSON = respuestaJSON.getJSONArray("contacto")
                    // Lee el segundo parametro que es un arreglo que trae la información de los alumnos
                    // idalumno, nombre, direccion

                    if (alumnosJSON.length() > 1) {
                        for (i in 0 until alumnosJSON.length()) {
                            nom = alumnosJSON.getJSONObject(i).getString("NomContacto")
                            dom = alumnosJSON.getJSONObject(i).getString("DomContacto")
                            cp = alumnosJSON.getJSONObject(i).getString("Cp")
                            corr = alumnosJSON.getJSONObject(i).getString("CorContacto")
                            telCa = alumnosJSON.getJSONObject(i).getString("TelCasa")
                            telCe = alumnosJSON.getJSONObject(i).getString("TelCelular")
                            rf = alumnosJSON.getJSONObject(i).getString("recFecha")
                            drf = convFecha(rf)
                            edo = alumnosJSON.getJSONObject(i).getString("Estado")
                            fec = alumnosJSON.getJSONObject(i).getString("FechaAlta")
                            dfec = convFecha(fec)
                            val contactoEntity = ContactoEntity(nomcontacto = nom, domcontacto = dom, telcasa = telCa, telcelular = telCe, estado = edo, fechalta = dfec, correo = etCorreoR.text.toString(), recfecha = drf)
                            doAsync {
                                AppDatabase.getInstance(this@registro)!!.contactoDao().insertContacto(contactoEntity)
                            }.execute()
                        }
                        startActivity(inte)
                    } else {
                        //No hay contactos
                    }
                }
                "422"  // Falta Información en el web service
                -> {
                    val messageJSON4 = respuestaJSON.getString("message")
                    Toast.makeText(baseContext,"Error 422: " + messageJSON4, Toast.LENGTH_LONG).show()
                }
                "500"  // Error al insertar el registro
                -> {
                    val messageJSON3 = respuestaJSON.getString("message")
                    Toast.makeText(baseContext, "Error 500: " +messageJSON3, Toast.LENGTH_LONG).show()
                }
                "505"  // Error al recuperar contraseña
                -> {
                    val messageJSON3 = respuestaJSON.getString("message")
                    Toast.makeText(baseContext, "Error 505: " +messageJSON3, Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: JSONException) {
            Toast.makeText(this@registro, "Error:" + e.toString(), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} // fin del web service

    // Función para convertir string a Fecha
    private fun convFecha(sFec: String): Date
    {
        var formatoDelTexto = SimpleDateFormat("yyyy-MM-dd")
        var fecha: Date? = null
        try {
            fecha = formatoDelTexto.parse(sFec);
        } catch (ex: ParseException) {
            val sFec1 = "1900-01-01"
            fecha = formatoDelTexto.parse(sFec1);
        }
        return  fecha!!
    }
}
