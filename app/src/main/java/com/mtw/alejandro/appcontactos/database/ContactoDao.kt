package com.mtw.alejandro.appcontactos.database

import android.arch.persistence.room.*

@Dao
interface ContactoDao {

    @Query("select * from contacto_entity where estado <> 'B' order by nom_contacto")
    fun getAll(): List<ContactoEntity>

    @Query("select * from contacto_entity where id = :idArg")
    fun findById(idArg: Int): ContactoEntity

    @Query("select * from contacto_entity where id > 1 and estado <> 'G'  Order by id")
    fun getBackup(): List<ContactoEntity>

    @Query("Update contacto_entity set estado = 'G' where estado = 'A' or estado = 'C'")
    fun updByEdo()

    @Insert
    fun insertContacto(contactoEntity: ContactoEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateContacto(contactoEntity: ContactoEntity)

}