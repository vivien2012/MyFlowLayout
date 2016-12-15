package com.vivien.flowlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MyFlowLayout myFlowLayout = (MyFlowLayout) findViewById(R.id.myflowlayout);
        myFlowLayout.setOnItemClickListener(new MyFlowLayout.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Toast.makeText(MainActivity.this, "pos : " + pos, Toast.LENGTH_SHORT).show();
            }
        });

        MyFlowLayout2 myFlowLayout2 = (MyFlowLayout2) findViewById(R.id.myflowlayout2);
        myFlowLayout2.addItems(getItems());
        myFlowLayout2.setOnItemDelListener(new MyFlowLayout2.OnItemDelListener() {
            @Override
            public void onDelete(int pos) {
                Toast.makeText(MainActivity.this, "pos : " + pos + "is delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ArrayList<String> getItems() {
        ArrayList<String> datas = new ArrayList<>();
        datas.add("hello");
        datas.add("sunshine");
        datas.add("Christmas");
        datas.add("Christmas Eve");
        datas.add("Christmas card");
        datas.add("you are my sunshine");
        return datas;
    }

}
