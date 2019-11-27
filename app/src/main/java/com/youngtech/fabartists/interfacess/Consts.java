package com.youngtech.fabartists.interfacess;

/**
 * Created by VARUN on 01/01/19.
 */

public interface Consts {
    String APP_NAME = "FabArtist";
    //old 677440
    String BASE_URL = "http://phpstack-132936-852279.cloudwaysapps.com/Webservice/";
    String PAYMENT_FAIL = "http://phpstack-132936-852279.cloudwaysapps.com/Stripe/Payment/fail";
    String PAYMENT_SUCCESS = "http://phpstack-132936-852279.cloudwaysapps.com/Stripe/Payment/success";
    String MAKE_PAYMENT = "http://phpstack-132936-852279.cloudwaysapps.com/Stripe/Payment/make_payment/";

    String PAYMENT_FAIL_Paypal = "http://phpstack-132936-852279.cloudwaysapps.com/Webservice/payufailure";
    String PAYMENT_SUCCESS_paypal = "http://phpstack-132936-852279.cloudwaysapps.com/Webservice/payusuccess";
    String MAKE_PAYMENT_paypal = "http://phpstack-132936-852279.cloudwaysapps.com/Webservice/paypalWallent?";
    String PRIVACY_URL = "http://phpstack-132936-852279.cloudwaysapps.com/Webservice/privacy";
    String TERMS_URL = "http://phpstack-132936-852279.cloudwaysapps.com/Webservice/term";


    /*Api Details*/
    String LOGIN_API = "signIn";
    String REGISTER_API = "SignUp";
    String GET_REFERRAL_CODE_API = "getMyReferralCode";
    String GET_CHAT_HISTORY_API = "getChatHistoryForArtist";
    String GET_CHAT_API = "getChat";
    String SEND_CHAT_API = "sendmsg";
    String GET_NOTIFICATION_API = "getNotifications";
    String GET_ARTIST_BY_ID_API = "getArtistByid";
    String UPDATE_PROFILE_API = "editPersonalInfo";
    String UPDATE_PROFILE_ARTIST_API = "artistPrsonalInfo";
    String GET_ALL_CATEGORY_API = "getAllCaegory";
    String GET_ALL_SKILLS_BY_CAT_API = "getSkillsByCategory";
    String ADD_QUALIFICATION_API = "addQualification";
    String ADD_PRODUCT_API = "addProduct";
    String ADD_GALLERY_API = "addGallery";
    String GET_INVOICE_API = "getMyInvoice";
    String CURRENT_BOOKING_API = "getMyCurrentBooking";
    String BOOKING_OPERATION_API = "booking_operation";
    String DECLINE_BOOKING_API = "decline_booking";
    String ONLINE_OFFLINE_API = "onlineOffline";
    String UPDATE_LOCATION_API = "updateLocation";
    String ARTIST_LOGOUT_API = "artistLogout";
    String GET_MY_TICKET_API = "getMyTicket";
    String GENERATE_TICKET_API = "generateTicket";
    String GET_TICKET_COMMENTS_API = "getTicketComments";
    String ADD_TICKET_COMMENTS_API = "addTicketComments";
    String FORGET_PASSWORD_API = "forgotPassword";
    String GET_APPOINTMENT_API = "getAppointment";
    String EDIT_APPOINTMENT_API = "edit_appointment";
    String APPOINTMENT_OPERATION_API = "appointment_operation";
    String GET_ALL_JOB_API = "get_all_job";
    String APPLIED_JOB_API = "applied_job";
    String JOB_STATUS_ARTIST_API = "job_status_artist";
    String GET_APPLIED_JOB_ARTIST_API = "get_applied_job_artist";
    String CHANGE_COMMISSION_ARTIST_API = "changeCommissionArtist";
    String START_JOB_API = "startJob";
    String DELETE_PROFILE_IMAGE_API = "deleteProfileImage";
    String MY_EARNING1_API = "myEarning1";
    String WALLET_REQUEST_API = "walletRequest";
    String ADD_MONEY_API = "addMoney";
    String GET_WALLET_HISTORY_API = "getWalletHistory";
    String GET_WALLET_API = "getWallet";
    String DELETE_GALLERY_API = "deleteGallery";
    String DELETE_PRODUCT_API = "deleteProduct";
    String UPDATE_QUALIFICATION_API = "updateQualification";
    String DELETE_QUALIFICATION_API = "deleteQualification";
    String GET_ALL_BOOKING_ARTIST_API = "getAllBookingArtist";
    String GET_APPROVAL_STATUS_API = "getApprovalStatus";

    /*app data*/
    String CAMERA_ACCEPTED = "camera_accepted";
    String STORAGE_ACCEPTED = "storage_accepted";
    String MODIFY_AUDIO_ACCEPTED = "modify_audio_accepted";
    String CALL_PRIVILAGE = "call_privilage";
    String FINE_LOC = "fine_loc";
    String CORAS_LOC = "coras_loc";
    String CALL_PHONE = "call_phone";
    String PAYMENT_URL = "payment_url";
    String SURL = "surl";
    String FURL = "furl";
    String ARTIST_DTO = "artist_dto";
    String CATEGORY_list = "category_list";
    String SCREEN_TAG = "screen_tag";
    String HISTORY_DTO = "history_dto";
    /*app data*/

    /*Project Parameter*/
    String ARTIST_ID = "artist_id";
    String CHAT_LIST_DTO = "chat_list_dto";
    String USER_DTO = "user_dto";
    String IS_REGISTERED = "is_registered";
    String IMAGE_URI_CAMERA = "image_uri_camera";
    String DATE_FORMATE_SERVER = "EEE, MMM dd, yyyy hh:mm a"; //Wed, JUL 06, 2018 04:30 pm
    String DATE_FORMATE_TIMEZONE = "z";
    String BROADCAST = "broadcast";
    String LOCATION1 = "location1";
    String PRODUCT_ID = "product_id";
    String BOOKING_FLAG = "booking_flag";


    /*Parameter Get Artist and Search*/
    String USER_ID = "user_id";
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";
    String ID = "id";
    /*Get All History*/
    String ROLE = "role";

    /*Send Message*/
    String MESSAGE = "message";
    String SEND_BY = "send_by";
    String SENDER_NAME = "sender_name";


    /*Login Parameter*/
    String NAME = "name";
    String EMAIL_ID = "email_id";
    String PASSWORD = "password";
    String DEVICE_TYPE = "device_type";
    String DEVICE_TOKEN = "device_token";
    String DEVICE_ID = "device_id";
    String REFERRAL_CODE = "referral_code";


    /*Update Profile*/
    String NEW_PASSWORD = "new_password";
    String GENDER = "gender";
    String MOBILE = "mobile";
    String OFFICE_ADDRESS = "office_address";
    String ADDRESS = "address";
    String IMAGE = "image";

    /*Update Profile Artist*/
    String CATEGORY_ID = "category_id";
    String BIO = "bio";
    String LOCATION = "location";
    String PRICE = "price";
    String ABOUT_US = "about_us";
    String SKILLS = "skills";
    String VIDEO_URL = "video_url";
    String CITY = "city";
    String COUNTRY = "country";
    String BANNER_IMAGE = "banner_image";

    /*Get Skills*/
    String CAT_ID = "cat_id";


    /*Update Qualification*/
    String TITLE = "title";
    String DESCRIPTION = "description";
    String QUALIFICATION_ID = "qualification_id";

    /*Update Qualification*/
    String PRODUCT_NAME = "product_name";
    String PRODUCT_IMAGE = "product_image";

    /*Booking Opreations*/
    String BOOKING_ID = "booking_id";
    String REQUEST = "request";

    /*Decline*/
    String DECLINE_BY = "decline_by";
    String DECLINE_REASON = "decline_reason";

    /*Online Offline*/
    String IS_ONLINE = "is_online";

    /*Add Ticket*/
    String REASON = "reason";


    /*Get Ticket*/
    String TICKET_ID = "ticket_id";
    String COMMENT = "comment";

    /*Edit Appointment*/
    String APPOINTMENT_ID = "appointment_id";


    /*Book Artist*/

    String DATE_STRING = "date_string";
    String TIMEZONE = "timezone";


    /*Apply Job*/
    String JOB_ID = "job_id";

    /*Job Status*/
    String AJ_ID = "aj_id";
    String STATUS = "status";


    // Google Console APIs developer key
    // Replace this key with your's
    static final String DEVELOPER_KEY = "AIzaSyArfMm1YCgq6FCtxA7w_W-pOxJ_0D6GGy8";

    /*Chat*/
    String CHAT_TYPE = "chat_type";

    /*Payment Type*/
    String ARTIST_COMMISSION_TYPE = "artist_commission_type";

    /*Add Money*/
    String TXN_ID = "txn_id";
    String ORDER_ID = "order_id";
    String AMOUNT = "amount";
    String CURRENCY = "currency";


    /*Notifications Codes*/
    String CHAT_NOTIFICATION = "10007";//both
    String TICKET_COMMENT_NOTIFICATION = "10009";//both
    String TICKET_STATUS_NOTIFICATION = "10015";//both
    String WALLET_NOTIFICATION = "10010";//both
    String DECLINE_BOOKING_ARTIST_NOTIFICATION = "10002";//both
    String START_BOOKING_ARTIST_NOTIFICATION = "10003";//ar
    String BRODCAST_NOTIFICATION = "10014";//both
    String ADMIN_NOTIFICATION = "10016";//both



    String BOOK_ARTIST_NOTIFICATION = "10001";//ar
    String END_BOOKING_ARTIST_NOTIFICATION = "10004";//user
    String CANCEL_BOOKING_ARTIST_NOTIFICATION = "10005";
    String ACCEPT_BOOKING_ARTIST_NOTIFICATION = "10006";//user
    String USER_BLOCK_NOTIFICATION = "1008";
    String JOB_NOTIFICATION = "10011";//ar
    String JOB_APPLY_NOTIFICATION = "10012";//user
    String DELETE_JOB_NOTIFICATION = "10013";//ar
    String TYPE = "type";
}
