package com.example.tiendaonline20;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    static String nameDB = "db_productos";
    static String tblProductos = "CREATE TABLE productos(idproducto integer primary key autoincrement, nombre text,  codigo text, descripcion text, marca text, precio text)";


    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, nameDB, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tblProductos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public Cursor mantenimientoProducto(String accion, String[] data){
        SQLiteDatabase sqLiteDatabaseReadable=getReadableDatabase();
        SQLiteDatabase sqLiteDatabaseWritable=getWritableDatabase();
        Cursor cursor = null;
        switch (accion){
            case "Consultar":
                cursor=sqLiteDatabaseReadable.rawQuery("SELECT * FROM productos ORDER BY nombre ASC", null);
                break;

            case "Nuevo":
                sqLiteDatabaseReadable.execSQL("INSERT INTO productos (nombre,codigo,descripcion,marca,precio) VALUES('"+ data[1] +"', '"+data[2] +"', '"+ data[3] +"', '"+ data[4] +"', '"+ data[5] +"')");
                break;

            case "Modificar":
                sqLiteDatabaseReadable.execSQL("UPDATE productos SET nombre='"+ data[1] +"',codigo='"+data[2] +"',descripcion='"+ data[3] +"',marca='"+ data[4] +"',precio='"+ data[5] +"' WHERE idproducto='"+data[0] +"'");
                break;

            case "Eliminar":
                sqLiteDatabaseReadable.execSQL("DELETE FROM productos WHERE idproducto='"+ data[0] +"'");
                break;
        }
        return cursor;

    }
}
