package com.github.tractionprojects.wgs;

import java.util.ArrayList;

public class Developers
{
    private static final ArrayList<Long> devs = new ArrayList<>();

    static
    {
        devs.add(403571050178150400L);//Matt
        devs.add(104306564151656448L);//Dani
        devs.add(668447111574192129L);//Steven
        devs.add(668548539156135946L);//Miles
        devs.add(667820316331999232L);//Tim
    }

    public static boolean isDev(Long id)
    {
        return devs.contains(id);
    }
}
