package com.example.tiendaonline20;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
//https://developer.android.com/training/camera/photobasics?hl=es-419#java

public class MainActivity extends AppCompatActivity{
    DB miBD;
    Cursor misproductos;
    ArrayList<String> stringArrayList = new ArrayList<String>();
    ArrayList<String> COPYstringArrayList = new ArrayList<String>();
    ArrayAdapter<String> stringArrayAdapter;
    ListView ltsproducto;
    detectarInternet di;
    ConexionCouch uc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        di = new detectarInternet(getApplicationContext());
        if( di.hayConexionInternet() ) {
            conexionServidor objObtenerAmigos = new conexionServidor();
            objObtenerAmigos.execute(uc.url_consulta, "GET");
        } else {
            Toast.makeText(getApplicationContext(), "No hay conexion a internet.", Toast.LENGTH_LONG).show();
        }

        FloatingActionButton btnAgregarProductos = (FloatingActionButton)findViewById(R.id.btnAgregarProductos);
        btnAgregarProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarProducto("nuevo", new String[]{});
            }
        });
        obtenerDatosProductos();
        buscarProducto();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo)menuInfo;
        misproductos.moveToPosition(adapterContextMenuInfo.position);
        menu.setHeaderTitle(misproductos.getString(1));


    }
    void buscarProducto(){
        final TextView tempVal = (TextView)findViewById(R.id.BuscarProducto);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                stringArrayList.clear();
                if(tempVal.getText().toString().trim().length()<1) {
                    stringArrayList.addAll(COPYstringArrayList);
                } else{//hacemos la busqueda
                    for (String producto : COPYstringArrayList){
                        if(producto.toLowerCase().contains(tempVal.getText().toString().trim().toLowerCase())){
                            stringArrayList.add(producto);
                        }
                    }
                }
                stringArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnxAgregar:
                agregarProducto("nuevo", new String[]{});
                return true;
            case R.id.mnxModificar:
                String[] dataProducto = {
                        misproductos.getString(0), //idProducto
                        misproductos.getString(1), //Nombre
                        misproductos.getString(2), //Codigo
                        misproductos.getString(3), // descripcion
                        misproductos.getString(4), //Marca
                        misproductos.getString(5) //precio
                };
                agregarProducto("Modificar", dataProducto);
                return true;
            case R.id.mnxEliminar:
                androidx.appcompat.app.AlertDialog eliminarProduct =  eliminarProducto();
                eliminarProduct.show();


            default:

                return super.onContextItemSelected(item);
        }

    }
    androidx.appcompat.app.AlertDialog eliminarProducto() {
        androidx.appcompat.app.AlertDialog.Builder confirmacion = new AlertDialog.Builder(MainActivity.this);
        confirmacion.setTitle(misproductos.getString(1));
        confirmacion.setMessage("Estas seguro de eliminar los datos?");
        confirmacion.setPositiveButton("si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                miBD.mantenimientoProducto("Eliminar", new String[]{misproductos.getString(0)});
                obtenerDatosProductos();
                Toast.makeText(getApplicationContext(), "Datos eliminados con exito", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        confirmacion.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "Eliminacion cancelada por el usuario", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        return confirmacion.create();
    }


    void obtenerDatosProductos(){
        miBD = new DB(getApplicationContext(), "", null, 1);
        misproductos = miBD.mantenimientoProducto( "Consultar", null);
        if (misproductos.moveToFirst()){ // si hay datos para mostrar
            mostrarDatosProductos();
        }else{
            Toast.makeText(getApplicationContext(), "No hay datos que mostrar", Toast.LENGTH_LONG).show();
            agregarProducto("Nuevo", new String[]{});

        }
    }
    void agregarProducto(String accion, String[] dataProducto){
        Bundle enviarParametros = new Bundle();
        enviarParametros.putString("accion",accion);
        enviarParametros.putStringArray("dataProducto",dataProducto);
        Intent agregarProductos = new Intent(MainActivity.this, agregarProducto.class);
        agregarProductos.putExtras(enviarParametros);
        startActivity(agregarProductos);
    }
    void mostrarDatosProductos(){
        stringArrayList.clear();
        ListView ltsproducto = (ListView)findViewById(R.id.ltsproducto);
        stringArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, stringArrayList);
        ltsproducto.setAdapter(stringArrayAdapter);
        do {
            stringArrayList.add(misproductos.getString(1));
        }while(misproductos.moveToNext());
        COPYstringArrayList.clear();//Limpiamos la lista de productos
        COPYstringArrayList.addAll(stringArrayList);//Creamos la lista de los productos
        stringArrayAdapter.notifyDataSetChanged();
        registerForContextMenu(ltsproducto);

    }
    private class conexionServidor extends AsyncTask<String,String, String> {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... parametros) {
            StringBuilder result = new StringBuilder();
            try {
                String uri = parametros[0];
                String metodo = parametros[1];

                URL url = new URL(uri);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(metodo);

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String linea;
                while ((linea = reader.readLine()) != null) {
                    result.append(linea);
                }
            } catch (Exception ex) {
                //
            }
            return result.toString();
        }
    }
}

