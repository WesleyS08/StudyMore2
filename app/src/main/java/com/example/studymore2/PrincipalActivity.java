package com.example.studymore2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


import com.example.studymore2.databinding.ActivityPrincipalBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PrincipalActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    ActivityPrincipalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Infla o layout usando a classe gerada pelo ViewBinding
        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());

        // Obtém a visualização raiz do layout
        View view = binding.getRoot();

        // Define o layout da atividade como a visualização raiz do layout
        setContentView(view);

        binding.botaoSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}