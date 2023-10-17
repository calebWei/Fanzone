package com.example.Entities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class League {
    public static ArrayList<String> eplLeague = new ArrayList<String>();
    public static ArrayList<String> laligaLeague = new ArrayList<String>();
    public static ArrayList<String> bundesligaLeague = new ArrayList<String>();
    ArrayList<ArrayList<String>> allLeagues = new ArrayList<ArrayList<String>>();

    public League() {
        addClubs();
    }

    public void addClubs() {
        // bundesliga
        bundesligaLeague.add("bayernMunich");
        bundesligaLeague.add("leipzig");
        bundesligaLeague.add("leverkusen");

        // epl
        eplLeague.add("arsenal");
        eplLeague.add("manchesterCity");
        eplLeague.add("manchesterUnited");

        // laliga
        laligaLeague.add("atleticoMadrd");
        laligaLeague.add("barcelona");
        laligaLeague.add("realMadrid");

        Collections.addAll(allLeagues,bundesligaLeague,eplLeague,laligaLeague);
    }

    public ArrayList<String> getAllClubs() {
        ArrayList<String> allClubs = new ArrayList<String>();

        for (ArrayList<String> array : allLeagues) {
            allClubs.addAll(array);
        }

        return allClubs;
    }
}
