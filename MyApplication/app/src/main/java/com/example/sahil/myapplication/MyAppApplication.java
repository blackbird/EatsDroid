package com.example.sahil.myapplication;

import android.app.Application;
import java.util.Set;
import feast.Meal;
import feast.Menu;

class MyAppApplication extends Application {

    private Set<Menu> menuSet;

    public Set<Menu> getGlobalVarValue() {
        return menuSet;
    }

    public void setGlobalVarValue(Set<Menu> meals) {
        menuSet = meals;
    }
}
