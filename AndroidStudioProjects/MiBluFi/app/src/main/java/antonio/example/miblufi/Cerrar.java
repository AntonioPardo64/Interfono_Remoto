package antonio.example.miblufi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Cerrar extends AppCompatActivity {
    private TextView mensaje;
    private TextView titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cerrar);
        mensaje = findViewById(R.id.txt_cerrar2);
        titulo = findViewById(R.id.txt_cerrar1);

        if(Comunicaciones.confWifi) {
            titulo.setText("EXITO");
            mensaje.setText("La dirección IP asignada: "+ Comunicaciones.ipWifi);
        } else if(Comunicaciones.errWifi){
            titulo.setText("ERROR");
            mensaje.setText("Credenciales WIFI erroneas");
        }
    }

    public void cerrar(View view){
        finish();
    }
}
