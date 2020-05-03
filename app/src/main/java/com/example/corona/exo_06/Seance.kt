package com.example.corona.exo_06

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.sql.Time
import java.time.LocalDateTime
import java.util.Date

@Entity
data class Seance(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val salle: String?,
    val enseignant: String?,
    val module : String?,
    val dateDebut : Date?,
    val dateFin : Date?
)