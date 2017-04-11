package com.airmovil.profuturo.ti.retencion.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper{
    private final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "profuturoApp";

    // Login table name
    private static final String TABLE_TRAMITE = "tramite";
    private static final String KEY_ID = "id_t";
    private static final String FK_ID_TRAMITE = "idTramite";

    private static final String TABLE_RETENCION_ENCUESTA = "retencion_encuesta";
    private static final String KEY_ESTATUS_TRAMITE = "estatusTramite";
    private static final String KEY_PREGUNTA1 = "pregunta1";
    private static final String KEY_PREGUNTA2 ="pregunta2";
    private static final String KEY_PREGUNTA3 ="pregunta3";
    private static final String KEY_PREGUNTA4 ="pregunta4";
    private static final String KEY_PREGUNTA5 ="pregunta5";
    private static final String KEY_PREGUNTA6 ="pregunta6";

    private static final String TABLE_OBSERVACIONES_ENCUESTA = "retencion_encuesta_observaciones";
    private static final String KEY_CLAVE_AFORE = "claveAfore";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PREGUNTA7 = "pregunta7";
    private static final String KEY_PREGUNTA8 ="pregunta8";
    private static final String KEY_PREGUNTA9 ="pregunta9";
    private static final String KEY_ESTATUS = "estatus";
    //private static final String KEY_ESTATUS_TRAMITE = "estatusTramite";
    private static final String KEY_ID_MOTIVO = "idMotivo";
    //private static final String FK_ID_TRAMITE = "idTramite";
    private static final String KEY_LEY_REGIMEN = "leyRegimen";
    private static final String KEY_OBSERVACION = "observacion";
    private static final String KEY_REGIMEN = "regimen";
    private static final String KEY_TELEFONO = "telefono";


    private static final String TABLE_FIRMA = "retencion_firma";
    //private static final String KEY_ESTATUS_TRAMITE = "estatusTramite";
    private static final String KEY_FIRMA = "firmaCliente";
    //private static final String FK_ID_TRAMITE = "idTramite";
    private static final String KEY_LONGITUD = "longitud";
    private static final String KEY_LATITUD = "latitud";

    private static final String TABLE_DOCUMENTACION = "retencion_documentacion";
    //private static final String KEY_ESTATUS_TRAMITE = "estatusTramite";
    private static final String KEY_FECHAHORAFIN = "fechaHoraFin";
    //private static final String FK_ID_TRAMITE = "idTramite";
    private static final String KEY_INEIFE = "ineIfe";
    private static final String KEY_NUMERO_CUENTA = "numeroCuenta";
    //private static final String KEY_LONGITUD = "longitud";
    //private static final String KEY_LATITUD = "latitud";
    private static final String KEY_USUARIO = "usuario";

    //private static String DB_PATH = "/data/data/com.airmovil.it.chato/databases/";
    //final Context myContext;

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //this.myContext = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRAMITE_TABLE = "CREATE TABLE " + TABLE_TRAMITE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + FK_ID_TRAMITE + " INTEGER"+ ")";

        String CREATE_ENCUESTA_TABLE = "CREATE TABLE " + TABLE_RETENCION_ENCUESTA+ "("
                + FK_ID_TRAMITE + " INTEGER PRIMARY KEY,"
                + KEY_ESTATUS_TRAMITE + " INTEGER,"
                + KEY_PREGUNTA1 + " BOOLEAN,"
                + KEY_PREGUNTA2 + " BOOLEAN,"
                + KEY_PREGUNTA3 + " BOOLEAN,"
                + KEY_OBSERVACION + " TEXT"+")";

        String CREATE_OBSERVACIONES_TABLE = "CREATE TABLE " + TABLE_OBSERVACIONES_ENCUESTA + "("
                + FK_ID_TRAMITE + " INTEGER PRIMARY KEY,"
                + KEY_CLAVE_AFORE + " INTEGER,"
                + KEY_EMAIL + " TEXT,"
                + KEY_PREGUNTA7 + " BOOLEAN,"
                + KEY_PREGUNTA8 + " BOOLEAN,"
                + KEY_PREGUNTA9 + " BOOLEAN,"
                + KEY_ESTATUS + " INTEGER,"
                + KEY_ESTATUS_TRAMITE + " INTEGER,"
                + KEY_ID_MOTIVO + " INTEGER,"
                + KEY_LEY_REGIMEN + " INTEGER,"
                + KEY_OBSERVACION + " TEXT,"
                + KEY_REGIMEN + " INTEGER,"
                + KEY_TELEFONO + " TEXT"+")";

        String CREATE_FIRMA_TABLE = "CREATE TABLE " + TABLE_FIRMA + "("
                + FK_ID_TRAMITE + " INTEGER PRIMARY KEY,"
                + KEY_ESTATUS_TRAMITE + " INTEGER,"
                + KEY_FIRMA + " TEXT,"
                + KEY_LATITUD + " DOUBLE,"
                + KEY_LONGITUD + " DOUBLE"+")";

        String CREATE_DOCUMENTACION_TABLE = "CREATE TABLE " + TABLE_DOCUMENTACION + "("
                + FK_ID_TRAMITE + " INTEGER PRIMARY KEY,"
                + KEY_FECHAHORAFIN + " TEXT,"
                + KEY_ESTATUS_TRAMITE + " INTEGER,"
                + KEY_INEIFE + " TEXT,"
                + KEY_NUMERO_CUENTA + " TEXT,"
                + KEY_USUARIO + " TEXT,"
                + KEY_LATITUD + " DOUBLE,"
                + KEY_LONGITUD + " DOUBLE"+")";


        db.execSQL(CREATE_TRAMITE_TABLE);
        db.execSQL(CREATE_ENCUESTA_TABLE);
        db.execSQL(CREATE_OBSERVACIONES_TABLE);
        db.execSQL(CREATE_FIRMA_TABLE);
        db.execSQL(CREATE_DOCUMENTACION_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATOS_CLIENTE);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREDITOS);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DETAILS);
        // Create tables again
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAMITE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RETENCION_ENCUESTA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVACIONES_ENCUESTA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIRMA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENTACION);
        onCreate(db);
    }

    public void addIDTramite(String idTramite){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(FK_ID_TRAMITE,idTramite); // fk id
        // Inserting Row
        long id =(int) db.insertWithOnConflict(TABLE_TRAMITE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(TABLE_TRAMITE,values, FK_ID_TRAMITE+"=?", new String[] {idTramite});  // number 1 is the _id here, update to variable for your code
        }
        //long bb = db.writeToSD();
        //copyFile();
        db.close(); // Closing database connection

        // writeToSD();
        Log.d(TAG, "DEtalles del usuario: " + idTramite);

    }


    public void addEncuesta(String idTramite,String statusTramite,Boolean pregunta1,Boolean pregunta2,Boolean pregunta3,String observaciones) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(FK_ID_TRAMITE,idTramite); // fk id
        values.put(KEY_ESTATUS_TRAMITE,statusTramite);
        values.put(KEY_PREGUNTA1,pregunta1);
        values.put(KEY_PREGUNTA2,pregunta2);
        values.put(KEY_PREGUNTA3,pregunta3);
        values.put(KEY_OBSERVACION,observaciones);
        /*values.put(KEY_PREGUNTA4,pregunta4);
        values.put(KEY_PREGUNTA5,pregunta5);
        values.put(KEY_PREGUNTA6,pregunta6);*/
        // Inserting Row
        long id =(int) db.insertWithOnConflict(TABLE_RETENCION_ENCUESTA, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(TABLE_RETENCION_ENCUESTA,values, FK_ID_TRAMITE+"=?", new String[] {idTramite});  // number 1 is the _id here, update to variable for your code
        }
        //long bb = db.writeToSD();
        //copyFile();
        db.close(); // Closing database connection

        // writeToSD();
        Log.d(TAG, "Encuesta: " +idTramite);
    }



    public void addObservaciones(String idTramite,String claveAfore,Boolean pregunta7,Boolean pregunta8
            ,Boolean pregunta9,int status,int statusTramite,int idMotivo,int leyRegimen,String observacion,int regimen,String telefono) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(FK_ID_TRAMITE,idTramite); // fk id
        values.put(KEY_CLAVE_AFORE,claveAfore);
        values.put(KEY_PREGUNTA7,pregunta7);
        values.put(KEY_PREGUNTA8,pregunta8);
        values.put(KEY_PREGUNTA9,pregunta9);
        values.put(KEY_ESTATUS,status);
        values.put(KEY_ESTATUS_TRAMITE,statusTramite);
        values.put(KEY_ID_MOTIVO,idMotivo);
        values.put(KEY_LEY_REGIMEN,leyRegimen);
        values.put(KEY_OBSERVACION,observacion);
        values.put(KEY_REGIMEN,regimen);
        values.put(KEY_TELEFONO,telefono);
        // Inserting Row
        long id =(int) db.insertWithOnConflict(TABLE_OBSERVACIONES_ENCUESTA, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(TABLE_OBSERVACIONES_ENCUESTA,values, FK_ID_TRAMITE+"=?", new String[] {idTramite});  // number 1 is the _id here, update to variable for your code
        }
        //long bb = db.writeToSD();
        //copyFile();
        db.close(); // Closing database connection

        // writeToSD();
        Log.d(TAG, "OBSERVACIONES: " + idTramite);
    }


    public void addFirma(String idTramite,int estatusTramite,String firma,double latitud,double longitud) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(FK_ID_TRAMITE,idTramite); // fk id
        values.put(KEY_ESTATUS_TRAMITE,estatusTramite);
        values.put(KEY_FIRMA,firma);
        values.put(KEY_LATITUD,latitud);
        values.put(KEY_LONGITUD,longitud);

        // Inserting Row
        long id =(int) db.insertWithOnConflict(TABLE_FIRMA, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(TABLE_FIRMA,values, FK_ID_TRAMITE+"=?", new String[] {idTramite});  // number 1 is the _id here, update to variable for your code
        }
        //long bb = db.writeToSD();
        //copyFile();
        db.close(); // Closing database connection

        // writeToSD();
        Log.d(TAG, "DOCUMENTOS: " +idTramite);
    }


    public void addDocumento(String idTramite,String fechaHoraFin,int estatusTramite,String ineIfe,String numeroCuenta,String usuario,double latitud,double longitud) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(FK_ID_TRAMITE,idTramite); // fk id
        values.put(KEY_FECHAHORAFIN,fechaHoraFin);
        values.put(KEY_ESTATUS_TRAMITE,estatusTramite);
        values.put(KEY_INEIFE,ineIfe);
        values.put(KEY_NUMERO_CUENTA,numeroCuenta);
        values.put(KEY_USUARIO,usuario);
        values.put(KEY_LATITUD,latitud);
        values.put(KEY_LONGITUD,longitud);

        // Inserting Row
        long id =(int) db.insertWithOnConflict(TABLE_DOCUMENTACION, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(TABLE_DOCUMENTACION,values, FK_ID_TRAMITE+"=?", new String[] {idTramite});  // number 1 is the _id here, update to variable for your code
        }
        //long bb = db.writeToSD();
        //copyFile();
        db.close(); // Closing database connection

        // writeToSD();
        Log.d(TAG, "DOCUMENTOS: " +idTramite);
    }



    /**
     * Getting user data from database
     * */


    public HashMap<String, String> getEncuesta(String idTramite) {
        HashMap<String, String> encuesta = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_RETENCION_ENCUESTA + " WHERE " + FK_ID_TRAMITE + "=?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {idTramite});
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            encuesta.put(FK_ID_TRAMITE, cursor.getString(0));
            encuesta.put(KEY_ESTATUS_TRAMITE, cursor.getString(1));
            encuesta.put(KEY_PREGUNTA1, cursor.getString(2));
            encuesta.put(KEY_PREGUNTA2, cursor.getString(3));
            encuesta.put(KEY_PREGUNTA3, cursor.getString(4));
            encuesta.put(KEY_OBSERVACION, cursor.getString(5));
            /*encuesta.put(KEY_PREGUNTA4, cursor.getString(5));
            encuesta.put(KEY_PREGUNTA5, cursor.getString(6));
            encuesta.put(KEY_PREGUNTA6, cursor.getString(7));*/
        }
        cursor.close();
        db.close();
        // return encuesta
        Log.d(TAG, "Fetching encuesta from Sqlite: " + encuesta.toString());

        return encuesta;
    }

    public HashMap<String, String> getObservaciones(String idTramite) {
        HashMap<String, String> observaciones = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_OBSERVACIONES_ENCUESTA + " WHERE " + FK_ID_TRAMITE +"=?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {idTramite});
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            observaciones.put(FK_ID_TRAMITE, cursor.getString(0));
            observaciones.put(KEY_CLAVE_AFORE, cursor.getString(1));
            observaciones.put(KEY_PREGUNTA7, cursor.getString(2));
            observaciones.put(KEY_PREGUNTA8, cursor.getString(3));
            observaciones.put(KEY_PREGUNTA9, cursor.getString(4));
            observaciones.put(KEY_ESTATUS, cursor.getString(5));
            observaciones.put(KEY_ESTATUS_TRAMITE, cursor.getString(6));
            observaciones.put(KEY_ID_MOTIVO, cursor.getString(7));
            observaciones.put(KEY_LEY_REGIMEN, cursor.getString(8));
            observaciones.put(KEY_OBSERVACION, cursor.getString(9));
            observaciones.put(KEY_REGIMEN, cursor.getString(10));
            observaciones.put(KEY_TELEFONO, cursor.getString(11));
        }
        cursor.close();
        db.close();
        // return observaciones
        Log.d(TAG, "Fetching observaciones from Sqlite: " + observaciones.toString());

        return observaciones;
    }

    public HashMap<String, String> getfirma(String idTramite) {
        HashMap<String, String> firma = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_FIRMA + " WHERE " + FK_ID_TRAMITE +"=?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {idTramite});
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            firma.put(FK_ID_TRAMITE, cursor.getString(0));
            firma.put(KEY_ESTATUS_TRAMITE, cursor.getString(1));
            firma.put(KEY_FIRMA, cursor.getString(2));
            firma.put(KEY_LATITUD, cursor.getString(3));
            firma.put(KEY_LONGITUD, cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return firma
        Log.d(TAG, "Fetching firma from Sqlite: " + firma.toString());

        return firma;
    }

    public HashMap<String, String> getdocumento(String idTramite) {
        HashMap<String, String> documento = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_DOCUMENTACION + " WHERE " + FK_ID_TRAMITE +"=?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {idTramite});
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            documento.put(FK_ID_TRAMITE, cursor.getString(0));
            documento.put(KEY_FECHAHORAFIN, cursor.getString(1));
            documento.put(KEY_ESTATUS_TRAMITE, cursor.getString(2));
            documento.put(KEY_INEIFE, cursor.getString(3));
            documento.put(KEY_NUMERO_CUENTA, cursor.getString(4));
            documento.put(KEY_USUARIO, cursor.getString(5));
            documento.put(KEY_LATITUD, cursor.getString(6));
            documento.put(KEY_LONGITUD, cursor.getString(7));
        }
        cursor.close();
        db.close();
        // return documento
        Log.d(TAG, "Fetching documento from Sqlite: " + documento.toString());

        return documento;
    }


    public Integer deleteEncuesta(int idTramite) {
        Log.d(TAG, "Deleted all user info from sqlite");
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        return db.delete(TABLE_RETENCION_ENCUESTA,
                FK_ID_TRAMITE + " = ? ",
                new String[] { Integer.toString(idTramite) });
    }

    public Integer deleteObservaciones(int idTramite) {
        Log.d(TAG, "Deleted all user info from sqlite");
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        return db.delete(TABLE_OBSERVACIONES_ENCUESTA,
                FK_ID_TRAMITE + " = ? ",
                new String[] { Integer.toString(idTramite) });
    }

    public Integer deleteFirma(int idTramite) {
        Log.d(TAG, "Deleted all user info from sqlite");
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        return db.delete(TABLE_FIRMA,
                FK_ID_TRAMITE + " = ? ",
                new String[] { Integer.toString(idTramite) });
    }

    public Integer deleteDocumentacion(int idTramite) {
        Log.d(TAG, "Deleted all user info from sqlite");
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        return db.delete(TABLE_DOCUMENTACION,
                FK_ID_TRAMITE + " = ? ",
                new String[] { Integer.toString(idTramite) });
    }
}
