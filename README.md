# Projeto de Estudo: Criação de Login com Firebase

Este projeto foi desenvolvido no Android Studio como parte do meu aprendizado em desenvolvimento mobile. O principal foco é explorar conceitos fundamentais, especialmente na criação de login e cadastro de um sistema.

## Objetivo do Projeto

O objetivo é proporcionar uma introdução prática e gradual ao desenvolvimento Android, com ênfase na criação de contas de usuários

## Instruções de Uso

1. Instale o Android Studio: Certifique-se de ter o Android Studio instalado em seu ambiente de desenvolvimento.
2. Baixe o Projeto: Clone ou baixe o repositório deste projeto.
3. Teste o Código: Abra o projeto no Android Studio e teste o código em um emulador ou dispositivo Android.

## Implementação de Ícone

Para criar uma experiência mais próxima de um sistema real, foi utilizado o conceito de criação e implementação de um ícone para o aplicativo. Veja abaixo o código para a implementação do ícone: 

![Aparencia do icon](/img/ICON%20DO%20APP.jpeg) 

```
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/icone_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

Além disso, a utilização de variáveis para cores e imagens permite uma manutenção mais eficiente do código.

## Aproveitamento de Código


Aproveitando a ideia de reutilização, foram criados arquivos distintos para textos, como no exemplo abaixo:

``` <resources>
    <string name="app_name">Study more</string>
    <string name="edit_usuario">Usuário</string>
    <string name="edit_senha">Senha</string>
    <string name="botao_entrar">Entrar</string>
    <string name="botao_cadastra">salvar</string>
    <string name="text_ou">Ou</string>
    <string name="botao_cadastro">Não possui conta? Se cadastre</string>
    <string name="logov">sair</string>
    <string name="web_client_id">########</string>
</resources>
```
Essa abordagem facilita a alteração de informações importantes, como chaves de API, tornando o código mais seguro e fácil de manter


## Tela de Apresentação 

Em vez de direcionar o usuário diretamente para a tela de login ou cadastro, foi implementada uma tela intermediária que é exibida por alguns segundos:

```    @Override
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
```
![Aparencia do home](/img/tela%20home%20.png)

## Resultado das telas 
![Aparencia do home](/img/tela%20de%20login%20.png)

![Aparencia do home](/img/tela%20de%20cadastro.png)

## Curiosidade

Caso você não faça logout e feche o aplicativo, na próxima vez que abrir, entrará diretamente sem a necessidade de fazer login. Isso proporciona uma experiência mais conveniente para o usuário.

## Considerações Finais

Este projeto representa uma jornada de aprendizado no desenvolvimento Android, abordando aspectos cruciais como autenticação de usuários e design eficiente. As práticas adotadas, como a modularização do código e a utilização de recursos externos, visam facilitar a manutenção e aprimoramento contínuo do aplicativo.

Sinta-se à vontade para explorar e modificar o projeto de acordo com suas necessidades e, se surgirem dúvidas, não hesite em consultar a documentação do Android Studio ou buscar suporte na comunidade de desenvolvedores.

Divirta-se programando e continue explorando novas possibilidades no vasto mundo do desenvolvimento mobile!