package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [ProductEntity::class, Product::class],
  version = 2,
  exportSchema = false
)
abstract class VedashilaDatabase : RoomDatabase() {

  abstract fun productDao(): ProductDao

  companion object {
    @Volatile
    private var INSTANCE: VedashilaDatabase? = null

    fun getDatabase(context: Context): VedashilaDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          VedashilaDatabase::class.java,
          "vedashila_offline_catalog.db"
        )
          .fallbackToDestructiveMigration(dropAllTables = true)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
