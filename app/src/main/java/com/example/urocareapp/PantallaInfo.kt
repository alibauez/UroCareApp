package com.example.urocareapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge

class PantallaInfo : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantallainfo)


    }
}
