package com.example.music_app4b.model

// ARCHIVO PARA RECIBIR LA RESPUESTA DE CONFIRMACIÓN DEL API

data class ITunesResponse(
    val resultCount : Int,
    val results : List<Album>
)
