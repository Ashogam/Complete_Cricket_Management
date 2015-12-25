package cric_grab.utility.cric_grap;

import java.util.Comparator;

/**
 * Created by ANDROID on 16-11-2015.
 */
public class Player_Info {

    private String player_name;
    private String player_mobile_number;

    public String getPlayer_mobile_number() {
        return player_mobile_number;
    }

    public void setPlayer_mobile_number(String player_mobile_number) {
        this.player_mobile_number = player_mobile_number;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public static Comparator<Player_Info> comparator=new Comparator<Player_Info>() {
        @Override
        public int compare(Player_Info lhs, Player_Info rhs) {
            String name1=lhs.getPlayer_name();
            String name2=rhs.getPlayer_name();
            return name1.compareTo(name2);
        }
    };
}
