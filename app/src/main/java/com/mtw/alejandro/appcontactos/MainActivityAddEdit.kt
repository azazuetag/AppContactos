package com.mtw.alejandro.appcontactos

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.mtw.alejandro.appcontactos.R.id.*
import com.mtw.alejandro.appcontactos.database.AppDatabase
import com.mtw.alejandro.appcontactos.database.ContactoEntity
import com.mtw.alejandro.appcontactos.helper.doAsync

import kotlinx.android.synthetic.main.activity_main_add_edit.*
import kotlinx.android.synthetic.main.content_main_activity_add_edit.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivityAddEdit : AppCompatActivity() {
    companion object {
        // Extra for the task ID to be received in the intent
        val EXTRA_CONTACTO_ID = "extraContactoId"
        val EXTRA_CONTACTO_CORREO = "extraContactoCorreo"
        private val DEFAULT_CONTACTO_ID: Int = -1
    }
    private var mContactoId: Int = DEFAULT_CONTACTO_ID
    private var sCorreo: String = ""
    private var fechAlta: Date? = null
    private  var sEstado: String = ""
    private  var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_add_edit)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        etFecha.setOnClickListener {
            val c = Calendar.getInstance()
            var day = c.get(Calendar.DAY_OF_MONTH)
            var month = c.get(Calendar.MONTH)
            var year = c.get(Calendar.YEAR)
            if (etFecha.length() != 0){
                val sFec : String = etFecha.text.toString()
                val fecha : Date = convFecha(sFec)
                day = dayFromDate(fecha)
                month = monthFromDate(fecha)
                year = yearFromDate(fecha)
            }

            val dpd = DatePickerDialog(this, android.R.style.Theme_Holo_Dialog, DatePickerDialog.OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
                val sMes = "$monthOfYear"
                val iMes = sMes.toInt()+1
                etFecha.setText("$year-" + iMes.toString() + "-$dayOfMonth")
            }, year, month, day)

            //show datepicker
            dpd.show()
        }

        val intent = intent
        if (intent != null && intent.hasExtra(EXTRA_CONTACTO_ID)) {
            if (mContactoId === DEFAULT_CONTACTO_ID) {
                // populate the UI
                mContactoId = intent.getIntExtra(EXTRA_CONTACTO_ID, DEFAULT_CONTACTO_ID)
                if (mContactoId != 0) {
                    doAsync {
                        var contacto = AppDatabase.getInstance(this@MainActivityAddEdit)?.contactoDao()?.findById(mContactoId)
                        runOnUiThread {
                            populateUI(contacto!!)
                        }
                    }.execute()
                }
            }
        }
        else
        {
            if (intent != null && intent.hasExtra(EXTRA_CONTACTO_CORREO)) {
                sCorreo =  intent.getStringExtra(EXTRA_CONTACTO_CORREO)
                mContactoId = 0
            }
        }

        fab.setOnClickListener { view ->
                doAsync {
                    var sfec = etFecha.text.toString()
                    if (etFecha.length() == 0)
                       sfec = "1900-01-01"

                    val fecha = convFecha(sfec)

                    if (mContactoId != 0) {
                        val contactoEntity = ContactoEntity(id = id,nomcontacto = etNombre.text.toString(),domcontacto = etDomicilio.text.toString(),cpcontacto = etCP.text.toString(), corcontacto = etCorreo.text.toString(),telcelular = etTelCelular.text.toString(),telcasa = etTelCasa.text.toString(),recfecha = fecha,correo = sCorreo, estado = "C", fechalta = fechAlta!! )
                        AppDatabase.getInstance(this@MainActivityAddEdit)?.contactoDao()?.updateContacto(contactoEntity)
                    }else{
                        val contactoEntity = ContactoEntity(nomcontacto = etNombre.text.toString(),domcontacto = etDomicilio.text.toString(),cpcontacto = etCP.text.toString(), corcontacto = etCorreo.text.toString(),telcelular = etTelCelular.text.toString(),telcasa = etTelCasa.text.toString(),recfecha = fecha,correo = sCorreo, estado = "A", fechalta = Date() )
                        AppDatabase.getInstance(this@MainActivityAddEdit)?.contactoDao()?.insertContacto(contactoEntity)
                    }

                    runOnUiThread {
                        val intent = Intent(this,MainActivityLista::class.java)
                        startActivity(intent)
                    }
                }.execute()
        }
    }

    private fun populateUI(contacto: ContactoEntity) {
        if (contacto == null) return
        id = contacto.id
        etNombre.setText(contacto.nomcontacto)
        etDomicilio.setText( contacto.domcontacto)
        etCP.setText(contacto.cpcontacto)
        etCorreo.setText(contacto.corcontacto)
        etTelCelular.setText(contacto.telcelular)
        etTelCasa.setText(contacto.telcasa)
        var formatoDelTexto = SimpleDateFormat("yyyy-MM-dd").format(contacto.recfecha)
        var sFecha = formatoDelTexto.toString()
        if (formatoDelTexto.toString() == "1900-01-01")
            sFecha = ""
        etFecha.setText(sFecha)
        sEstado = contacto.estado
        sCorreo = contacto.correo
        fechAlta = contacto.fechalta
    }

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

    private fun monthFromDate(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.MONTH)
    }
    private fun yearFromDate(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.YEAR)
    }

    private fun dayFromDate(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_MONTH)
    }
}
