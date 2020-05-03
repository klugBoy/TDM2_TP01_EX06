package com.example.corona.exo_06

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SeanceDao {
    @Query("SELECT * FROM seance")
    fun getAll(): List<Seance>

    @Insert
    fun insertAll(vararg seances: Seance)


    @Query("DELETE FROM seance")
    fun deleteAll()

    @Query("SELECT * FROM seance WHERE salle = :salle")
    fun findBySalle(salle: String): List<Seance>

    @Query("SELECT * FROM seance WHERE enseignant = :enseignant")
    fun findByEnseignant(enseignant: String): List<Seance>

    @Query("SELECT * FROM seance WHERE module = :module")
    fun findByModule(module: String): List<Seance>

    @Query("SELECT * FROM seance WHERE dateDebut BETWEEN :dayst AND :dayet")
    fun findByDate(dayst: Long,dayet:Long): List<Seance>

    @Query("SELECT DISTINCT module FROM seance")
    fun getAllModule(): List<String>

    @Query("SELECT DISTINCT salle FROM seance")
    fun getAllSalle(): List<String>

    @Query("SELECT DISTINCT enseignant FROM seance")
    fun getAllEnseignant(): List<String>



}