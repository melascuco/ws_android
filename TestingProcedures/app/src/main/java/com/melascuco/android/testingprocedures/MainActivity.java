package com.melascuco.android.testingprocedures;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> elements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        elements = new ArrayList<>();
        addElement("uno");
        addElement("dos");

    }

    private void addElement(String name) {
        elements.add(name);
    }

    private List<String> listElement() {
        return elements;
    }

    private void deleteElement(String name) {
        for (String nameInElements : elements) {
            if (name.equalsIgnoreCase(nameInElements)) {
                elements.remove(nameInElements);
            }
        }
    }

    private void updateElement(String name1, String name2) {
        for (String nameInElements : elements) {
            if (name1.equalsIgnoreCase(nameInElements)) {
                elements.remove(nameInElements);
                elements.add(name2);
            }
        }
    }

}
