package com.mtw.alejandro.appcontactos.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import java.util.*


@Entity(tableName = "registro_entity")

data class RegistroEntity (
    @PrimaryKey
    var correo:String,
    @ColumnInfo(name = "nom_registro")
    var nomregistro:String,
    var contrasena:String,
    @ColumnInfo(name = "fech_alta")
    var fechalta:Date
)
