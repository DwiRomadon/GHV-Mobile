package pptik.startup.ghvmobile.Setup;


public interface ApplicationConstants {

    /*Server API Key help
    AIzaSyDFVG5x8zo1blEqSGq95fAt80-uOTuonPQ
    Sender ID help
    135626507167*/
    // Php Application URL to store Reg ID created
   // static final String APP_SERVER_URL = "http://gcm.hynra.com/insertuser.php";

    // Google Project Number
   static final String GOOGLE_PROJ_ID = "135626507167";
    // Message Key
  static final String MSG_KEY = "m";
  //  static final String MSG_KEY_USER_OJEK = "g";
    // http url
  //  static final String HTTP_URL = "http://svc-bsts.lskk.ee.itb.ac.id/dev/api/";
    static final String HTTP_URL = "http://167.205.7.226:60327/v1/";
    static final String API_REGISTRATION = "http://bsts-svc.lskk.ee.itb.ac.id/dev/api/opank/daftaropank/";
    static final String API_LOGIN =HTTP_URL+ "useract/authenticate";
    static final String API_GET_START_PROGRAM = HTTP_URL+ "beritaact/showstart";
    static final String API_GET_ALL_PROGRAM = HTTP_URL+ "beritaact/showallverified";
    static final String API_DAFTAR_RELAWAN = HTTP_URL+ "useract/storerelawan/";
    static final String API_GET_RELAWAN_BY_ID =HTTP_URL+ "useract/profileshowbyid/";
    static final String API_GET_STATUS_DAFTAR = HTTP_URL+ "useract/statusdaftar/";
    static final String API_GET_LIST_BELUM_DI_APPROVE = HTTP_URL+ "useract/listbelumapprove";
    static final String API_GET_LIST_SUDAH_DI_APPROVE = HTTP_URL+ "useract/listsudahapprove";
    static final String API_APPROVAL = HTTP_URL+ "useract/approverelawan/";
    static final String API_GET_PROGRAM_SUDAH_DI_APPROVE = HTTP_URL+ "beritaact/listsudahapprove";
    static final String API_GET_PROGRAM_BELUM_DI_APPROVE = HTTP_URL+ "beritaact/listbelumapprove";
    static final String API_GET_PROGRAM_BY_ID = HTTP_URL+ "beritaact/beritabyid/";
    static final String API_GET_PROGRAM_BY_ID_USER = HTTP_URL+ "beritaact/beritabyiduser/";
    static final String API_APPROVAL_BERITA = HTTP_URL+ "beritaact/approveberita/";
    static final String API_DELETE_BERITA =HTTP_URL+ "beritaact/deleteberitabyid/";
    static final String API_GET_MAP_VIEW = HTTP_URL+ "useract/locationanddetail";
    static final String API_GET_MAP_VIEW_ADMIN = HTTP_URL+ "useract/locationanddetailadmin";
    static final String API_UPDATE_CURRENT_LOCATION = "useract/updatecurrentlocation/";
    static final String API_GET_URGENT_NEWS = HTTP_URL+ "beritaact/urgentnews";
  static final String API_UPDATE_PROGRAM_BY_ID = HTTP_URL+ "beritaact/update/";
  static final String API_GET_IMAGE_GALLERY_BY_ID_PROGRAM = HTTP_URL+ "beritaact/getimagegallerybyidprogram/";

  static final String FINE_REQUEST="FinERequest";
  static final String REG_ID = "regId";
  static final String EMAIL_ID = "eMailId";
  static final String LEVEL_ID = "roleId";
  static final String BSTS_ID = "userId";
  static final String USER_ID="UsErId";
  static final String USER_PREFS_NAME="UserDetails";
  static final String USER_LATITUDE="LaTidude";
  static final String USER_LONGITUDE="LongiTude";
  static final String PATH_FOTO_USER = "PatHFotOUser";
  static final String LOGIN_TAG_BY = "LogInTagBy";
  static final String DEFAULT_PATH_FOTO="http://167.205.7.228:8087/v1/resources/assets/image/default.png";
  static final int MARKER_ME = 0,MARKER_ADMIN=1,MARKER_USER=2,MARKER_PROGRAM=3;
  //------- MARKER PREFS NAME

}