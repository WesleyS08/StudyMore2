package com.example.studymore2;


// =============================================== Importações =======================================================================
import static android.content.ContentValues.TAG;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studymore2.databinding.ActivityTelaLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
//================================================================================================================================


public class TelaLogin extends AppCompatActivity {

    // =========================================== Declaração de variáveis adicionada para o ViewBinding =====================================
    private ActivityTelaLoginBinding binding;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    // =============================================================================================


    // ====================================== Configurações para Login com Google e BTN de Entrar-======================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Infla o layout usando a classe gerada pelo ViewBinding
        binding = ActivityTelaLoginBinding.inflate(getLayoutInflater());

        // Inicialização da instância do FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Configuração das opções de login do Google
        // requestIdToken é sua chave SHA1 ou a SHA-256 que vc salvou no google cloud (ultilizar a sua chave para web )
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();


        // Configuração do cliente de login do Google
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Obtém a visualização raiz do layout
        View view = binding.getRoot();

        // Define o layout da atividade como a visualização raiz do layout
        setContentView(view);

        // Altera o texto do botão Google (se deixar sem isso ocorre traduçoes automaticas)
        TextView text_botao_logar_google = (TextView) binding.botaoGoogle.getChildAt(0);
        text_botao_logar_google.setText( "Continuar com o Google");

        //-===================================== Configura ao "Entrar"  de clique para o botão de entrada ========================================
        // chama um evento de click
        binding.botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Chama o método para realizar o login com e-mail e senha
                    loginUsuarioESenha(
                            binding.editTextTextUsuario.getText().toString(),
                            binding.editTextTextSenha.getText().toString());
                    // Caso esteja vazio retorna uma mensagem de verificação
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Por favor, verifique seu email e senha.",
                            Toast.LENGTH_LONG).show(); // Toast é como a mensagem sera exibida
                }
            }
        });

        // Configura o ouvinte de clique para o botão Google
        binding.botaoGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(); // Chama o outro metodo para realizar o login
            }
        });


        // Chama a tela para o usuario se cafastrar atravez de email e senha
        binding.cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaLogin.this, TelaCadastro.class);
                startActivity(intent);
            }
        });
    };
    //==============================================================================================================================

    // ========================================= Método para iniciar o processo de login com o Google ==========================================
    private void signIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 1);
        abreActivity.launch(intent);
    }

    ActivityResultLauncher<Intent> abreActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    // Obtem o token da conta que estiver logada
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);

                    try {
                        // Obtém a conta do Google e inicia o processo de login com o Google
                        GoogleSignInAccount conta = task.getResult(ApiException.class);
                        if (conta != null) {
                            // Inicia o processo de login com o Google, passando o token
                            if (mAuth.getCurrentUser() == null) {
                                // Se o usuário não estiver autenticado, redirecione-o para a tela de cadastro
                                Intent cadastroIntent = new Intent(TelaLogin.this, TelaCadastro.class);
                                cadastroIntent.putExtra("googleToken", conta.getIdToken()); // Passe o token do Google para a tela de cadastro
                                startActivity(cadastroIntent);
                            } else {
                                // Se o usuário já estiver autenticado, faça o login com o Google
                                LoginComGoogle(conta.getIdToken());
                            }
                        }
                        // Log de sucesso
                        Log.i("LoginComGoogle", "Login com Google bem-sucedido");
                        // caso de erro retorna uma mensagem
                    } catch (ApiException exception) {
                        // Log de falha
                        Log.e("LoginComGoogle", "Erro no login com Google: " + exception.getStatusCode());
                        Toast.makeText(getApplicationContext(), "Login com Google falho, verifique se possui conta ",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
    // ===============================================================================================================================

    //===========================  Método para realizar o login com o Google usando o token recebido ===================================
    private void LoginComGoogle(String token) {
        // Criação de uma credencial de autenticação usando o token do Google
        AuthCredential credential = GoogleAuthProvider.getCredential(token, null);

        // Inicia o processo de login com a credencial gerada
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Se o login com o Google for bem-sucedido, obtenha as informações do usuário
                GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

                if (googleSignInAccount != null) {
                    // Obtém informações do usuário (por exemplo, e-mail e nome)
                    String userEmail = googleSignInAccount.getEmail();
                    String userName = googleSignInAccount.getDisplayName();

                    // Exibe uma mensagem de sucesso usando o Toast
                    Toast.makeText(getApplicationContext(), "Login com Email " + userEmail + " realizado com sucesso",
                            Toast.LENGTH_SHORT).show();

                    // Abre a atividade principal após o login bem-sucedido
                    abrePrincipal();

                    // Finaliza a atividade atual
                    finish();
                } else {
                    // Caso não seja possível obter informações do usuário do Google
                    Toast.makeText(getApplicationContext(), "Falha ao obter informações do usuário do Google", Toast.LENGTH_LONG).show();
                }
            } else {
                // Caso ocorra uma falha no login com o Google
                String errorMessage = "Erro ao realizar login com o Google";
                if (task.getException() != null) {
                    errorMessage += ": " + task.getException().getMessage();

                    // Adiciona uma mensagem de erro ao console usando Log
                    Log.e(TAG, errorMessage);
                }

                // Exibe uma mensagem de erro usando Toast
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
    //===========================================================================================================================


    // ========================================== Tesultado do login com Google ===================================================
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 1) {
            // Verifica se a solicitação foi para o código 1, que é usado para login com o Google
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                // Obtém a conta do Google a partir da intenção
                GoogleSignInAccount conta = task.getResult(ApiException.class);

                // Chama o método para realizar o login com o token da conta do Google
                LoginComGoogle(conta.getIdToken());
            } catch (ApiException exception) {
                // Se houver um erro ao obter a conta do Google
                String errorMessage = "Erro ao fazer login com o Google";
                if (exception.getMessage() != null) {
                    errorMessage += ": " + exception.getMessage();
                }
                Log.e(TAG, errorMessage); // Adiciona esta linha para imprimir o erro no console
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
    //===========================================================================================================================



    // ====================================== Método para realizar o login com e-mail e senha =============================================
    private void loginUsuarioESenha(String usuario, String senha) {
        mAuth.signInWithEmailAndPassword(usuario, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Usuário logado com sucesso
                            Log.i(TAG, "signInWithEmailAndPassword: success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Login realizado com sucesso",
                                    Toast.LENGTH_SHORT).show();
                            abrePrincipal();
                        } else {
                            // Falha no login
                            Log.w(TAG, "signInWithEmailAndPassword: failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Erro ao realizar login.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //===========================================================================================================================


    // =================================== Método para abrir a atividade principal ==============================================
    private void abrePrincipal() {
        // Limpa os camps de email e senha
        binding.editTextTextUsuario.setText("");
        binding.editTextTextSenha.setText("");
        // vai pra tela de Login
        Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
        startActivity(intent);
        // e finaliza o login para não ficar login em cima de login
        finish();
    }
    //===========================================================================================================================


    // ====================================== Método chamado quando o login  é iniciado =========================================
    // Método chamado quando o login é iniciado
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Toast.makeText(getApplicationContext(), "Usuário: " + currentUser.getEmail() + " Logado",
                    Toast.LENGTH_LONG).show();
            abrePrincipal();
        }
    }
    //===========================================================================================================================
}
