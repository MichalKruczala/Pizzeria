package model;

import java.util.ArrayList;
import java.util.List;

public class Pizzeria {
    private List<Table> tables;


    public Pizzeria(List<Table> tables) {
        this.tables = tables;
    }

    @Override
    public String toString() {
        return "model.Pizzeria{" +
                "tables=" + tables +
                "}\n";
    }

    public List<Table> getTables() {
        return tables;
    }



}