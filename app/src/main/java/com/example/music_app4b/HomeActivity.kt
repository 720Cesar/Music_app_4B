package com.example.music_app4b

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.music_app4b.model.Album
import com.example.music_app4b.model.ITunesResponse
import com.example.music_app4b.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvResultado : TextView
    private lateinit var contenedorIndie : LinearLayout
    private lateinit var contenedorRock : LinearLayout
    private lateinit var contenedorTOP : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)


        tvResultado = findViewById(R.id.tvResultado)
        contenedorIndie = findViewById(R.id.contenedorIndie)
        contenedorRock = findViewById(R.id.contenedorRock)
        contenedorTOP = findViewById(R.id.contenedorTOP)


        // TODO: Agregar método de llamado de álbum
        obtenerAlbum("indie", contenedorIndie)
        obtenerAlbum("rock", contenedorRock)
        obtenerAlbum("twenty one pilots", contenedorTOP)

    }

    // MÉTODO PARA OBTENER INFORMACIÓN DEL ALBUM
    private fun obtenerAlbum(genero : String, contenedor : LinearLayout){

        // Hacer el llamado a la construcción de la URL
        RetrofitClient.api.obtenerAlbumGenero(
            genero = genero,
            pais = "MX",
            media = "music",
            entidad = "album",
            limite = 10
        ).enqueue( object : Callback<ITunesResponse>{

            // MÉTODO EN CASO DE QUE LA RESPUESTA SEA CORRECTA
            override fun onResponse(call: Call<ITunesResponse?>, response: Response<ITunesResponse?>) {

                // Variable para guardar el número de albumes consultados
                // Variable que guarda una lista de tipo Album
                val albumes = response.body()?.results ?: emptyList()

                // Validar si la variable albumes tiene información
                if(albumes.isNotEmpty()){

                    tvResultado.visibility = View.GONE
                    mostrarAlbumes(albumes, contenedor)

                }else{
                    Toast.makeText(this@HomeActivity, "No hay respuesta del API", Toast.LENGTH_SHORT).show()
                }


            }

            // MÉTODO PARA HACER UNA ACCIÓN SI EL API FALLA
            override fun onFailure(call: Call<ITunesResponse?>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error en el API", Toast.LENGTH_SHORT).show()
            }

        })

    }

    // MÉTODO PARA MOSTRAR EL LISTADO DE DATOS DE UN API
    private fun mostrarAlbumes(albumes: List<Album>, contenedor: LinearLayout){

        // OBLIGATORIO: Evitar elementos duplicados
        contenedor.removeAllViews()

        // Ciclo para recorrer las múltiples peticiones a un API
        for(album in albumes){

            // Usar el LinearLayout con el contenedor
            val vistaAlbum = layoutInflater.inflate(
                R.layout.item_album,
                contenedor,
                false
            )

            val imgPortada = vistaAlbum.findViewById<ImageView>(R.id.imgPortadaAlbum)
            val tvNombreAlbum  = vistaAlbum.findViewById<TextView>(R.id.tvNombreAlbum)
            val tvNombreArtista = vistaAlbum.findViewById<TextView>(R.id.tvNombreArtista)

            // Asignando los nombre obtenidos del API
            tvNombreAlbum.text = album.collectionName ?: "Album sin nombre"
            tvNombreArtista.text = album.artistName ?: "Artista desconocido"

            // Mostrar la imagen en la tarjeta
            Glide.with(this@HomeActivity)
                .load(album.obtenerImagenGrande())
                .into(imgPortada)

            // EVENTO
            vistaAlbum.setOnClickListener {

                // Necesitamos obtener el ID
                val idAlbum = album.collectionId

                // Validación para saber que se cuenta con ID
                if(idAlbum != null){

                    val intent = Intent(this@HomeActivity, DetailsActivity::class.java)

                    // Enviar datos
                    intent.putExtra("ID_ALBUM", idAlbum)
                    intent.putExtra("NOMBRE_ALBUM", album.collectionName)
                    intent.putExtra("ARTISTA_ALBUM", album.artistName)
                    intent.putExtra("GENERO_ALBUM", album.primaryGenreName)
                    intent.putExtra("CANCIONES_ALBUM", album.trackCount)
                    intent.putExtra("IMAGEN_ALBUM", album.obtenerImagenGrande())

                    startActivity(intent)

                }

            }

            // Asignar la vista del contendor a las tarjetas de álbumes
            contenedor.addView(vistaAlbum)

        }





    }

}