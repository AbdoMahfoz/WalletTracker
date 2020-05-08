package com.example.wallettracker.database

import android.content.Context
import androidx.room.*
import java.util.*

@Database(entities = [SpendEntity::class, GoalEntity::class], version = 1, exportSchema = false)
@TypeConverters(WalletDatabase.Converters::class)
abstract class WalletDatabase : RoomDatabase() {
    abstract val spendRepo : SpendRepo
    abstract val goalRepo : GoalRepo
    companion object{
        @Volatile
        private var INSTANCE: WalletDatabase? = null
        fun get(context: Context): WalletDatabase {
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WalletDatabase::class.java,
                        WalletDatabase::class.simpleName!!
                    ).addMigrations()
                     .fallbackToDestructiveMigration().build()
                }
                INSTANCE = instance
                return instance
            }
        }
    }
    class Converters {
        @TypeConverter
        fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }
        @TypeConverter
        fun dateToTimestamp(date: Date?): Long? = date?.time
        @TypeConverter
        fun importanceToInt(imp: Important?) : Int? = imp?.ordinal
        @TypeConverter
        fun intToImportance(imp: Int?) : Important? = imp?.let{ Important.values()[it] }
    }
}

