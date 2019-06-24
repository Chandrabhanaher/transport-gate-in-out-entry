package com.example.ugc.ugc;

 public class Config {
     public static final String API = "http://14.143.175.253:8080/ugc_trans/";//"14.143.175.253:8080";

     public static final String USER_LOGINS = API+"user_login.php";
     public static final String CUST_LIST = API+"cust_spinner.php";
     public static final String SHARED_PREF_NAME = "UGC";
     public static final String LOGGEDIN_SHARED_PREF = "loggedIn";
     public static final String EMAIL_SHARED_PREF = "username";
     public static final String CUST_SHARED_PREF = "cust_name";

     public static final String TPID = API+"tp_getTid.php";
     public static final String DATE_REPORT = API+"datewise_vehicle_details.php";
     public static final String VEHICLE_REPORT = API+"vehicle_report.php";
     public static final String DRIVER_DETAILS = API +"driver_details.php";
     public static final String CHECK_LOADING_UNLOADING = API +"check_loading_unloading.php";
     public static final String OUT_VEHICLE1 = API + "tp_vehicle_out1.php";
     public static String VEH_DETAILS= API+"veh_details.php";
     public static final String CHECK_VEHICLES = API+"check_vehicle_no.php";
     public static final String NEWENTRY = API+"tp_intime.php";
     public static final String OUT_VEHICLE = API+"tp_vehicle_out.php";

 }
