package com.example.studymore2;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.studymore2.databinding.ActivityPrincipalBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PrincipalActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    ActivityPrincipalBinding binding;

    private void deletarUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PrincipalActivity.this, "Conta deletada com sucesso", Toast.LENGTH_SHORT).show();
                                // Faça aqui qualquer ação adicional após a exclusão bem-sucedida, se necessário.
                                Intent intent = new Intent(getApplicationContext(), TelaLogin.class);
                                startActivity(intent);
                            } else {
                                Log.w(TAG, "Falha ao deletar conta", task.getException());
                                Toast.makeText(PrincipalActivity.this, "Erro ao deletar conta", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(PrincipalActivity.this, "Nenhum usuário logado", Toast.LENGTH_SHORT).show();
        }
    }
    public void onClickDeletarConta(View view) {
        deletarUsuario();
    }
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