package com.po.app;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({Functional.class, WebAccess.class})
public class AppTest {

    @Test
    public void mainTestShow()
    {
        App.main(new String[]{"-d", "GIOS", "-s"});
        App.main(new String[]{"-d", "Airly", "-s"});
    }

    @Test
    public void mainTestF1() {
        App.main(new String[]{"-d", "GIOS", "-f1", "Kraków, Aleja Krasińskiego"});
        App.main(new String[]{"-d", "Airly", "-f1", "Polska Stacja Polarna"});
        App.main(new String[]{"-d", "GIOS", "-f1", ""});
    }

    @Test
    public void mainTestF2() {
        App.main(new String[]{"-d", "GIOS", "-f2", "Kraków, Aleja Krasińskiego", "PM10"});
        App.main(new String[]{"-d", "Airly", "-f2", "Wieliczka_Adama Asnyka", "PM25"});
    }

    @Test
    public void mainTestF3() {
        App.main(new String[]{"-d", "GIOS", "-f3", "bg", "GIOS", "2018-12-22_12:04:00", "2018-12-22_12:04:00"});
        App.main(new String[]{"-d", "GIOS", "-c", "-f3", "Kraków, Aleja Krasińskiego", "PM10",
                "2018-12-12_12:04:00", "2019-12-12_12:04:00"});
    }

    @Test
    public void mainTestF4() {
        App.main(new String[] {"-d", "Airly", "-f4", "Polska Stacja Polarna", "2018-12-12_12:04:00"});
        App.main(new String[] {"-d", "GIOS", "-f4", "Kraków, Aleja Krasińskiego", "Kraków, ul. Bujaka", "2018-12-22_12:04:00"});
    }

    @Test
    public void mainTestF5() {
        App.main(new String[] {"-d", "Airly", "-f5", "Polska Stacja Polarna", "2019-01-20_03:00:00"});
        App.main(new String[] {"-d", "GIOS", "-f5", "Kraków, os. Swoszowice", "2019-01-20_12:00:00"});
    }

    @Test
    public void mainTestF6() {
        App.main(new String[] {"-d", "GIOS", "-f6", "Żyrardów-Roosevelta", "2019-01-20_03:00:00", "1"});
        App.main(new String[] {"-d", "Airly", "-f6", "Wieliczka_Adama Asnyka", "2019-01-20_03:00:00", "5"});
    }


    @Test
    public void mainTestF7() {
        App.main(new String[] {"-d", "GIOS", "-f7", "Żyrardów-Roosevelta", "Kraków, os. Swoszowice", "PM10"});
        App.main(new String[] {"-d", "Airly", "-f7", "Tarnów_Bitwy pod Studziankami",
                "Wieliczka_Adama Asnyka", "Polska Stacja", "TEMPERATURE"});
    }

    @Test
    public void mainTestF8() {
        App.main(new String[] {"-d", "GIOS", "-f8", "O3", "Żyrardów-Roosevelta",
                "Kraków, Aleja Krasińskiego", "Kraków, ul. Bujaka", "2019-01-20_20:00:00",
                "2019-01-20_20:00:00"});
    }
}
