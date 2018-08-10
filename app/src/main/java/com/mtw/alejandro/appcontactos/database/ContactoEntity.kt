package com.mtw.alejandro.appcontactos.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "contacto_entity",
        foreignKeys = arrayOf(
             ForeignKey(entity = RegistroEntity::class,
                        parentColumns = arrayOf("correo"),
                        childColumns = arrayOf("correo"),
        onDelete = ForeignKey.CASCADE)))
data class ContactoEntity (
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,
    @ColumnInfo(name = "nom_contacto")
    var nomcontacto:String = "",
    @ColumnInfo(name = "dom_contacto")
    var domcontacto:String = "",
    @ColumnInfo(name = "cp_contacto")
    var cpcontacto:String = "",
    @ColumnInfo(name = "cor_contacto")
    var corcontacto:String = "",
    @ColumnInfo(name = "tel_casa")
    var telcasa:String = "",
    @ColumnInfo(name = "tel_celular")
    var telcelular:String,
    @ColumnInfo(name = "rec_fecha")
    var recfecha:Date,
    var estado:String,
    @ColumnInfo(name = "fech_alta")
    var fechalta:Date,
    var correo:String
)