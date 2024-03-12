package com.example.studymore2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;

public class TelaCadastro extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private static final String TAG = "GoogleSignIn";
    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro);

        mAuth = FirebaseAuth.getInstance();

        Button btnCadastro = findViewById(R.id.botaoEntrar);
        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarUsuario();
            }
        });

    }

    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private static final int RC_SIGN_IN = 123;


    //Dentro do método firebaseAuthWithGoogle
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(TelaCadastro.this, "Autenticação com Google bem-sucedida", Toast.LENGTH_SHORT).show();
                            abrePrincipal();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(TelaCadastro.this, "Falha ao autenticar com o Google", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void cadastrarUsuario() {
        EditText editTextUsuario = findViewById(R.id.editTextTextUsuario);
        EditText editTextSenha = findViewById(R.id.editTextTextSenha);

        final String email = editTextUsuario.getText().toString().trim();
        String senha = editTextSenha.getText().toString().trim();

        if (!isValidEmail(email)) {
            Toast.makeText(TelaCadastro.this, "Por favor, insira um e-mail válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica se o e-mail já está cadastrado
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(this, new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    SignInMethodQueryResult result = task.getResult();
                    if (result != null && result.getSignInMethods() != null && result.getSignInMethods().size() > 0) {
                        // E-mail já cadastrado, exiba uma mensagem ou tome outra ação necessária
                        Toast.makeText(TelaCadastro.this, "E-mail já cadastrado. Tente usar outro e-mail.", Toast.LENGTH_SHORT).show();
                    } else {
                        // E-mail ainda não cadastrado, prossiga com o processo de cadastro
                        cadastrarUsuarioFirebase(email, senha); // Corrigido aqui
                    }
                } else {
                    // Tratamento de erro ao verificar o e-mail
                    Log.w(TAG, "fetchSignInMethodsForEmail:failure", task.getException());
                    Toast.makeText(TelaCadastro.this, "Erro ao verificar e-mail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cadastrarUsuarioFirebase(String email, String senha) { // Renomeado o método para evitar conflitos de nome
        if (!email.isEmpty() && !senha.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                Toast.makeText(TelaCadastro.this, "Cadastro " + currentUser.getEmail() + " bem-sucedido", Toast.LENGTH_SHORT).show();
                                abrePrincipal();
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(TelaCadastro.this, "Falha ao cadastrar usuário", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(TelaCadastro.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrePrincipal() {
        Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
        startActivity(intent);
        finish();
    }
}