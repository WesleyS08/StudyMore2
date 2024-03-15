package com.example.studymore2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // Nome e versão do banco de dados
    public static final String DATABASE_NAME = "materias.db";
    private static final int DATABASE_VERSION = 2; // Altere a versão do banco de dados, se necessário

    // Tabela de matérias
    public static final String TABLE_MATERIAS = "materias";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NOME = "nome";
    public static final String COLUMN_USER_ID = "user_id"; // Adicionando a coluna para o ID do usuário

    // Tabela de temas
    public static final String TABLE_TEMAS = "temas";
    public static final String COLUMN_MATERIA_ID = "materia_id";
    public static final String COLUMN_TEMA = "tema";

    // Comando SQL para criação da tabela de matérias
    private static final String SQL_CREATE_TABLE_MATERIAS = "CREATE TABLE " + TABLE_MATERIAS +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NOME + " TEXT, " +
            COLUMN_USER_ID + " TEXT)";

    // Comando SQL para criação da tabela de temas
    private static final String SQL_CREATE_TABLE_TEMAS = "CREATE TABLE " + TABLE_TEMAS +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_MATERIA_ID + " INTEGER, " +
            COLUMN_TEMA + " TEXT, " +
            "FOREIGN KEY(" + COLUMN_MATERIA_ID + ") REFERENCES " + TABLE_MATERIAS + "(" + COLUMN_ID + "))";

    // Construtor da classe DBHelper
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Método chamado quando o banco de dados é criado pela primeira vez
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criação das tabelas quando o banco de dados é criado pela primeira vez
        db.execSQL(SQL_CREATE_TABLE_MATERIAS);
        db.execSQL(SQL_CREATE_TABLE_TEMAS);
    }

    // Método chamado quando é necessário atualizar o banco de dados
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Excluir a tabela de matérias existente
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATERIAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMAS);

        // Recriar a tabela de matérias com a nova estrutura
        onCreate(db);
    }
}
