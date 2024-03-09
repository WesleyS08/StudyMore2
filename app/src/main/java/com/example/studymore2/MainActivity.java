package com.example.studymore2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


public class MainActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Adicione um atraso de 2 segundos (2000 milissegundos) antes de iniciar a tela de login
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, TelaLogin.class);
                startActivity(intent);
            }
        }, 1000); // Ajuste o tempo conforme necessário (2 segundos neste exemplo)
    }
}