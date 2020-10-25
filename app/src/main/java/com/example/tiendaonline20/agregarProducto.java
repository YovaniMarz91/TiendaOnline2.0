package com.example.tiendaonline20;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class agregarProducto extends AppCompatActivity {
    DB miDB;
    String accion = "nuevo";
    String idProducto = "0";
    ConexionCouch CC;
    String resp, id, rev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        Button btnProductos = (Button)findViewById(R.id.btnguardarproducto);
        btnProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tempVal = (TextView)findViewById(R.id.txtNombreProducto);
                String nombre = tempVal.getText().toString();

                tempVal = (TextView)findViewById(R.id.txtcodigoProducto);
                String codigo = tempVal.getText().toString();

                tempVal = (TextView)findViewById(R.id.txtdescripcionProducto);
                String descripcion = tempVal.getText().toString();

                tempVal = (TextView)findViewById(R.id.txtmarcaProducto);
                String marca = tempVal.getText().toString();

                tempVal = (TextView)findViewById(R.id.txtprecioProducto);
                String precio = tempVal.getText().toString();

                String[] data = {idProducto,nombre,codigo,descripcion,marca,precio};

                miDB = new DB(getApplicationContext(), "", null, 1);
                miDB.mantenimientoProducto(accion, data);

                Toast.makeText(getApplicationContext(), "Datos del producto insertado con exito", Toast.LENGTH_LONG).show();
                mostrarlistaProducto();

                try {
                    JSONObject datosProducto = new JSONObject();
                    if (accion.equals("modificar")){
                        datosProducto.put("_id",id);
                        datosProducto.put("_rev",rev);
                    }
                    datosProducto.put("nombre", nombre);
                    datosProducto.put("codigo", codigo);
                    datosProducto.put("descripcion ", descripcion);
                    datosProducto.put("marca", marca);
                    datosProducto.put("precio", precio);
                    enviarDatosProducto objGuardarAmigo = new enviarDatosProducto();
                    objGuardarAmigo.execute(datosProducto.toString());
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

        btnProductos = (Button)findViewById(R.id.btnmostrarproducto);
        btnProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarlistaProducto();
            }
        });

        mostrarDatosProducto();
    }
    void mostrarlistaProducto(){
        Intent mostrarProductos = new Intent( agregarProducto.this, MainActivity.class);
        startActivity(mostrarProductos);
    }
    void mostrarDatosProducto(){
        try {
            Bundle recibirParametros = getIntent().getExtras();
            accion = recibirParametros.getString("accion");
            if (accion.equals("Modificar")){
                JSONObject dataProducto = new JSONObject(recibirParametros.getString("dataAmigo")).getJSONObject("value");
                TextView tempVal = (TextView)findViewById(R.id.txtNombreProducto);
                tempVal.setText(dataProducto.getString("nombre "));
                tempVal = (TextView)findViewById(R.id.txtcodigoProducto);
                tempVal.setText(dataProducto.getString("codigo"));
                tempVal = (TextView)findViewById(R.id.txtdescripcionProducto);
                tempVal.setText(dataProducto.getString("descripcion"));
                tempVal = (TextView)findViewById(R.id.txtmarcaProducto);
                tempVal.setText(dataProducto.getString("marca"));
                tempVal = (TextView)findViewById(R.id.txtprecioProducto);
                tempVal.setText(dataProducto.getString("precio"));
                id = dataProducto.getString("_id");
                rev = dataProducto.getString("_rev");

            }
        }catch (Exception ex){
            ///
        }

        try {
            Bundle recibirParametros = getIntent().getExtras();
            accion = recibirParametros.getString("accion");
            if (accion.equals("Modificar")){
                String[] dataProducto = recibirParametros.getStringArray("dataProducto");

                idProducto = dataProducto[0];

                TextView tempVal = (TextView)findViewById(R.id.txtNombreProducto);
                tempVal.setText(dataProducto[1]);

                tempVal = (TextView)findViewById(R.id.txtcodigoProducto);
                tempVal.setText(dataProducto[2]);

                tempVal = (TextView)findViewById(R.id.txtdescripcionProducto);
                tempVal.setText(dataProducto[3]);

                tempVal = (TextView)findViewById(R.id.txtmarcaProducto);
                tempVal.setText(dataProducto[4]);

                tempVal = (TextView)findViewById(R.id.txtprecioProducto);
                tempVal.setText(dataProducto[5]);


            }
        }catch (Exception ex){
            ///
        }

    }
    private class enviarDatosProducto extends AsyncTask<String,String, String> {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... parametros) {
            StringBuilder stringBuilder = new StringBuilder();
            String jsonResponse = null;
            String jsonDatos = parametros[0];
            BufferedReader reader;
            try {
                URL url = new URL(CC.url_mto);
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestProperty("Accept","application/json");
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(jsonDatos);
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream==null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                resp = reader.toString();
                String inputLine;
                StringBuffer stringBuffer = new StringBuffer();
                while ((inputLine=reader.readLine())!= null){
                    stringBuffer.append(inputLine+"\n");
                }
                if(stringBuffer.length()==0){
                    return null;
                }
                jsonResponse = stringBuffer.toString();
                return jsonResponse;
            }catch (Exception ex){
                //
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getBoolean("ok")){
                    Toast.makeText(getApplicationContext(), "Datos de amigo guardado con exito", Toast.LENGTH_SHORT).show();
                    mostrarDatosProducto();
                } else {
                    Toast.makeText(getApplicationContext(), "Error al intentar guardar datos de amigo", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Error al guardar amigo: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}