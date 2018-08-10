package com.mtw.alejandro.appcontactos.database

import android.arch.persistence.room.*
@Dao
interface RegistroDao {

        @Query("select * from registro_entity")
        fun loadAllRegistro(): RegistroEntity

        @Insert
        fun insertRegistro(registroEntity: RegistroEntity)

        @Update(onConflict = OnConflictStrategy.REPLACE)
        fun updateRegistro(registroEntity: RegistroEntity)
}
