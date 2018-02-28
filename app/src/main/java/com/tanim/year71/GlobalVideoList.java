package com.tanim.year71;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tanim on 2/27/18.
 */

public class GlobalVideoList {
    public static ArrayList<VideoInfo> MOVIE_LIST = new ArrayList<>();
    public static ArrayList<VideoInfo> DOCUMENTARY_LIST = new ArrayList<>();
    public static final List<HomeActivity.VideoEntry> VIDEO_LIST;
    static {
        List<HomeActivity.VideoEntry> list = new ArrayList<HomeActivity.VideoEntry>();
        list.add(new HomeActivity.VideoEntry("Bangla Song \"Dipannita\"", "Bph709EqnHk"));
        list.add(new HomeActivity.VideoEntry("Kolkata | Full Video Song | PRAKTAN | Anupam Roy | Shreya Ghoshal | Prosenjit & Rituparna", "YmIhZCNXfJE"));
        list.add(new HomeActivity.VideoEntry("কেউ কথা রাখে নি (Keu kotha rakhe ni) | সুনীল গঙ্গোপাধ্যায় | Medha Bandopadhyay recitation", "nhrOuQYU8XI"));
        list.add(new HomeActivity.VideoEntry("Deyale Deyale | Minar | Tomar Amar Prem | Siam | Ognila | Mizanur Rahman Aryan |Bangla New Song 2017", "XChdfPIvoIo"));
        list.add(new HomeActivity.VideoEntry("ICC T20 WC 2016_BD vs NED (Netherlands Full Innings)", "P_PkB7FAfok&t=5336s"));
        list.add(new HomeActivity.VideoEntry("Australia Vs Bangladesh Highlights Cricket News", "WPrSMmcXkys"));
        list.add(new HomeActivity.VideoEntry("দায়িত্ব পেয়েই পুরো উদ্যমে কাজ শুরু করেছেন কোর্টনি ওয়ালশ,ভাগ্য ফেরাতে সবরকম চেষ্টা।", "ZQ7HvZBDcoM"));
        list.add(new HomeActivity.VideoEntry("** Rare ** India vs Sri Lanka Final ICC Champions Trophy 2002 HQ Extended Highlights", "1UjB2DNF00Q"));
        list.add(new HomeActivity.VideoEntry("ICC #WT20 Bangladesh vs New Zealand Match Highlights", "hb_CnY1jS_c"));
        list.add(new HomeActivity.VideoEntry("Mirakkel Akkel Challenger 6 September 13 '11 - Abu Heena Roni", "YYR7wznAUbk"));
        list.add(new HomeActivity.VideoEntry("CHUMMA | ROMANTIC SONG | AMI NETA HOBO | SHAKIB KHAN | BIDYA SINHA | LATEST BENGALI SONG 2018", "8IGv50XasM4"));
        VIDEO_LIST = Collections.unmodifiableList(list);
    }
}
