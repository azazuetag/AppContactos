package com.mtw.alejandro.appcontactos

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.mtw.alejandro.appcontactos.database.AppDatabase
import com.mtw.alejandro.appcontactos.database.ContactoEntity
import com.mtw.alejandro.appcontactos.helper.doAsync
import android.Manifest
import android.annotation.SuppressLint
import com.mtw.alejandro.appcontactos.R.id.*

import kotlinx.android.synthetic.main.activity_main_detalle.*
import kotlinx.android.synthetic.main.content_main_activity_detalle.*
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat

class MainActivityDetalle : AppCompatActivity() {
    val REQUEST_PHONE_CALL = 1

    companion object {
        val EXTRA_CONTACTO_ID = "extraContactoId"
        private val DEFAULT_CONTACTO_ID: Int = -1

    }
    private var mContactoId: Int = DEFAULT_CONTACTO_ID
    var contacto: ContactoEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_detalle)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        if (intent != null && intent.hasExtra(MainActivityAddEdit.EXTRA_CONTACTO_ID)) {

            if (mContactoId === MainActivityDetalle.DEFAULT_CONTACTO_ID) {
                // populate the UI
                mContactoId = intent.getIntExtra(EXTRA_CONTACTO_ID, DEFAULT_CONTACTO_ID)
                doAsync{
                    contacto = AppDatabase.getInstance(this@MainActivityDetalle)?.contactoDao()?.findById(mContactoId)
                    runOnUiThread{
                        populateUI(contacto!!)
                    }
                }.execute()
            }
        }

        fab.setOnClickListener { view ->
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
                } else {
                    startCall()
                }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startCall(){
        var telefono: String = etTelCasa.text.toString()
        //var tel: String = telefono.substring(10)
        //Toast.makeText(MainActivityDetalle.this, "Marcaste Casa:"+tel, Toast.LENGTH_SHORT).show();
        var callIntent : Intent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:" + telefono)

        startActivity(callIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PHONE_CALL) startCall()
    }

    private fun populateUI(contacto: ContactoEntity) {
        if (contacto == null) return
        tvNombre.append(": "+ contacto.nomcontacto)
        etDomicilio.setText(contacto.domcontacto)
        etCP.setText(contacto.cpcontacto)
        etCorreo.setText(contacto.corcontacto)
        etTelCelular.setText(contacto.telcelular)
        etTelCasa.setText(contacto.telcelular)
        var formatoDelTexto = SimpleDateFormat("yyyy-MM-dd").format(contacto.recfecha)
        var sFecha = formatoDelTexto.toString()
        if (formatoDelTexto.toString() == "1900-01-01")
            sFecha = ""
        etFecha.setText(sFecha)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_detalle, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.opcEditar -> {
                val intent = Intent(this,MainActivityAddEdit::class.java)
                intent.putExtra(MainActivityAddEdit.EXTRA_CONTACTO_ID, contacto?.id)
                startActivity(intent)
                true
            }
            R.id.opcElimina -> {
                doAsync{
                    contacto?.estado = "B"
                    AppDatabase.getInstance((this@MainActivityDetalle))?.contactoDao()?.updateContacto(contacto!!)
                    runOnUiThread{
                        val intent = Intent(this,MainActivityLista::class.java)
                        startActivity(intent)
                    }
                }.execute()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
