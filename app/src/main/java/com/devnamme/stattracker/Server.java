package com.devnamme.stattracker;

final class Server {
    static final byte BR = 0;
    static final byte EUNE = 1;
    static final byte EUW = 2;
    static final byte LAN = 3;
    static final byte LAS = 4;
    static final byte NA = 5;
    static final byte OCE = 6;
    static final byte RU = 7;
    static final byte TR = 8;
    static final byte JP = 9;
    static final class SEA {
        static final byte PH = 10;
        static final byte SG = 11;
        static final byte TW = 12;
        static final byte VN = 13;
        static final byte TH = 14;
    }
    static final byte KR = 15;
    static final byte CN = 16;
    static final byte PBE = 17;

    static String toString(byte server) {
        switch(server) {
            case BR:    return "BR";
            case EUNE:  return "EUNE";
            case EUW:   return "EUW";
            case LAN:   return "LAN";
            case LAS:   return "LAS";
            case NA:    return "NA";
            case OCE:   return "OCE";
            case RU:    return "RU";
            case TR:    return "TR";
            case JP:    return "JP";
            case SEA.PH:    return "PH";
            case SEA.SG:    return "SG";
            case SEA.TW:    return "TW";
            case SEA.VN:    return "VN";
            case SEA.TH:    return "TH";
            case KR:    return "KR";
            case CN:    return "CN";
            case PBE:   return "PBE";
            default:    return "?";
        }
    }

    static int getServerFlag(byte server) {
        switch(server) {
            case BR:    return R.drawable.regionflag_br;
            case EUNE:  return R.drawable.regionflag_eune;
            case EUW:   return R.drawable.regionflag_euw;
            case LAN:   return R.drawable.regionflag_lan;
            case LAS:   return R.drawable.regionflag_las;
            case NA:    return R.drawable.regionflag_na;
            case OCE:   return R.drawable.regionflag_oce;
            case RU:    return R.drawable.regionflag_ru;
            case TR:    return R.drawable.regionflag_tr;
            case JP:    return R.drawable.regionflag_jp;
            case SEA.PH:    return R.drawable.regionflag_garena_ph;
            case SEA.SG:    return R.drawable.regionflag_garena_sg;
            case SEA.TW:    return R.drawable.regionflag_garena_tw;
            case SEA.VN:    return R.drawable.regionflag_garena_vn;
            case SEA.TH:    return R.drawable.regionflag_garena_th;
            case KR:    return R.drawable.regionflag_kr;
            case CN:    return R.drawable.regionflag_cn;
            case PBE:
            default:    return R.drawable.regionflag_default;
        }
    }
}
