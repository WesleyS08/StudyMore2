package com.example.studymore2;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studymore2.DBHelper;
import com.example.studymore2.MainActivity;
import com.example.studymore2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class PrincipalActivity extends AppCompatActivity {
    private FirebaseAuth mAuth; // Instância do FirebaseAuth para autenticação
    private DBHelper dbHelper; // Instância do DBHelper para manipulação do banco de dados
    private RecyclerView recyclerViewMaterias; // RecyclerView para exibir as matérias do usuário
    private MateriasAdapter materiasAdapter; // Adaptador para a RecyclerView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        // Inicializar a instância do FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Inicializar a instância do DBHelper
        dbHelper = new DBHelper(this);

        // Obter a referência da RecyclerView das matérias
        recyclerViewMaterias = findViewById(R.id.recyclerViewMaterias);

        // Configurar o layout manager da RecyclerView
        recyclerViewMaterias.setLayoutManager(new LinearLayoutManager(this));

        // Configurar o OnClickListener para o botão de sair
        findViewById(R.id.botao_sair).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fazer logout do usuário atual
                mAuth.signOut();

                // Redirecionar para a MainActivity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Chama o método para mostrar os dados do banco de dados
        mostrarDadosDoBanco();
    }

    // Método para lidar com o clique no botão de adicionar matéria
    public void onClickAdicionarMateria(View view) {
        exibirDialogoAdicionarMateria();
    }

    // Método para exibir o diálogo para adicionar uma nova matéria
    private void exibirDialogoAdicionarMateria() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adicionar Matéria");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String novaMateria = input.getText().toString();
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (!novaMateria.isEmpty() && currentUser != null) {
                    // Adicionar a nova matéria ao banco de dados
                    adicionarMateriaAoBanco(novaMateria, currentUser.getUid());
                    // Atualizar a exibição das matérias
                    mostrarDadosDoBanco();
                } else {
                    Toast.makeText(PrincipalActivity.this, "Nome da matéria não pode estar vazio", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Método para adicionar uma nova matéria ao banco de dados
    private void adicionarMateriaAoBanco(String nomeMateria, String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NOME, nomeMateria);
        values.put(DBHelper.COLUMN_USER_ID, userId); // Adiciona o ID do usuário
        long newRowId = db.insert(DBHelper.TABLE_MATERIAS, null, values);
        db.close();
    }

    // Método para mostrar os dados do banco de dados na RecyclerView
    private void mostrarDadosDoBanco() {
        ArrayList<String> materiasList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Consulta ao banco de dados para obter as matérias do usuário atual
            Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_MATERIAS +
                    " WHERE " + DBHelper.COLUMN_USER_ID + " = ?", new String[]{userId});

            // Iterar sobre o cursor e adicionar as matérias à lista
            if (cursor.moveToFirst()) {
                do {
                    String materiaNome = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOME));
                    materiasList.add(materiaNome);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        }

        // Configurar o adapter da RecyclerView para exibir as matérias
        materiasAdapter = new MateriasAdapter(this, materiasList);
        recyclerViewMaterias.setAdapter(materiasAdapter);
    }

    // Método para lidar com o clique no botão de deletar conta
    private void deletarUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Excluir todas as matérias associadas ao ID do usuário
            excluirMateriasDoUsuario(user.getUid());

            // Em seguida, excluir o próprio usuário
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PrincipalActivity.this, "Conta deletada com sucesso", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
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

    // Método para excluir as matérias associadas ao ID do usuário
    private void excluirMateriasDoUsuario(String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_MATERIAS, DBHelper.COLUMN_USER_ID + "=?", new String[]{userId});
        db.close();
    }

    // Método chamado quando o botão de deletar conta é clicado
    public void onClickDeletarConta(View view) {
        deletarUsuario();
    }
}
