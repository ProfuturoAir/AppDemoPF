package com.airmovil.profuturo.ti.retencion.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper{
    private final String TAG = SQLiteHandler.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "profuturoApp";
    private static final String TABLE_TRAMITE = "tramite";
    public static final String FK_ID_TRAMITE = "idTramite";
    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_HORA = "hora";
    public static final String TABLE_RETENCION_ENCUESTA = "retencion_encuesta";
    public static final String KEY_ESTATUS_TRAMITE = "estatusTramite";
    public static final String KEY_PREGUNTA1 = "pregunta1";
    public static final String KEY_PREGUNTA2 ="pregunta2";
    public static final String KEY_PREGUNTA3 ="pregunta3";
    public static final String TABLE_OBSERVACIONES_ENCUESTA = "retencion_encuesta_observaciones";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ID_MOTIVO = "idMotivo";
    public static final String KEY_OBSERVACION = "observacion";
    public static final String KEY_TELEFONO = "telefono";
    public static final String KEY_ID_AFORE = "idAfore";
    public static final String KEY_ID_ESTATUS = "idEstatus";
    public static final String KEY_ID_INSTITUTO = "idInstituto";
    public static final String KEY_ID_REGIMEN_PENSIONARIO = "idRegimenPensionario";
    public static final String KEY_ID_DOCUMENTACION = "idDocumentacion";
    public static final String TABLE_FIRMA = "retencion_firma";
    public static final String KEY_FIRMA = "firmaCliente";
    public static final String KEY_LONGITUD = "longitud";
    public static final String KEY_LATITUD = "latitud";
    public static final String TABLE_DOCUMENTACION = "retencion_documentacion";
    public static final String KEY_FECHAHORAFIN = "fechaHoraFin";
    public static final String KEY_INEIFE = "ineIfe";
    public static final String KEY_NUMERO_CUENTA = "numeroCuenta";
    public static final String KEY_USUARIO = "usuario";
    public static final String KEY_INE_IFE_REVERSO = "ineIfeReverso";
    // TODO: Constructor
    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // TODO: Creacion de tablas
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRAMITE_TABLE = "CREATE TABLE " + TABLE_TRAMITE + "("
                + FK_ID_TRAMITE + " INTEGER PRIMARY KEY,"
                + KEY_NOMBRE + " TEXT,"
                + KEY_NUMERO_CUENTA + " TEXT,"
                + KEY_HORA + " TEXT"+ ")";

        String CREATE_ENCUESTA_TABLE = "CREATE TABLE " + TABLE_RETENCION_ENCUESTA+ "("
                + FK_ID_TRAMITE + " INTEGER PRIMARY KEY,"
                + KEY_ESTATUS_TRAMITE + " INTEGER,"
                + KEY_PREGUNTA1 + " BOOLEAN,"
                + KEY_PREGUNTA2 + " BOOLEAN,"
                + KEY_PREGUNTA3 + " BOOLEAN,"
                + KEY_OBSERVACION + " TEXT"+")";

        String CREATE_OBSERVACIONES_TABLE = "CREATE TABLE " + TABLE_OBSERVACIONES_ENCUESTA + "("
                + FK_ID_TRAMITE + " INTEGER PRIMARY KEY,"
                + KEY_ID_AFORE + " INTEGER,"
                + KEY_ID_MOTIVO + " INTEGER,"
                + KEY_ID_ESTATUS + " INTEGER,"
                + KEY_ID_INSTITUTO + " INTEGER,"
                + KEY_ID_REGIMEN_PENSIONARIO + " INTEGER,"
                + KEY_ID_DOCUMENTACION + " INTEGER,"
                + KEY_TELEFONO + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_ESTATUS_TRAMITE + " INTEGER"+")";

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
                + KEY_LONGITUD + " DOUBLE,"
                + KEY_INE_IFE_REVERSO + " TEXT" + ")";

        db.execSQL(CREATE_TRAMITE_TABLE);
        db.execSQL(CREATE_ENCUESTA_TABLE);
        db.execSQL(CREATE_OBSERVACIONES_TABLE);
        db.execSQL(CREATE_FIRMA_TABLE);
        db.execSQL(CREATE_DOCUMENTACION_TABLE);
    }

    // TODO: Actualizando BD
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAMITE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RETENCION_ENCUESTA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVACIONES_ENCUESTA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIRMA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENTACION);
        onCreate(db);
    }

    /**
     * Creacion de registro de tramite
     * @param idTramite idTramite
     * @param nombre nombre del cliente
     * @param numeroDeCuenta numero de cuenta
     * @param hora hora del tramite
     */
    public void addIDTramite(String idTramite,String nombre,String numeroDeCuenta,String hora){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FK_ID_TRAMITE,idTramite); // fk id
        values.put(KEY_NOMBRE,nombre);
        values.put(KEY_NUMERO_CUENTA,numeroDeCuenta);
        values.put(KEY_HORA,hora);
        // TODO: insertando filas
        long id =(int) db.insertWithOnConflict(TABLE_TRAMITE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(TABLE_TRAMITE,values, FK_ID_TRAMITE+"=?", new String[] {idTramite});  // number 1 is the _id here, update to variable for your code
        }
        db.close(); // cerrando la conexion a la base de datos
    }

    /**
     * Creacion de datos para la encuesta
     * @param idTramite idTramite
     * @param statusTramite Estatus del tramite
     * @param pregunta1 pregunta 1
     * @param pregunta2 pregunta 2
     * @param pregunta3 pregunta 3
     * @param observaciones observaciones
     */
    public void addEncuesta(String idTramite,int statusTramite,Boolean pregunta1,Boolean pregunta2,Boolean pregunta3,String observaciones) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FK_ID_TRAMITE,idTramite); // fk id
        values.put(KEY_ESTATUS_TRAMITE,statusTramite);
        values.put(KEY_PREGUNTA1,pregunta1);
        values.put(KEY_PREGUNTA2,pregunta2);
        values.put(KEY_PREGUNTA3,pregunta3);
        values.put(KEY_OBSERVACION,observaciones);
        long id =(int) db.insertWithOnConflict(TABLE_RETENCION_ENCUESTA, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(TABLE_RETENCION_ENCUESTA,values, FK_ID_TRAMITE+"=?", new String[] {idTramite});
        }
        db.close();
    }

    /**
     * Metodo para agregar encuesta 2
     * @param idTramite idTramite, es generado por la BD
     * @param idAfore idAfore
     * @param idMotivo idMotivo
     * @param idEstatus idEstatus
     * @param idInstituto idInstituto
     * @param idRegimenPensionario idRegimenPensionario
     * @param idDocumentacion idDocumentacion
     * @param telefono telefonoCliente
     * @param email emailCliente
     * @param statusTramite estatusTramite
     */
    public void addObservaciones(String idTramite,int idAfore,int idMotivo,int idEstatus,int idInstituto,int idRegimenPensionario,int idDocumentacion,String telefono,String email,int statusTramite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FK_ID_TRAMITE,idTramite); // fk id
        values.put(KEY_ID_AFORE ,idAfore);
        values.put(KEY_ID_MOTIVO,idMotivo);
        values.put(KEY_ID_ESTATUS ,idEstatus);
        values.put(KEY_ID_INSTITUTO ,idInstituto);
        values.put(KEY_ID_REGIMEN_PENSIONARIO,idRegimenPensionario);
        values.put(KEY_ID_DOCUMENTACION ,idDocumentacion);
        values.put(KEY_TELEFONO ,telefono);
        values.put(KEY_EMAIL,email);
        values.put(KEY_ESTATUS_TRAMITE,statusTramite);
        long id =(int) db.insertWithOnConflict(TABLE_OBSERVACIONES_ENCUESTA, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(TABLE_OBSERVACIONES_ENCUESTA,values, FK_ID_TRAMITE+"=?", new String[] {idTramite});  // number 1 is the _id here, update to variable for your code
        }
        db.close(); // cerrando la conexion bd
    }

    /**
     * Metodo para agregar la firma
     * @param idTramite idTramite
     * @param estatusTramite estatusTramite
     * @param firma firma
     * @param latitud latitud
     * @param longitud longitud
     */
    public void addFirma(String idTramite,int estatusTramite,String firma,double latitud,double longitud) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FK_ID_TRAMITE,idTramite);
        values.put(KEY_ESTATUS_TRAMITE,estatusTramite);
        values.put(KEY_FIRMA,firma);
        values.put(KEY_LATITUD,latitud);
        values.put(KEY_LONGITUD,longitud);
        long id =(int) db.insertWithOnConflict(TABLE_FIRMA, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(TABLE_FIRMA,values, FK_ID_TRAMITE+"=?", new String[] {idTramite});
        }
        db.close();
    }

    /**
     * Metodo para guardar datos de INE o IFE
     * @param idTramite  idTramite
     * @param fechaHoraFin fechaHoraFin
     * @param estatusTramite estatusTramite
     * @param ineIfe ineIfe
     * @param numeroCuenta numeroCuenta
     * @param usuario usuario
     * @param latitud latitud
     * @param longitud longitud
     */
    public void addDocumento(String idTramite,String fechaHoraFin,int estatusTramite,String ineIfe, String numeroCuenta,String usuario,double latitud,double longitud, String ineIfeReverso) {
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
        values.put(KEY_INE_IFE_REVERSO, ineIfeReverso);
        long id =(int) db.insertWithOnConflict(TABLE_DOCUMENTACION, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            db.update(TABLE_DOCUMENTACION,values, FK_ID_TRAMITE+"=?", new String[] {idTramite});
        }
        db.close();
    }

    /**
     * Obteniendo datos del usuario
     * */
    public Cursor getAllPending() {
        String selectQuery = "SELECT  * FROM " + TABLE_TRAMITE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    /**
     * Obtiene el tramite
     * @param idTramite idTramite
     * @return retirna el estatus del tramite
     */
    public HashMap<String, String> getTramite(String idTramite) {
        HashMap<String, String> tramite = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_TRAMITE + " WHERE " + FK_ID_TRAMITE + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {idTramite});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            tramite.put(FK_ID_TRAMITE, cursor.getString(0));
            tramite.put(KEY_NOMBRE, cursor.getString(1));
            tramite.put(KEY_NUMERO_CUENTA, cursor.getString(2));
            tramite.put(KEY_HORA, cursor.getString(3));
        }
        cursor.close();
        db.close();
        return tramite;
    }

    /**
     * @param idTramite tramite de referencia
     * @return el estado de la encuesta
     */
    public HashMap<String, String> getEncuesta(String idTramite) {
        HashMap<String, String> encuesta = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_RETENCION_ENCUESTA + " WHERE " + FK_ID_TRAMITE + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {idTramite});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            encuesta.put(FK_ID_TRAMITE, cursor.getString(0));
            encuesta.put(KEY_ESTATUS_TRAMITE, cursor.getString(1));
            encuesta.put(KEY_PREGUNTA1, cursor.getString(2));
            encuesta.put(KEY_PREGUNTA2, cursor.getString(3));
            encuesta.put(KEY_PREGUNTA3, cursor.getString(4));
            encuesta.put(KEY_OBSERVACION, cursor.getString(5));
        }
        cursor.close();
        db.close();
        return encuesta;
    }

    /**
     * @param idTramite tramite con el que se guardo el registro
     * @return los datos guardados en encuesta
     */
    public HashMap<String, String> getObservaciones(String idTramite) {
        HashMap<String, String> observaciones = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_OBSERVACIONES_ENCUESTA + " WHERE " + FK_ID_TRAMITE +"=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {idTramite});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            observaciones.put(FK_ID_TRAMITE, cursor.getString(0));
            observaciones.put(KEY_ID_AFORE, cursor.getString(1));
            observaciones.put(KEY_ID_MOTIVO, cursor.getString(2));
            observaciones.put(KEY_ID_ESTATUS, cursor.getString(3));
            observaciones.put(KEY_ID_INSTITUTO, cursor.getString(4));
            observaciones.put(KEY_ID_REGIMEN_PENSIONARIO, cursor.getString(5));
            observaciones.put(KEY_ID_DOCUMENTACION, cursor.getString(6));
            observaciones.put(KEY_TELEFONO, cursor.getString(7));
            observaciones.put(KEY_EMAIL, cursor.getString(8));
            observaciones.put(KEY_ESTATUS_TRAMITE, cursor.getString(9));
        }
        cursor.close();
        db.close();
        return observaciones;
    }

    /**
     * @param idTramite con el que se guardo el registro
     * @return los datos guardado con idTramite
     */
    public HashMap<String, String> getfirma(String idTramite) {
        HashMap<String, String> firma = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_FIRMA + " WHERE " + FK_ID_TRAMITE +"=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {idTramite});
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
        return firma;
    }

    /**
     * @param idTramite con el que se guardo el registro
     * @return los datos guardado con idTramite
     */
    public HashMap<String, String> getdocumento(String idTramite) {
        HashMap<String, String> documento = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_DOCUMENTACION + " WHERE " + FK_ID_TRAMITE +"=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {idTramite});
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
            documento.put(KEY_INE_IFE_REVERSO, cursor.getString(8));
        }
        cursor.close();
        db.close();
        return documento;
    }

    /**
     * @param idTramite id para eliminar el registro ya procesado
     * @return la eliminacion del registro
     */
    public Integer deleteTramite(String idTramite) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TRAMITE, FK_ID_TRAMITE + " = ? ", new String[] { idTramite });
    }

    /**
     * @param idTramite id para eliminar el registro ya procesado
     * @return la eliminacion del registro
     */
    public Integer deleteEncuesta(String idTramite) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_RETENCION_ENCUESTA, FK_ID_TRAMITE + " = ? ", new String[] { idTramite});
    }

    /**
     * @param idTramite id para eliminar el registro ya procesado
     * @return la eliminacion del registro
     */
    public Integer deleteObservaciones(String idTramite) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_OBSERVACIONES_ENCUESTA, FK_ID_TRAMITE + " = ? ", new String[] { idTramite });
    }

    /**
     * @param idTramite id para eliminar el registro ya procesado
     * @return la eliminacion del registro
     */
    public Integer deleteFirma(String idTramite) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_FIRMA, FK_ID_TRAMITE + " = ? ", new String[] {idTramite});
    }

    /**
     * @param idTramite id para eliminar el registro ya procesado
     * @return la eliminacion del registro
     */
    public Integer deleteDocumentacion(String idTramite) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DOCUMENTACION, FK_ID_TRAMITE + " = ? ", new String[] { idTramite });
    }
}
