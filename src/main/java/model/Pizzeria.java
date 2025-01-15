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

    public Pizzeria(List<Table> tables) {
      this.tables = tables;
    }



}