package model;

import java.util.ArrayList;
import java.util.List;

public class Pizzeria {
    @Override
    public String toString() {
        return "model.Pizzeria{" +
                "tables=" + tables +
                "}\n";
    }

    public List<Table> getTables() {
        return tables;
    }

    private List<Table> tables= new ArrayList<>();

    public Pizzeria(int x1, int x2, int x3, int x4) {
        for (int i = 0; i < x1; i++) tables.add(new Table(1,false,new ArrayList<>()));
        for (int i = 0; i < x2; i++) tables.add(new Table(2,false,new ArrayList<>()));
        for (int i = 0; i < x3; i++) tables.add(new Table(3,false,new ArrayList<>()));
        for (int i = 0; i < x4; i++) tables.add(new Table(4,false,new ArrayList<>()));
    }



}