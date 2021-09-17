package com.example.projetopdmsam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    BaseDados bd = new BaseDados(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_criar = findViewById(R.id.btn_criar);
        Button btn_buscar = findViewById(R.id.btn_buscar);
        Button btn_logout = findViewById(R.id.btn_logout);

        TextView txt_Id = findViewById(R.id.txt_Id);
        TextView txt_Nome = findViewById(R.id.txt_Nome);
        TextView txt_Username = findViewById(R.id.txt_Username);
        TextView txt_Password = findViewById(R.id.txt_Password);
        TextView txt_Email = findViewById(R.id.txt_Email);
        TextView txt_Telemovel = findViewById(R.id.txt_Telemovel);
        TextView txt_IsActive = findViewById(R.id.txt_IsActive);

        Utilizador utilizador = new Utilizador(2423423, "José Maia", "josemaia",
                "02e51b530d3045a0d2c437696788566a3ccbe23dd2f6a1ee975822f015641cc3b77dbe523b88a79e77bdeecbd566700438c986a357e4e56477d91b020fa08469",
                "josemaia@renergy.pt", "+351915592811", true);

        btn_criar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bd.loginLocal(utilizador);
            }
        });

        btn_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilizador loggedInUser = bd.getLoggedInUser();

                if(loggedInUser.isActive()){
                    txt_Id.setText(String.valueOf(loggedInUser.getId()));
                    txt_Nome.setText(loggedInUser.getNome());
                    txt_Username.setText(loggedInUser.getUsername());
                    txt_Password.setText(loggedInUser.getPassword());
                    txt_Email.setText(loggedInUser.getEmail());
                    txt_Telemovel.setText(loggedInUser.getTelemovel());
                    txt_IsActive.setText(loggedInUser.isActive() ? "True" : "False");
                }else{
                    txt_Id.setText("Não existe nenhum utilizador logado");
                }
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bd.logoutLocal();
                txt_Id.setText("");
                txt_Nome.setText("");
                txt_Username.setText("");
                txt_Password.setText("");
                txt_Email.setText("");
                txt_Telemovel.setText("");
                txt_IsActive.setText("");
            }
        });

    }

}