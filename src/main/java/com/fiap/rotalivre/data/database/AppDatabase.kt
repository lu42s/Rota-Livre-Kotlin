package com.fiap.rotalivre.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fiap.rotalivre.data.dao.OcorrenciaDao
import com.fiap.rotalivre.data.entity.Ocorrencia

@Database(entities = [Ocorrencia::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ocorrenciaDao(): OcorrenciaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rota_livre_db"
                )
                    // Versão 2 adicionou o campo "perfilAfetado".
                    // Em protótipo acadêmico, recriamos o banco em vez de escrever uma migração.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
