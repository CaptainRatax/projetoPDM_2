package com.example.projetopdmsam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BaseDados extends SQLiteOpenHelper {

    private static final int VERSAO_BASE_DADOS = 1;
    private static final String NOME_BASE_DADOS = "bd_renergy";

    /* TABELA UTILIZADORES */
    private static final String TABELA_UTILIZADORES = "tb_utilizadores";
    private static final String UTILIZADORES_ID = "Id";
    private static final String UTILIZADORES_NOME = "Nome";
    private static final String UTILIZADORES_USERNAME = "Username";
    private static final String UTILIZADORES_PASSWORD = "Password";
    private static final String UTILIZADORES_EMAIL = "Email";
    private static final String UTILIZADORES_TELEMOVEL = "Telemovel";
    private static final String UTILIZADORES_ISACTIVE = "IsActive";

    /* TABELA OBRAS */
    private static final String TABELA_OBRAS = "tb_obras";
    private static final String OBRAS_ID = "Id";
    private static final String OBRAS_NOME = "Nome";
    private static final String OBRAS_DESCRICAO = "Descricao";
    private static final String OBRAS_MORADA = "Morada";
    private static final String OBRAS_CODIGOPOSTAL = "CodigoPostal";
    private static final String OBRAS_LOCALIDADE = "Localidade";
    private static final String OBRAS_PAIS = "Pais";
    private static final String OBRAS_DATAINICIO = "DataInicio";
    private static final String OBRAS_RESPONSAVEL = "Responsavel";
    private static final String OBRAS_ISACTIVE = "IsActive";

    /* TABELA INSPECOES */
    private static final String TABELA_INSPECOES = "tb_inspecoes";
    private static final String INSPECOES_ID = "Id";
    private static final String INSPECOES_DATAINICIO = "DataInicio";
    private static final String INSPECOES_DATAFIM = "DataFim";
    private static final String INSPECOES_ISFINISHED = "IsFinished";
    private static final String INSPECOES_INSPETORID = "InspetorId";
    private static final String INSPECOES_OBRAID = "ObraId";
    private static final String INSPECOES_ISACTIVE = "IsActive";

    /* TABELA CASOS */
    private static final String TABELA_CASOS = "tb_casos";
    private static final String CASOS_ID = "Id";
    private static final String CASOS_TITULO = "Titulo";
    private static final String CASOS_DESCRICAO = "Descricao";
    private static final String CASOS_IMAGEM = "Imagem";
    private static final String CASOS_INSPECAOID = "InspecaoId";

    public BaseDados(Context context) {
        super(context, NOME_BASE_DADOS, null, VERSAO_BASE_DADOS);
    }

    @Override
    public void onCreate(SQLiteDatabase bd) {

        String QUERY_CREATE_TABLE_UTILIZADORES = "CREATE TABLE " + TABELA_UTILIZADORES + "("
                + UTILIZADORES_ID + " INTEGER PRIMARY KEY, " + UTILIZADORES_NOME + " TEXT, "
                + UTILIZADORES_USERNAME + " TEXT, " + UTILIZADORES_PASSWORD + " TEXT, "
                + UTILIZADORES_EMAIL + " TEXT, " + UTILIZADORES_TELEMOVEL + " TEXT,"
                + UTILIZADORES_ISACTIVE + " INTEGER)";
        String QUERY_CREATE_TABLE_OBRAS = "CREATE TABLE " + TABELA_OBRAS + "("
                + OBRAS_ID + " INTEGER PRIMARY KEY, " + OBRAS_NOME + " TEXT, "
                + OBRAS_DESCRICAO + " TEXT, " + OBRAS_MORADA + " TEXT, "
                + OBRAS_CODIGOPOSTAL + " TEXT, " + OBRAS_LOCALIDADE + " TEXT,"
                + OBRAS_PAIS + " TEXT, " + OBRAS_DATAINICIO + " TEXT, "
                + OBRAS_RESPONSAVEL + " TEXT, " + OBRAS_ISACTIVE + " INTEGER)";
        String QUERY_CREATE_TABLE_INSPECOES = "CREATE TABLE " + TABELA_INSPECOES + "("
                + INSPECOES_ID + " INTEGER PRIMARY KEY, " + INSPECOES_DATAINICIO + " TEXT, "
                + INSPECOES_DATAFIM + " TEXT, " + INSPECOES_ISFINISHED + " INTEGER, "
                + INSPECOES_INSPETORID + " TEXT, " + INSPECOES_OBRAID + " TEXT,"
                + INSPECOES_ISACTIVE + " INTEGER)";
        String QUERY_CREATE_TABLE_CASOS = "CREATE TABLE " + TABELA_CASOS + "("
                + CASOS_ID + " INTEGER PRIMARY KEY, " + CASOS_TITULO + " TEXT, "
                + CASOS_DESCRICAO + " TEXT, " + CASOS_IMAGEM + " TEXT, "
                + CASOS_INSPECAOID + " INTEGER)";
        bd.execSQL(QUERY_CREATE_TABLE_UTILIZADORES);
        bd.execSQL(QUERY_CREATE_TABLE_OBRAS);
        bd.execSQL(QUERY_CREATE_TABLE_INSPECOES);
        bd.execSQL(QUERY_CREATE_TABLE_CASOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase bd, int i, int i1) {

    }

    /*CRUD UTILIZADORES*/

    //Adicionar Utilizador
    void addUtilizador(Utilizador utilizador){

        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(UTILIZADORES_NOME, utilizador.getNome());
        values.put(UTILIZADORES_USERNAME, utilizador.getUsername());
        values.put(UTILIZADORES_PASSWORD, utilizador.getPassword());
        values.put(UTILIZADORES_EMAIL, utilizador.getEmail());
        values.put(UTILIZADORES_TELEMOVEL, utilizador.getTelemovel());
        values.put(UTILIZADORES_ISACTIVE, utilizador.isActive());

        bd.insert(TABELA_UTILIZADORES, null, values);
        bd.close();
    }

    void deleteUtilizador(int Id){
        SQLiteDatabase bd = this.getWritableDatabase();

        bd.delete(TABELA_UTILIZADORES, UTILIZADORES_ID + " = ?", new String[] {String.valueOf(Id)});

        bd.close();
    }

    Utilizador getUtilizadorPorId(int Id){
        SQLiteDatabase bd = this.getReadableDatabase();

        Cursor cursor = bd.query(TABELA_UTILIZADORES, new String[] {UTILIZADORES_ID, UTILIZADORES_NOME,
                UTILIZADORES_USERNAME, UTILIZADORES_PASSWORD, UTILIZADORES_EMAIL,
                UTILIZADORES_TELEMOVEL, UTILIZADORES_ISACTIVE}, UTILIZADORES_ID + " = ?",
                new String[] {String.valueOf(Id)}, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        Utilizador utilizador = new Utilizador(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6) == "1");

        return utilizador;
    }

    void editarUtilizador(Utilizador utilizador){
        SQLiteDatabase bd = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UTILIZADORES_NOME, utilizador.getNome());
        values.put(UTILIZADORES_USERNAME, utilizador.getUsername());
        values.put(UTILIZADORES_PASSWORD, utilizador.getPassword());
        values.put(UTILIZADORES_EMAIL, utilizador.getEmail());
        values.put(UTILIZADORES_TELEMOVEL, utilizador.getTelemovel());
        values.put(UTILIZADORES_ISACTIVE, utilizador.isActive());

        bd.update(TABELA_UTILIZADORES, values, UTILIZADORES_ID + " = ?",
                new String[] {String.valueOf(utilizador.getId())});

        bd.close();
    }

    public List<Utilizador> getTodosUtilizadores(){
        List<Utilizador> listaUtilizadores = new ArrayList<Utilizador>();

        String query = "SELECT * FROM " + TABELA_UTILIZADORES;

        SQLiteDatabase bd = this.getWritableDatabase();
        Cursor cursor = bd.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do {
                Utilizador utilizador = new Utilizador();
                utilizador.setId(Integer.parseInt(cursor.getString(0)));
                utilizador.setNome(cursor.getString(1));
                utilizador.setUsername(cursor.getString(2));
                utilizador.setPassword(cursor.getString(3));
                utilizador.setEmail(cursor.getString(4));
                utilizador.setTelemovel(cursor.getString(5));
                utilizador.setActive(cursor.getString(6) == "1");

                listaUtilizadores.add(utilizador);
            } while (cursor.moveToNext());
        }

        return listaUtilizadores;
    }

}
