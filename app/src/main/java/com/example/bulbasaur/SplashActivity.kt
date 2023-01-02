package com.example.bulbasaur

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Thread.sleep(5000)
        // Aquí puedes agregar cualquier código que desees ejecutar durante la pantalla de inicio,
        // como cargar datos o realizar una petición a una API.

        // Después de que hayas terminado, puedes iniciar la siguiente actividad con:
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        // Y finalmente, cierra la pantalla de inicio con:
        finish()
    }
}
