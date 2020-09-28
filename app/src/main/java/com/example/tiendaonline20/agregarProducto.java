package com.example.tiendaonline20;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class agregarProducto extends AppCompatActivity {
    DB miDB;
    String accion = "nuevo";
    String idProducto = "0";

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
            if (accion.equals("modificar")){
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
}